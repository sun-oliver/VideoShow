package com.helloworld.videoshow.base;

import android.graphics.drawable.BitmapDrawable;
import android.support.v4.util.LruCache;

import com.helloworld.videoshow.utils.CommonUtils;

/**
 * Created by Hello on 2016/7/11.
 */

/**
 * 缓存
 */
public class CacheCommon {
    private LruCache<String, BitmapDrawable> mMemoryCache;
    private static CacheCommon cacheCommon;

    private CacheCommon() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, BitmapDrawable>(cacheSize) {
            @Override
            protected int sizeOf(String key, BitmapDrawable drawable) {
                //这个方法会在每次存入缓存的时候调用
                return CommonUtils.getSizeInBytes(drawable.getBitmap());
            }
        };
    }

    public static CacheCommon getInstance() {
        if (cacheCommon == null) {
            cacheCommon = new CacheCommon();
        }
        return cacheCommon;
    }

    public void addBitmapToMemoryCache(String key, BitmapDrawable drawable) {
        if (getBitmapFromMemoryCache(key) == null) {
            //当前地址没有缓存时，就添加
            mMemoryCache.put(key, drawable);
        }
    }

    public BitmapDrawable getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }
}
