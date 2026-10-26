package de.javagl.jgltf.model.io;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.LongConsumer;

public final class ProgressInputStream extends FilterInputStream {
   private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
   private final List<LongConsumer> totalNumBytesReadConsumers = new CopyOnWriteArrayList<>();
   private volatile long totalNumBytesRead;

   public ProgressInputStream(InputStream inputStream) {
      super(inputStream);
   }

   long getTotalNumBytesRead() {
      return this.totalNumBytesRead;
   }

   void addPropertyChangeListener(PropertyChangeListener listener) {
      this.propertyChangeSupport.addPropertyChangeListener(listener);
   }

   void removePropertyChangeListener(PropertyChangeListener listener) {
      this.propertyChangeSupport.removePropertyChangeListener(listener);
   }

   public void addTotalNumBytesReadConsumer(LongConsumer consumer) {
      this.totalNumBytesReadConsumers.add(consumer);
   }

   public void removeTotalNumBytesReadConsumer(LongConsumer consumer) {
      this.totalNumBytesReadConsumers.remove(consumer);
   }

   @Override
   public int read() throws IOException {
      int b = super.read();
      if (b != -1) {
         this.updateProgress(1L);
      }

      return b;
   }

   @Override
   public int read(byte[] b) throws IOException {
      return this.read(b, 0, b.length);
   }

   @Override
   public int read(byte[] b, int off, int len) throws IOException {
      int read = super.read(b, off, len);
      return read == -1 ? -1 : (int)this.updateProgress(read);
   }

   @Override
   public long skip(long n) throws IOException {
      return this.updateProgress(super.skip(n));
   }

   @Override
   public void mark(int readlimit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void reset() throws IOException {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean markSupported() {
      return false;
   }

   private long updateProgress(long numBytesRead) {
      if (numBytesRead > 0L) {
         long oldTotalNumBytesRead = this.totalNumBytesRead;
         this.totalNumBytesRead += numBytesRead;
         this.propertyChangeSupport.firePropertyChange("totalNumBytesRead", oldTotalNumBytesRead, this.totalNumBytesRead);
         this.fireTotalNumBytesRead();
      }

      return numBytesRead;
   }

   private void fireTotalNumBytesRead() {
      for (LongConsumer consumer : this.totalNumBytesReadConsumers) {
         consumer.accept(this.totalNumBytesRead);
      }
   }
}
