package me.drake.vieclient.modules.donut.chunkprobe;

import java.util.Collection;
import me.drake.vieclient.CoreEntry;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.PostBlitRenderEvent;
import me.drake.vieclient.render.fx.FlaggedChunkRenderer;
import me.drake.vieclient.render.fx.WorldProjector;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.RenderUtil;

public final class ChunkEvidenceRenderer {
   private static final double PLATE_Y = 63.0;
   private static final FlaggedChunkRenderer CHUNK_RENDERER = new FlaggedChunkRenderer();
   private static final WorldProjector PROJECTOR = new WorldProjector();
   private static boolean registered = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private ChunkEvidenceRenderer() {
   }

   private static synchronized void ensureSubscribed() {
      if (!registered) {
         try {
            CoreEntry var0 = CoreEntry.getInstance();
            if (var0 != null && var0.getEventBus() != null) {
               var0.getEventBus().subscribe(new Object() {
                  // $VF: synthetic field
                  private static final boolean $$vie_p = true;

                  @EventHandler
                  public void onPostBlit(PostBlitRenderEvent event) {
                     if (!ChunkEvidenceRenderer.CHUNK_RENDERER.getVisuals().isEmpty()) {
                        if (ChunkEvidenceRenderer.PROJECTOR.snapshot()) {
                           try (NanoVGFrame var2 = NanoVGRenderer.frame()) {
                              if (!var2.isActive()) {
                                 return;
                              }

                              ChunkEvidenceRenderer.CHUNK_RENDERER.renderNanoVG(ChunkEvidenceRenderer.PROJECTOR);
                           }
                        }
                     }
                  }
               });
               registered = true;
            }
         } catch (Throwable var1) {
         }
      }
   }

   public static void render(Collection evidence, int possibleColor, int highColor, int alpha, boolean tracers, float tickDelta) {
      ensureSubscribed();
      int var6 = Math.max(5, Math.min(255, alpha));
      CHUNK_RENDERER.syncEvidence(evidence, possibleColor, highColor, var6, 63.0);
      if (tracers) {
         for (FlaggedChunkRenderer.FlaggedChunkVisual var8 : CHUNK_RENDERER.getVisuals()) {
            if (!(var8.getAlpha() <= 0.005F)) {
               int var9 = var8.getColorRgb() & (16777215);
               int var10 = (int)(var8.getAlpha() * 255.0F);
               int var11 = var10 << (24) | var9;
               RenderUtil.drawTracerToPos(var8.getCurrentX() + 8.0, 63.0, var8.getCurrentZ() + 8.0, var11, tickDelta);
            }
         }
      }
   }

   public static void clear() {
      CHUNK_RENDERER.clear();
   }
}
