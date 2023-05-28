package com.example.myapplication;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.BuildConfig;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class ImageViewActivity extends AppCompatActivity {
    private ImageView imageView;
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;
    private GestureDetector gestureDetector;

    ImageButton shareBtn;
    Button downloadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        String imageUrl = getIntent().getStringExtra("image_url");
        String imageName = getIntent().getStringExtra("image_name");
        gestureDetector = new GestureDetector(this, new SwipeGestureListener());

        imageView = findViewById(R.id.imageView);
        shareBtn = findViewById(R.id.shareBtn);
        downloadBtn = findViewById(R.id.download);
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImage();
            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageUrl = getIntent().getStringExtra("image_url");
                String imageName = getIntent().getStringExtra("image_name");

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

                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                if (downloadManager != null) {
                    downloadManager.enqueue(request);
                    Toast.makeText(getApplicationContext(), "Image Downloaded", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to download image", Toast.LENGTH_SHORT).show();
                }
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                    }
                }, 5000);
                shareImage(file);
            }
        });
        TextView nameTextView = findViewById(R.id.imageNameTextView);

        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(this)
                .load(imageUrl)
                .into(imageView);

        nameTextView.setText(imageName);
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
    }

    public void downloadImage() {
        String imageUrl = getIntent().getStringExtra("image_url");
        String imageName = getIntent().getStringExtra("image_name");

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

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
            Toast.makeText(getApplicationContext(), "Image Downloaded", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Failed to download image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Pass the touch event to ScaleGestureDetector
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return true;
    }

    private void shareImage(File imageFile) {
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
                this,
                "com.example.myapplication.fileprovider",
                imageFile
        );
        Log.d("tharun", String.valueOf(imageUri));
        // Add the image URI to the intent as an extra
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

        // Grant temporary permissions to the content URI
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Start the share activity
        startActivity(Intent.createChooser(shareIntent, "Share Image"));
    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // Calculate the scale factor
            scaleFactor *= detector.getScaleFactor();

            // Limit the scale factor within a certain range (e.g., 0.5 to 2.0)
            scaleFactor = Math.max(0.5f, Math.min(scaleFactor, 2.0f));

            // Apply the scale factor to the ImageView
            imageView.setScaleX(scaleFactor);
            imageView.setScaleY(scaleFactor);

            return true;
        }
    }
    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_DISTANCE_THRESHOLD = 500;
        private static final int SWIPE_VELOCITY_THRESHOLD = 500;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();

            if (Math.abs(distanceX) > Math.abs(distanceY)
                    && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD
                    && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0) {
                    // Right swipe (optional)
                } else {
                    // Left swipe (optional)
                }
            } else if (Math.abs(distanceY) > Math.abs(distanceX)
                    && Math.abs(distanceY) > SWIPE_DISTANCE_THRESHOLD
                    && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceY > 0) {
                    // Down swipe
                    openMainActivity();
                } else {
                    // Up swipe (optional)
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    private void openMainActivity() {
        Intent intent = new Intent(ImageViewActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Optional: finish the current activity
    }
}