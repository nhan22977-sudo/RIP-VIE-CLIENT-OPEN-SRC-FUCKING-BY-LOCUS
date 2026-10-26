package me.drake.vieclient.modules.client.music;

import java.awt.image.BufferedImage;

@FunctionalInterface
public interface ArtworkLoader {
   BufferedImage load(String var1);
}
