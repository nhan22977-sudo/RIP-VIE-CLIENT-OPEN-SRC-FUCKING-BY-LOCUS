package de.labystudio.spotifyapi.open;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cache<T> {
   private final Map<String, T> cache = new ConcurrentHashMap<>();
   private final List<String> cacheQueue = new ArrayList<>();
   private int cacheSize;

   public Cache(int cacheSize) {
      this.cacheSize = cacheSize;
   }

   public void setCacheSize(int cacheSize) {
      this.cacheSize = cacheSize;
   }

   public void push(String key, T value) {
      if (key == null) {
         throw new IllegalArgumentException("Key cannot be null");
      }

      if (value == null) {
         throw new IllegalArgumentException("Value cannot be null");
      }

      if (this.cacheQueue.size() > this.cacheSize) {
         String urlToRemove = this.cacheQueue.remove(0);
         this.cache.remove(urlToRemove);
      }

      this.cache.put(key, value);
      this.cacheQueue.add(key);
   }

   public boolean has(String key) {
      return this.cache.containsKey(key);
   }

   public T get(String key) {
      return this.cache.get(key);
   }

   public void clear() {
      this.cache.clear();
      this.cacheQueue.clear();
   }
}
