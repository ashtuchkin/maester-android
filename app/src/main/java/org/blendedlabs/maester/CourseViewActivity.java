package org.blendedlabs.maester;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.simple.SimpleTextRequest;

public class CourseViewActivity extends FragmentActivity {
    private SpiceManager spiceManager = new SpiceManager(SpiceService.class);

    // Basic data
    private Uri mCourseUrl;
    private CourseModel mCourse;

    // Views
    private SlidesPagerAdapter mSlideAdapter;


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
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        if (getResources().getConfiguration().orientation == 1) {
            getActionBar().show();
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getActionBar().hide();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.course_view);

        mCourseUrl = getIntent().getData();
        mSlideAdapter = new SlidesPagerAdapter(getSupportFragmentManager());

        if (savedInstanceState != null) {
            mCourse = savedInstanceState.getParcelable("course");
            setTitle(" " + mCourse.name);
            mSlideAdapter.setCourseSlides(mCourseUrl, mCourse.slides);
        } else {
            setTitle("");
            loadCourseData(false);
        }

        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager)findViewById(R.id.course_view_pager);
        viewPager.setOffscreenPageLimit(5); // Load 5 slides before and after current.
        viewPager.setAdapter(mSlideAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSlideAdapter = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("course", mCourse);
        // mCourseUrl is already saved in getIntent().getData()
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_view_refresh) {
            loadCourseData(true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadCourseData(final boolean refresh) {
        String cacheKey = Utils.md5(mCourseUrl.toString());
        long cacheDuration = DurationInMillis.ONE_DAY;
        if (refresh)
            spiceManager.removeAllDataFromCache(); // Remove images and other resources.

        SimpleTextRequest request = new SimpleTextRequest(mCourseUrl.toString());
        spiceManager.execute(request, cacheKey, cacheDuration, new RequestListener<String>() {
            @Override
            public void onRequestFailure(SpiceException e) {
                if (isDestroyed()) return;
                Toast.makeText(CourseViewActivity.this, "Error loading course.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRequestSuccess(String str) {
                if (isDestroyed()) return;

                try {
                    mCourse = new Gson().fromJson(str, CourseModel.class);
                } catch (JsonSyntaxException e) {
                    String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                    Toast.makeText(CourseViewActivity.this, "Invalid Course JSON:\n" + msg, Toast.LENGTH_LONG).show();
                    return;
                }
                if (mCourse == null || mCourse.name == null || mCourse.slides == null) {
                    Toast.makeText(CourseViewActivity.this, "Invalid Course JSON: name and slides must exist.", Toast.LENGTH_LONG).show();
                    return;
                }

                setTitle(" " + mCourse.name);
                if (mSlideAdapter != null)
                    mSlideAdapter.setCourseSlides(mCourseUrl, mCourse.slides);

                if (refresh)
                    Toast.makeText(CourseViewActivity.this, "Course reloaded.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // TODO: look for possible problems at http://speakman.net.nz/blog/2014/02/20/a-bug-in-and-a-fix-for-the-way-fragmentstatepageradapter-handles-fragment-restoration/
    public static class SlidesPagerAdapter extends FragmentStatePagerAdapter {
        private CourseModel.Slide[] mSlides = new CourseModel.Slide[] {};
        private Uri mCourseBaseUrl;

        public SlidesPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setCourseSlides(Uri courseBaseUrl, CourseModel.Slide[] slides) {
            mSlides = slides;
            mCourseBaseUrl = courseBaseUrl;
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE; // Recreate all views on notifyDataSetChanged().
        }

        @Override
        public Fragment getItem(int position) {
            return SlideFragment.newInstance(mSlides[position], mCourseBaseUrl, position + 1, mSlides.length);
        }

        @Override
        public int getCount() {
            return mSlides.length;
        }
    }

}
