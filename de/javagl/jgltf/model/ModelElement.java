package de.javagl.jgltf.model;

import java.util.Map;

public interface ModelElement {
   Map<String, Object> getExtensions();

   Object getExtras();
}
