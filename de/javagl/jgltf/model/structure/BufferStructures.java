package de.javagl.jgltf.model.structure;

import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.impl.DefaultBufferModel;
import de.javagl.jgltf.model.io.GltfReference;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class BufferStructures {
   private static final Logger logger = Logger.getLogger(BufferStructures.class.getName());

   public static void resolve(Iterable<? extends GltfReference> bufferReferences, BufferStructure bufferStructure) {
      List<DefaultBufferModel> bufferModels = bufferStructure.getBufferModels();
      Map<String, BufferModel> uriToBufferModel = new LinkedHashMap<>();

      for (BufferModel bufferModel : bufferModels) {
         String uri = bufferModel.getUri();
         uriToBufferModel.put(uri, bufferModel);
      }

      for (GltfReference bufferReference : bufferReferences) {
         String uri = bufferReference.getUri();
         BufferModel bufferModel = uriToBufferModel.get(uri);
         if (bufferModel == null) {
            logger.warning("Could not resolve buffer model for URI " + uri + " in buffer structure");
         } else {
            Consumer<ByteBuffer> target = bufferReference.getTarget();
            target.accept(bufferModel.getBufferData());
         }
      }
   }

   private BufferStructures() {
   }
}
