package com.rudanic.movieschart.activity.movies_details;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.rudanic.movieschart.model.Movies;
import com.rudanic.movieschart.widgets.FragmentStatePagerAdapter;

/**
 * Created by rudani_c on 19-05-2016.
 */
public class MoviesDetailsAdapter extends FragmentStatePagerAdapter {

    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[]{"Overview", "Videos", "Reviews"};
    private FragmentManager fm;
    private Movies movies;

    public MoviesDetailsAdapter(FragmentManager fm, Movies movies) {
        super(fm);
        this.fm=fm;
        this.movies = movies;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return OverviewFragment.newInstance(movies);
            case 1:
                return VideosFragment.newInstance(movies);
            case 2:
                return ReviewsFragment.newInstance(movies);
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

}
