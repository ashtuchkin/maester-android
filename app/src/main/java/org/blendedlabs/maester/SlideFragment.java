package org.blendedlabs.maester;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.simple.BitmapRequest;

import java.io.File;

public class SlideFragment extends Fragment {
    private static final String ARG_SLIDE = "slide";
    private static final String ARG_SLIDE_POS = "slide_pos";
    private static final String ARG_SLIDE_TOTAL = "slide_total";
    private static final String ARG_SLIDE_COURSE_URL = "slide_course_url";

    private SpiceManager spiceManager = new SpiceManager(SpiceService.class);

    // Basic data (unchanged)
    private CourseModel.Slide mSlide;
    private int mPos, mTotal;
    private Uri mCourseBaseUrl;

    // Subviews (recreated multiple times)
    private ImageView mImageView;
    private TextView mTextView;

    // Retained resources
    private Bitmap mBitmap;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SlideFragment newInstance(CourseModel.Slide slide, Uri courseBaseUrl, int pos, int total) {
        SlideFragment fragment = new SlideFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SLIDE, slide);
        args.putInt(ARG_SLIDE_POS, pos);
        args.putInt(ARG_SLIDE_TOTAL, total);
        args.putParcelable(ARG_SLIDE_COURSE_URL, courseBaseUrl);
        fragment.setArguments(args);
        return fragment;
    }

    public SlideFragment() {
    }

    @Override
    public void onStart() {
        spiceManager.start(getActivity());
        super.onStart();
    }

    @Override
    public void onStop() {
        if (spiceManager.isStarted())
            spiceManager.shouldStop();
        super.onStop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        mSlide = getArguments().getParcelable(ARG_SLIDE);
        mPos = getArguments().getInt(ARG_SLIDE_POS);
        mTotal = getArguments().getInt(ARG_SLIDE_TOTAL);
        mCourseBaseUrl = getArguments().getParcelable(ARG_SLIDE_COURSE_URL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.slide_fragment, container, false);
        mImageView = (ImageView) rootView.findViewById(R.id.imageView);
        mTextView  = (TextView) rootView.findViewById(R.id.textView);

        if (mSlide.text != null)
            mTextView.setText(mSlide.text);
        if (mSlide.backgroundColor != null)
            rootView.setBackgroundColor(Color.parseColor(mSlide.backgroundColor));

        // Use retained bitmap if available, request otherwise.
        if (mBitmap != null)
            mImageView.setImageBitmap(mBitmap);
        else if (mSlide.imageUrl != null)
            requestSlideImage(mSlide.imageUrl);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mImageView = null;
        mTextView = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(String.format("%d / %d", mPos, mTotal)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    private void requestSlideImage(String uri) {
        String url = Utils.relativeUri(mCourseBaseUrl, uri).toString();
        final String cacheKey = Utils.md5(url);
        final long cacheDuration = DurationInMillis.ONE_WEEK;

        //BinaryRequest request = new SmallBinaryRequest(url);
        BitmapRequest request = new BitmapRequest(url, new File(getActivity().getCacheDir(), cacheKey));

        spiceManager.execute(request, cacheKey, cacheDuration, new RequestListener<Bitmap>() {
            @Override
            public void onRequestFailure(SpiceException e) {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), "Failed to fetch slide.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRequestSuccess(Bitmap bitmap) {
                if (mImageView != null) {
                    mImageView.setImageBitmap(bitmap);
                    mBitmap = bitmap;
                }
            }
        });
    }
}
