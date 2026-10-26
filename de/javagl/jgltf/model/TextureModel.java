package de.javagl.jgltf.model;

public interface TextureModel extends NamedModelElement {
   Integer getMagFilter();

   Integer getMinFilter();

   Integer getWrapS();

   Integer getWrapT();

   ImageModel getImageModel();
}
