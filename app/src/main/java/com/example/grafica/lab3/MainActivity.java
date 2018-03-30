package com.example.grafica.lab3;

import android.support.v4.app.Fragment;

public class MainActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return new MainFragment();
    }

}
