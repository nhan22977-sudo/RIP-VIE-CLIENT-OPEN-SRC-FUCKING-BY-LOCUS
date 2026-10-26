package de.javagl.jgltf.model.structure;

import de.javagl.jgltf.impl.v1.Accessor;
import de.javagl.jgltf.impl.v1.Buffer;
import de.javagl.jgltf.impl.v1.BufferView;
import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.impl.DefaultAccessorModel;
import de.javagl.jgltf.model.impl.DefaultBufferModel;
import de.javagl.jgltf.model.impl.DefaultBufferViewModel;
import de.javagl.jgltf.model.v1.GltfCreatorV1;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BufferStructureGltfV1 {
   public static Map<String, Accessor> createAccessors(BufferStructure bufferStructure) {
      List<DefaultAccessorModel> accessorModels = bufferStructure.getAccessorModels();
      Map<String, Accessor> accessors = new LinkedHashMap<>();

      for (AccessorModel accessorModel : accessorModels) {
         BufferViewModel bufferViewModel = accessorModel.getBufferViewModel();
         String bufferViewId = bufferStructure.getBufferViewId(bufferViewModel);
         String accessorId = bufferStructure.getAccessorId(accessorModel);
         Accessor accessor = GltfCreatorV1.createAccessor(accessorModel, bufferViewId);
         accessors.put(accessorId, accessor);
      }

      return accessors;
   }

   public static Map<String, BufferView> createBufferViews(BufferStructure bufferStructure) {
      List<DefaultBufferViewModel> bufferViewModels = bufferStructure.getBufferViewModels();
      Map<String, BufferView> bufferViews = new LinkedHashMap<>();

      for (BufferViewModel bufferViewModel : bufferViewModels) {
         BufferModel bufferModel = bufferViewModel.getBufferModel();
         String bufferId = bufferStructure.getBufferId(bufferModel);
         String bufferViewId = bufferStructure.getBufferViewId(bufferViewModel);
         BufferView bufferView = GltfCreatorV1.createBufferView(bufferViewModel, bufferId);
         bufferViews.put(bufferViewId, bufferView);
      }

      return bufferViews;
   }

   public static Map<String, Buffer> createBuffers(BufferStructure bufferStructure) {
      List<DefaultBufferModel> bufferModels = bufferStructure.getBufferModels();
      Map<String, Buffer> buffers = new LinkedHashMap<>();

      for (BufferModel bufferModel : bufferModels) {
         String bufferId = bufferStructure.getBufferId(bufferModel);
         Buffer buffer = GltfCreatorV1.createBuffer(bufferModel);
         buffers.put(bufferId, buffer);
      }

      return buffers;
   }

   private BufferStructureGltfV1() {
   }
}
