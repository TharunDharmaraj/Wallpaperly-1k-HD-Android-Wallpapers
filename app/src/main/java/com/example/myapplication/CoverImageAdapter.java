package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
public class CoverImageAdapter extends ArrayAdapter<String> {
    private final List<String> folderList;
    private final LayoutInflater inflater;
    private final Map<String, String> folderCoverMap; // Map to store folder cover image URLs
    private final Random random; // Random number generator

    public CoverImageAdapter(Context context, List<String> folderList) {
        super(context, 0, folderList);
        this.folderList = folderList;
        inflater = LayoutInflater.from(context);
        folderCoverMap = new HashMap<>();
        random = new Random();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.category_grid_item, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.coverImageView);
            holder.folderNameTextView = convertView.findViewById(R.id.folderNameTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String folderName = folderList.get(position);
        holder.folderNameTextView.setText(folderName);

        // Check if the cover image URL for this folder has already been loaded
        if (folderCoverMap.containsKey(folderName)) {
            // If the URL is available, load it into the ImageView using Glide
            String coverImageUrl = folderCoverMap.get(folderName);
            loadCoverImage(coverImageUrl, holder.imageView);
        } else {
            // Set a placeholder image while the actual image is being loaded
            holder.imageView.setImageResource(R.drawable.seek_progress);
            Animation waterdropAnimation = AnimationUtils.loadAnimation(holder.imageView.getContext(), R.anim.waterdrop_anim);
            Animation waterdropAnimationText = AnimationUtils.loadAnimation(holder.folderNameTextView.getContext(), R.anim.waterdrop_anim);

// Apply the animation to the desired view
            holder.imageView.startAnimation(waterdropAnimation);
            holder.folderNameTextView.startAnimation(waterdropAnimationText);
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(folderName);
            storageRef.listAll().addOnSuccessListener(listResult -> {
                if (listResult.getItems().size() > 0) {
                    StorageReference randomImageRef = listResult.getItems().get(random.nextInt(listResult.getItems().size()));
                    randomImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String coverImageUrl = uri.toString();
                        folderCoverMap.put(folderName, coverImageUrl); // Store the cover image URL
                        loadCoverImage(coverImageUrl, holder.imageView);
                    }).addOnFailureListener(e -> {
                        // Handle the error
                        e.printStackTrace();
                    });
                }
            }).addOnFailureListener(e -> {
                // Handle the error
                e.printStackTrace();
            });
        }

        return convertView;
    }

    private void loadCoverImage(String imageUrl, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.seek_progress)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL); // Enable caching

        Glide.with(getContext())
                .load(imageUrl)
                .apply(requestOptions)
                .into(imageView);
    }

    static class ViewHolder {
        ImageView imageView;
        TextView folderNameTextView;
    }
}