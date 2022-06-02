package com.lee.playandroid.base.cache.impl;


import androidx.collection.LruCache;

/**
 *
 * @author jv.lee
 * @date 2019-11-14
 */
public class MemoryCache extends LruCache<String,String> {
    public MemoryCache(int maxSize) {
        super(maxSize);
    }
}