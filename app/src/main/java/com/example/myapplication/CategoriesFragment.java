package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoriesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final int animationDuration = 200;
    private final int previousVisibleItem = 0;
    private final boolean isScrollingUp = false;
    BottomNavigationView navRail;
    View borderLine;
    TextView heading;
    private ListView folderListView;
    private boolean isNavBarVisible = false;
    private ArrayAdapter<String> adapter;
    private List<String> folderList;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GridView folderGridView;
    private boolean isBorderLineVisible = false;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoriesFragment newInstance(String param1, String param2) {
        CategoriesFragment fragment = new CategoriesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, requireActivity().findViewById(R.id.container), false); //pass the correct layout name for the fragment
        folderGridView = view.findViewById(R.id.folderGridView);
        folderList = new ArrayList<>();
        navRail = getActivity().findViewById(R.id.navigation_rail);
        borderLine = getActivity().findViewById(R.id.viewLine);
        heading = getActivity().findViewById(R.id.heading);

        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefresh);
        int myColor = Color.parseColor("#00668B");
        pullToRefresh.setProgressBackgroundColorSchemeColor(myColor);
        pullToRefresh.setColorSchemeResources(R.color.white);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(); // your code
                pullToRefresh.setRefreshing(false);
            }
        });
        folderGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int previousVisibleItem = 0;
            private boolean isScrollingUp = false;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Called when the scroll state changes
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > previousVisibleItem) {
                    // Scrolled down
                    if (isScrollingUp) {
                        toggleOutNavBar();
                        isScrollingUp = false;
                    }
                } else if (firstVisibleItem < previousVisibleItem) {
                    // Scrolled up
                    if (!isScrollingUp) {
                        toggleInNavBar();
                        isScrollingUp = true;
                    }
                }
                previousVisibleItem = firstVisibleItem;
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

        folderGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String folderName = folderList.get(position);
                categoryEachImageFragment folderFragment = categoryEachImageFragment.newInstance(folderName);

                // Replace the current fragment with the FolderFragment
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, folderFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        getData();
        return view;

    }

    private void getData() {
        adapter = new CoverImageAdapter(getContext(), folderList);
        folderGridView.setAdapter(adapter);
        folderList.clear();
        // Retrieve the reference to the root folder in Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        // List all the items (folders and files) inside the root folder
        storageRef.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference prefix : listResult.getPrefixes()) {

                if (!prefix.getName().startsWith("Trending")) {
                    // Add the folder name to the list
                    folderList.add(prefix.getName());
                }
            }
            adapter.notifyDataSetChanged();
            heading.setText("Categories (" + folderList.size() + ")");

        }).addOnFailureListener(e -> {
            // Handle the error
            // TODO: Add error handling
        });

        int numColumns = 2; // Number of columns in the grid
        folderGridView.setNumColumns(numColumns);
    }

}