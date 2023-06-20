package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavFragment extends Fragment {

    private final int animationDuration = 200;
    BottomNavigationView navRail;
    TextView heading;
    View borderLine;
    TextView favText;
    // TODO: Rename and change types of parameters

    private RecyclerView recyclerView;

    private SharedPreferences sharedPreferences;
    private boolean isNavBarVisible = false;

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

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fav, requireActivity().findViewById(R.id.container), false);
        navRail = (getActivity()).findViewById(R.id.navigation_rail);
        borderLine = getActivity().findViewById(R.id.viewLine);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        heading = getActivity().findViewById(R.id.heading);
//        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//        Fragment favFragment = this;
        List<String> imageUrls = new ArrayList<>();
        ImageAdapter adapter = new ImageAdapter(imageUrls);
//        ImageAdapter adapter = new ImageAdapter(imageUrls, fragmentManager, favFragment);
        recyclerView.setAdapter(adapter);
        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefresh);
        int myColor = Color.parseColor("#00668B");
        pullToRefresh.setProgressBackgroundColorSchemeColor(myColor);
        pullToRefresh.setColorSchemeResources(R.color.white);

        pullToRefresh.setOnRefreshListener(() -> {
            displayStoredUrls(view); // your code
            pullToRefresh.setRefreshing(false);
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

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
                }

            }

            private void toggleOutNavBar() {
                if (isNavBarVisible) {
                    navRail.animate()
                            .translationY(navRail.getHeight())
                            .setDuration(animationDuration)
                            .withEndAction(() -> navRail.setVisibility(View.GONE))
                            .start();
                    isNavBarVisible = false;
                    borderLine.animate()
                            .translationY(navRail.getHeight())
                            .setDuration(animationDuration)
                            .withEndAction(() -> navRail.setVisibility(View.GONE))
                            .start();
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
            ImageAdapterFav adapter = new ImageAdapterFav(imageUrls);
            recyclerView.setAdapter(adapter);
        }
        heading.setText("Favourites (" + allEntries.size() + ")");

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