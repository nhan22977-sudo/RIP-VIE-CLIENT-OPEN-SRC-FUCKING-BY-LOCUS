package de.javagl.jgltf.model.io.v1;

import de.javagl.jgltf.impl.v1.GlTF;
import de.javagl.jgltf.model.io.GltfAssetWriter;
import de.javagl.jgltf.model.io.GltfWriter;
import de.javagl.jgltf.model.v1.GltfModelV1;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class GltfModelWriterV1 {
   public void write(GltfModelV1 gltfModel, File file) throws IOException {
      GltfAssetV1 gltfAsset = GltfAssetsV1.createDefault(gltfModel);
      GltfAssetWriter gltfAssetWriter = new GltfAssetWriter();
      gltfAssetWriter.write(gltfAsset, file);
   }

   public void writeBinary(GltfModelV1 gltfModel, File file) throws IOException {
      try (OutputStream outputStream = new FileOutputStream(file)) {
         this.writeBinary(gltfModel, outputStream);
      }
   }

   public void writeBinary(GltfModelV1 gltfModel, OutputStream outputStream) throws IOException {
      GltfAssetV1 gltfAsset = GltfAssetsV1.createBinary(gltfModel);
      GltfAssetWriterV1 gltfAssetWriter = new GltfAssetWriterV1();
      gltfAssetWriter.writeBinary(gltfAsset, outputStream);
   }

   public void writeEmbedded(GltfModelV1 gltfModel, OutputStream outputStream) throws IOException {
      GltfAssetV1 gltfAsset = GltfAssetsV1.createEmbedded(gltfModel);
      GltfWriter gltfWriter = new GltfWriter();
      GlTF gltf = gltfAsset.getGltf();
      gltfWriter.write(gltf, outputStream);
   }
}
