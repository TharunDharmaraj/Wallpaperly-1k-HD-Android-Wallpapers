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
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.common.internal.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView navRail;
    FrameLayout recyclerView;
    ShimmerFrameLayout shimmerFrameLayout;
    View borderLine;
    private static final long INTERVAL_ONE_WEEK = 7 * 24 * 60 * 60 * 1000; // One week in milliseconds
    private BroadcastReceiver shutdownReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SHUTDOWN.equals(intent.getAction())) {
                // Schedule cache clearing when the application is closed
                scheduleCacheClear(context);
            }
        }
    };
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
    private boolean isNavBarVisible = false;
    private int animationDuration = 200;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Register the shutdown receiver
        IntentFilter filter = new IntentFilter(Intent.ACTION_SHUTDOWN);
        registerReceiver(shutdownReceiver, filter);
        if (isOnline()) {
//        shimmerFrameLayout = findViewById(R.id.shimmer);
//        shimmerFrameLayout.startShimmer();
            borderLine = findViewById(R.id.viewLine);
            navRail = findViewById(R.id.navigation_rail);
            navRail.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
            openFragment(HomeFragment.newInstance("", ""));
        }else{
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.CustomAlertDialog).create();
                alertDialog.setTitle("No Internet !");
                alertDialog.setMessage("Internet not available, Cross check your internet connectivity and try again");
                alertDialog.setIcon(R.drawable.ic_app_logo);
                alertDialog.setButton("Try Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        recreate();
                    }
                });

                alertDialog.show();
            }
            catch(Exception e)
            {
                Log.d("Tag", "Show Dialog: "+e.getMessage());
            }
        }
    }
    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
//            Toast.makeText(getApplicationContext(), "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
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
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // Schedule the cache clearing alarm
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long oneWeekInMillis = 7 * 24 * 60 * 60 * 1000; // 1 week in milliseconds
        long triggerTime = System.currentTimeMillis() + oneWeekInMillis;
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }
}
