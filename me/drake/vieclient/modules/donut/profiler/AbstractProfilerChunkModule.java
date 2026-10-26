package me.drake.vieclient.modules.donut.profiler;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.IntSetting;
import net.minecraft.class_1923;

public abstract class AbstractProfilerChunkModule extends Module {
   private final ColorSetting possibleColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "Possible Color",
         "Possible chunk color",
         -38476
      )
   );
   private final IntSetting alpha = (IntSetting)this.addSetting(
      new IntSetting(
         "Alpha",
         "Pillar opacity",
         100,
         5,
         255
      )
   );
   private final FinderEvidenceStore evidence = new FinderEvidenceStore();
   private final List subscriptions = new ArrayList();
   private final AtomicLong analysisGeneration = new AtomicLong();
   private final ThreadLocal callbackGeneration = new ThreadLocal();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   protected AbstractProfilerChunkModule(String name, String description) {
      super(name, description, Category.BASE_FINDING);
      DonutProfilerService.get().registerStore(this.evidence);
      DonutProfilerService.get().registerSessionReset(this::resetProfilerContext);
   }

   @Override
   public final void onEnable() {
      super.onEnable();
      DonutProfilerService.get().acquire(this.sourceId());
      this.onProfilerEnable();
   }

   @Override
   public final void onDisable() {
      this.onProfilerDisable();
      this.subscriptions.forEach(DonutProfilerService.Subscription::close);
      this.subscriptions.clear();
      DonutProfilerService.get().release(this.sourceId());
      this.resetProfilerContext();
      super.onDisable();
   }

   @Override
   public final void onTick() {
      if (this.isInGame()) {
         String var1 = mc.field_1687.method_27983().method_29177().toString();
         DonutProfilerService.get().updateDimension(var1);
         this.onProfilerTick();
         int var2 = mc.field_1690.method_38521();
         class_1923 var3 = new class_1923(mc.field_1724.method_24515());
         FinderEvidenceStore.SessionDimension var4 = this.currentSessionDimension();
         if (var4 != null) {
            this.evidence.pruneOutOfRange(var4, var3.field_9181, var3.field_9180, var2);
         }
      }
   }

   @Override
   public String getInfo() {
      FinderEvidenceStore.SessionDimension var1 = this.currentSessionDimension();
      return var1 == null ? "0" : Integer.toString(this.evidence.snapshot(var1).size());
   }

   @EventHandler
   public final void onRenderEvidence(RenderEvent event) {
      if (event.getStage() == RenderEvent.Stage.WORLD_3D && this.isInGame()) {
         FinderEvidenceStore.SessionDimension var2 = this.currentSessionDimension();
         if (var2 != null) {
            FinderEvidenceRenderer.render(this.evidence.snapshot(var2), (Integer)this.possibleColor.get(), (Integer)this.alpha.get());
         }
      }
   }

   protected abstract String sourceId();

   protected void onProfilerEnable() {
   }

   protected void onProfilerDisable() {
   }

   protected void onProfilerSessionReset() {
   }

   protected void onProfilerTick() {
   }

   protected final void subscribe(Class type, Consumer listener) {
      this.subscriptions.add(DonutProfilerService.get().subscribe(type, sample -> {
         long var3 = this.analysisGeneration.get();
         this.callbackGeneration.set(var3);

         try {
            listener.accept(sample);
         } finally {
            this.callbackGeneration.remove();
         }
      }));
   }

   protected final void publishEvidence(long chunkKey, FinderConfidence confidence, double score, int sampleCount, String reason, long updatedMs) {
      long var10 = DonutProfilerService.get().currentDispatchSession();
      long var12 = DonutProfilerService.get().currentDispatchContext();
      Long var14 = (Long)this.callbackGeneration.get();
      long var15 = var14 == null ? this.analysisGeneration.get() : var14;
      mc.execute(
         () -> {
            if (var10 == DonutProfilerService.get().sessionId()
               && var12 == DonutProfilerService.get().currentDispatchContext()
               && var15 == this.analysisGeneration.get()
               && this.isInGame()) {
               FinderEvidenceStore.SessionDimension var16 = this.currentSessionDimension();
               if (var16 != null) {
                  FinderEvidenceStore.UpdateResult var17 = this.evidence.put(var16, chunkKey, confidence, score, sampleCount, reason, updatedMs);
                  if (var17.becameConfirmed() && mc.field_1724 != null) {
                  }
               }
            }
         }
      );
   }

   private void resetProfilerContext() {
      this.analysisGeneration.incrementAndGet();
      this.onProfilerSessionReset();
   }

   protected final FinderEvidenceStore.SessionDimension currentSessionDimension() {
      return !this.isInGame()
         ? null
         : new FinderEvidenceStore.SessionDimension(DonutProfilerService.get().sessionId(), mc.field_1687.method_27983().method_29177().toString());
   }

   // $VF: synthetic method
   private static String $$v_s(byte[] var0, long var1) {
      byte[] var3 = new byte[var0.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         var3[var4] = (byte)(
            var0[var4] ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255) ^ (var4 + 1) * (37)
         );
      }

      return new String(var3, StandardCharsets.UTF_8);
   }
}
