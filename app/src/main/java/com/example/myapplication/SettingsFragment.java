package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextView textViewClearCacheSize;
    NestedScrollView recyclerView;
    View borderLine;
    private final int animationDuration = 200;

    private boolean isNavBarVisible = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    BottomNavigationView navRail;
    TextView heading;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.textViewClearFavorites) {
            clearFavorites();
        } else if (v.getId() == R.id.textViewClearDownloads) {
            // TODO: Implement clear downloads logic
            clearDownloads();
        } else if (v.getId() == R.id.textViewClearCache) {
            clearCache();
        } else if (v.getId() == R.id.textViewClearAppData) {
            clearAppData();
        } else if (v.getId() == R.id.sourceCode) {
            toSourceCode();
        }
    }

    private void toSourceCode() {
        String url = "https://github.com/TharunDharmaraj/Wallpaperly-1k-HD-Android-Wallpapers";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void clearAppData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog);
        builder.setTitle("Clear App Data");
        builder.setMessage("Are you sure you want to clear app data?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Clear the app data
                try {


                    // Get the app's data directory
                    File dataDir = new File(requireActivity().getApplicationInfo().dataDir);

                    // Delete the app's data directory
                    deleteDirectory(dataDir);

                    // Display a success message
                    Toast.makeText(requireContext(), "App data cleared", Toast.LENGTH_SHORT).show();

                    // Update the cache size TextView
                    showCacheSize();
                } catch (Exception e) {
                    // Display an error message
                    Toast.makeText(requireContext(), "Failed to clear app data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        dialog.show();
    }

    private void clearCache() {
        // Get the app's cache directory
        File cacheDir = requireContext().getCacheDir();

        // Delete all files within the cache directory
        if (cacheDir != null && cacheDir.isDirectory()) {
            File[] cacheFiles = cacheDir.listFiles();
            if (cacheFiles != null) {
                for (File file : cacheFiles) {
                    file.delete();
                }
            }
        }
    }

    private void clearDownloads() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog);
        builder.setTitle("Clear Downloads");
        builder.setMessage("Clear Downloads you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Clear the app data
                try {
                    // Get the SharedPreferences instance
                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("image_urls", Context.MODE_PRIVATE);

                    // Clear the SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    // Display a success message
                    Toast.makeText(requireContext(), "All Downloads cleared", Toast.LENGTH_SHORT).show();

                    // Update the cache size TextView
                    showCacheSize();
                } catch (Exception e) {
                    // Display an error message
                    Toast.makeText(requireContext(), "Failed to Downloads", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        dialog.show();

    }

    private void clearFavorites() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog);
        builder.setTitle("Clear Favourites");
        builder.setMessage("Clear Favourites you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Clear the app data
                try {
                    // Get the SharedPreferences instance
                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("favorites", Context.MODE_PRIVATE);

                    // Clear the SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    // Display a success message
                    Toast.makeText(requireContext(), "All Favourites cleared", Toast.LENGTH_SHORT).show();

                    // Update the cache size TextView
                    showCacheSize();
                } catch (Exception e) {
                    // Display an error message
                    Toast.makeText(requireContext(), "Failed to clear Favourites", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        dialog.show();
    }

    //    https://github.com/TharunDharmaraj/Wallpaperly-1k-HD-Android-Wallpapers
    private void deleteDirectory(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            File[] files = fileOrDirectory.listFiles();
            if (files != null) {
                for (File child : files) {
                    deleteDirectory(child);
                }
            }
        }
        fileOrDirectory.delete();
    }

    private void showCacheSize() {
        // Get the app's cache directory
        File cacheDir = requireContext().getCacheDir();

        // Calculate the total cache size
        long cacheSize = getDirSize(cacheDir);

        // Format the cache size to a human-readable format
        String formattedCacheSize = formatSize(cacheSize);

        // Display the cache size in the TextView
        textViewClearCacheSize.setText(formattedCacheSize);
    }

    // Helper method to calculate the size of a directory
    private long getDirSize(File dir) {
        long size = 0;
        if (dir != null && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        size += file.length();
                    } else if (file.isDirectory()) {
                        size += getDirSize(file);
                    }
                }
            }
        }
        return size;
    }

    // Helper method to format the size in a human-readable format
    private String formatSize(long size) {
        final int kiloBytes = 1024;
        final int megaBytes = kiloBytes * kiloBytes;
        final int gigaBytes = megaBytes * kiloBytes;

        if (size < kiloBytes) {
            return size + " B";
        } else if (size < megaBytes) {
            return String.format(Locale.getDefault(), "%.2f KB", (float) size / kiloBytes);
        } else if (size < gigaBytes) {
            return String.format(Locale.getDefault(), "%.2f MB", (float) size / megaBytes);
        } else {
            return String.format(Locale.getDefault(), "%.2f GB", (float) size / gigaBytes);
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, requireActivity().findViewById(R.id.container), false);
        borderLine = getActivity().findViewById(R.id.viewLine);
        recyclerView = view.findViewById(R.id.recyclerView);
        navRail = getActivity().findViewById(R.id.navigation_rail);
        borderLine = getActivity().findViewById(R.id.viewLine);

        textViewClearCacheSize = view.findViewById(R.id.textViewClearCacheSize);
        heading = getActivity().findViewById(R.id.heading);
        heading.setText("Settings");
        LinearLayout sourceCodee = view.findViewById(R.id.sourceCode);
        sourceCodee.setOnClickListener(this);

        LinearLayout clearFavoritesTextView = view.findViewById(R.id.textViewClearFavorites);
        clearFavoritesTextView.setOnClickListener(this);

        LinearLayout clearDownloadsTextView = view.findViewById(R.id.textViewClearDownloads);
        clearDownloadsTextView.setOnClickListener(this);
        showCacheSize();

        TextView clearCacheTextView = view.findViewById(R.id.textViewClearCache);
        clearCacheTextView.setOnClickListener(this);

        LinearLayout clearAppDataTextView = view.findViewById(R.id.textViewClearAppData);
        clearAppDataTextView.setOnClickListener(this);


        recyclerView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                Log.d("ScrollView","scrollX_"+scrollX+"_scrollY_"+scrollY+"_oldScrollX_"+oldScrollX+"_oldScrollY_"+oldScrollY);
                if (scrollY > oldScrollY) {
                    // Scrolled down
                    toggleOutNavBar();
                } else if (scrollY < oldScrollY) {
                    // Scrolled up
                    toggleInNavBar();
                }
            }
        });

        return view;
    }

    private void toggleInNavBar() {
        if (!isNavBarVisible) {
            navRail.setVisibility(View.VISIBLE);
            navRail.animate()
                    .translationY(0)
                    .setDuration(animationDuration)
                    .start();
            isNavBarVisible = true;
            borderLine.setVisibility(View.VISIBLE);
            borderLine.animate()
                    .translationY(0)
                    .setDuration(animationDuration)
                    .start();
        }

    }

    private void toggleOutNavBar() {
        if (isNavBarVisible) {
            navRail.animate()
                    .translationY(navRail.getHeight())
                    .setDuration(animationDuration)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            navRail.setVisibility(View.GONE);
                        }
                    })
                    .start();
            isNavBarVisible = false;
            borderLine.animate()
                    .translationY(navRail.getHeight())
                    .setDuration(animationDuration)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            navRail.setVisibility(View.GONE);
                        }
                    })
                    .start();
        }
    }
}