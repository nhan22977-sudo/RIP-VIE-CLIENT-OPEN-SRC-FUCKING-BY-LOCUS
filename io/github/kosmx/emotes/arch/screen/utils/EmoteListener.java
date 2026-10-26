package io.github.kosmx.emotes.arch.screen.utils;

import com.google.common.base.Stopwatch;
import io.github.kosmx.emotes.PlatformTools;
import io.github.kosmx.emotes.arch.EmotecraftClientMod;
import io.github.kosmx.emotes.common.CommonData;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_5375.class_5426;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmoteListener extends class_5426 {
   public static final class_2561 RELOADING_WAIT = class_2561.method_43471("emotecraft.reloading.wait");
   public static final class_2561 RELOADING = class_2561.method_43471("emotecraft.reloading");
   private static final DecimalFormat FORMAT = new DecimalFormat("#0.000");
   private CompletableFuture<?> loader;

   protected EmoteListener(Path path) throws IOException {
      super(path);
   }

   @Nullable
   public static EmoteListener create(Path packPath) {
      try {
         return new EmoteListener(packPath);
      } catch (IOException ex) {
         CommonData.LOGGER.warn("Failed to initialize emote dir monitoring", ex);
         return null;
      }
   }

   public void load(Runnable onComplete, @NotNull Executor executor) {
      if (this.loader != null) {
         this.loader.cancel(true);
      }

      PlatformTools.addToast(RELOADING);
      Stopwatch stopwatch = Stopwatch.createStarted();
      this.loader = EmotecraftClientMod.loadEmotes()
         .thenRun(
            () -> PlatformTools.addToast(
               class_2561.method_43469("emotecraft.reloading.done", new Object[]{FORMAT.format(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) / 1000.0)})
            )
         )
         .thenRunAsync(onComplete, Objects.requireNonNullElseGet(executor, class_310::method_1551));
   }

   public boolean isLoading() {
      return this.loader != null && !this.loader.isDone() && !this.loader.isCompletedExceptionally();
   }

   public void close() throws IOException {
      super.close();
      if (this.loader != null) {
         this.loader.cancel(true);
         this.loader = null;
      }
   }

   public void blockWhileLoading() {
      if (this.loader != null && !this.loader.isDone() && !this.loader.isCompletedExceptionally()) {
         try {
            this.loader.get(10L, TimeUnit.SECONDS);
         } catch (Throwable th) {
            CommonData.LOGGER.warn("Failed to wait for emote loading!", th);
            this.loader.cancel(true);
         }
      }
   }
}
