package de.javagl.jgltf.model.io;

import de.javagl.jgltf.model.io.v1.GltfAssetV1;
import de.javagl.jgltf.model.io.v1.GltfAssetWriterV1;
import de.javagl.jgltf.model.io.v2.GltfAssetV2;
import de.javagl.jgltf.model.io.v2.GltfAssetWriterV2;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Map.Entry;

public class GltfAssetWriter {
   public void write(GltfAsset gltfAsset, String fileName) throws IOException {
      this.write(gltfAsset, new File(fileName));
   }

   public void write(GltfAsset gltfAsset, File file) throws IOException {
      try (OutputStream outputStream = new FileOutputStream(file)) {
         this.writeJson(gltfAsset, outputStream);
      }

      this.writeReferenceDatas(gltfAsset, file);
   }

   public void writeJson(GltfAsset gltfAsset, String fileName) throws IOException {
      this.writeJson(gltfAsset, new File(fileName));
   }

   public void writeJson(GltfAsset gltfAsset, File file) throws IOException {
      try (OutputStream outputStream = new FileOutputStream(file)) {
         this.writeJson(gltfAsset, outputStream);
      }
   }

   public void writeJson(GltfAsset gltfAsset, OutputStream outputStream) throws IOException {
      Object gltf = gltfAsset.getGltf();
      GltfWriter gltfWriter = new GltfWriter();
      gltfWriter.write(gltf, outputStream);
   }

   public void writeBinary(GltfAsset gltfAsset, String fileName) throws IOException {
      this.writeBinary(gltfAsset, new File(fileName));
   }

   public void writeBinary(GltfAsset gltfAsset, File file) throws IOException {
      try (OutputStream outputStream = new FileOutputStream(file)) {
         this.writeBinary(gltfAsset, outputStream);
      }

      this.writeReferenceDatas(gltfAsset, file);
   }

   public void writeBinary(GltfAsset gltfAsset, OutputStream outputStream) throws IOException {
      if (gltfAsset instanceof GltfAssetV1) {
         GltfAssetV1 gltfAssetV1 = (GltfAssetV1)gltfAsset;
         GltfAssetWriterV1 gltfAssetWriterV1 = new GltfAssetWriterV1();
         gltfAssetWriterV1.writeBinary(gltfAssetV1, outputStream);
      } else {
         if (!(gltfAsset instanceof GltfAssetV2)) {
            throw new IOException("The gltfAsset has an unknown version: " + gltfAsset);
         }

         GltfAssetV2 gltfAssetV2 = (GltfAssetV2)gltfAsset;
         GltfAssetWriterV2 gltfAssetWriterV2 = new GltfAssetWriterV2();
         gltfAssetWriterV2.writeBinary(gltfAssetV2, outputStream);
      }
   }

   private void writeReferenceDatas(GltfAsset gltfAsset, File file) throws IOException {
      for (Entry<String, ByteBuffer> entry : gltfAsset.getReferenceDatas().entrySet()) {
         String relativeUrlString = entry.getKey();
         ByteBuffer data = entry.getValue();
         String referenceFileName = file.toPath().getParent().resolve(relativeUrlString).toString();

         try (WritableByteChannel writableByteChannel = Channels.newChannel(new FileOutputStream(referenceFileName))) {
            writableByteChannel.write(data.slice());
         }
      }
   }
}
