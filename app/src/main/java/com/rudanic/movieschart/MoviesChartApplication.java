package com.rudanic.movieschart;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.rudanic.movieschart.core.OkHttpStack;

/**
 * Created by rudani_c on 27-03-2016.
 */
public class MoviesChartApplication extends Application {

    public static final String TAG = MoviesChartApplication.class.getSimpleName();

    private static MoviesChartApplication _instance;
    private RequestQueue mRequestQueue;


    @Override
    public void onCreate() {
        super.onCreate();

        Fresco.initialize(this);

        _instance = this;
    }

    public static MoviesChartApplication getInstance() {
        return _instance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new OkHttpStack());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }
}
