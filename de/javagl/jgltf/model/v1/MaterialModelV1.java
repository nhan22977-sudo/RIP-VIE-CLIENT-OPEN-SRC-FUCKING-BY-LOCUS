package de.javagl.jgltf.model.v1;

import de.javagl.jgltf.model.MaterialModel;
import de.javagl.jgltf.model.gl.TechniqueModel;
import de.javagl.jgltf.model.impl.AbstractNamedModelElement;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class MaterialModelV1 extends AbstractNamedModelElement implements MaterialModel {
   private TechniqueModel techniqueModel;
   private Map<String, Object> values = Collections.emptyMap();

   public void setValues(Map<String, Object> values) {
      if (values == null) {
         this.values = Collections.emptyMap();
      } else {
         this.values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
      }
   }

   public void setTechniqueModel(TechniqueModel techniqueModel) {
      this.techniqueModel = techniqueModel;
   }

   public TechniqueModel getTechniqueModel() {
      return this.techniqueModel;
   }

   public Map<String, Object> getValues() {
      return this.values;
   }
}
