package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class coverImageAdapter extends ArrayAdapter<String> {
    private List<String> folderList;
    private LayoutInflater inflater;
    private Map<String, String> folderCoverMap; // Map to store folder cover image URLs

    public coverImageAdapter(Context context, List<String> folderList) {
        super(context, 0, folderList);
        this.folderList = folderList;
        inflater = LayoutInflater.from(context);
        folderCoverMap = new HashMap<>();
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
            Picasso.get()
                    .load(coverImageUrl)
                    .placeholder(R.drawable.baseline_category_24) // optional placeholder image while loading
                    .error(R.drawable.seek_progress) // optional error image if loading fails
                    .fit()
                    .centerCrop()
                    .into(holder.imageView);
//            Glide.with(getContext())
//                    .load(coverImageUrl)
//                    .apply(new RequestOptions()
//                            .placeholder(R.drawable.baseline_category_24) // optional placeholder image
//                            .error(R.drawable.seek_progress) // optional error image
//                            .diskCacheStrategy(DiskCacheStrategy.ALL)) // caching strategy
//                    .into(holder.imageView);
        } else {
            // If the URL is not available, fetch the first image URL for the folder
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(folderName);
            storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    if (listResult.getItems().size() > 0) {
                        StorageReference firstImageRef = listResult.getItems().get(0);
                        firstImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String coverImageUrl = uri.toString();
                                folderCoverMap.put(folderName, coverImageUrl); // Store the cover image URL
                                Picasso.get()
                                        .load(coverImageUrl)
                                        .placeholder(R.drawable.baseline_category_24) // optional placeholder image while loading
                                        .error(R.drawable.seek_progress) // optional error image if loading fails
                                        .fit()
                                        .centerCrop()
                                        .into(holder.imageView);
//                                Glide.with(getContext())
//                                        .load(coverImageUrl)
//                                        .apply(new RequestOptions()
//                                                .placeholder(R.drawable.baseline_category_24) // optional placeholder image
//                                                .error(R.drawable.seek_progress) // optional error image
//                                                .diskCacheStrategy(DiskCacheStrategy.ALL)) // caching strategy
//                                        .into(holder.imageView);
                            }
                        });
                    }
                }
            }).addOnFailureListener(e -> {
                // Handle the error
                // TODO: Add error handling
            });
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView folderNameTextView;
    }
}
