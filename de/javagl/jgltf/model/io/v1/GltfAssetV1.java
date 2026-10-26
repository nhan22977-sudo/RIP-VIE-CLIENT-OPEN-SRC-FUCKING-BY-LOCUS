package de.javagl.jgltf.model.io.v1;

import de.javagl.jgltf.impl.v1.Buffer;
import de.javagl.jgltf.impl.v1.GlTF;
import de.javagl.jgltf.impl.v1.Image;
import de.javagl.jgltf.impl.v1.Shader;
import de.javagl.jgltf.model.Optionals;
import de.javagl.jgltf.model.io.Buffers;
import de.javagl.jgltf.model.io.GltfAsset;
import de.javagl.jgltf.model.io.GltfReference;
import de.javagl.jgltf.model.io.IO;
import de.javagl.jgltf.model.v1.BinaryGltfV1;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class GltfAssetV1 implements GltfAsset {
   private final GlTF gltf;
   private final ByteBuffer binaryData;
   private final Map<String, ByteBuffer> referenceDatas;

   public GltfAssetV1(GlTF gltf, ByteBuffer binaryData) {
      this.gltf = Objects.requireNonNull(gltf, "The gltf may not be null");
      this.binaryData = binaryData;
      this.referenceDatas = new ConcurrentHashMap<>();
   }

   void putReferenceData(String uriString, ByteBuffer byteBuffer) {
      if (byteBuffer == null) {
         this.referenceDatas.remove(uriString);
      } else {
         this.referenceDatas.put(uriString, byteBuffer);
      }
   }

   public GlTF getGltf() {
      return this.gltf;
   }

   @Override
   public ByteBuffer getBinaryData() {
      return Buffers.createSlice(this.binaryData);
   }

   @Override
   public List<GltfReference> getReferences() {
      List<GltfReference> references = new ArrayList<>();
      references.addAll(this.getBufferReferences());
      references.addAll(this.getImageReferences());
      references.addAll(this.getShaderReferences());
      return references;
   }

   public List<GltfReference> getBufferReferences() {
      List<GltfReference> references = new ArrayList<>();
      Map<String, Buffer> buffers = Optionals.of(this.gltf.getBuffers());

      for (Entry<String, Buffer> entry : buffers.entrySet()) {
         String bufferId = entry.getKey();
         if (!BinaryGltfV1.isBinaryGltfBufferId(bufferId)) {
            Buffer buffer = buffers.get(bufferId);
            String uri = buffer.getUri();
            if (!IO.isDataUriString(uri)) {
               Consumer<ByteBuffer> target = byteBuffer -> this.putReferenceData(uri, byteBuffer);
               GltfReference reference = new GltfReference(bufferId, uri, target);
               references.add(reference);
            }
         }
      }

      return references;
   }

   public List<GltfReference> getImageReferences() {
      List<GltfReference> references = new ArrayList<>();
      Map<String, Image> images = Optionals.of(this.gltf.getImages());

      for (Entry<String, Image> entry : images.entrySet()) {
         String imageId = entry.getKey();
         Image image = entry.getValue();
         if (!BinaryGltfV1.hasBinaryGltfExtension(image)) {
            String uri = image.getUri();
            if (!IO.isDataUriString(uri)) {
               Consumer<ByteBuffer> target = byteBuffer -> this.putReferenceData(uri, byteBuffer);
               GltfReference reference = new GltfReference(imageId, uri, target);
               references.add(reference);
            }
         }
      }

      return references;
   }

   public List<GltfReference> getShaderReferences() {
      List<GltfReference> references = new ArrayList<>();
      Map<String, Shader> shaders = Optionals.of(this.gltf.getShaders());

      for (Entry<String, Shader> entry : shaders.entrySet()) {
         String shaderId = entry.getKey();
         Shader shader = entry.getValue();
         if (!BinaryGltfV1.hasBinaryGltfExtension(shader)) {
            String uri = shader.getUri();
            if (!IO.isDataUriString(uri)) {
               Consumer<ByteBuffer> target = byteBuffer -> this.putReferenceData(uri, byteBuffer);
               GltfReference reference = new GltfReference(shaderId, uri, target);
               references.add(reference);
            }
         }
      }

      return references;
   }

   @Override
   public ByteBuffer getReferenceData(String uriString) {
      return Buffers.createSlice(this.referenceDatas.get(uriString));
   }

   @Override
   public Map<String, ByteBuffer> getReferenceDatas() {
      return Collections.unmodifiableMap(this.referenceDatas);
   }
}
