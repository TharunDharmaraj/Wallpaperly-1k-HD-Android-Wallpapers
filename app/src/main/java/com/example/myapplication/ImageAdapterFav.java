package com.example.myapplication;


import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageAdapterFav extends RecyclerView.Adapter<ImageAdapterFav.ImageViewHolder> {
    private final List<String> imageUrls;
    private SharedPreferences sharedPreferences;

    public ImageAdapterFav(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

//    private FragmentManager fragmentManager;
//    private Fragment favFragment;
//
//    public ImageAdapter(List<String> imageUrls, FragmentManager fragmentManager, Fragment favFragment) {
//        this.imageUrls = imageUrls;
//        this.fragmentManager = fragmentManager;
//        this.favFragment = favFragment;
//    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_grid_item_fav, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        holder.bindData(imageUrl);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageView;
        public ImageButton shareBtn, favBtn;
        public SharedPreferences sharedPreferences;
        CardView cardView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            imageView = itemView.findViewById(R.id.imageView);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            favBtn = itemView.findViewById(R.id.favBtn);

//            boolean isInFavFragment = isFavFragmentVisible();
//            if (isInFavFragment) {
//                // favFragment is visible, change the color of favBtn
//                favBtn.setImageResource(R.drawable.baseline_favorite_24);
//            } else {
//                // favFragment is not visible, change the color back to default
//                favBtn.setImageResource(R.drawable.unfavourite);
//            }

            itemView.setOnClickListener(this);
            sharedPreferences = itemView.getContext().getSharedPreferences("favorites", Context.MODE_PRIVATE);

            cardView.setRadius(itemView.getContext().getResources().getDimensionPixelSize(R.dimen.card_corner_radius));
            Animation waterdropAnimation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.waterdrop_anim);

// Apply the animation to the desired view
            itemView.startAnimation(waterdropAnimation);
            favBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String imageUrl = imageUrls.get(getAdapterPosition());
                    storeImageUrlFav(imageUrl, v);
                }
            });
            shareBtn.setOnClickListener(new View.OnClickListener() {
                 public void onClick(View v) {
                    String imageUrl = imageUrls.get(getAdapterPosition());
                    String imageName = getImageNameFromUrl(imageUrl);
                    String imageExt = getImageExtensions(imageUrl);
                    String directoryName = "wallpaperly";
                    String fileName = imageName + "." + imageExt;
                    File directory = new File(itemView.getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), directoryName);
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }

                    File file = new File(directory, fileName);

                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imageUrl));
                    request.setTitle(imageName);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationUri(Uri.fromFile(file));

                    DownloadManager downloadManager = (DownloadManager) v.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                    if (downloadManager != null) {
                        downloadManager.enqueue(request);
//                        Toast.makeText(v.getContext(), "Image Downloaded", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(v.getContext(), "Failed to Share image", Toast.LENGTH_SHORT).show();
                    }
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                        }
                    }, 5000);
                    shareImage(file, v);
                }
            });

            // Get the device screen width
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) itemView.getContext().getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;

            // Calculate the desired width and height for the CardView
            int cardWidth = (screenWidth / 2) - 60; // Set the width to half of the screen width
            int cardHeight = (int) (cardWidth * 1.6); // Adjust the height as per your requirement

            // Set the calculated width and height for the CardView
            ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
            layoutParams.width = cardWidth;
            layoutParams.height = cardHeight;
            cardView.setLayoutParams(layoutParams);
        }

        public void bindData(String imageUrl) {
            imageView.setTag(imageUrl);

            // Load the image into the ImageView using a library like Glide or Picasso
            Glide.with(itemView.getContext()).load(imageUrl).into(imageView);
        }

        @Override
        public void onClick(View v) {
            String imageUrl = imageUrls.get(getAdapterPosition());
            String imageName = getImageNameFromUrl(imageUrl);
            String imageExt = getImageExtensions(imageUrl);
            Intent intent = new Intent(v.getContext(), ImageViewActivityFav.class);
            intent.putStringArrayListExtra("image_url_list", (ArrayList<String>) imageUrls);
            intent.putExtra("image_url", imageUrl);
            intent.putExtra("image_name", imageName);
            v.getContext().startActivity(intent);
        }

