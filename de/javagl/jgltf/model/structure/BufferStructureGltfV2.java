package de.javagl.jgltf.model.structure;

import de.javagl.jgltf.impl.v2.Accessor;
import de.javagl.jgltf.impl.v2.Buffer;
import de.javagl.jgltf.impl.v2.BufferView;
import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.impl.DefaultAccessorModel;
import de.javagl.jgltf.model.impl.DefaultBufferModel;
import de.javagl.jgltf.model.impl.DefaultBufferViewModel;
import de.javagl.jgltf.model.v2.GltfCreatorV2;
import java.util.ArrayList;
import java.util.List;

public class BufferStructureGltfV2 {
   public static List<Accessor> createAccessors(BufferStructure bufferStructure) {
      List<DefaultAccessorModel> accessorModels = bufferStructure.getAccessorModels();
      List<Accessor> accessors = new ArrayList<>();

      for (AccessorModel accessorModel : accessorModels) {
         BufferViewModel bufferViewModel = accessorModel.getBufferViewModel();
         int bufferViewIndex = bufferStructure.getBufferViewIndex(bufferViewModel);
         accessors.add(GltfCreatorV2.createAccessor(accessorModel, bufferViewIndex));
      }

      return accessors;
   }

   public static List<BufferView> createBufferViews(BufferStructure bufferStructure) {
      List<DefaultBufferViewModel> bufferViewModels = bufferStructure.getBufferViewModels();
      List<BufferView> bufferViews = new ArrayList<>();

      for (BufferViewModel bufferViewModel : bufferViewModels) {
         BufferModel bufferModel = bufferViewModel.getBufferModel();
         int bufferIndex = bufferStructure.getBufferIndex(bufferModel);
         bufferViews.add(GltfCreatorV2.createBufferView(bufferViewModel, bufferIndex));
      }

      return bufferViews;
   }

   public static List<Buffer> createBuffers(BufferStructure bufferStructure) {
      List<DefaultBufferModel> bufferModels = bufferStructure.getBufferModels();
      List<Buffer> buffers = new ArrayList<>();

      for (BufferModel bufferModel : bufferModels) {
         buffers.add(GltfCreatorV2.createBuffer(bufferModel));
      }

      return buffers;
   }

   private BufferStructureGltfV2() {
   }
}
