package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigationrail.NavigationRailView;

public class MainActivity extends AppCompatActivity {
    private boolean isNavBarVisible = false;
    private int animationDuration = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button showNavRailButton = findViewById(R.id.button2);
        BottomNavigationView navRail = findViewById(R.id.navigation_rail);
        ScrollView scrollView = findViewById(R.id.scrollView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                private int previousScrollPosition = 0;

                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > previousScrollPosition) {
                        // Scrolling down
                        onScrollDown();
                    } else if (scrollY < previousScrollPosition) {
                        // Scrolling up
                        onScrollUp();
                    }
                    previousScrollPosition = scrollY;
                }

                private void onScrollDown() {
                    toggleOutNavBar();
                }

                private void onScrollUp() {
                    toggleInNavBar();
                }
            });

            showNavRailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleNavBar();
                }
            });

            navRail.setOnItemSelectedListener(new NavigationRailView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    int id = menuItem.getItemId();

                    if (id == R.id.alarms) {
                        Toast.makeText(MainActivity.this, "alarms", Toast.LENGTH_SHORT).show();
                        return true;
                    } else if (id == R.id.schedule) {
                        Toast.makeText(MainActivity.this, "schedule", Toast.LENGTH_SHORT).show();
                        return true;
                    } else if (id == R.id.timer) {
                        Toast.makeText(MainActivity.this, "timer", Toast.LENGTH_SHORT).show();
                        return true;
                    } else if (id == R.id.stopwatch) {
                        Toast.makeText(MainActivity.this, "stopwatch", Toast.LENGTH_SHORT).show();
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            ;
        }
    }

    private void toggleNavBar() {
        BottomNavigationView navRail = findViewById(R.id.navigation_rail);

        if (isNavBarVisible) { // toggle out
            navRail.animate()
                    .translationY(navRail.getWidth())
                    .setDuration(animationDuration)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            navRail.setVisibility(View.GONE);
                        }
                    })
                    .start();
        } else { // toggle in
            navRail.setVisibility(View.VISIBLE);
            navRail.animate()
                    .translationY(0)
                    .setDuration(animationDuration)
                    .start();
        }
        isNavBarVisible = !isNavBarVisible;
    }
    private void toggleInNavBar() {

        BottomNavigationView navRail = findViewById(R.id.navigation_rail);
        if (!isNavBarVisible) { // toggle In

            navRail.setVisibility(View.VISIBLE);
            navRail.animate()
                    .translationY(0)
                    .setDuration(animationDuration)
                    .start();
        }
        isNavBarVisible = !isNavBarVisible;
    }
    private void toggleOutNavBar() {
        BottomNavigationView navRail = findViewById(R.id.navigation_rail);

        if (isNavBarVisible) { // toggle out
            navRail.animate()
                    .translationY(navRail.getWidth())
                    .setDuration(animationDuration)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            navRail.setVisibility(View.GONE);
                        }
                    })
                    .start();
        }
        isNavBarVisible = !isNavBarVisible;
    }
}