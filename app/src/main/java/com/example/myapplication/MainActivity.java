package com.example.myapplication;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView navRail;
    FrameLayout recyclerView;
    private boolean isNavBarVisible = false;
    private int animationDuration = 200;
    ShimmerFrameLayout shimmerFrameLayout;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        shimmerFrameLayout = findViewById(R.id.shimmer);
//        shimmerFrameLayout.startShimmer();
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        navRail = findViewById(R.id.navigation_rail);
        navRail.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(HomeFragment.newInstance("", ""));
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.id.home) {
                        openFragment(HomeFragment.newInstance("", ""));
                        return true;
                    } else if (item.getItemId() == R.id.category) {
                        openFragment(CategoriesFragment.newInstance("", ""));
                        return true;
                    } else if (item.getItemId() == R.id.fav) {
                        openFragment(FavFragment.newInstance("", ""));
                        return true;
                    } else if (item.getItemId() == R.id.downloads) {
                        openFragment(DownloadFragment.newInstance("", ""));
                        return true;
                    }
                    return false;
                }
            };

}
