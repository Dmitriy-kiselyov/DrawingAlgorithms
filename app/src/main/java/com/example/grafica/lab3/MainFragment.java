package com.example.grafica.lab3;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Sex_predator on 01.03.2016.
 */
public class MainFragment extends Fragment {

    private static long ANIMATION_INTERVAL = 1_000; //1s

    private SeaView mSeaView;

    private Handler  mHandler;
    private Runnable mRunnable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        mSeaView = (SeaView) v.findViewById(R.id.sea_view);

        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mSeaView.invalidate();
                mHandler.postDelayed(mRunnable, ANIMATION_INTERVAL);
            }
        };

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.postDelayed(mRunnable, ANIMATION_INTERVAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
    }
}
