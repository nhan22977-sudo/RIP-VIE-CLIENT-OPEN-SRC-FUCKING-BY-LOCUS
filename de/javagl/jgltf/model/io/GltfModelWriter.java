package de.javagl.jgltf.model.io;

import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.io.v1.GltfModelWriterV1;
import de.javagl.jgltf.model.io.v2.GltfModelWriterV2;
import de.javagl.jgltf.model.v1.GltfModelV1;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class GltfModelWriter {
   public void write(GltfModel gltfModel, String fileName) throws IOException {
      this.write(gltfModel, new File(fileName));
   }

   public void write(GltfModel gltfModel, File file) throws IOException {
      if (gltfModel instanceof GltfModelV1) {
         GltfModelV1 gltfModelV1 = (GltfModelV1)gltfModel;
         GltfModelWriterV1 gltfModelWriterV1 = new GltfModelWriterV1();
         gltfModelWriterV1.write(gltfModelV1, file);
      } else {
         GltfModelWriterV2 gltfModelWriterV2 = new GltfModelWriterV2();
         gltfModelWriterV2.write(gltfModel, file);
      }
   }

   public void writeBinary(GltfModel gltfModel, File file) throws IOException {
      try (OutputStream outputStream = new FileOutputStream(file)) {
         this.writeBinary(gltfModel, outputStream);
      }
   }

   public void writeBinary(GltfModel gltfModel, OutputStream outputStream) throws IOException {
      if (gltfModel instanceof GltfModelV1) {
         GltfModelV1 gltfModelV1 = (GltfModelV1)gltfModel;
         GltfModelWriterV1 gltfModelWriterV1 = new GltfModelWriterV1();
         gltfModelWriterV1.writeBinary(gltfModelV1, outputStream);
      } else {
         GltfModelWriterV2 gltfModelWriterV2 = new GltfModelWriterV2();
         gltfModelWriterV2.writeBinary(gltfModel, outputStream);
      }
   }

   public void writeEmbedded(GltfModel gltfModel, File file) throws IOException {
      try (OutputStream outputStream = new FileOutputStream(file)) {
         this.writeEmbedded(gltfModel, outputStream);
      }
   }

   public void writeEmbedded(GltfModel gltfModel, OutputStream outputStream) throws IOException {
      if (gltfModel instanceof GltfModelV1) {
         GltfModelV1 gltfModelV1 = (GltfModelV1)gltfModel;
         GltfModelWriterV1 gltfModelWriterV1 = new GltfModelWriterV1();
         gltfModelWriterV1.writeEmbedded(gltfModelV1, outputStream);
      } else {
         GltfModelWriterV2 gltfModelWriterV2 = new GltfModelWriterV2();
         gltfModelWriterV2.writeEmbedded(gltfModel, outputStream);
      }
   }
}
