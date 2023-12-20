package com.st4rry.myapp.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.st4rry.myapp.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Target;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AbsorbedFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView wordText;
    private ImageView imageView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.absorbed, container, false);
        imageView = view.findViewById(R.id.imageView);
        wordText = view.findViewById(R.id.myTextView);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
//        Button refreshButton = view.findViewById(R.id.refreshButton);
        wordText.animate()
                .alpha(1.0f)
                .translationY(0)
                .setDuration(1000)
                .setStartDelay(500)
                .start();
        sendRequestWithHttpClient(); // Initial request
        loadImageFromApi();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Call your methods to refresh data
                sendRequestWithHttpClient();
                loadImageFromApi();
                swipeRefreshLayout.setRefreshing(false); // Turn off the refresh icon
            }
        });

        return view;
    }

    private void sendRequestWithHttpClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url1 = "https://v1.hitokoto.cn";
                    URL url = new URL(url1);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        String result = is2String(inputStream);
                        JSONObject jsonObject = new JSONObject(result);
                        final String value = jsonObject.optString("hitokoto");

                        if (isAdded()) { // Check if Fragment is currently added to its activity
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    wordText.setText(value);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void loadImageFromApi() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String imageUrlApi = "https://random.imagecdn.app/v1/image?width=300&height=400&category=buildings&format=json"; // Replace with your image API URL
                    URL url = new URL(imageUrlApi);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        String result = is2String(inputStream);
                        JSONObject jsonObject = new JSONObject(result);
                        final String imageUrl = jsonObject.getString("url"); // Adjust key as per your JSON response
//                        final String imageUrl = "https://www.dmoe.cc/random.php"; // Adjust key as per your JSON response
                        if (isAdded()) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.with(AbsorbedFragment.this)
                                            .load(imageUrl)
                                            .listener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException e, Object model, com.bumptech.glide.request.target.Target<Drawable> target, boolean isFirstResource) {
                                                    Log.e("GlideError", "Load failed", e);
                                                    return false; // return false so the error placeholder can be placed
                                                }

                                                @Override
                                                public boolean onResourceReady(Drawable resource, Object model, com.bumptech.glide.request.target.Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                    Log.d("GlideSuccess", "Load success");
                                                    return false;
                                                }
                                            })
                                            .into(imageView);
                                    Log.d("Image", "是否加载图片");
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String is2String(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}