package me.drake.vieclient.util;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.FloatControl.Type;
import me.drake.vieclient.gui.nvg.adapter.modules.client.Sounds;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_3414;
import net.minecraft.class_7923;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Sfx {
   private static final Logger LOG = LoggerFactory.getLogger("VieSfx");
   public static final class_3414 BOOLEAN_ACTIVATE = of(
      "boolean_activate"
   );
   public static final class_3414 BOOLEAN_DEACTIVATE = of(
      "boolean_deactivate"
   );
   public static final class_3414 CHECK_ON = of("check_on");
   public static final class_3414 CHECK_OFF = of("check_off");
   public static final class_3414 HOVERING = of("hovering_sound");
   public static final class_3414 KEY_PRESS = of("key_press");
   public static final class_3414 TEXT_TYPING = of("text_typing");
   public static final class_3414 MODULE_ACTIVATE = of(
      "module_activate"
   );
   public static final class_3414 MODULE_DEACTIVATE = of(
      "module_deactivate"
   );
   public static final class_3414 MODULE_COLLAPSE = of(
      "module_collapse"
   );
   public static final class_3414 MODULE_EXPAND = of("module_expand");
   public static final class_3414 MODULE_KEYBIND_ACTIVATE = of(
      "module_keybind_activate"
   );
   public static final class_3414 MODULE_KEYBIND_DEACTIVATE = of(
      "module_keybind_deactivate"
   );
   public static final class_3414 NO_MODULE_SETTINGS = of(
      "no_module_settings"
   );
   public static final class_3414 TAP = of("tap");
   public static final class_3414 CLICK_ON = of("click_on");
   public static final class_3414 CLICK_OFF = of("click_off");
   public static final class_3414 THEME_CHANGE = of("theme_change");
   public static final class_3414 GUI_OPEN = of("gui_open");
   public static final class_3414 GUI_CLOSE = of("gui_close");
   public static final class_3414 DROPDOWN_OPEN = of(
      "dropdown_open"
   );
   public static final class_3414 DROPDOWN_CLOSE = of(
      "dropdown_close"
   );
   public static final class_3414 TAB_SWITCH = of("tab_switch");
   public static final class_3414 PAGE_TURN = of("page_turn");
   public static final class_3414 SLIDER = of("slider");
   public static final class_3414 LIST_SCROLL = of("list_scroll");
   public static final class_3414 BIND_SUCCESS = of("bind_success");
   public static final class_3414 BIND_REMOVE = of("bind_remove");
   public static final class_3414 CFGLOAD = of("cfgload");
   public static final class_3414 GUI_BG = of("gui_bg");
   public static final class_3414 SUCCESS = of("success");
   public static final class_3414 ERROR = of("error");
   public static final class_3414 WARN = of("warn");
   public static final class_3414 NOTIFICATION = of("notification");
   public static final class_3414 CONFIRM = of("confirm");
   public static final class_3414 POP = of("pop");
   public static final class_3414 WHOOSH = of("whoosh");
   public static final class_3414 BLIP = of("blip");
   private static final List ALL = List.of(
      BOOLEAN_ACTIVATE,
      BOOLEAN_DEACTIVATE,
      CHECK_ON,
      CHECK_OFF,
      HOVERING,
      KEY_PRESS,
      TEXT_TYPING,
      MODULE_ACTIVATE,
      MODULE_DEACTIVATE,
      MODULE_COLLAPSE,
      MODULE_EXPAND,
      MODULE_KEYBIND_ACTIVATE,
      MODULE_KEYBIND_DEACTIVATE,
      NO_MODULE_SETTINGS,
      TAP,
      THEME_CHANGE,
      CLICK_ON,
      CLICK_OFF,
      GUI_OPEN,
      GUI_CLOSE,
      DROPDOWN_OPEN,
      DROPDOWN_CLOSE,
      TAB_SWITCH,
      PAGE_TURN,
      SLIDER,
      LIST_SCROLL,
      BIND_SUCCESS,
      BIND_REMOVE,
      CFGLOAD,
      GUI_BG,
      SUCCESS,
      ERROR,
      WARN,
      NOTIFICATION,
      CONFIRM,
      POP,
      WHOOSH,
      BLIP
   );
   private static long lastSliderSound = 0L;
   private static long lastHoverSound = 0L;
   private static long lastTypingSound = 0L;
   private static long lastScrollSound = 0L;
   private static long lastKeyPress = 0L;
   private static final AtomicBoolean SHUTDOWN = new AtomicBoolean(false);
   private static final float SAMPLE_RATE = 44100.0F;
   private static final int CHANNELS = 1;
   private static final int SAMPLE_BITS = 16;
   private static final AudioFormat PCM_FMT = new AudioFormat(Encoding.PCM_SIGNED, 44100.0F, 16, 1, 2, 44100.0F, false);
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private Sfx() {
   }

   public static void click(boolean on) {
      play(on ? MODULE_ACTIVATE : MODULE_DEACTIVATE, 0.75F);
   }

   public static void sliderTick() {
      long var0 = System.currentTimeMillis();
      if (var0 - lastSliderSound > 55L) {
         lastSliderSound = var0;
         play(SLIDER, 0.6F);
      }
   }

   public static void listScroll() {
      long var0 = System.currentTimeMillis();
      if (var0 - lastScrollSound > 70L) {
         lastScrollSound = var0;
         play(LIST_SCROLL, 0.45F);
      }
   }

   public static void typingTick() {
      long var0 = System.currentTimeMillis();
      if (var0 - lastTypingSound > 45L) {
         lastTypingSound = var0;
         play(TEXT_TYPING, 0.45F);
      }
   }

   public static void keyPress() {
      long var0 = System.currentTimeMillis();
      if (var0 - lastKeyPress >= 30L) {
         lastKeyPress = var0;
         play(KEY_PRESS, 0.45F);
      }
   }

   public static void hover() {
      play(HOVERING, 0.3F);
   }

   public static void guiOpen() {
      play(GUI_OPEN, 0.85F);
   }

   public static void guiClose() {
      play(GUI_CLOSE, 0.85F);
   }

   public static void success() {
      play(SUCCESS, 0.85F);
   }

   public static void error() {
      play(ERROR, 0.85F);
   }

   public static void warn() {
      play(WARN, 0.85F);
   }

   public static void notifyBeep() {
      play(NOTIFICATION, 0.75F);
   }

   public static void pop() {
      play(POP, 0.75F);
   }

   public static void whoosh() {
      play(WHOOSH, 0.65F);
   }

   public static void confirm() {
      play(CONFIRM, 0.75F);
   }

   public static void blip() {
      play(BLIP, 0.55F);
   }

   public static void stopAll() {
      SHUTDOWN.set(true);
   }

   public static void register() {
      int var0 = 0;
      int var1 = 0;

      for (class_3414 var3 : ALL) {
         if (class_7923.field_41172.method_10250(var3.comp_3319())) {
            var1++;
         } else {
            class_2378.method_10230(class_7923.field_41172, var3.comp_3319(), var3);
            var0++;
         }
      }

      LOG.info(
         "[VieSfx] Registered {} sounds ({} new, {} already known) — procedural playback",
         new Object[]{ALL.size(), var0, var1}
      );
   }

   public static void play(class_3414 event) {
      play(event, 1.0F);
   }

   public static void play(class_3414 event, float volume) {
      if (event != null && !SHUTDOWN.get()) {
         if (Sounds.instance.soundEnabled) {
            if (event == HOVERING) {
               long var2 = System.currentTimeMillis();
               if (var2 - lastHoverSound < 35L) {
                  return;
               }

               lastHoverSound = var2;
            }

            byte[] var4 = Sfx.Synth.synthesize(event, 44100.0F);
            playPcmAsync(var4, volume, event.comp_3319());
         }
      }
   }

   private static void playPcmAsync(byte[] pcm, float volume, class_2960 id) {
      Thread var3 = new Thread(() -> playPcmBlocking(pcm, volume, id), "vie-sfx-" + id.method_12832());
      var3.setDaemon(true);
      var3.start();
   }

   private static void playPcmBlocking(byte[] pcm, float volume, class_2960 id) {
      SourceDataLine var3 = null;

      try {
         var3 = AudioSystem.getSourceDataLine(PCM_FMT);
         var3.open(PCM_FMT, 8192);
         setVolume(var3, volume);
         var3.start();
         int var4 = 0;

         while (var4 < pcm.length && !SHUTDOWN.get()) {
            var4 += var3.write(pcm, var4, pcm.length - var4);
         }

         if (!SHUTDOWN.get()) {
            var3.drain();
         }
      } catch (LineUnavailableException var20) {
         LOG.warn(
            "[VieSfx] play: no audio line for {} ({}Hz {}ch) → {}",
            new Object[]{id, PCM_FMT.getSampleRate(), PCM_FMT.getChannels(), var20.toString()}
         );
      } catch (Throwable var21) {
         LOG.error(
            "[VieSfx] play: threw for {}: {}",
            id,
            var21.toString()
         );
      } finally {
         if (var3 != null) {
            try {
               var3.stop();
            } catch (Throwable var19) {
            }

            try {
               var3.close();
            } catch (Throwable var18) {
            }
         }
      }
   }

   private static void setVolume(SourceDataLine line, float vol) {
      try {
         if (line.isControlSupported(Type.MASTER_GAIN)) {
            FloatControl var2 = (FloatControl)line.getControl(Type.MASTER_GAIN);
            float var3 = vol <= 1.0E-4F ? var2.getMinimum() : (float)(20.0 * Math.log10(vol));
            var2.setValue(Math.max(var2.getMinimum(), Math.min(var2.getMaximum(), var3)));
         }
      } catch (Throwable var4) {
      }
   }

   private static class_3414 of(String path) {
      byte[] var10000 = new byte[10];
      var10000[0] = (byte)(102);
      var10000[1] = (byte)(-13);
      var10000[2] = (byte)(121);
      var10000[3] = (byte)(-127);
      var10000[4] = (byte)(-59);
      var10000[5] = (byte)(38);
      var10000[6] = (byte)(-105);
      var10000[7] = (byte)(-2);
      var10000[8] = (byte)(22);
      var10000[9] = (byte)(-42);
      return class_3414.method_47908(class_2960.method_60655($$v_s(var10000, -5477058709698654155L), path));
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

   private static final class Synth {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      static byte[] synthesize(class_3414 event, float sr) {
         ThreadLocalRandom var2 = ThreadLocalRandom.current();
         float var3 = 1.0F + (var2.nextFloat() - 0.5F) * 0.04F;
         float var4 = 0.95F + var2.nextFloat() * 0.1F;
         int var5 = durationMs(event);
         int var6 = var2.nextInt(2);
         float[] var7 = new float[(int)(sr * (var5 / 1000.0)) + 64];
         switch (event.comp_3319().method_12832()) {
            case "tap":
               addClick(var7, sr, j(1800.0F, var3), j(1100.0F, var3), 60, 0.65F * var4);
               break;
            case "hovering_sound":
               addClick(var7, sr, j(1200.0F, var3), j(1100.0F, var3), 40, 0.3F * var4);
               break;
            case "click_on":
               addClick(var7, sr, j(1500.0F, var3), j(1200.0F, var3), 55, 0.55F * var4);
               break;
            case "click_off":
               addClick(var7, sr, j(1200.0F, var3), j(900.0F, var3), 55, 0.55F * var4);
               break;
            case "key_press":
               addClick(var7, sr, j(2400.0F, var3), j(2200.0F, var3), 30, 0.4F * var4);
               break;
            case "text_typing":
               addClick(var7, sr, j(900 + var2.nextInt(400), var3), j(700 + var2.nextInt(300), var3), 28, 0.4F * var4);
               break;
            case "boolean_activate":
               addClick(var7, sr, j(2200.0F, var3), j(2400.0F, var3), 70, 0.55F * var4);
               break;
            case "boolean_deactivate":
               addClick(var7, sr, j(2400.0F, var3), j(1800.0F, var3), 70, 0.55F * var4);
               break;
            case "check_on":
               addClick(var7, sr, j(1600.0F, var3), j(2000.0F, var3), 50, 0.6F * var4);
               break;
            case "check_off":
               addClick(var7, sr, j(2000.0F, var3), j(1400.0F, var3), 50, 0.6F * var4);
               break;
            case "module_activate":
               addChord(var7, sr, new float[]{600.0F, 900.0F, 1500.0F}, 100, 0.7F * var4, false, var3);
               break;
            case "module_deactivate":
               addChord(var7, sr, new float[]{1500.0F, 900.0F, 600.0F}, 100, 0.7F * var4, true, var3);
               break;
            case "module_keybind_activate":
               addChord(var7, sr, new float[]{800.0F, 1200.0F, 1600.0F}, 80, 0.65F * var4, false, var3);
               break;
            case "module_keybind_deactivate":
               addChord(var7, sr, new float[]{1600.0F, 1200.0F, 800.0F}, 80, 0.65F * var4, true, var3);
               break;
            case "module_expand":
               addClick(var7, sr, j(900.0F, var3), j(1400.0F, var3), 70, 0.55F * var4);
               break;
            case "module_collapse":
               addClick(var7, sr, j(1400.0F, var3), j(900.0F, var3), 70, 0.55F * var4);
               break;
            case "no_module_settings":
               addClick(var7, sr, j(400.0F, var3), j(300.0F, var3), 80, 0.45F * var4);
               break;
            case "gui_open":
               float[][] var11 = new float[][]{{523.25F, 659.25F, 783.99F}, {587.33F, 698.46F, 880.0F}, {523.25F, 659.25F, 783.99F, 1046.5F}};
               addArpeggio(var7, sr, j(var11[var6 % var11.length], var3), 130, 0.55F * var4);
               break;
            case "gui_close":
               float[][] var10 = new float[][]{{783.99F, 659.25F, 523.25F}, {880.0F, 698.46F, 587.33F}, {1046.5F, 783.99F, 659.25F, 523.25F}};
               addArpeggio(var7, sr, j(var10[var6 % var10.length], var3), 130, 0.55F * var4);
               break;
            case "dropdown_open":
               addPop(var7, sr, j(450.0F, var3), j(1100.0F, var3), 70, 0.6F * var4);
               break;
            case "dropdown_close":
               addPop(var7, sr, j(1100.0F, var3), j(450.0F, var3), 70, 0.6F * var4);
               break;
            case "tab_switch":
               addArpeggio(var7, sr, j(new float[]{1046.5F, 1318.5F}, var3), 60, 0.55F * var4);
               break;
            case "page_turn":
               addWhoosh(var7, sr, 0.18F, 0.5F, 0.55F, var3, 0.6F * var4);
               break;
            case "slider":
               addNoise(var7, sr, 0.3F, 0.85F, 0.65F * var4);
               break;
            case "list_scroll":
               addNoise(var7, sr, 0.1F, 0.4F, 0.45F * var4);
               break;
            case "theme_change":
               addArpeggio(var7, sr, j(new float[]{523.25F, 587.33F, 659.25F, 783.99F}, var3), 130, 0.55F * var4);
               break;
            case "bind_success":
               addArpeggio(var7, sr, j(new float[]{523.25F, 659.25F, 783.99F, 1046.5F}, var3), 110, 0.55F * var4);
               break;
            case "bind_remove":
               addGlide(var7, sr, j(900.0F, var3), j(200.0F, var3), 120, 0.55F * var4);
               break;
            case "cfgload":
               addChord(var7, sr, new float[]{440.0F, 660.0F, 880.0F}, 180, 0.55F * var4, false, var3);
               break;
            case "success":
               addArpeggio(var7, sr, j(new float[]{659.25F, 830.61F, 987.77F}, var3), 140, 0.65F * var4);
               break;
            case "error":
               addArpeggio(var7, sr, j(new float[]{440.0F, 415.3F, 311.13F}, var3), 160, 0.65F * var4);
               break;
            case "warn":
               addChord(var7, sr, new float[]{500.0F, 700.0F}, 100, 0.55F * var4, true, var3);
               break;
            case "notification":
               addBell(var7, sr, j(880.0F, var3), 220, 0.55F * var4);
               break;
            case "confirm":
               addBell(var7, sr, j(1318.5F, var3), 180, 0.55F * var4);
               break;
            case "pop":
               addPop(var7, sr, j(200.0F, var3), j(1100.0F, var3), 90, 0.7F * var4);
               break;
            case "whoosh":
               addWhoosh(var7, sr, 0.1F, 0.8F, 0.4F, var3, 0.6F * var4);
               break;
            case "blip":
               addClick(var7, sr, j(800.0F, var3), j(800.0F, var3), 45, 0.55F * var4);
               break;
            case "gui_bg":
            default:
               addNoise(var7, sr, 0.04F, 0.1F, 0.2F * var4);
         }

         return floatToPcm16Le(var7, var4);
      }

      private static int durationMs(class_3414 event) {
         String var1 = event.comp_3319().method_12832();
         switch (var1) {
            case "hovering_sound":
            case "key_press":
            case "text_typing":
               return 40;
            case "tap":
            case "click_on":
            case "click_off":
            case "check_on":
            case "check_off":
               return 65;
            case "blip":
               return 50;
            case "slider":
            case "list_scroll":
               return 50;
            case "module_activate":
            case "module_deactivate":
               return 110;
            case "module_keybind_activate":
            case "module_keybind_deactivate":
               return 90;
            case "module_expand":
            case "module_collapse":
            case "boolean_activate":
            case "boolean_deactivate":
               return 75;
            case "gui_open":
            case "gui_close":
            case "dropdown_open":
            case "dropdown_close":
               return 130;
            case "tab_switch":
               return 70;
            case "theme_change":
               return 150;
            case "bind_success":
               return 130;
            case "bind_remove":
               return 130;
            case "cfgload":
               return 200;
            case "no_module_settings":
               return 90;
            case "success":
               return 150;
            case "error":
               return 180;
            case "warn":
               return 110;
            case "notification":
               return 240;
            case "confirm":
               return 200;
            case "pop":
               return 100;
            case "whoosh":
            case "page_turn":
               return 280;
            case "gui_bg":
               return 250;
            default:
               return 100;
         }
      }

      private static float j(float f, float jitter) {
         return f * jitter;
      }

      private static float[] j(float[] fs, float jitter) {
         float[] var2 = new float[fs.length];

         for (int var3 = 0; var3 < fs.length; var3++) {
            var2[var3] = fs[var3] * jitter;
         }

         return var2;
      }

      private static void addClick(float[] dst, float sr, float f0, float f1, int durMs, float amp) {
         int var6 = Math.min(dst.length, (int)(sr * (durMs / 1000.0)));

         for (int var7 = 0; var7 < var6; var7++) {
            float var8 = var7 / sr;
            float var9 = (float)var7 / var6;
            float var10 = f0 + (f1 - f0) * var9;
            float var11 = (float)Math.exp(-4.0 * var9);
            if (var7 < sr * 0.0015F) {
               var11 *= var7 / (sr * 0.0015F);
            }

            dst[var7] += amp * var11 * (float)Math.sin((Math.PI * 2) * var10 * var8);
         }
      }

      private static void addChord(float[] dst, float sr, float[] freqs, int durMs, float amp, boolean descending, float jitter) {
         int var7 = Math.min(dst.length, (int)(sr * (durMs / 1000.0)));

         for (int var8 = 0; var8 < var7; var8++) {
            float var9 = var8 / sr;
            float var10 = (float)var8 / var7;
            float var11 = (float)Math.exp(-3.0 * var10);
            if (var8 < sr * 0.003F) {
               var11 *= var8 / (sr * 0.003F);
            }

            float var12 = 0.0F;

            for (float var16 : freqs) {
               float var17 = descending ? var16 * (1.0F - 0.15F * var10) : var16 * (1.0F + 0.15F * var10);
               var17 *= jitter;
               var12 += (float)Math.sin((Math.PI * 2) * var17 * var9);
            }

            dst[var8] += amp * var11 * 0.4F * var12;
         }
      }

      private static void addArpeggio(float[] dst, float sr, float[] freqs, int durMs, float amp) {
         int var5 = Math.max(20, durMs / Math.max(1, freqs.length));

         for (int var6 = 0; var6 < freqs.length; var6++) {
            int var7 = (int)(var6 * var5 * sr / 1000.0);
            if (var7 >= dst.length) {
               break;
            }

            int var8 = dst.length - var7;
            int var9 = Math.min(var8, (int)(sr * (var5 / 1000.0)));
            float var10 = freqs[var6];

            for (int var11 = 0; var11 < var9; var11++) {
               float var12 = var11 / sr;
               float var13 = (float)var11 / var9;
               float var14 = (float)Math.exp(-2.5 * var13);
               if (var11 < sr * 0.002F) {
                  var14 *= var11 / (sr * 0.002F);
               }

               dst[var7 + var11] = dst[var7 + var11] + amp * var14 * (float)Math.sin((Math.PI * 2) * var10 * var12);
            }
         }
      }

      private static void addNoise(float[] dst, float sr, float cutoff, float q, float amp) {
         float var5 = (float)(2.0 * Math.sin(Math.PI * Math.min(0.99, cutoff / sr)));
         float var6 = 1.0F / q;
         float var7 = 0.0F;
         float var8 = 0.0F;

         for (int var9 = 0; var9 < dst.length; var9++) {
            float var10 = (float)(Math.random() * 2.0 - 1.0);
            var7 += var5 * var8;
            float var11 = var10 - var7 - var6 * var8;
            var8 += var5 * var11;
            float var12 = (float)var9 / dst.length;
            float var13 = (float)Math.exp(-12.0 * var12);
            if (var9 < sr * 0.001F) {
               var13 *= var9 / (sr * 0.001F);
            }

            dst[var9] += amp * var13 * var7;
         }
      }

      private static void addPop(float[] dst, float sr, float f0, float f1, int durMs, float amp) {
         int var6 = Math.min(dst.length, (int)(sr * (durMs / 1000.0)));

         for (int var7 = 0; var7 < var6; var7++) {
            float var8 = var7 / sr;
            float var9 = (float)var7 / var6;
            float var10 = f0 + (f1 - f0) * (var9 * var9);
            float var11 = (float)Math.exp(-6.0 * var9);
            if (var7 < sr * 0.001F) {
               var11 *= var7 / (sr * 0.001F);
            }

            dst[var7] += amp * var11 * (float)Math.sin((Math.PI * 2) * var10 * var8);
         }
      }

      private static void addGlide(float[] dst, float sr, float f0, float f1, int durMs, float amp) {
         int var6 = Math.min(dst.length, (int)(sr * (durMs / 1000.0)));
         double var7 = 0.0;

         for (int var9 = 0; var9 < var6; var9++) {
            float var10 = var9 / sr;
            float var11 = (float)var9 / var6;
            float var12 = f0 + (f1 - f0) * var11;
            var7 += (Math.PI * 2) * var12 / sr;
            float var13 = (float)Math.exp(-2.0 * var11);
            if (var9 < sr * 0.003F) {
               var13 *= var9 / (sr * 0.003F);
            }

            dst[var9] += amp * var13 * (float)Math.sin(var7);
         }
      }

      private static void addBell(float[] dst, float sr, float f0, int durMs, float amp) {
         int var5 = Math.min(dst.length, (int)(sr * (durMs / 1000.0)));
         float[] var10000 = new float[4];
         var10000[0] = 1.0F;
         var10000[1] = 2.0F;
         var10000[2] = 3.0F;
         var10000[3] = 4.2F;
         float[] var6 = var10000;
         var10000 = new float[4];
         var10000[0] = 1.0F;
         var10000[1] = 0.55F;
         var10000[2] = 0.3F;
         var10000[3] = 0.18F;
         float[] var7 = var10000;
         var10000 = new float[4];
         var10000[0] = 2.5F;
         var10000[1] = 4.0F;
         var10000[2] = 5.5F;
         var10000[3] = 7.0F;
         float[] var8 = var10000;

         for (int var9 = 0; var9 < var6.length; var9++) {
            float var10 = f0 * var6[var9];
            float var11 = amp * var7[var9];
            float var12 = var8[var9];

            for (int var13 = 0; var13 < var5; var13++) {
               float var14 = var13 / sr;
               float var15 = (float)Math.exp(-var12 * ((float)var13 / var5));
               dst[var13] += var11 * var15 * (float)Math.sin((Math.PI * 2) * var10 * var14);
            }
         }
      }

      private static void addWhoosh(float[] dst, float sr, float cutoffStart, float cutoffEnd, float q, float jitter, float amp) {
         float var7 = (float)(2.0 * Math.sin(Math.PI * Math.min(0.99, cutoffStart / sr)));
         float var8 = 1.0F / q;
         float var9 = 0.0F;
         float var10 = 0.0F;

         for (int var11 = 0; var11 < dst.length; var11++) {
            float var12 = (float)var11 / dst.length;
            float var13 = cutoffStart + (cutoffEnd - cutoffStart) * var12;
            var7 = (float)(2.0 * Math.sin(Math.PI * Math.min(0.99, var13 / sr)));
            float var14 = (float)(Math.random() * 2.0 - 1.0);
            var9 += var7 * var10;
            float var15 = var14 - var9 - var8 * var10;
            var10 += var7 * var15;
            float var16 = (float)Math.exp(-Math.pow((var12 - 0.45) * 5.0, 2.0));
            dst[var11] += amp * var16 * var9 * jitter;
         }
      }

      private static byte[] floatToPcm16Le(float[] src, float ampJitter) {
         byte[] var2 = new byte[src.length * (2)];

         for (int var3 = 0; var3 < src.length; var3++) {
            float var4 = src[var3] * ampJitter;
            if (var4 > 1.0F) {
               var4 = 1.0F;
            }

            if (var4 < -1.0F) {
               var4 = -1.0F;
            }

            short var5 = (short)Math.round(var4 * 32767.0F);
            var2[var3 * (2)] = (byte)(var5 & (255));
            var2[var3 * (2) + 1] = (byte)(var5 >> (8) & (255));
         }

         return var2;
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
}
