package de.javagl.jgltf.model.io;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.javagl.jgltf.impl.v1.GlTF;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.logging.Logger;

final class GltfReader {
   private static final Logger logger = Logger.getLogger(GltfReader.class.getName());
   private Consumer<? super JsonError> jsonErrorConsumer = JacksonUtils.loggingJsonErrorConsumer();
   private final ObjectMapper objectMapper = JacksonUtils.createObjectMapper(this.jsonErrorConsumer);
   private JsonNode rootNode;

   void setJsonErrorConsumer(Consumer<? super JsonError> jsonErrorConsumer) {
      this.jsonErrorConsumer = jsonErrorConsumer;
   }

   void read(InputStream inputStream) throws IOException {
      JacksonUtils.configure(this.objectMapper, this.jsonErrorConsumer);
      this.rootNode = this.objectMapper.readTree(inputStream);
   }

   String getVersion() {
      return this.rootNode == null ? null : getVersion(this.rootNode);
   }

   int getMajorVersion() {
      if (this.rootNode == null) {
         return 0;
      }

      int[] version = VersionUtils.computeMajorMinorPatch(this.getVersion());
      return version[0];
   }

   GlTF getAsGltfV1() {
      return this.rootNode == null ? null : (GlTF)this.objectMapper.convertValue(this.rootNode, GlTF.class);
   }

   de.javagl.jgltf.impl.v2.GlTF getAsGltfV2() {
      return this.rootNode == null ? null : (de.javagl.jgltf.impl.v2.GlTF)this.objectMapper.convertValue(this.rootNode, de.javagl.jgltf.impl.v2.GlTF.class);
   }

   private static String getVersion(JsonNode rootNode) {
      JsonNode assetNode = rootNode.get("asset");
      if (assetNode == null) {
         return "1.0";
      } else {
         JsonNode versionNode = assetNode.get("version");
         if (versionNode == null) {
            return "1.0";
         } else if (!versionNode.isValueNode()) {
            logger.warning("No valid 'version' property in 'asset'. Assuming version 1.0");
            return "1.0";
         } else {
            return versionNode.asText();
         }
      }
   }
}
