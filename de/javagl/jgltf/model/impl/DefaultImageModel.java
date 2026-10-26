package de.javagl.jgltf.model.impl;

import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.io.Buffers;
import java.nio.ByteBuffer;

public class DefaultImageModel extends AbstractNamedModelElement implements ImageModel {
   private String uri;
   private String mimeType;
   private BufferViewModel bufferViewModel;
   private ByteBuffer imageData;

   public void setUri(String uri) {
      this.uri = uri;
   }

   public void setMimeType(String mimeType) {
      this.mimeType = mimeType;
   }

   public void setBufferViewModel(BufferViewModel bufferViewModel) {
      this.bufferViewModel = bufferViewModel;
   }

   public void setImageData(ByteBuffer imageData) {
      this.imageData = imageData;
   }

   @Override
   public String getUri() {
      return this.uri;
   }

   @Override
   public String getMimeType() {
      return this.mimeType;
   }

   @Override
   public BufferViewModel getBufferViewModel() {
      return this.bufferViewModel;
   }

   @Override
   public ByteBuffer getImageData() {
      return this.imageData == null ? this.bufferViewModel.getBufferViewData() : Buffers.createSlice(this.imageData);
   }
}
