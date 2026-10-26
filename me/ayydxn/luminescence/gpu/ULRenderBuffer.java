package me.ayydxn.luminescence.gpu;

public record ULRenderBuffer(int textureID, int width, int height, boolean hasStencilBuffer, boolean hasDepthBuffer) {
}
