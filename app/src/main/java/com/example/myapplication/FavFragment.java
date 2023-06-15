package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    BottomNavigationView navRail;
    TextView heading;
    View borderLine;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    TextView favText;
    private SharedPreferences sharedPreferences;
    private boolean isBorderLineVisible = false;

    private boolean isNavBarVisible = false;
    private int animationDuration = 200;

    // TODO: Rename and change types of parameters
    public FavFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavFragment newInstance(String param1, String param2) {
        FavFragment fragment = new FavFragment();
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
        View view = inflater.inflate(R.layout.fragment_fav, requireActivity().findViewById(R.id.container), false);
        navRail = getActivity().findViewById(R.id.navigation_rail);
        borderLine = getActivity().findViewById(R.id.viewLine);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        heading = getActivity().findViewById(R.id.heading);
        heading.setText("Favourites");
        List<String> imageUrls = new ArrayList<>();
        ImageAdapter adapter = new ImageAdapter(imageUrls);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int previousScrollPosition = 0;

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
        sharedPreferences = getActivity().getSharedPreferences("favorites", Context.MODE_PRIVATE);
        displayStoredUrls(view);

        return view;
    }

    private void displayStoredUrls(View view) {
        StringBuilder urlsBuilder = new StringBuilder();
        List<String> imageUrls = new ArrayList<>();
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            favText = view.findViewById(R.id.textview);
            favText.setVisibility(View.GONE);
            String imageUrl = entry.getValue().toString();
            urlsBuilder.append(imageUrl).append("\n");
            imageUrls.add(imageUrl);
            ImageAdapter adapter = new ImageAdapter(imageUrls);
            recyclerView.setAdapter(adapter);
        }
    }
//        public void addUrlToFavorites(String url) {
//            Set<String> favoriteUrls = getFavoriteUrls();
//            favoriteUrls.add(url);
//            saveFavoriteUrls(favoriteUrls);
//
//            // Update the UI
//            adapter.addImageUrl(url);
//        }
//
//        public void removeUrlFromFavorites(String url) {
//            Set<String> favoriteUrls = getFavoriteUrls();
//            favoriteUrls.remove(url);
//            saveFavoriteUrls(favoriteUrls);
//
//            // Update the UI
//            adapter.removeImageUrl(url);
//        }
}