package com.example.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link categoryEachImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class categoryEachImageFragment extends Fragment {
    private final int animationDuration = 200;
    BottomNavigationView navRail;
    List<String> imageUrls = new ArrayList<>();
    ImageAdapter adapter;
    RecyclerView recyclerView;
    private boolean doubleBackToExitPressedOnce;
    private boolean isNavBarVisible = false;
    private String folderName;

    public categoryEachImageFragment() {
        // Required empty public constructor
    }

    public static categoryEachImageFragment newInstance(String folderName) {
        categoryEachImageFragment fragment = new categoryEachImageFragment();
        Bundle args = new Bundle();
        args.putString("folderName", folderName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            folderName = getArguments().getString("folderName");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_each_image, container, false); //pass the correct layout name for the fragment

        recyclerView = view.findViewById(R.id.imagesFOrEachFolderView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        List<String> imageUrls = new ArrayList<>();
        navRail = getActivity().findViewById(R.id.navigation_rail);
        adapter = new ImageAdapter(imageUrls);
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

        StorageReference imagesRef = storageRef.child(folderName);

        imagesRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        List<StorageReference> imageRefs = listResult.getItems();

                        // Initialize the RecyclerView adapter
                        ImageAdapter adapter = new ImageAdapter(imageUrls);
                        recyclerView.setAdapter(adapter);

                        // Load and display each image one by one
                        for (StorageReference imageRef : imageRefs) {
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    imageUrls.add(imageUrl);

                                    // Notify the adapter that a new image has been added
                                    adapter.notifyItemInserted(imageUrls.size() - 1);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle any errors that occurred during image URL retrieval
                                }
                            });
                        }
                        TextView heading = getActivity().findViewById(R.id.heading);
                        heading.setText("Categories > " + folderName + " (" + imageRefs.size() + ")");
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
