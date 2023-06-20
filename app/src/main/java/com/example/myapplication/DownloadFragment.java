package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DownloadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DownloadFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final int animationDuration = 200;
    BottomNavigationView navRail;
    RecyclerView recyclerView;
    View borderLine;
    private boolean isBorderLineVisible = false;
    private boolean isNavBarVisible = false;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView heading;
    private SharedPreferences sharedPreferences;

    public DownloadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DownloadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DownloadFragment newInstance(String param1, String param2) {
        DownloadFragment fragment = new DownloadFragment();
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
        View view = inflater.inflate(R.layout.fragment_download, requireActivity().findViewById(R.id.container), false); //pass the correct layout name for the fragment
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        List<String> imageUrls = new ArrayList<>();
        navRail = getActivity().findViewById(R.id.navigation_rail);
        borderLine = getActivity().findViewById(R.id.viewLine);

        ImageAdapter adapter = new ImageAdapter(imageUrls);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private final int previousScrollPosition = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // Handle scroll state changes
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Scrolled down
                    toggleOutNavBar();
                } else if (dy < 0) {
                    // Scrolled up
                    toggleInNavBar();
                }
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
                    isBorderLineVisible = true;
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
                    isBorderLineVisible = false;
                }
            }
        });
        // Inflate the layout for this fragment
        sharedPreferences = getActivity().getSharedPreferences("image_urls", Context.MODE_PRIVATE);


        displayStoredUrls(view);

        // Set the URLs as text in the TextView
//        storedUrlsTextView.setText(urlsBuilder.toString());
        return view;
    }

    private void displayStoredUrls(View view) {
        StringBuilder urlsBuilder = new StringBuilder();
        List<String> imageUrls = new ArrayList<>();

        // Loop through the stored URLs and append them to the StringBuilder
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            heading = view.findViewById(R.id.heading);
            heading.setVisibility(View.GONE);
            String imageUrl = entry.getValue().toString();
            urlsBuilder.append(imageUrl).append("\n");
            imageUrls.add(imageUrl);
            ImageAdapter adapter = new ImageAdapter(imageUrls);
            Log.d("IMAGEURL", imageUrl);
            recyclerView.setAdapter(adapter);
        }
        heading = getActivity().findViewById(R.id.heading);
        heading.setText("Downloads (" + imageUrls.size() + ")");

//        Log.d("storedUrlsTextView", "inside Download");

//        storedUrlsTextView.setText(urlsBuilder.toString());
    }
}