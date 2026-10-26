package de.javagl.jgltf.model.structure;

import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.impl.DefaultAccessorModel;
import de.javagl.jgltf.model.impl.DefaultBufferModel;
import de.javagl.jgltf.model.impl.DefaultBufferViewModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class BufferStructure {
   private final Map<DefaultAccessorModel, String> accessorIds = new LinkedHashMap<>();
   private final Set<String> accessorIdValues = new LinkedHashSet<>();
   private final Map<DefaultAccessorModel, Integer> accessorIndices = new LinkedHashMap<>();
   private final List<DefaultBufferViewModel> bufferViewModels = new ArrayList<>();
   private final Map<DefaultBufferViewModel, String> bufferViewIds = new LinkedHashMap<>();
   private final Set<String> bufferViewIdValues = new LinkedHashSet<>();
   private final Map<DefaultBufferViewModel, Integer> bufferViewIndices = new LinkedHashMap<>();
   private final Map<DefaultBufferViewModel, List<DefaultAccessorModel>> bufferViewAccessorModels = new LinkedHashMap<>();
   private final List<DefaultBufferModel> bufferModels = new ArrayList<>();
   private final Map<DefaultBufferModel, String> bufferIds = new LinkedHashMap<>();
   private final Set<String> bufferIdValues = new LinkedHashSet<>();
   private final Map<DefaultBufferModel, Integer> bufferIndices = new LinkedHashMap<>();
   private final Map<DefaultBufferModel, List<DefaultBufferViewModel>> bufferBufferViewModels = new LinkedHashMap<>();
   private final Map<DefaultBufferModel, Set<Integer>> paddingByteIndices = new LinkedHashMap<>();
   private final Map<String, Integer> idSuffixes = new LinkedHashMap<>();

   public void addAccessorModel(DefaultAccessorModel accessorModel, String idPrefix) {
      String id = this.createId(idPrefix, this.accessorIdValues);
      this.accessorIndices.put(accessorModel, this.accessorIndices.size());
      this.accessorIds.put(accessorModel, id);
      this.accessorIdValues.add(id);
   }

   public void addBufferViewModel(DefaultBufferViewModel bufferViewModel, String idPrefix, Collection<? extends DefaultAccessorModel> accessorModels) {
      this.bufferViewModels.add(bufferViewModel);
      String id = this.createId(idPrefix, this.bufferViewIdValues);
      this.bufferViewIds.put(bufferViewModel, id);
      this.bufferViewIdValues.add(id);
      this.bufferViewIndices.put(bufferViewModel, this.bufferViewIndices.size());
      this.bufferViewAccessorModels.put(bufferViewModel, new ArrayList<>(accessorModels));
   }

   public void addBufferModel(DefaultBufferModel bufferModel, String idPrefix, Collection<? extends DefaultBufferViewModel> bufferViewModels) {
      this.bufferModels.add(bufferModel);
      String id = this.createId(idPrefix, this.bufferIdValues);
      this.bufferIds.put(bufferModel, id);
      this.bufferIdValues.add(id);
      this.bufferIndices.put(bufferModel, this.bufferIndices.size());
      this.bufferBufferViewModels.put(bufferModel, new ArrayList<>(bufferViewModels));
   }

   private void addPaddingByteIndex(DefaultBufferModel bufferModel, int index) {
      this.paddingByteIndices.computeIfAbsent(bufferModel, bm -> new LinkedHashSet<>()).add(index);
   }

   void addPaddingByteIndices(DefaultBufferModel bufferModel, int startIndex, int count) {
      for (int index = startIndex; index < startIndex + count; index++) {
         this.addPaddingByteIndex(bufferModel, index);
      }
   }

   public List<DefaultBufferModel> getBufferModels() {
      return Collections.unmodifiableList(this.bufferModels);
   }

   public String getBufferId(BufferModel bufferModel) {
      return this.bufferIds.get(bufferModel);
   }

   public Integer getBufferIndex(BufferModel bufferModel) {
      return this.bufferIndices.get(bufferModel);
   }

   public List<DefaultBufferViewModel> getBufferViewModels(BufferModel bufferModel) {
      List<DefaultBufferViewModel> bufferViewModels = this.bufferBufferViewModels.get(bufferModel);
      return bufferViewModels == null ? Collections.emptyList() : Collections.unmodifiableList(bufferViewModels);
   }

   public List<DefaultBufferViewModel> getBufferViewModels() {
      return Collections.unmodifiableList(this.bufferViewModels);
   }

   public String getBufferViewId(BufferViewModel bufferViewModel) {
      return this.bufferViewIds.get(bufferViewModel);
   }

   public Integer getBufferViewIndex(BufferViewModel bufferViewModel) {
      return this.bufferViewIndices.get(bufferViewModel);
   }

   public List<DefaultAccessorModel> getAccessorModels(BufferViewModel bufferViewModel) {
      List<DefaultAccessorModel> accessorModels = this.bufferViewAccessorModels.get(bufferViewModel);
      return accessorModels == null ? Collections.emptyList() : Collections.unmodifiableList(accessorModels);
   }

   public List<DefaultAccessorModel> getAccessorModels() {
      List<DefaultAccessorModel> allAccessorModels = new ArrayList<>(this.accessorIds.keySet());
      return Collections.unmodifiableList(allAccessorModels);
   }

   public String getAccessorId(AccessorModel accessorModel) {
      return this.accessorIds.get(accessorModel);
   }

   public Integer getAccessorIndex(AccessorModel accessorModel) {
      return this.accessorIndices.get(accessorModel);
   }

   public boolean isPaddingByteIndex(BufferModel bufferModel, int index) {
      Set<Integer> indices = this.paddingByteIndices.get(bufferModel);
      return indices == null ? false : indices.contains(index);
   }

   private String createId(String prefix, Collection<? extends String> existingIds) {
      int counter = this.idSuffixes.getOrDefault(prefix, 0);

      String id;
      for (id = prefix; existingIds.contains(id); counter++) {
         id = prefix + "_" + counter;
      }

      this.idSuffixes.put(prefix, counter);
      return id;
   }
}
