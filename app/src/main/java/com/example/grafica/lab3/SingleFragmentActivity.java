package com.example.grafica.lab3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Sex_predator on 29.02.2016.
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

    public abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single);

        FragmentManager fm = getSupportFragmentManager();
        Fragment container = fm.findFragmentById(R.id.fragment_container);

        if (container == null) {
            container = createFragment();
            fm.beginTransaction()
              .add(R.id.fragment_container, container)
              .commit();
        }
    }
}
