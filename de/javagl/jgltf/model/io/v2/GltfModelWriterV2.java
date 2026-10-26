package de.javagl.jgltf.model.io.v2;

import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.io.GltfAssetWriter;
import de.javagl.jgltf.model.io.GltfWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public final class GltfModelWriterV2 {
   public void write(GltfModel gltfModel, File file) throws IOException {
      GltfAssetV2 gltfAsset = GltfAssetsV2.createDefault(gltfModel);
      GltfAssetWriter gltfAssetWriter = new GltfAssetWriter();
      gltfAssetWriter.write(gltfAsset, file);
   }

   public void writeBinary(GltfModel gltfModel, OutputStream outputStream) throws IOException {
      GltfAssetV2 gltfAsset = GltfAssetsV2.createBinary(gltfModel);
      GltfAssetWriterV2 gltfAssetWriter = new GltfAssetWriterV2();
      gltfAssetWriter.writeBinary(gltfAsset, outputStream);
   }

   public void writeEmbedded(GltfModel gltfModel, OutputStream outputStream) throws IOException {
      GltfAssetV2 gltfAsset = GltfAssetsV2.createEmbedded(gltfModel);
      GltfWriter gltfWriter = new GltfWriter();
      GlTF gltf = gltfAsset.getGltf();
      gltfWriter.write(gltf, outputStream);
   }
}
