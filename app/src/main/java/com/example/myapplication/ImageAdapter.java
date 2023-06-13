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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<String> imageUrls;
    private SharedPreferences sharedPreferences;

    public ImageAdapter(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_grid_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        holder.imageView.setTag(imageUrl);

        // Load the image into the ImageView using a library like Glide or Picasso
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageView;
        public ImageButton shareBtn, favBtn;
        public SharedPreferences sharedPreferences;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            CardView cardView = itemView.findViewById(R.id.cardView);
            imageView = itemView.findViewById(R.id.imageView);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            favBtn = itemView.findViewById(R.id.favBtn);
            itemView.setOnClickListener(this);
            sharedPreferences = itemView.getContext().getSharedPreferences("favorites", Context.MODE_PRIVATE);

            cardView.setRadius(itemView.getContext().getResources().getDimensionPixelSize(R.dimen.card_corner_radius));

            favBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String imageUrl = imageUrls.get(getAdapterPosition());
                    storeImageUrlFav(imageUrl);
                    Toast.makeText(v.getContext(),"Added as Favourites",Toast.LENGTH_SHORT).show();
                }
            });
            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String imageUrl = imageUrls.get(getAdapterPosition());
                    String imageName = getImageNameFromUrl(imageUrl);
                    String directoryName = "wallpaperly";
                    String fileName = imageName + ".png";
                    File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), directoryName);
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
                        Toast.makeText(v.getContext(), "Image Downloaded", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(v.getContext(), "Failed to download image", Toast.LENGTH_SHORT).show();
                    }
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                        }
                    }, 5000);
                    shareImage(file,v);
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

        @Override
        public void onClick(View v) {
//            // Retrieve the clicked image URL
            String imageUrl = imageUrls.get(getAdapterPosition());
            String imageName = getImageNameFromUrl(imageUrl);
            // Start the new activity and pass the image URL
            Intent intent = new Intent(v.getContext(), ImageViewActivity.class);
            intent.putExtra("image_url", imageUrl);
            intent.putExtra("image_name", imageName);
            v.getContext().startActivity(intent);
        }
        private void shareImage(File imageFile,View v) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                }
            }, 5000);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);

            // Set the type of the content to "image/png"
            shareIntent.setType("image/png");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Made in India");

            // Get the URI of the image file using FileProvider
            Uri imageUri = FileProvider.getUriForFile(
                    v.getContext(),
                    "com.example.myapplication.fileprovider",
                    imageFile
            );
            Log.d("tharun", String.valueOf(imageUri));
            // Add the image URI to the intent as an extra
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

            // Grant temporary permissions to the content URI
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Start the share activity
            v.getContext().startActivity(Intent.createChooser(shareIntent, "Share Image"));
        }
        private String getImageNameFromUrl(String imageUrl) {
            // Extract the image name from the URL
            Uri uri = Uri.parse(imageUrl);
            String fileName = uri.getLastPathSegment();
            int folder = fileName.indexOf("/") + 1;
            fileName = fileName.substring(folder);
            // Remove any query parameters or extensions
            int dotIndex = fileName.indexOf(".");
            if (dotIndex != -1) {
                return toTitleCase(fileName.substring(0, dotIndex));
            } else {
                return toTitleCase(fileName);
            }
        }
        private void storeImageUrlFav(String imageUrl) {
            String key = "image_" + imageUrl;
            String storedUrl = sharedPreferences.getString(key, null);

            if (storedUrl == null || !storedUrl.equals(imageUrl)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(key, imageUrl);
                editor.apply();
            }
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
