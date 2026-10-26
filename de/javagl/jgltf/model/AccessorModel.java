package de.javagl.jgltf.model;

public interface AccessorModel extends NamedModelElement {
   BufferViewModel getBufferViewModel();

   int getComponentType();

   Class<?> getComponentDataType();

   boolean isNormalized();

   int getComponentSizeInBytes();

   int getElementSizeInBytes();

   int getPaddedElementSizeInBytes();

   int getByteOffset();

   int getCount();

   ElementType getElementType();

   int getByteStride();

   AccessorData getAccessorData();

   Number[] getMin();

   Number[] getMax();
}
