package com.example.gameinfoapp.utils;

import android.content.Context;

import com.bumptech.glide.Glide;

public class GlideCacheUtils {

    // Clear Glide disk cache (must be run on a background thread)
    public static void clearDiskCache(Context context) {
        new Thread(() -> Glide.get(context).clearDiskCache()).start();
    }

    // Clear Glide memory cache (can be run on the main thread)
    public static void clearMemoryCache(Context context) {
        Glide.get(context).clearMemory();
    }
}
