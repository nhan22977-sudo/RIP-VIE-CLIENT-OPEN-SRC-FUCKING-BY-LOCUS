package de.javagl.jgltf.model.impl;

import de.javagl.jgltf.model.ExtensionsModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DefaultExtensionsModel implements ExtensionsModel {
   private final Set<String> extensionsUsed = new LinkedHashSet<>();
   private final Set<String> extensionsRequired = new LinkedHashSet<>();

   public void addExtensionUsed(String extension) {
      this.extensionsUsed.add(extension);
   }

   public void removeExtensionUsed(String extension) {
      this.extensionsUsed.remove(extension);
      this.removeExtensionRequired(extension);
   }

   public void addExtensionsUsed(Collection<String> extensions) {
      if (extensions != null) {
         this.extensionsUsed.addAll(extensions);
      }
   }

   public void clearExtensionUsed() {
      this.extensionsUsed.clear();
      this.clearExtensionRequired();
   }

   @Override
   public List<String> getExtensionsUsed() {
      return Collections.unmodifiableList(new ArrayList<>(this.extensionsUsed));
   }

   public void addExtensionRequired(String extension) {
      this.extensionsRequired.add(extension);
      this.addExtensionUsed(extension);
   }

   public void removeExtensionRequired(String extension) {
      this.extensionsRequired.remove(extension);
   }

   public void addExtensionsRequired(Collection<String> extensions) {
      if (extensions != null) {
         this.extensionsRequired.addAll(extensions);
         this.addExtensionsUsed(extensions);
      }
   }

   public void clearExtensionRequired() {
      this.extensionsRequired.clear();
   }

   @Override
   public List<String> getExtensionsRequired() {
      return Collections.unmodifiableList(new ArrayList<>(this.extensionsRequired));
   }
}
