package me.drake.vieclient.gui.web;

import java.io.InputStream;
import java.net.URI;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.class_1041;
import net.minecraft.class_10799;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_156;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_429;
import net.minecraft.class_437;
import net.minecraft.class_500;
import net.minecraft.class_526;
import net.ostore.ultralight.UltralightBrowserView;
import net.ostore.ultralight.UltralightEngine;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VieWebTitleScreen extends class_437 {
   private static final Logger LOG;
   private static final double REF_CSS_HEIGHT = 600.0;
   private static final String DISCORD_URL = "https://discord.gg/RhqaEw6FdR";
   private static final String WEBSITE_URL = "https://trillium.solutions";
   private UltralightBrowserView view;
   private int fbW;
   private int fbH;
   private int viewFbW;
   private int viewFbH;
   private double deviceScale;
   private double viewScale;
   private static boolean introCompleted = false;
   private final Map cursorCache;
   private int lastLiveW;
   private int lastLiveH;
   private int viewRetryCount;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public VieWebTitleScreen() {
      byte[] var10001 = new byte[3];
      var10001[0] = (byte)(114);
      var10001[1] = (byte)(-13);
      var10001[2] = (byte)(121);
      super(class_2561.method_43470($$v_s(var10001, 368391320595845121L)));
      this.viewFbW = 0;
      this.viewFbH = 0;
      this.deviceScale = 1.0;
      this.viewScale = 0.0;
      this.cursorCache = new HashMap();
      this.viewRetryCount = 0;
   }

   protected void method_25426() {
      super.method_25426();
      this.applyWindowMetrics();
      UltralightEngine.renderFrame();
      if (this.view == null || this.viewFbW == this.fbW && this.viewFbH == this.fbH && !(Math.abs(this.viewScale - this.deviceScale) > 1.0E-4)) {
         this.ensureView();
      } else {
         LOG.info(
            $$v_s(
               new byte[]{
                  127,
                  -20,
                  117,
                  -69,
                  98,
                  96,
                  122,
                  79,
                  97,
                  -42,
                  -115,
                  -126,
                  121,
                  -86,
                  106,
                  117,
                  29,
                  36,
                  -91,
                  -38,
                  -35,
                  -125,
                  42,
                  9,
                  -7,
                  113,
                  -32,
                  35,
                  -95,
                  -65,
                  21,
                  -64,
                  -73,
                  85,
                  16,
                  11,
                  -39,
                  -34,
                  -48,
                  -93,
                  -52,
                  -95,
                  44,
                  119,
                  27,
                  8,
                  -78,
                  -43,
                  114,
                  -104,
                  67,
                  -93,
                  125,
                  124,
                  -110,
                  101,
                  71,
                  -49,
                  -44,
                  -110,
                  74,
                  31,
                  124,
                  56,
                  28,
                  33,
                  -95,
                  -78,
                  45,
                  -91,
                  58,
                  14,
                  -2,
                  7,
                  -59,
                  -62,
                  -68,
                  -31,
                  16,
                  -75,
                  -62,
                  99,
                  -23,
                  25
               },
               368391320595845121L
            ),
            new Object[]{this.viewFbW, this.viewFbH, this.fbW, this.fbH}
         );
         this.recreateView();
      }

      class_1041 var1 = this.field_22787 != null ? this.field_22787.method_22683() : null;
      if (var1 != null) {
         this.lastLiveW = var1.method_4486();
         this.lastLiveH = var1.method_4502();
      }
   }

   private void applyWindowMetrics() {
      if (this.field_22787 != null) {
         class_1041 var1 = this.field_22787.method_22683();
         int var2 = var1.method_4489();
         int var3 = var1.method_4506();
         long var4 = var1.method_4490();
         if (var4 != 0L) {
            try {
               MemoryStack var6 = MemoryStack.stackPush();

               try {
                  IntBuffer var7 = var6.mallocInt(1);
                  IntBuffer var8 = var6.mallocInt(1);
                  GLFW.glfwGetFramebufferSize(var4, var7, var8);
                  int var9 = var7.get(0);
                  int var10 = var8.get(0);
                  if (var9 > 0 && var10 > 0) {
                     var2 = var9;
                     var3 = var10;
                  }
               } catch (Throwable var12) {
                  if (var6 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var11) {
                        var12.addSuppressed(var11);
                     }
                  }

                  throw var12;
               }

               if (var6 != null) {
                  var6.close();
               }
            } catch (Throwable var13) {
            }
         }

         this.fbW = var2;
         this.fbH = var3;
         this.lastLiveW = var1.method_4486();
         this.lastLiveH = var1.method_4502();
         this.deviceScale = Math.max(1.0, Math.min(4.0, this.fbH / 600.0));
      }
   }

   private void adaptToWindowSize() {
      if (this.field_22787 != null) {
         class_1041 var1 = this.field_22787.method_22683();
         int var2 = var1.method_4489();
         int var3 = var1.method_4506();
         long var4 = var1.method_4490();
         if (var4 != 0L) {
            try {
               MemoryStack var6 = MemoryStack.stackPush();

               try {
                  IntBuffer var7 = var6.mallocInt(1);
                  IntBuffer var8 = var6.mallocInt(1);
                  GLFW.glfwGetFramebufferSize(var4, var7, var8);
                  int var9 = var7.get(0);
                  int var10 = var8.get(0);
                  if (var9 > 0 && var10 > 0) {
                     var2 = var9;
                     var3 = var10;
                  }
               } catch (Throwable var17) {
                  if (var6 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var16) {
                        var17.addSuppressed(var16);
                     }
                  }

                  throw var17;
               }

               if (var6 != null) {
                  var6.close();
               }
            } catch (Throwable var18) {
            }
         }

         int var19 = var1.method_4486();
         int var20 = var1.method_4502();
         double var21 = Math.max(1.0, Math.min(4.0, var3 / 600.0));
         if (this.view == null
            || var2 != this.viewFbW
            || var3 != this.viewFbH
            || !(Math.abs(var21 - this.viewScale) < 1.0E-4)
            || var19 != this.lastLiveW
            || var20 != this.lastLiveH) {
            int var22 = this.viewFbW;
            int var11 = this.viewFbH;
            double var12 = this.viewScale;
            int var14 = this.lastLiveW;
            int var15 = this.lastLiveH;
            this.fbW = var2;
            this.fbH = var3;
            this.deviceScale = var21;
            this.lastLiveW = var19;
            this.lastLiveH = var20;
            LOG.info(
               $$v_s(
                  new byte[]{
                     127,
                     -20,
                     117,
                     -69,
                     98,
                     96,
                     122,
                     79,
                     97,
                     -42,
                     -115,
                     -126,
                     121,
                     -86,
                     106,
                     117,
                     21,
                     46,
                     -83,
                     -34,
                     -119,
                     -77,
                     32,
                     42,
                     -11,
                     124,
                     -16,
                     41,
                     -78,
                     -52,
                     14,
                     -33,
                     -95,
                     0,
                     92,
                     8,
                     -60,
                     -46,
                     -56,
                     -19,
                     -118,
                     -96,
                     100,
                     109,
                     8,
                     23,
                     -84,
                     -120,
                     84,
                     -111,
                     81,
                     -18,
                     112,
                     57,
                     -49,
                     106,
                     85,
                     -36,
                     -112,
                     -119,
                     82,
                     31,
                     97,
                     39,
                     68,
                     33,
                     -95,
                     -26,
                     118,
                     -86,
                     31,
                     22,
                     -15,
                     78,
                     -124,
                     -38,
                     -68,
                     -7,
                     18,
                     -75,
                     -49,
                     119,
                     -12,
                     21,
                     -64,
                     -121,
                     -94,
                     -125,
                     -4,
                     -87,
                     41,
                     126,
                     -2,
                     34
                  },
                  368391320595845121L
               ),
               new Object[]{var22, var11, var12, this.fbW, this.fbH, this.deviceScale, var14, var15, var19, var20}
            );
            this.recreateView();
         }
      }
   }

   private void recreateView() {
      if (this.view != null) {
         try {
            this.view.close();
         } catch (Throwable var2) {
         }

         this.view = null;
      }

      this.viewFbW = 0;
      this.viewFbH = 0;
      this.viewScale = 0.0;
      this.ensureView();
   }

   private void ensureView() {
      if (this.view == null) {
         if (!UltralightEngine.isReady()) {
            UltralightEngine.renderFrame();
            if (!UltralightEngine.isReady()) {
               return;
            }
         }

         try {
            this.view = new UltralightBrowserView(this.fbW, this.fbH, this.deviceScale);
            this.viewFbW = this.fbW;
            this.viewFbH = this.fbH;
            this.viewScale = this.deviceScale;
            this.view.setQueryHandler(this::onJsAction);
            this.view.setCursorHandler(this::applyCursor);
            this.view.setOnPageReadyCallback(v -> this.pushInitToPage());
            String var1 = read(
               $$v_s(
                  new byte[]{
                     69,
                     -23,
                     111,
                     -69,
                     57,
                     100,
                     48,
                     91,
                     37,
                     -57,
                     -69,
                     -107,
                     121,
                     -90,
                     82,
                     59,
                     0,
                     101,
                     -92,
                     -38,
                     -112,
                     -117,
                     96,
                     9,
                     -11,
                     102,
                     -8,
                     35,
                     -21,
                     -9,
                     19,
                     -56,
                     -88
                  },
                  368391320595845121L
               )
            );
            if (var1 != null) {
               this.view.loadHTML(var1);
            }

            this.view.focus();
         } catch (Throwable var2) {
            LOG.error(
               $$v_s(
                  new byte[]{
                     127,
                     -20,
                     117,
                     -69,
                     98,
                     96,
                     122,
                     79,
                     97,
                     -42,
                     -115,
                     -126,
                     121,
                     -86,
                     106,
                     117,
                     2,
                     35,
                     -87,
                     -39,
                     -35,
                     -124,
                     61,
                     24,
                     -3,
                     102,
                     -3,
                     41,
                     -85,
                     -65,
                     1,
                     -60,
                     -83,
                     86,
                     25,
                     26
                  },
                  368391320595845121L
               ),
               var2
            );
            this.view = null;
            this.viewFbW = 0;
            this.viewFbH = 0;
            this.viewScale = 0.0;
         }
      }
   }

   private void pushInitToPage() {
      if (this.view != null) {
         String var1 = this.resolveUsername();
         boolean var2 = !introCompleted;
         String var3 = "try{window.VIE&&window.VIE.init({username:'"
            + var1
            + "',playIntro:"
            + var2
            + ",reduceMotion:false});}catch(e){console.error('[vie] init failed',e);}";
         this.view.executeJavaScript(var3);
      }
   }

   private int liveW() {
      return this.field_22787 != null ? this.field_22787.method_22683().method_4486() : this.field_22789;
   }

   private int liveH() {
      return this.field_22787 != null ? this.field_22787.method_22683().method_4502() : this.field_22790;
   }

   public void method_25394(class_332 ctx, int mouseX, int mouseY, float delta) {
      this.adaptToWindowSize();
      UltralightEngine.renderFrame();
      this.ensureView();
      super.method_25394(ctx, mouseX, mouseY, delta);
      if (this.view == null) {
         this.viewRetryCount++;
         if (this.viewRetryCount > 300 && this.field_22787 != null) {
            LOG.warn(
               $$v_s(
                  new byte[]{
                     127,
                     -20,
                     117,
                     -69,
                     98,
                     96,
                     122,
                     79,
                     97,
                     -42,
                     -115,
                     -126,
                     121,
                     -86,
                     106,
                     117,
                     33,
                     38,
                     -72,
                     -36,
                     -100,
                     -117,
                     38,
                     26,
                     -12,
                     102,
                     -76,
                     48,
                     -84,
                     -6,
                     16,
                     -123,
                     -86,
                     85,
                     8,
                     94,
                     -52,
                     -63,
                     -34,
                     -92,
                     -128,
                     -93,
                     38,
                     122,
                     16,
                     79,
                     -74,
                     -109,
                     96,
                     -113,
                     94,
                     -18,
                     38,
                     122,
                     -49,
                     123,
                     78,
                     -45,
                     -103,
                     -125,
                     86
                  },
                  368391320595845121L
               ),
               this.viewRetryCount
            );
         }
      } else {
         this.viewRetryCount = 0;
         class_2960 var5 = this.view.getTextureIdentifier();
         if (var5 != null) {
            int var6 = this.liveW();
            int var7 = this.liveH();
            ctx.method_25302(class_10799.field_56883, var5, 0, 0, 0.0F, 0.0F, var6, var7, this.fbW, this.fbH, this.fbW, this.fbH);
         }
      }
   }

   private int vx(double screenX) {
      int var3 = this.liveW();
      return var3 == 0 ? 0 : (int)Math.round(screenX / var3 * (this.fbW / this.deviceScale));
   }

   private int vy(double screenY) {
      int var3 = this.liveH();
      return var3 == 0 ? 0 : (int)Math.round(screenY / var3 * (this.fbH / this.deviceScale));
   }

   public void method_16014(double mx, double my) {
      if (this.view != null) {
         this.view.mouseMoved(this.vx(mx), this.vy(my));
      }
   }

   public boolean method_25402(class_11909 click, boolean doubled) {
      if (this.view != null) {
         this.view.mousePressed(this.vx(click.comp_4798()), this.vy(click.comp_4799()), click.method_74245());
      }

      return true;
   }

   public boolean method_25406(class_11909 click) {
      if (this.view != null) {
         this.view.mouseReleased(this.vx(click.comp_4798()), this.vy(click.comp_4799()), click.method_74245());
      }

      return true;
   }

   public boolean method_25401(double mx, double my, double horizontal, double vertical) {
      if (this.view != null) {
         this.view.scroll((int)(horizontal * 60.0), (int)(vertical * 60.0));
      }

      return true;
   }

   public boolean method_25404(class_11908 key) {
      if (this.view != null) {
         this.view.keyPressed(key.method_74228(), key.comp_4797());
      }

      return true;
   }

   public boolean method_16803(class_11908 key) {
      if (this.view != null) {
         this.view.keyReleased(key.method_74228(), key.comp_4797());
      }

      return true;
   }

   public boolean method_25400(class_11905 input) {
      if (this.view != null) {
         this.view.charTyped(input.method_74226());
      }

      return true;
   }

   public void method_25432() {
      if (this.view != null) {
         this.view.close();
         this.view = null;
      }

      this.viewFbW = 0;
      this.viewFbH = 0;
      this.viewScale = 0.0;
      if (this.field_22787 != null) {
         GLFW.glfwSetCursor(this.field_22787.method_22683().method_4490(), 0L);
      }
   }

   public boolean method_25421() {
      return false;
   }

   private void onJsAction(String msg) {
      if (msg != null && !msg.isEmpty()) {
         String var2 = extractAction(msg);
         if (var2 != null && !var2.isEmpty()) {
            class_310 var3 = class_310.method_1551();
            if (var3 != null) {
               var3.execute(() -> this.dispatch(var2, var3));
            }
         } else {
            byte[] var10001 = new byte[44];
            var10001[0] = (byte)(127);
            var10001[1] = (byte)(-20);
            var10001[2] = (byte)(117);
            var10001[3] = (byte)(-69);
            var10001[4] = (byte)(98);
            var10001[5] = (byte)(96);
            var10001[6] = (byte)(122);
            var10001[7] = (byte)(79);
            var10001[8] = (byte)(97);
            var10001[9] = (byte)(-42);
            var10001[10] = (byte)(-115);
            var10001[11] = (byte)(-126);
            var10001[12] = (byte)(121);
            var10001[13] = (byte)(-86);
            var10001[14] = (byte)(106);
            var10001[15] = (byte)(117);
            var10001[16] = 1;
            var10001[17] = (byte)(36);
            var10001[18] = (byte)(-92);
            var10001[19] = (byte)(-49);
            var10001[20] = (byte)(-109);
            var10001[21] = (byte)(-125);
            var10001[22] = (byte)(35);
            var10001[23] = (byte)(24);
            var10001[24] = (byte)(-8);
            var10001[25] = (byte)(50);
            var10001[26] = (byte)(-10);
            var10001[27] = (byte)(52);
            var10001[28] = (byte)(-84);
            var10001[29] = (byte)(-5);
            var10001[30] = 0;
            var10001[31] = (byte)(-64);
            var10001[32] = (byte)(-28);
            var10001[33] = (byte)(87);
            var10001[34] = (byte)(25);
            var10001[35] = (byte)(13);
            var10001[36] = (byte)(-34);
            var10001[37] = (byte)(-42);
            var10001[38] = (byte)(-40);
            var10001[39] = (byte)(-88);
            var10001[40] = (byte)(-42);
            var10001[41] = (byte)(-30);
            var10001[42] = (byte)(63);
            var10001[43] = (byte)(107);
            LOG.warn($$v_s(var10001, 368391320595845121L), msg);
         }
      }
   }

   private void dispatch(String action, class_310 mc) {
      try {
         switch (action) {
            case $$v_s(new byte[]{75, -22, 121, -80, 119, 100, 118, 67, 43, -50, -127, -122, 121, -82, 78, 48, 6}, 368391320595845121L):
               introCompleted = true;
               mc.method_1507(new class_526(this));
               break;
            case $$v_s(new byte[]{75, -22, 121, -80, 119, 122, 106, 65, 56, -53, -108, -102, 116, -74, 82, 39}, 368391320595845121L):
               introCompleted = true;
               mc.method_1507(new class_500(this));
               break;
            case $$v_s(new byte[]{75, -22, 121, -80, 119, 120, 111, 89, 37, -51, -118, -123}, 368391320595845121L):
               introCompleted = true;
               mc.method_1507(new class_429(this, mc.field_1690));
               break;
            case $$v_s(new byte[]{81, -24, 112, -28, 58, 114, 125, 94, 37, -42, -127}, 368391320595845121L):
               openUrl($$v_s(new byte[]{76, -18, 104, -82, 62, 45, 48, 2, 58, -53, -127, -107, 121, -90, 82, 59, 0, 100, -81, -51}, 368391320595845121L));
               break;
            case $$v_s(new byte[]{81, -24, 112, -28, 41, 126, 108, 78, 35, -48, -128}, 368391320595845121L):
               openUrl(
                  $$v_s(
                     new byte[]{
                        76, -18, 104, -82, 62, 45, 48, 2, 40, -53, -105, -107, 122, -67, 83, 123, 19, 45, -29, -102, -56, -109, 40, 5, -24, 83, -29, 60, -127
                     },
                     368391320595845121L
                  )
               );
               break;
            case $$v_s(new byte[]{85, -17, 117, -86}, 368391320595845121L):
               mc.method_1592();
               break;
            case $$v_s(new byte[]{73, -17, 111, -73, 46, 45, 111, 95, 41, -44}, 368391320595845121L):
            case $$v_s(new byte[]{73, -17, 111, -73, 46, 45, 107, 66, 43, -59, -120, -109}, 368391320595845121L):
            case $$v_s(new byte[]{73, -17, 111, -73, 46, 45, 113, 72, 52, -42}, 368391320595845121L):
               LOG.info(
                  $$v_s(
                     new byte[]{
                        127,
                        -20,
                        117,
                        -69,
                        98,
                        96,
                        122,
                        79,
                        97,
                        -42,
                        -115,
                        -126,
                        121,
                        -86,
                        106,
                        117,
                        25,
                        63,
                        -65,
                        -57,
                        -98,
                        -57,
                        46,
                        30,
                        -24,
                        123,
                        -5,
                        40,
                        -1,
                        -65,
                        28,
                        -40
                     },
                     368391320595845121L
                  ),
                  action
               );
               break;
            case $$v_s(new byte[]{77, -12, 104, -84, 34, 45, 123, 66, 34, -57}, 368391320595845121L):
               introCompleted = true;
               break;
            default:
               LOG.warn(
                  $$v_s(
                     new byte[]{
                        127,
                        -20,
                        117,
                        -69,
                        98,
                        96,
                        122,
                        79,
                        97,
                        -42,
                        -115,
                        -126,
                        121,
                        -86,
                        106,
                        117,
                        1,
                        36,
                        -92,
                        -49,
                        -109,
                        -125,
                        35,
                        24,
                        -8,
                        50,
                        -11,
                        37,
                        -79,
                        -10,
                        8,
                        -53,
                        -2,
                        26,
                        7,
                        3
                     },
                     368391320595845121L
                  ),
                  action
               );
         }
      } catch (Throwable var5) {
         LOG.error(
            $$v_s(
               new byte[]{
                  127,
                  -20,
                  117,
                  -69,
                  98,
                  96,
                  122,
                  79,
                  97,
                  -42,
                  -115,
                  -126,
                  121,
                  -86,
                  106,
                  117,
                  21,
                  41,
                  -72,
                  -57,
                  -110,
                  -119,
                  111,
                  90,
                  -25,
                  111,
                  -77,
                  102,
                  -79,
                  -9,
                  21,
                  -64,
                  -77,
                  26,
                  -98,
                  -2,
                  57,
                  -105,
                  -52,
                  -71,
                  -115,
                  -69,
                  45,
                  120,
                  18,
                  79,
                  -72,
                  -101,
                  52,
                  -98,
                  69,
                  -70,
                  49,
                  98
               },
               368391320595845121L
            ),
            action,
            var5
         );
      }
   }

   private static String extractAction(String msg) {
      if (msg == null) {
         return null;
      }

      msg = msg.trim();
      if (msg.isEmpty()) {
         return null;
      }

      if (msg.startsWith("{")) {
         byte[] var10001 = new byte[8];
         var10001[0] = (byte)(6);
         var10001[1] = (byte)(-5);
         var10001[2] = (byte)(127);
         var10001[3] = (byte)(-86);
         var10001[4] = (byte)(36);
         var10001[5] = (byte)(120);
         var10001[6] = (byte)(113);
         var10001[7] = (byte)(15);
         if (msg.contains($$v_s(var10001, 368391320595845121L))) {
            var10001 = new byte[8];
            var10001[0] = (byte)(6);
            var10001[1] = (byte)(-5);
            var10001[2] = (byte)(127);
            var10001[3] = (byte)(-86);
            var10001[4] = (byte)(36);
            var10001[5] = (byte)(120);
            var10001[6] = (byte)(113);
            var10001[7] = (byte)(15);
            int var1 = msg.indexOf($$v_s(var10001, 368391320595845121L));
            int var2 = msg.indexOf(58, var1 + (8));
            if (var2 < 0) {
               return null;
            }

            int var3 = msg.indexOf(34, var2 + 1);
            if (var3 < 0) {
               return null;
            }

            int var4 = msg.indexOf(34, var3 + 1);
            if (var4 < 0) {
               return null;
            }

            return msg.substring(var3 + 1, var4);
         }
      }

      if (msg.length() >= (2) && msg.charAt(0) == (34) && msg.charAt(msg.length() - 1) == (34)) {
         msg = msg.substring(1, msg.length() - 1);
      }

      return msg;
   }

   private static void openUrl(String url) {
      try {
         class_156.method_668().method_673(URI.create(url));
      } catch (Throwable var2) {
         byte[] var10001 = new byte[38];
         var10001[0] = (byte)(127);
         var10001[1] = (byte)(-20);
         var10001[2] = (byte)(117);
         var10001[3] = (byte)(-69);
         var10001[4] = (byte)(98);
         var10001[5] = (byte)(96);
         var10001[6] = (byte)(122);
         var10001[7] = (byte)(79);
         var10001[8] = (byte)(97);
         var10001[9] = (byte)(-42);
         var10001[10] = (byte)(-115);
         var10001[11] = (byte)(-126);
         var10001[12] = (byte)(121);
         var10001[13] = (byte)(-86);
         var10001[14] = (byte)(106);
         var10001[15] = (byte)(117);
         var10001[16] = (byte)(18);
         var10001[17] = (byte)(43);
         var10001[18] = (byte)(-91);
         var10001[19] = (byte)(-62);
         var10001[20] = (byte)(-104);
         var10001[21] = (byte)(-125);
         var10001[22] = (byte)(111);
         var10001[23] = (byte)(9);
         var10001[24] = (byte)(-13);
         var10001[25] = (byte)(50);
         var10001[26] = (byte)(-5);
         var10001[27] = (byte)(54);
         var10001[28] = (byte)(-96);
         var10001[29] = (byte)(-15);
         var10001[30] = (byte)(71);
         var10001[31] = (byte)(-16);
         var10001[32] = (byte)(-106);
         var10001[33] = (byte)(118);
         var10001[34] = (byte)(70);
         var10001[35] = (byte)(94);
         var10001[36] = (byte)(-42);
         var10001[37] = (byte)(-54);
         LOG.warn($$v_s(var10001, 368391320595845121L), url, var2);
      }
   }

   private void applyCursor(int glfwShape) {
      if (this.field_22787 != null) {
         long var2 = (Long)this.cursorCache.computeIfAbsent(glfwShape, GLFW::glfwCreateStandardCursor);
         GLFW.glfwSetCursor(this.field_22787.method_22683().method_4490(), var2);
      }
   }

   private String resolveUsername() {
      class_310 var1 = class_310.method_1551();
      String var3;
      if (var1 != null && var1.method_1548() != null) {
         var3 = var1.method_1548().method_1676();
      } else {
         byte[] var10000 = new byte[6];
         var10000[0] = (byte)(116);
         var10000[1] = (byte)(-10);
         var10000[2] = (byte)(125);
         var10000[3] = (byte)(-89);
         var10000[4] = (byte)(40);
         var10000[5] = (byte)(101);
         var3 = $$v_s(var10000, 368391320595845121L);
      }

      String var2 = var3;
      if (var2 == null || var2.isEmpty()) {
         byte[] var4 = new byte[6];
         var4[0] = (byte)(116);
         var4[1] = (byte)(-10);
         var4[2] = (byte)(125);
         var4[3] = (byte)(-89);
         var4[4] = (byte)(40);
         var4[5] = (byte)(101);
         var2 = $$v_s(var4, 368391320595845121L);
      }

      byte[] var10002 = new byte[2];
      var10002[0] = (byte)(120);
      var10002[1] = (byte)(-58);
      var3 = var2.replace("\\", $$v_s(var10002, 368391320595845121L));
      var10002 = new byte[2];
      var10002[0] = (byte)(120);
      var10002[1] = (byte)(-67);
      return var3.replace("'", $$v_s(var10002, 368391320595845121L)).replace("\n", "").replace("\r", "");
   }

   private static String read(String resource) {
      try (InputStream var1 = VieWebTitleScreen.class.getResourceAsStream("/" + resource)) {
         return var1 == null ? null : new String(var1.readAllBytes(), StandardCharsets.UTF_8);
      } catch (Exception var6) {
         byte[] var10001 = new byte[33];
         var10001[0] = (byte)(127);
         var10001[1] = (byte)(-20);
         var10001[2] = (byte)(117);
         var10001[3] = (byte)(-69);
         var10001[4] = (byte)(98);
         var10001[5] = (byte)(96);
         var10001[6] = (byte)(122);
         var10001[7] = (byte)(79);
         var10001[8] = (byte)(97);
         var10001[9] = (byte)(-42);
         var10001[10] = (byte)(-115);
         var10001[11] = (byte)(-126);
         var10001[12] = (byte)(121);
         var10001[13] = (byte)(-86);
         var10001[14] = (byte)(106);
         var10001[15] = (byte)(117);
         var10001[16] = (byte)(18);
         var10001[17] = (byte)(43);
         var10001[18] = (byte)(-91);
         var10001[19] = (byte)(-62);
         var10001[20] = (byte)(-104);
         var10001[21] = (byte)(-125);
         var10001[22] = (byte)(111);
         var10001[23] = (byte)(9);
         var10001[24] = (byte)(-13);
         var10001[25] = (byte)(50);
         var10001[26] = (byte)(-26);
         var10001[27] = (byte)(35);
         var10001[28] = (byte)(-92);
         var10001[29] = (byte)(-5);
         var10001[30] = (byte)(71);
         var10001[31] = (byte)(-34);
         var10001[32] = (byte)(-71);
         LOG.error($$v_s(var10001, 368391320595845121L), resource, var6);
         return null;
      }
   }

   static {
      byte[] var10000 = new byte[13];
      var10000[0] = (byte)(82);
      var10000[1] = (byte)(-13);
      var10000[2] = (byte)(121);
      var10000[3] = (byte)(-15);
      var10000[4] = (byte)(58);
      var10000[5] = (byte)(114);
      var10000[6] = (byte)(125);
      var10000[7] = 0;
      var10000[8] = (byte)(56);
      var10000[9] = (byte)(-53);
      var10000[10] = (byte)(-112);
      var10000[11] = (byte)(-102);
      var10000[12] = (byte)(112);
      LOG = LoggerFactory.getLogger($$v_s(var10000, 368391320595845121L));
   }

   // $VF: synthetic method
   private static String $$v_s0(byte[] var0, long var1) {
      byte[] var3 = new byte[var0.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         var3[var4] = (byte)(
            var0[var4] ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255) ^ (var4 + 1) * (37)
         );
      }

      return new String(var3, StandardCharsets.UTF_8);
   }

   // $VF: synthetic method
   private static String $$v_s(byte[] var0, long var1) {
      return $$v_s0(var0, var1)
         .replace("discord.gg/45tgxtAwzD", "discord.gg/RhqaEw6FdR")
         .replace("vieclient.cc", "trillium.solutions")
         .replace("discord.gg/vieclient", "discord.gg/RhqaEw6FdR")
         .replace("https://vieservices.top", "https://trillium.solutions");
   }
}
