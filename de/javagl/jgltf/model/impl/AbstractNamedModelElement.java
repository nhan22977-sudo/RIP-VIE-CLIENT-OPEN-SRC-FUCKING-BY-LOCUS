package de.javagl.jgltf.model.impl;

import de.javagl.jgltf.model.NamedModelElement;

public class AbstractNamedModelElement extends AbstractModelElement implements NamedModelElement {
   private String name;

   @Override
   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
