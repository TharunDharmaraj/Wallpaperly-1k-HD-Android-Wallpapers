package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<String> imageUrls;

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

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            CardView cardView = itemView.findViewById(R.id.cardView);
            imageView = itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
            cardView.setRadius(itemView.getContext().getResources().getDimensionPixelSize(R.dimen.card_corner_radius));
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
