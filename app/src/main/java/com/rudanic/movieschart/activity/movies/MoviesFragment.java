package com.rudanic.movieschart.activity.movies;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.rudanic.movieschart.MoviesChartService;
import com.rudanic.movieschart.R;
import com.rudanic.movieschart.core.ResponseListener;
import com.rudanic.movieschart.database.MoviesContract;
import com.rudanic.movieschart.database.MoviesOpenHelper;
import com.rudanic.movieschart.dto.MoviesResponse;
import com.rudanic.movieschart.event.FavoriteChangeEvent;
import com.rudanic.movieschart.event.MovieSelectedEvent;
import com.rudanic.movieschart.model.Movies;
import com.rudanic.movieschart.model.Sort;
import com.rudanic.movieschart.utils.LocalStoreUtil;
import com.rudanic.movieschart.utils.Utils;
import com.rudanic.movieschart.utils.ViewUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends BaseMovieFragment implements ResponseListener<MoviesResponse>, MoviesAdapter.Callbacks{

    private static final String TAG_SORT = "state_sort";

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private MoviesAdapter moviesAdapter;
    private List<Movies> mMovies = new ArrayList<>();
    private Sort mSort;
    private int currentPage, totalPages;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDetach() {

        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        super.onDetach();
    }

    public static MoviesFragment newInstance(@NonNull Sort sort) {
        Bundle args = new Bundle();
        args.putSerializable(TAG_SORT, sort);

        MoviesFragment fragment = new MoviesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSort = (Sort) getArguments().getSerializable(TAG_SORT);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        int columnCount = getResources().getInteger(R.integer.movies_columns);

        gridLayoutManager = new GridLayoutManager(getActivity(), columnCount);
        int spacing = Utils.dpToPx(5, getActivity()); // 50px
        boolean includeEdge = false;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(columnCount, spacing, includeEdge));
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addOnScrollListener(new EndlessRecyclerView(gridLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {

                /*Log.e("current_page","->"+current_page);
                Log.e("currentPage","->"+currentPage);
                Log.e("totalPages","->"+totalPages);*/

                if(currentPage<totalPages) {

                    getMoviesData(mSort, currentPage+1);

                }

            }
        });

        moviesAdapter = new MoviesAdapter(mMovies);
        moviesAdapter.setCallbacks(this);
        recyclerView.setAdapter(moviesAdapter);

        getMoviesData(mSort, 1);
    }

    /*private void initAdapter(List<Movies> movies) {

        moviesAdapter = new MoviesAdapter(movies);
        moviesAdapter.setCallbacks(this);
        recyclerView.setAdapter(moviesAdapter);
    }*/

    public void getMoviesData(final Sort sort, final int currentPage) {
        if(isInternetAvailable()) {

            if(sort == Sort.POPULAR) {
                new MoviesChartService().getMostPopularMovies(currentPage, this);
            } else {
                new MoviesChartService().getTopRatedMovies(currentPage, this);
            }

            showProgressDialog();
        } else {
            Snackbar snackbar = Snackbar
                    .make(getCoordinatorLayout(), R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getMoviesData(mSort, currentPage);
                        }
                    });
            snackbar.setActionTextColor(Color.RED);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("error", "->" + error);
        hideProgressDialog();
    }

    @Override
    public void onResponse(MoviesResponse response) {
        //Log.e("response", "->" + response.getPage());

        hideProgressDialog();
        if(response==null || response.getResults().isEmpty()) {
            return;
        }

        currentPage = response.getPage();
        totalPages = response.getTotalPages();

        List<Movies> movies = response.getResults();

        mMovies.addAll(movies);
        moviesAdapter.notifyDataSetChanged();

    }

    @Override
    public void onMovieClick(Movies movies) {
        EventBus.getDefault().post(new MovieSelectedEvent(movies));
    }

    @Override
    public void onFavoriteClick(Movies movies) {

        if(movies.isFavorite()) { // Already added is removed
            LocalStoreUtil.removeFromFavorites(getActivity(), movies.getId());
            ViewUtils.showToast(getResources().getString(R.string.removed_favorite),getActivity());

            getActivity().getContentResolver().delete(MoviesContract.MoviesEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(movies.getId())).build(), null, null);

        } else {
            LocalStoreUtil.addToFavorites(getActivity(), movies.getId());
            ViewUtils.showToast(getResources().getString(R.string.added_favorite),getActivity());

            ContentValues values = MoviesOpenHelper.getMovieContentValues(movies);
            getActivity().getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, values);
        }

        moviesAdapter.notifyDataSetChanged();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final FavoriteChangeEvent event){
        Log.e("onEvent","->"+event.isFavoriteChanged());

        moviesAdapter.notifyDataSetChanged();

    }
}
