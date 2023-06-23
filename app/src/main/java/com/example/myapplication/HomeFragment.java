package com.example.myapplication;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final int animationDuration = 200;
    BottomNavigationView navRail;
    RecyclerView recyclerView;
    View borderLine;
    private boolean isNavBarVisible = false;

    TextView heading;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ImageAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, requireActivity().findViewById(R.id.container), false); //pass the correct layout name for the fragment
        heading = getActivity().findViewById(R.id.heading);
        heading.setText("Trending");
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        List<String> imageUrls = new ArrayList<>();
        navRail = getActivity().findViewById(R.id.navigation_rail);
        borderLine = getActivity().findViewById(R.id.viewLine);

        adapter = new ImageAdapter(imageUrls);
        recyclerView.setAdapter(adapter);

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

                // Get the last visible item position
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                // Update the last visible item position in the adapter
                adapter.setLastVisibleItemPosition(lastVisibleItem);
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
        });
        getData();

        // Inflate the layout for this fragment
        return view;
    }

    private void getData() {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        ArrayList<String> al = new ArrayList<>();
        al.add("Trending");
        al.add("Trending1");
        al.add("Trending2");
        al.add("Trending3");
        al.add("Trending4");
        al.add("Trending5");
        int randomNum = ThreadLocalRandom.current().nextInt(0, 5 + 1);
        // Assuming you have a "images" folder in your Firebase Storage
        StorageReference imagesRef = storageRef.child(al.get(randomNum));

        imagesRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        List<StorageReference> imageRefs = listResult.getItems();
                        List<String> imageUrls = new ArrayList<>();
                        ImageAdapter adapter = new ImageAdapter(imageUrls);
                        recyclerView.setAdapter(adapter);

                        for (StorageReference imageRef : imageRefs) {
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    String imageName = imageRef.getName(); // Retrieve the image name
                                    imageUrls.add(imageUrl);
                                    adapter.notifyItemInserted(imageUrls.size() - 1);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle any errors that occurred during image URL retrieval
                                }
                            });
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors that occurred during listing images
                    }
                });

//        new Timer().scheduleAtFixedRate(new TimerTask(){
//            @Override
//            public void run(){
//                Log.i("tag", "A Kiss every 5 seconds");
//            }
//        },0,5000);
//        shimmerFrameLayout.stopShimmer();
//        shimmerFrameLayout.setVisibility(View.GONE);
//        recyclerView.setVisibility(View.VISIBLE);
    }
}