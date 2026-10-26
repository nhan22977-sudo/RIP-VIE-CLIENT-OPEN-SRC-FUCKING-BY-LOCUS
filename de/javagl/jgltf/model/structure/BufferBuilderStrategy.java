package de.javagl.jgltf.model.structure;

import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.impl.DefaultAccessorModel;
import de.javagl.jgltf.model.impl.DefaultBufferModel;
import de.javagl.jgltf.model.impl.DefaultBufferViewModel;
import de.javagl.jgltf.model.impl.DefaultImageModel;
import java.util.List;

public interface BufferBuilderStrategy {
   void process(GltfModel var1);

   List<DefaultAccessorModel> getAccessorModels();

   List<DefaultBufferViewModel> getBufferViewModels();

   List<DefaultBufferModel> getBufferModels();

   void validateImageModel(DefaultImageModel var1);
}
