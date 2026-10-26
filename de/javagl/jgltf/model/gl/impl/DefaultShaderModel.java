package de.javagl.jgltf.model.gl.impl;

import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.gl.ShaderModel;
import de.javagl.jgltf.model.impl.AbstractNamedModelElement;
import de.javagl.jgltf.model.io.Buffers;
import java.nio.ByteBuffer;

public class DefaultShaderModel extends AbstractNamedModelElement implements ShaderModel {
   private String uri;
   private BufferViewModel bufferViewModel;
   private ByteBuffer shaderData;
   private ShaderModel.ShaderType shaderType;

   public DefaultShaderModel() {
   }

   public DefaultShaderModel(String uri, ShaderModel.ShaderType shaderType) {
      this.uri = uri;
      this.shaderType = shaderType;
   }

   public void setUri(String uri) {
      this.uri = uri;
   }

   public void setShaderType(ShaderModel.ShaderType shaderType) {
      this.shaderType = shaderType;
   }

   public void setBufferViewModel(BufferViewModel bufferViewModel) {
      this.bufferViewModel = bufferViewModel;
   }

   public void setShaderData(ByteBuffer shaderData) {
      this.shaderData = shaderData;
   }

   @Override
   public String getUri() {
      return this.uri;
   }

   @Override
   public BufferViewModel getBufferViewModel() {
      return this.bufferViewModel;
   }

   @Override
   public ByteBuffer getShaderData() {
      return this.shaderData == null ? this.bufferViewModel.getBufferViewData() : Buffers.createSlice(this.shaderData);
   }

   @Override
   public String getShaderSource() {
      return Buffers.readAsString(this.getShaderData());
   }

   @Override
   public ShaderModel.ShaderType getShaderType() {
      return this.shaderType;
   }
}
