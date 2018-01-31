package de.lmu.ifi.pixelfighter.activities;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.lmu.ifi.pixelfighter.R;

public class OnboardingActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private String TAG = "OnboardingActivity";


    @BindView(R.id.onbrd_btn_skip)
    Button skip_button;
    @BindView(R.id.onbrd_btn_finish)
    Button finish_button;
    @BindView(R.id.onbrd_btn_next)
    ImageButton next_button;
    @BindView(R.id.onbrd_btn_prev)
    ImageButton prev_button;

    @BindViews({R.id.intro_indicator_0, R.id.intro_indicator_1, R.id.intro_indicator_2, R.id.intro_indicator_3})
    ImageView[] indicators;


    //page variables
    private int page = 0;

    //Color variables
    private ArgbEvaluator evaluator = new ArgbEvaluator();

    int[] colorList;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar_color_onbrd));

        setContentView(R.layout.activity_onboarding);

        ButterKnife.bind(this);


//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mViewPager.setCurrentItem(page);
        updateIndicators(page);

        int color1 = ContextCompat.getColor(this, R.color.onboarding_screen_bg_1);
        int color2 = ContextCompat.getColor(this, R.color.onboarding_screen_bg_2);
        int color3 = ContextCompat.getColor(this, R.color.onboarding_screen_bg_3);
        int color4 = ContextCompat.getColor(this, R.color.onboarding_screen_bg_4);
        colorList = new int[]{color1, color2, color3, color4};
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //color update
        int colorUpdate = (Integer) evaluator.evaluate(positionOffset, colorList[position], colorList[position == 3 ? position : position + 1]);
        mViewPager.setBackgroundColor(colorUpdate);
    }

    @Override
    public void onPageSelected(int position) {
        page = position;
        updateIndicators(page);

        switch (position) {
            case 0:
                mViewPager.setBackgroundColor(colorList[position]);
                break;
            case 1:
                mViewPager.setBackgroundColor(colorList[position]);
                break;
            case 2:
                mViewPager.setBackgroundColor(colorList[position]);
                break;
            case 3:
                mViewPager.setBackgroundColor(colorList[position]);
                break;
        }

        next_button.setVisibility(position == 3 ? View.GONE : View.VISIBLE);
        finish_button.setVisibility(position == 3 ? View.VISIBLE : View.GONE);
        skip_button.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        prev_button.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    void updateIndicators(int position) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(
                    i == position ? R.drawable.ic_indicator_selected : R.drawable.ic_indicator_unselected
            );
        }
    }

    @OnClick(R.id.onbrd_btn_next)
    public void next() {
        page++;
        mViewPager.setCurrentItem(page, true);
    }

    @OnClick(R.id.onbrd_btn_prev)
    public void previous() {
        page--;
        mViewPager.setCurrentItem(page, true);
    }

    @OnClick(R.id.onbrd_btn_skip)
    public void skip() {
        finish();
    }

    @OnClick(R.id.onbrd_btn_finish)
    public void finishOnboard() {
        finish();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        ImageView img;

        int[] onboarding_imgs = new int[]{R.drawable.onboarding1, R.drawable.onboarding2, R.drawable.onboarding3, R.drawable.onboarding4};
        int[] onboarding_titles = new int[]{R.string.onboarding1_title, R.string.onboarding2_title, R.string.onboarding3_title, R.string.onboarding4_title};
        int[] onboarding_txts = new int[]{R.string.onboarding1_text, R.string.onboarding2_text, R.string.onboarding3_text, R.string.onboarding4_text};

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_onboarding, container, false);

            TextView textView_title = rootView.findViewById(R.id.section_title);
            textView_title.setText(getString(onboarding_titles[getArguments().getInt(ARG_SECTION_NUMBER) - 1]));

            TextView textView_text = rootView.findViewById(R.id.section_label);
            textView_text.setText(getString(onboarding_txts[getArguments().getInt(ARG_SECTION_NUMBER) - 1]));

            img = rootView.findViewById(R.id.section_image);
            img.setBackgroundResource(onboarding_imgs[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }
    }
}
