package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private final Random random; // Random number generator
    private final Map<String, String> folderCoverMap; // Map to store folder cover image URLs

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
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.baseline_category_24)
                    .error(R.drawable.seek_progress)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL); // Enable caching

            Glide.with(getContext())
                    .load(coverImageUrl)
                    .apply(requestOptions)
                    .into(holder.imageView);
        } else {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(folderName);
            storageRef.listAll().addOnSuccessListener(listResult -> {
                if (listResult.getItems().size() > 0) {
                    StorageReference randomImageRef = listResult.getItems().get(random.nextInt(listResult.getItems().size()));
                    randomImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String coverImageUrl = uri.toString();

                        RequestOptions requestOptions = new RequestOptions()
                                .placeholder(R.drawable.baseline_category_24)
                                .error(R.drawable.seek_progress)
                                .fitCenter()
                                .diskCacheStrategy(DiskCacheStrategy.ALL); // Enable caching

                        Glide.with(getContext())
                                .load(coverImageUrl)
                                .apply(requestOptions)
                                .into(holder.imageView);

                        folderCoverMap.put(folderName, coverImageUrl); // Store the cover image URL
                    });
                }
            }).addOnFailureListener(e -> {
                // Handle the error
                e.printStackTrace();
            });
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView folderNameTextView;
    }
}
