package com.example.myapplication;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    //    BottomNavigationView.OnNavigationItemReselectedListener navigationItemReSelectedListener =
//            new BottomNavigationView.OnNavigationItemReselectedListener() {
//        @Override
//        public void onNavigationItemReselected(@NonNull MenuItem item) {
//           return ;
//        }
//    };
    private final BroadcastReceiver shutdownReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SHUTDOWN.equals(intent.getAction())) {
                // Schedule cache clearing when the application is closed
                scheduleCacheClear(context);
            }
        }
    };
    private final boolean isNavBarVisible = false;
    private final int animationDuration = 200;
    BottomNavigationView navRail;
    View borderLine;
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
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
            }else if (item.getItemId() == R.id.settings) {
                openFragment(SettingsFragment.newInstance("", ""));
                return true;
            }
            return false;
        }
    };
    private long backPressedTime;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        hideStatusBar();
        // Register the shutdown receiver
        IntentFilter filter = new IntentFilter(Intent.ACTION_SHUTDOWN);
        registerReceiver(shutdownReceiver, filter);
        if (isOnline()) {
//        shimmerFrameLayout = findViewById(R.id.shimmer);
//        shimmerFrameLayout.startShimmer();
            borderLine = findViewById(R.id.viewLine);
            navRail = findViewById(R.id.navigation_rail);
            navRail.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
//            navRail.setOnNavigationItemReselectedListener(navigationItemReSelectedListener);
            openFragment(HomeFragment.newInstance("", ""));
        } else {
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.CustomAlertDialog).create();
                alertDialog.setTitle("No Internet !");
                alertDialog.setMessage("Internet not available, Cross check your internet connectivity and try again");
                alertDialog.setIcon(R.drawable.ic_app_logo);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setCancelable(false);

                alertDialog.setButton2("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        recreate();
                    }
                });
                alertDialog.setButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alertDialog.show();
            } catch (Exception e) {
                Log.d("Tag", "Show Dialog: " + e.getMessage());
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        //            Toast.makeText(getApplicationContext(), "No Internet connection!", Toast.LENGTH_LONG).show();
        return netInfo != null && netInfo.isConnected() && netInfo.isAvailable();
    }

    //    private void hideStatusBar() {
//        // Hide the status bar for devices running on Android 11 (API level 30) or above
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            getWindow().getDecorView().getWindowInsetsController().hide(
//                    WindowInsets.Type.statusBars()
//            );
//        } else {
//            // Hide the status bar for devices running below Android 11
//            getWindow().setFlags(
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN
//            );
//        }
//    }
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister the shutdown receiver
        unregisterReceiver(shutdownReceiver);
    }

    private void scheduleCacheClear(Context context) {
        Intent intent = new Intent(context, CacheClearReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Schedule the cache clearing alarm
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long oneWeekInMillis = 7 * 24 * 60 * 60 * 1000; // 1 week in milliseconds
        long triggerTime = System.currentTimeMillis() + oneWeekInMillis;
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment instanceof categoryEachImageFragment) {
            super.onBackPressed(); // Allow normal back button behavior for categoryEachImageFragment
        } else {
            // Get the current time
            long currentTime = System.currentTimeMillis();

            if (backPressedTime == 0 || currentTime - backPressedTime > 2000) {
                // First back press or time difference greater than 2 seconds
                backPressedTime = currentTime;
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            } else {
                // Second back press within 2 seconds, exit the activity
                finish();
            }
        }
    }

}