package de.javagl.jgltf.model;

import java.nio.ByteBuffer;

public interface AccessorData {
   Class<?> getComponentType();

   int getNumElements();

   int getNumComponentsPerElement();

   int getTotalNumComponents();

   ByteBuffer createByteBuffer();
}
