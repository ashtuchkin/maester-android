package org.blendedlabs.maester;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.octo.android.robospice.request.simple.BitmapRequest;

import java.io.File;

public class CourseListActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {
    private SpiceManager spiceManager = new SpiceManager(SpiceService.class);
    private ListView listView;
    private CourseListAdapter listViewAdapter;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_list);
        setTitle(" Featured Courses");

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.course_list_layout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorScheme(R.color.swipe_refresh_color1, R.color.swipe_refresh_color2,
                R.color.swipe_refresh_color3, R.color.swipe_refresh_color4);

        listView = (ListView) findViewById(R.id.list);

        listViewAdapter = new CourseListAdapter();

        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Uri courseUrl = Uri.withAppendedPath(SpiceService.baseUrl, ((CourseCoverModel) view.getTag()).courseUrl);
                startActivity(new Intent(Intent.ACTION_VIEW, courseUrl, getApplicationContext(), CourseViewActivity.class));
            }
        });

        requestCourseList(false);
    }

    private void requestCourseList(final boolean refresh) {
        final String cacheKey = "courseList";
        final long cacheDuration = refresh ? DurationInMillis.ALWAYS_EXPIRED : DurationInMillis.ONE_DAY;

        RetrofitSpiceRequest<CourseCoverModel.List, LearnFlowWebAPI> request =
                new RetrofitSpiceRequest<CourseCoverModel.List, LearnFlowWebAPI>(CourseCoverModel.List.class, LearnFlowWebAPI.class) {
            @Override
            public CourseCoverModel.List loadDataFromNetwork() throws Exception {
                return getService().courses();
            }
        };

        spiceManager.execute(request, cacheKey, cacheDuration, new RequestListener<CourseCoverModel.List>() {
            @Override
            public void onRequestFailure(SpiceException e) {
                Toast.makeText(CourseListActivity.this, "Failed to fetch course list.", Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onRequestSuccess(CourseCoverModel.List courses) {
                listViewAdapter.setNotifyOnChange(false);
                listViewAdapter.clear();
                listViewAdapter.addAll(courses);
                listViewAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
        });
    }


    private void requestCourseCover(final int viewId, String url) {
        final String cacheKey = Utils.md5(url);
        final long cacheDuration = DurationInMillis.ONE_WEEK;

        //BinaryRequest request = new SmallBinaryRequest(url);
        BitmapRequest request = new BitmapRequest(url, new File(getCacheDir(), cacheKey));

        spiceManager.execute(request, cacheKey, cacheDuration, new RequestListener<Bitmap>() {
            @Override
            public void onRequestFailure(SpiceException e) {
                Toast.makeText(CourseListActivity.this, "Failed to fetch bitmap.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRequestSuccess(Bitmap bitmap) {
                View itemView = listView.findViewById(viewId);
                if (itemView != null) {
                    ImageView courseCoverView = (ImageView) itemView.findViewById(R.id.imageView);
                    //Bitmap bitmap = BitmapFactory.decodeStream(bitmapStream);
                    courseCoverView.setImageBitmap(bitmap);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            Toast.makeText(this, "Not implemented yet.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        requestCourseList(true);
    }


    public class CourseListAdapter extends ArrayAdapter<CourseCoverModel> {

        public CourseListAdapter() {
            super(CourseListActivity.this, R.layout.course_list_item);
        }

        @Override
        public View getView(int pos, View view, ViewGroup parent) {
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.course_list_item, parent, false);
            }

            CourseCoverModel item = getItem(pos);
            CourseCoverModel oldItem = (CourseCoverModel) view.getTag();
            if (item.equals(oldItem))
                return view;
            else {
                view.setTag(item);
                view.setId(Utils.generateViewId());
            }

            ((TextView) view.findViewById(R.id.name)).setText(item.name);
            ((ImageView) view.findViewById(R.id.imageView)).setImageBitmap(null);
            if (item.author != null)
                ((TextView) view.findViewById(R.id.author)).setText("By " + item.author);

            requestCourseCover(view.getId(), Uri.withAppendedPath(SpiceService.baseUrl, item.imageUrl).toString());

            return view;
        }
    }
}
