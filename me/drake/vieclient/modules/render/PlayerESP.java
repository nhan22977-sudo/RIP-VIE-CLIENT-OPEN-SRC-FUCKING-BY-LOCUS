package me.drake.vieclient.modules.render;

import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import java.awt.Color;
import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.PostBlitRenderEvent;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.render.fx.WorldProjector;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.RenderUtil;
import me.drake.vieclient.util.render.EspFade;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_4184;

public class PlayerESP extends Module {
   private static final int TRACER_ALPHA = 255;
   private static final int FREECAM_DUMMY_ID = -6969;
   private static final float ARROW_BASE_RATIO = 0.62F;
   private static final int MIN_VISIBLE_ALPHA = 6;
   private static final Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0);
   private int lastBaseArgb = -1;
   private int lastOutlineA = -1;
   private int lastFillA = -1;
   private PlayerESP.BoxStyle lastBoxStyle = null;
   private Color cachedLosCoreColor;
   private Color cachedLosFillColor;
   private Color cachedOccCoreColor;
   private Color cachedOccFillColor;
   private Color cachedLosTopFill;
   private Color cachedLosBottomFill;
   private Color cachedOccTopFill;
   private Color cachedOccBottomFill;
   private int cachedDimOutline = -1;
   private int cachedR = 0;
   private int cachedG = 0;
   private int cachedB = 0;
   private int lastBreathA = -1;
   private Color cachedLosHaloColor;
   private int lastDimHaloA = -1;
   private Color cachedOccHaloColor;
   private static final int[][] EDGES = new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 0}, {4, 5}, {5, 6}, {6, 7}, {7, 4}, {0, 4}, {1, 5}, {2, 6}, {3, 7}};
   private static final int[][] FACES = new int[][]{{0, 1, 2, 3}, {4, 5, 6, 7}, {0, 1, 5, 4}, {2, 3, 7, 6}, {0, 3, 7, 4}, {1, 2, 6, 5}};
   private final EnumSetting style = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Style",
         "Visual style of the NanoVG player box",
         PlayerESP.BoxStyle.CYBERPUNK
      )
   );
   private final ColorSetting color = (ColorSetting)this.addSetting(
      new ColorSetting(
         "Color",
         "Main accent colour",
         -4160260
      )
   );
   private final IntSetting alpha = (IntSetting)this.addSetting(
      new IntSetting(
         "Alpha",
         "Fill opacity",
         45,
         0,
         255
      )
   );
   private final IntSetting outlineAlpha = (IntSetting)this.addSetting(
      new IntSetting(
         "Outline Alpha",
         "Box outline / bracket opacity",
         220,
         10,
         255
      )
   );
   private final BoolSetting glow = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Glow",
         "Draw outer neon glow halo around the box",
         true
      )
   );
   private final BoolSetting gradientFill = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Gradient Fill",
         "Soft vertical gradient fill inside the box",
         true
      )
   );
   private final BoolSetting healthBar = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Health Bar",
         "Dynamic gradient health bar with HP indicator",
         true
      )
   );
   private final BoolSetting playerInfo = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Player Info",
         "Tactical name, distance, and held item badge",
         true
      )
   );
   private final BoolSetting showName = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Name",
         "Show player name above the ESP box",
         false
      )
   );
   private final BoolSetting showDistance = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Distance",
         "Show player distance above the ESP box",
         true
      )
   );
   private final BoolSetting showItem = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Held Item",
         "Show player held item badge below the ESP box",
         true
      )
   );
   private final BoolSetting scanline = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Scanline",
         "Animated radar scanline sweep",
         true
      )
   );
   private final BoolSetting occlusion = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Occlusion",
         "Adapt colors when player is behind walls",
         true
      )
   );
   private final BoolSetting tracer = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Tracer",
         "Draw a line from the screen centre to each player",
         false
      )
   );
   private final DoubleSetting tracerWidth = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Tracer Width",
         "Tracer line thickness in pixels",
         1.0,
         0.1,
         5.0,
         0.1
      )
   );
   private final BoolSetting arrow = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Arrow",
         "Draw a triangle around the crosshair pointing at each player",
         false
      )
   );
   private final DoubleSetting arrowRadius = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Arrow Radius",
         "Radius of the invisible circle the arrows sit on",
         46.0,
         12.0,
         220.0,
         1.0
      )
   );
   private final DoubleSetting arrowSize = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Arrow Size",
         "Length of each triangle",
         8.0,
         3.0,
         24.0,
         0.5
      )
   );
   private final ColorSetting arrowColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "Arrow Color",
         "Arrow colour",
         -4160260
      )
   );
   private final BoolSetting arrowGlow = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Arrow Glow",
         "Render a vibrant neon bloom glow around direction arrows",
         true
      )
   );
   private final BoolSetting arrowFade = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Arrow Fade",
         "Fade the arrow out with distance instead of drawing every one at full opacity",
         true
      )
   );
   private final DoubleSetting arrowFadeRange = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Arrow Fade Range",
         "Distance at which a faded arrow reaches its dimmest",
         96.0,
         16.0,
         256.0,
         4.0
      )
   );
   private static final int HEALTH_TRACK_COLOR = -1274410481;
   private static final int PILL_BG_COLOR = -938866160;
   private static final int ITEM_BG_COLOR = -1106638320;
   private static final int DIST_TEXT_COLOR = -592463411;
   private static final int ITEM_TEXT_COLOR = -590160661;
   private static final int WHITE_ARGB = -1;
   private static final int HP_GREEN_TOP_INT = -13315175;
   private static final int HP_GREEN_BOTTOM_INT = -15681151;
   private static final int HP_AMBER_TOP_INT = -278748;
   private static final int HP_AMBER_BOTTOM_INT = -680437;
   private static final int HP_RED_TOP_INT = -495247;
   private static final int HP_RED_BOTTOM_INT = -2349530;
   private static final Color HP_GREEN_TOP = new Color(-13315175);
   private static final Color HP_GREEN_BOTTOM = new Color(-15681151);
   private static final Color HP_AMBER_TOP = new Color(-278748);
   private static final Color HP_AMBER_BOTTOM = new Color(-680437);
   private static final Color HP_RED_TOP = new Color(-495247);
   private static final Color HP_RED_BOTTOM = new Color(-2349530);
   private final double[][] rawCornersScratch = new double[8][3];
   private final double[][] ptsScratch = new double[8][];
   private final double[][] ptsCoordsScratch = new double[8][2];
   private final WorldProjector projector = new WorldProjector();
   private final float[] projScratch = new float[3];
   private final float[] boxBoundsScratch = new float[5];
   private int visible;
   private static final long LOS_CACHE_TTL_MS = 65L;
   private final Int2LongOpenHashMap losTimestampMap = new Int2LongOpenHashMap();
   private final Int2BooleanOpenHashMap losResultMap = new Int2BooleanOpenHashMap();
   private int cachedArrowRgb = -1;
   private final Color[] arrowBaseColorCache = new Color[256];
   private final Color[] arrowHighlightColorCache = new Color[256];
   private final EspFade espFade = new EspFade(this::finishDisable);
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public PlayerESP() {
      super(
         "PlayerESP",
         "Cyberpunk NanoVG player boxes with glowing brackets and tracers",
         Category.RENDER
      );
      this.showName.visibleWhen(this.playerInfo::get);
      this.showDistance.visibleWhen(this.playerInfo::get);
      this.showItem.visibleWhen(this.playerInfo::get);
      this.arrowRadius.visibleWhen(this.arrow::get);
      this.arrowSize.visibleWhen(this.arrow::get);
      this.arrowColor.visibleWhen(this.arrow::get);
      this.arrowGlow.visibleWhen(this.arrow::get);
      this.arrowFade.visibleWhen(this.arrow::get);
      this.tracerWidth.setVisibleInGui(false);
      this.outlineAlpha.setVisibleInGui(false);
      this.gradientFill.setVisibleInGui(false);
      this.scanline.setVisibleInGui(false);
      this.occlusion.setVisibleInGui(false);
      this.arrowFadeRange.setVisibleInGui(false);
   }

   @Override
   public String getInfo() {
      return this.visible == 0 ? null : String.valueOf(this.visible);
   }

   private void finishDisable() {
      this.visible = 0;
      this.losTimestampMap.clear();
      this.losResultMap.clear();
      super.onDisable();
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.espFade.onEnable();
   }

   @Override
   public void onDisable() {
      this.espFade.onDisable();
   }

   private boolean checkPlayerLineOfSight(class_1657 player, long nowMs) {
      int var4 = player.method_5628();
      long var5 = this.losTimestampMap.get(var4);
      if (nowMs - var5 < 65L) {
         return this.losResultMap.get(var4);
      }

      boolean var7 = WorldProjector.hasLineOfSight(player.method_33571());
      this.losTimestampMap.put(var4, nowMs);
      this.losResultMap.put(var4, var7);
      return var7;
   }

   private Color getArrowBaseColor(int r, int g, int b, int a) {
      int var5 = class_3532.method_15340(a, 0, 255);
      int var6 = r << (16) | g << (8) | b;
      if (this.cachedArrowRgb != var6) {
         this.cachedArrowRgb = var6;

         for (int var7 = 0; var7 < (256); var7++) {
            this.arrowBaseColorCache[var7] = null;
            this.arrowHighlightColorCache[var7] = null;
         }
      }

      Color var8 = this.arrowBaseColorCache[var5];
      if (var8 == null) {
         var8 = new Color(r, g, b, var5);
         this.arrowBaseColorCache[var5] = var8;
      }

      return var8;
   }

   private Color getArrowHighlightColor(int hr, int hg, int hb, int a) {
      int var5 = class_3532.method_15340(a, 0, 255);
      Color var6 = this.arrowHighlightColorCache[var5];
      if (var6 == null) {
         var6 = new Color(hr, hg, hb, var5);
         this.arrowHighlightColorCache[var5] = var6;
      }

      return var6;
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         this.espFade.update();
      }
   }

   @EventHandler
   public void onRender(RenderEvent event) {
      if (event.getStage() == RenderEvent.Stage.WORLD_3D && this.isInGame()) {
         float var2 = this.espFade.getFade();
         if (!(var2 <= 0.001F)) {
            float var3 = event.getTickDelta();
            int var4 = (int)(255.0F * var2);
            int var5 = withAlpha((Integer)this.color.get(), var4);
            boolean var6 = (Boolean)this.tracer.get() && var4 > 0;
            int var7 = 0;

            for (class_1657 var9 : mc.field_1687.method_18456()) {
               if (var9 != mc.field_1724 && var9.method_5628() != (-6969)) {
                  if (var6) {
                     RenderUtil.drawTracer(var9, var5, var3, ((Double)this.tracerWidth.get()).floatValue());
                  }

                  var7++;
               }
            }

            this.visible = var7;
         }
      }
   }

   @EventHandler
   public void onPostBlit(PostBlitRenderEvent event) {
      if (this.espFade.isActive() && this.isInGame()) {
         if (mc.field_1724 != null && mc.field_1687 != null) {
            class_4184 var2 = mc.field_1773.method_19418();
            if (var2 != null) {
               if (this.projector.snapshot()) {
                  float var3 = event.tickDelta;
                  float var4 = this.espFade.getFade();
                  if (!(var4 <= 0.001F)) {
                     try (NanoVGFrame var5 = NanoVGRenderer.frame()) {
                        if (!var5.isActive()) {
                           return;
                        }

                        NanoVGRenderer.globalAlpha(var4);

                        try {
                           if ((Boolean)this.arrow.get()) {
                              this.drawArrows(var2, var3);
                           }

                           this.renderPlayerBoxes(var3);
                        } finally {
                           NanoVGRenderer.globalAlpha(1.0F);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void renderPlayerBoxes(float tickDelta) {
      float[] var2 = this.projScratch;
      int var3 = (Integer)this.color.get();
      int var4 = (Integer)this.outlineAlpha.get();
      int var5 = (Integer)this.alpha.get();
      boolean var6 = (Boolean)this.glow.get();
      boolean var7 = (Boolean)this.healthBar.get();
      boolean var8 = (Boolean)this.playerInfo.get();
      boolean var9 = (Boolean)this.scanline.get();
      boolean var10 = (Boolean)this.occlusion.get();
      PlayerESP.BoxStyle var11 = (PlayerESP.BoxStyle)this.style.get();
      float var12 = 0.82F + 0.18F * (float)Math.sin(System.currentTimeMillis() * 0.0035);
      if (var3 != this.lastBaseArgb || var4 != this.lastOutlineA || var5 != this.lastFillA || var11 != this.lastBoxStyle) {
         this.lastBaseArgb = var3;
         this.lastOutlineA = var4;
         this.lastFillA = var5;
         this.lastBoxStyle = var11;
         this.cachedLosCoreColor = withAlphaColor(var3, var4);
         this.cachedLosFillColor = withAlphaColor(var3, var5);
         int var13 = var3 >> 16 & 0xFF;
         int var14 = var3 >> 8 & 0xFF;
         int var15 = var3 & 0xFF;
         int var16 = (var13 + var14 + var15) / 3;
         this.cachedR = (var13 + var16) / 2;
         this.cachedG = (var14 + var16) / 2;
         this.cachedB = (var15 + var16) / 2;
         this.cachedDimOutline = Math.max(20, (int)(var4 * 0.4F));
         int var17 = Math.max(5, (int)(var5 * 0.4F));
         this.cachedOccCoreColor = new Color(this.cachedR, this.cachedG, this.cachedB, this.cachedDimOutline);
         this.cachedOccFillColor = new Color(this.cachedR, this.cachedG, this.cachedB, var17);
         float var18 = var11 == PlayerESP.BoxStyle.CYBERPUNK ? 0.2F : (var11 == PlayerESP.BoxStyle.CORNERS ? 0.15F : 0.25F);
         this.cachedLosTopFill = withAlphaColor(this.cachedLosCoreColor.getRGB(), (int)(var5 * var18));
         this.cachedLosBottomFill = withAlphaColor(this.cachedLosCoreColor.getRGB(), var5);
         this.cachedOccTopFill = withAlphaColor(this.cachedOccCoreColor.getRGB(), (int)(var17 * var18));
         this.cachedOccBottomFill = this.cachedOccFillColor;
      }

      int var40 = (int)(var4 * 0.42F * var12);
      if (var40 != this.lastBreathA || this.cachedLosHaloColor == null) {
         this.lastBreathA = var40;
         this.cachedLosHaloColor = withAlphaColor(var3, var40);
      }

      int var41 = (int)(this.cachedDimOutline * 0.35F);
      if (var41 != this.lastDimHaloA || this.cachedOccHaloColor == null) {
         this.lastDimHaloA = var41;
         this.cachedOccHaloColor = new Color(this.cachedR, this.cachedG, this.cachedB, var41);
      }

      Color var42 = this.cachedLosCoreColor;
      Color var43 = this.cachedLosHaloColor;
      Color var44 = this.cachedLosFillColor;
      Color var45 = this.cachedOccCoreColor;
      Color var19 = this.cachedOccHaloColor;
      Color var20 = this.cachedOccFillColor;
      Color var21 = this.cachedLosTopFill;
      Color var22 = this.cachedLosBottomFill;
      Color var23 = this.cachedOccTopFill;
      Color var24 = this.cachedOccBottomFill;
      long var25 = System.currentTimeMillis();

      for (class_1657 var28 : mc.field_1687.method_18456()) {
         if (var28 != mc.field_1724 && var28.method_5628() != -6969) {
            class_238 var29 = RenderUtil.getInterpolatedBox(var28, tickDelta);
            boolean var30 = !var10 || this.checkPlayerLineOfSight(var28, var25);
            Color var31 = var30 ? var42 : var45;
            Color var32 = var30 ? var43 : var19;
            Color var33 = var30 ? var44 : var20;
            if (var11 == PlayerESP.BoxStyle.BOX_3D) {
               this.render3DBox(var29, var31, var33, var28, var7, var8, var6);
            } else if (this.projector.projectBox(var29, this.boxBoundsScratch)) {
               float var34 = this.boxBoundsScratch[0];
               float var35 = this.boxBoundsScratch[1];
               float var36 = this.boxBoundsScratch[2];
               float var37 = this.boxBoundsScratch[3];
               if (!(var36 < 2.0F) && !(var37 < 2.0F) && this.projector.onScreen(var34 + var36 * 0.5F, var35 + var37 * 0.5F, Math.max(var36, var37) + 32.0F)) {
                  Color var38 = var30 ? var21 : var23;
                  Color var39 = var30 ? var22 : var24;
                  if (var11 == PlayerESP.BoxStyle.CYBERPUNK) {
                     if ((Boolean)this.gradientFill.get() && var5 > 0) {
                        NanoVGRenderer.drawVerticalGradientRoundedRect(var34, var35, var36, var37, 2.0F, var38, var39);
                     }

                     this.drawCornerBrackets(var34, var35, var36, var37, var31, var32, var6);
                     this.drawReticleTicks(var34, var35, var36, var37, var31);
                     if (var9) {
                        this.drawScanline(var34, var35, var36, var37, var31);
                     }
                  } else if (var11 == PlayerESP.BoxStyle.CORNERS) {
                     if ((Boolean)this.gradientFill.get() && var5 > 0) {
                        NanoVGRenderer.drawVerticalGradientRoundedRect(var34, var35, var36, var37, 2.0F, var38, var39);
                     }

                     this.drawCornerBrackets(var34, var35, var36, var37, var31, var32, var6);
                  } else if (var11 == PlayerESP.BoxStyle.BOX_2D) {
                     if (var6) {
                        NanoVGRenderer.drawRoundedRectWithShadow(var34, var35, var36, var37, 3.0F, TRANSPARENT_COLOR, var32, 6.0F, 1.0F);
                     }

                     if ((Boolean)this.gradientFill.get() && var5 > 0) {
                        NanoVGRenderer.drawVerticalGradientRoundedRect(var34, var35, var36, var37, 3.0F, var38, var39);
                     }

                     NanoVGRenderer.drawRoundedRectOutline(var34, var35, var36, var37, 3.0F, 1.5F, var31);
                     if (var9) {
                        this.drawScanline(var34, var35, var36, var37, var31);
                     }
                  }

                  if (var7) {
                     this.drawHealthBar(var34, var35, var37, var28, var6);
                  }

                  if (var8) {
                     this.drawPlayerInfo(var34, var35, var36, var37, var28, var31);
                  }
               }
            }
         }
      }
   }

   private void render3DBox(class_238 box, Color coreColor, Color fillColor, class_1657 player, boolean drawHealth, boolean drawInfo, boolean drawGlow) {
      this.rawCornersScratch[0][0] = box.field_1323;
      this.rawCornersScratch[0][1] = box.field_1322;
      this.rawCornersScratch[0][2] = box.field_1321;
      this.rawCornersScratch[1][0] = box.field_1320;
      this.rawCornersScratch[1][1] = box.field_1322;
      this.rawCornersScratch[1][2] = box.field_1321;
      this.rawCornersScratch[2][0] = box.field_1320;
      this.rawCornersScratch[2][1] = box.field_1322;
      this.rawCornersScratch[2][2] = box.field_1324;
      this.rawCornersScratch[3][0] = box.field_1323;
      this.rawCornersScratch[3][1] = box.field_1322;
      this.rawCornersScratch[3][2] = box.field_1324;
      this.rawCornersScratch[4][0] = box.field_1323;
      this.rawCornersScratch[4][1] = box.field_1325;
      this.rawCornersScratch[4][2] = box.field_1321;
      this.rawCornersScratch[5][0] = box.field_1320;
      this.rawCornersScratch[5][1] = box.field_1325;
      this.rawCornersScratch[5][2] = box.field_1321;
      this.rawCornersScratch[6][0] = box.field_1320;
      this.rawCornersScratch[6][1] = box.field_1325;
      this.rawCornersScratch[6][2] = box.field_1324;
      this.rawCornersScratch[7][0] = box.field_1323;
      this.rawCornersScratch[7][1] = box.field_1325;
      this.rawCornersScratch[7][2] = box.field_1324;
      float[] var8 = this.projScratch;
      boolean var9 = false;
      float var10 = Float.MAX_VALUE;
      float var11 = -Float.MAX_VALUE;
      float var12 = Float.MAX_VALUE;
      float var13 = -Float.MAX_VALUE;

      for (int var14 = 0; var14 < 8; var14++) {
         if (this.projector.project(this.rawCornersScratch[var14][0], this.rawCornersScratch[var14][1], this.rawCornersScratch[var14][2], var8)) {
            this.ptsCoordsScratch[var14][0] = var8[0];
            this.ptsCoordsScratch[var14][1] = var8[1];
            this.ptsScratch[var14] = this.ptsCoordsScratch[var14];
            var10 = Math.min(var10, var8[0]);
            var11 = Math.max(var11, var8[0]);
            var12 = Math.min(var12, var8[1]);
            var13 = Math.max(var13, var8[1]);
            var9 = true;
         } else {
            this.ptsScratch[var14] = null;
         }
      }

      if (var9) {
         if ((Boolean)this.gradientFill.get() && fillColor.getAlpha() > 0) {
            NanoVGRenderer.drawEspFill(this.ptsScratch, FACES, 1.0, fillColor);
         }

         NanoVGRenderer.drawEspBox(this.ptsScratch, EDGES, 1.0, 1.5F, coreColor);
         float var18 = var10;
         float var15 = var12;
         float var16 = var11 - var10;
         float var17 = var13 - var12;
         if (var16 >= 4.0F && var17 >= 4.0F) {
            if (drawHealth) {
               this.drawHealthBar(var18, var15, var17, player, drawGlow);
            }

            if (drawInfo) {
               this.drawPlayerInfo(var18, var15, var16, var17, player, coreColor);
            }
         }
      }
   }

   private void drawCornerBrackets(float x, float y, float w, float h, Color coreColor, Color haloColor, boolean drawGlow) {
      float var8 = Math.max(5.0F, Math.min(Math.min(w * 0.26F, h * 0.2F), 22.0F));
      if (drawGlow) {
         NanoVGRenderer.beginLineBatch(haloColor, 3.8F);
         NanoVGRenderer.batchLine(x, y, x + var8, y);
         NanoVGRenderer.batchLine(x, y, x, y + var8);
         NanoVGRenderer.batchLine(x + w, y, x + w - var8, y);
         NanoVGRenderer.batchLine(x + w, y, x + w, y + var8);
         NanoVGRenderer.batchLine(x, y + h, x + var8, y + h);
         NanoVGRenderer.batchLine(x, y + h, x, y + h - var8);
         NanoVGRenderer.batchLine(x + w, y + h, x + w - var8, y + h);
         NanoVGRenderer.batchLine(x + w, y + h, x + w, y + h - var8);
         NanoVGRenderer.endLineBatch();
      }

      NanoVGRenderer.beginLineBatch(coreColor, 1.5F);
      NanoVGRenderer.batchLine(x, y, x + var8, y);
      NanoVGRenderer.batchLine(x, y, x, y + var8);
      NanoVGRenderer.batchLine(x + w, y, x + w - var8, y);
      NanoVGRenderer.batchLine(x + w, y, x + w, y + var8);
      NanoVGRenderer.batchLine(x, y + h, x + var8, y + h);
      NanoVGRenderer.batchLine(x, y + h, x, y + h - var8);
      NanoVGRenderer.batchLine(x + w, y + h, x + w - var8, y + h);
      NanoVGRenderer.batchLine(x + w, y + h, x + w, y + h - var8);
      NanoVGRenderer.endLineBatch();
   }

   private void drawReticleTicks(float x, float y, float w, float h, Color color) {
      float var6 = Math.min(Math.min(w, h) * 0.08F, 5.0F);
      if (!(var6 < 2.0F)) {
         NanoVGRenderer.beginLineBatch(colorx, 1.2F);
         NanoVGRenderer.batchLine(x + w * 0.5F - var6, y, x + w * 0.5F + var6, y);
         NanoVGRenderer.batchLine(x + w * 0.5F - var6, y + h, x + w * 0.5F + var6, y + h);
         NanoVGRenderer.batchLine(x, y + h * 0.5F - var6, x, y + h * 0.5F + var6);
         NanoVGRenderer.batchLine(x + w, y + h * 0.5F - var6, x + w, y + h * 0.5F + var6);
         NanoVGRenderer.endLineBatch();
      }
   }

   private void drawScanline(float x, float y, float w, float h, Color color) {
      long var6 = System.currentTimeMillis();
      float var8 = (float)(var6 % 2000L) / 2000.0F;
      float var9 = y + h * var8;
      float var10 = (float)Math.sin(var8 * Math.PI);
      int var11 = (int)(colorx.getAlpha() * 0.45F * var10);
      if (var11 > (5)) {
         int var12 = var11 << (24) | colorx.getRGB() & (16777215);
         NanoVGRenderer.drawLine(x + 2.0F, var9, x + w - 2.0F, var9, 1.2F, var12);
      }
   }

   private void drawHealthBar(float x, float y, float h, class_1657 player, boolean drawGlow) {
      float var6 = 3.0F;
      float var7 = 4.0F;
      float var8 = x - var6 - var7;
      float var9 = y;
      float var10 = h;
      float var11 = player.method_6063();
      float var12 = class_3532.method_15363(player.method_6032(), 0.0F, var11);
      float var13 = var12 / Math.max(1.0F, var11);
      float var14 = var10 * var13;
      float var15 = var9 + (var10 - var14);
      NanoVGRenderer.drawRoundedRect(var8 - 0.5F, var9 - 0.5F, var6 + 1.0F, var10 + 1.0F, 1.5F, -1274410481);
      int var16;
      int var17;
      if (var13 > 0.6F) {
         var16 = -13315175;
         var17 = -15681151;
      } else if (var13 > 0.25F) {
         var16 = -278748;
         var17 = -680437;
      } else {
         var16 = -495247;
         var17 = -2349530;
      }

      if (var14 > 0.5F) {
         NanoVGRenderer.drawVerticalGradientRoundedRect(var8, var15, var6, var14, 1.0F, var16, var17);
      }

      if (drawGlow && var14 > 1.0F) {
         int var18 = 2013265920 | var16 & (16777215);
         NanoVGRenderer.drawCircle(var8 + var6 * 0.5F, var15, 2.2F, var18);
      }

      if (var12 < var11 && var12 > 0.0F) {
         String var21 = String.valueOf(Math.round(var12));
         float var19 = 7.0F;
         float var20 = NanoVGRenderer.getTextWidth(var21, var19);
         NanoVGRenderer.drawTextWithShadow(var21, var8 - var20 - 2.5F, var15 - 3.0F, var19, var16);
      }
   }

   private void drawPlayerInfo(float x, float y, float w, float h, class_1657 player, Color accentColor) {
      boolean var7 = (Boolean)this.showName.get();
      boolean var8 = (Boolean)this.showDistance.get();
      boolean var9 = (Boolean)this.showItem.get();
      if (var7 || var8 || var9) {
         String var10 = player.method_5477().getString();
         int var11 = mc.field_1724 != null ? Math.round(mc.field_1724.method_5739(player)) : 0;
         String var12 = var11 + "m";
         float var13 = 8.0F;
         float var14 = var7 ? NanoVGRenderer.getTextWidth(var10, var13) : 0.0F;
         float var15 = var8 ? NanoVGRenderer.getTextWidth(var12, var13) : 0.0F;
         float var16 = var7 && var8 ? 4.0F : 0.0F;
         float var17 = var14 + var16 + var15;
         float var18 = NanoVGRenderer.getTextHeight(var13);
         if (var7 || var8) {
            float var19 = 4.0F;
            float var20 = 2.0F;
            float var21 = var17 + var19 * 2.0F;
            float var22 = var18 + var20 * 2.0F;
            float var23 = x + (w - var21) * 0.5F;
            float var24 = y - var22 - 4.0F;
            NanoVGRenderer.drawRoundedRect(var23, var24, var21, var22, 3.0F, -938866160);
            int var25 = 1677721600 | accentColor.getRGB() & 16777215;
            NanoVGRenderer.drawRoundedRectOutline(var23, var24, var21, var22, 3.0F, 0.8F, var25);
            float var26 = var23 + var19;
            if (var7) {
               NanoVGRenderer.drawTextWithShadow(var10, var26, var24 + var20, var13, -1);
               var26 += var14 + var16;
            }

            if (var8) {
               NanoVGRenderer.drawTextWithShadow(var12, var26, var24 + var20, var13, -592463411);
            }
         }

         if (var9) {
            class_1799 var28 = player.method_6047();
            if (var28 != null && !var28.method_7960()) {
               String var29 = var28.method_7964().getString();
               float var30 = 7.0F;
               float var31 = NanoVGRenderer.getTextWidth(var29, var30);
               float var32 = NanoVGRenderer.getTextHeight(var30);
               float var33 = var31 + 8.0F;
               float var34 = var32 + 3.0F;
               float var35 = x + (w - var33) * 0.5F;
               float var27 = y + h + 4.0F;
               NanoVGRenderer.drawRoundedRect(var35, var27, var33, var34, 2.5F, -1106638320);
               NanoVGRenderer.drawTextWithShadow(var29, var35 + 4.0F, var27 + 1.5F, var30, -590160661);
            }
         }
      }
   }

   private void drawArrows(class_4184 camera, float tickDelta) {
      float var3 = camera.method_19330();
      class_243 var4 = camera.method_71156();
      float var5 = mc.method_22683().method_4486() / 2.0F;
      float var6 = mc.method_22683().method_4502() / 2.0F;
      float var7 = ((Double)this.arrowRadius.get()).floatValue();
      float var8 = ((Double)this.arrowSize.get()).floatValue();
      float var9 = var8 * 0.62F;
      int var10 = (Integer)this.arrowColor.get();
      int var11 = var10 >>> 24 & 0xFF;
      boolean var12 = (Boolean)this.arrowFade.get();
      double var13 = (Double)this.arrowFadeRange.get();
      boolean var15 = (Boolean)this.arrowGlow.get();
      float var16 = 0.82F + 0.18F * (float)Math.sin(System.currentTimeMillis() * 0.005);
      int var17 = var10 >> 16 & 0xFF;
      int var18 = var10 >> 8 & 0xFF;
      int var19 = var10 & 0xFF;
      int var20 = Math.min(255, var17 + 45);
      int var21 = Math.min(255, var18 + 45);
      int var22 = Math.min(255, var19 + 45);

      for (class_1657 var24 : mc.field_1687.method_18456()) {
         if (var24 != mc.field_1724 && var24.method_5628() != -6969) {
            class_243 var25 = var24.method_30950(tickDelta);
            double var26 = var25.field_1352 - var4.field_1352;
            double var28 = var25.field_1350 - var4.field_1350;
            double var30 = var26 * var26 + var28 * var28;
            if (!(var30 < 1.0E-4)) {
               float var32 = (float)Math.toDegrees(Math.atan2(var28, var26)) - 90.0F;
               float var33 = class_3532.method_15393(var32 - var3);
               double var34 = Math.toRadians(var33);
               float var36 = (float)Math.sin(var34);
               float var37 = (float)(-Math.cos(var34));
               float var38 = -var37;
               float var39 = var36;
               int var40 = var11;
               if (var12) {
                  double var41 = Math.sqrt(var30);
                  double var43 = 1.0 - 0.75 * Math.min(1.0, var41 / var13);
                  var40 = (int)Math.round(var11 * var43);
               }

               if (var40 >= 6) {
                  float var55 = var5 + var36 * var7;
                  float var42 = var6 + var37 * var7;
                  float var56 = var55 + var36 * var8;
                  float var44 = var42 + var37 * var8;
                  float var45 = var55 + var38 * var9;
                  float var46 = var42 + var39 * var9;
                  float var47 = var55 - var38 * var9;
                  float var48 = var42 - var39 * var9;
                  if (var15 && var40 > 8) {
                     float var49 = (var56 + 2.0F * var55) / 3.0F;
                     float var50 = (var44 + 2.0F * var42) / 3.0F;
                     float var51 = var8 * 2.2F;
                     Color var52 = this.getArrowBaseColor(var17, var18, var19, (int)(var40 * 0.4F * var16));
                     NanoVGRenderer.drawRadialGlow(var49, var50, var51, var52);
                     Color var53 = this.getArrowBaseColor(var17, var18, var19, (int)(var40 * 0.22F * var16));
                     NanoVGRenderer.drawTriangleOutline(var56, var44, var45, var46, var47, var48, 5.5F, var53);
                     Color var54 = this.getArrowBaseColor(var17, var18, var19, (int)(var40 * 0.45F));
                     NanoVGRenderer.drawTriangleOutline(var56, var44, var45, var46, var47, var48, 2.8F, var54);
                  }

                  NanoVGRenderer.drawTriangle(var56, var44, var45, var46, var47, var48, this.getArrowBaseColor(var17, var18, var19, var40));
                  if (var15 && var40 > 12) {
                     Color var57 = this.getArrowHighlightColor(var20, var21, var22, (int)(var40 * 0.85F));
                     NanoVGRenderer.drawTriangleOutline(var56, var44, var45, var46, var47, var48, 1.2F, var57);
                  }
               }
            }
         }
      }
   }

   private static Color withAlphaColor(int argb, int a) {
      return new Color(
         argb >> (16) & (255),
         argb >> (8) & (255),
         argb & (255),
         class_3532.method_15340(a, 0, 255)
      );
   }

   public boolean hasTracer() {
      return (Boolean)this.tracer.get();
   }

   public double getTracerWidth() {
      return (Double)this.tracerWidth.get();
   }

   private static int withAlpha(int argb, int a) {
      return class_3532.method_15340(a, 0, 255) << (24) | argb & (16777215);
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

   public enum BoxStyle {
      CYBERPUNK,
      CORNERS,
      BOX_2D,
      BOX_3D;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static PlayerESP.BoxStyle[] $values() {
         PlayerESP.BoxStyle[] var10000 = new PlayerESP.BoxStyle[4];
         var10000[0] = CYBERPUNK;
         var10000[1] = CORNERS;
         var10000[2] = BOX_2D;
         var10000[3] = BOX_3D;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[9];
         var10002[0] = (byte)(124);
         var10002[1] = (byte)(-61);
         var10002[2] = (byte)(94);
         var10002[3] = (byte)(-101);
         var10002[4] = (byte)(-8);
         var10002[5] = (byte)(-85);
         var10002[6] = (byte)(106);
         var10002[7] = (byte)(45);
         var10002[8] = (byte)(28);
         CYBERPUNK = new PlayerESP.BoxStyle();
         var10002 = new byte[7];
         var10002[0] = (byte)(124);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-112);
         var10002[4] = (byte)(-17);
         var10002[5] = (byte)(-87);
         var10002[6] = (byte)(108);
         CORNERS = new PlayerESP.BoxStyle();
         var10002 = new byte[6];
         var10002[0] = (byte)(125);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(68);
         var10002[3] = (byte)(-127);
         var10002[4] = (byte)(-104);
         var10002[5] = (byte)(-65);
         BOX_2D = new PlayerESP.BoxStyle();
         var10002 = new byte[6];
         var10002[0] = (byte)(125);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(68);
         var10002[3] = (byte)(-127);
         var10002[4] = (byte)(-103);
         var10002[5] = (byte)(-65);
         BOX_3D = new PlayerESP.BoxStyle();
      }

      // $VF: synthetic method
      private static String $$v_s(byte[] var0, long var1) {
         byte[] var3 = new byte[var0.length];

         for (int var4 = 0; var4 < var0.length; var4++) {
            var3[var4] = (byte)(
               var0[var4]
                  ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                  ^ (var4 + 1) * (37)
            );
         }

         return new String(var3, StandardCharsets.UTF_8);
      }
   }
}