//        private boolean isFavFragmentVisible() {
//            if (favFragment != null && favFragment.isVisible()) {
//                Fragment currentFragment = getCurrentFragment();
//                return currentFragment != null && currentFragment.equals(favFragment);
//            }
//            return false;
//        }
//
//        private Fragment getCurrentFragment() {
//            if (fragmentManager != null && fragmentManager.getBackStackEntryCount() > 0) {
//                String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
//                return fragmentManager.findFragmentByTag(fragmentTag);
//            }
//            return null;
//        }

        private void shareImage(File imageFile, View v) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                }
            }, 5000);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "View The Source Code at https://github.com/TharunDharmaraj/Wallpaperly");

            Uri imageUri = FileProvider.getUriForFile(
                    v.getContext(),
                    "com.example.myapplication.fileprovider",
                    imageFile
            );
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            v.getContext().startActivity(Intent.createChooser(shareIntent, "Share Image"));
        }

        private String getImageNameFromUrl(String imageUrl) {
            Uri uri = Uri.parse(imageUrl);
            String fileName = uri.getLastPathSegment();
            int folder = fileName.indexOf("/") + 1;
            fileName = fileName.substring(folder);
            int dotIndex = fileName.indexOf(".");
            if (dotIndex != -1) {
                return toTitleCase(fileName.substring(0, dotIndex));
            } else {
                return toTitleCase(fileName);
            }
        }

        private String getImageExtensions(String imageUrl) {
            Uri uri = Uri.parse(imageUrl);
            String fileName = uri.getLastPathSegment();
//            System.out.println(uri);
            int folder = fileName.indexOf("/") + 1;
            fileName = fileName.substring(folder);
            System.out.println(fileName);
            int dotIndex = fileName.indexOf(".");
            int fileNameLen = fileName.length();
            if (dotIndex != -1) {
                return (fileName.substring(dotIndex + 1, fileNameLen));
            } else {
                return "png";
            }
        }
        private void storeImageUrlFav(String imageUrl, View v) {
            String key = "image_" + imageUrl;
            String storedUrl = sharedPreferences.getString(key, null);

            if (storedUrl == null || !storedUrl.equals(imageUrl)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(key, imageUrl);
                editor.apply();
                Toast.makeText(v.getContext(), "Added as Favorites", Toast.LENGTH_SHORT).show();
                favBtn.setImageResource(R.drawable.unfavourite);
            } else {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(key);
                editor.apply();
                showSnackbarWithUndo(imageUrl, v);
            }
        }

        private void showSnackbarWithUndo(final String imageUrl, View v) {
            Snackbar snackbar = Snackbar.make(v, "Removed from Favorites", Snackbar.LENGTH_SHORT);
            favBtn.setImageResource(R.drawable.baseline_favorite_24);

            snackbar.setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("image_" + imageUrl, imageUrl);
                    favBtn.setImageResource(R.drawable.unfavourite);
                    editor.apply();
                    Toast.makeText(v.getContext(), "Added again to Favorites", Toast.LENGTH_SHORT).show();
                }
            });
            snackbar.show();
        }

        public String toTitleCase(String input) {
            StringBuilder titleCase = new StringBuilder(input.length());
            boolean nextTitleCase = true;

            for (char c : input.toCharArray()) {
                if (Character.isSpaceChar(c)) {
                    nextTitleCase = true;
                } else if (nextTitleCase) {
                    c = Character.toTitleCase(c);
                    nextTitleCase = false;
                }

                titleCase.append(c);
            }
            return titleCase.toString();
        }
    }
}
