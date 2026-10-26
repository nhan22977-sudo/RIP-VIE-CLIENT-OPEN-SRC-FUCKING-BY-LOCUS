package me.drake.vieclient.modules.client;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.GameJoinEvent;
import me.drake.vieclient.core.event.events.ReceivePacketEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.StringSetting;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_266;
import net.minecraft.class_268;
import net.minecraft.class_269;
import net.minecraft.class_2724;
import net.minecraft.class_2736;
import net.minecraft.class_274;
import net.minecraft.class_5250;
import net.minecraft.class_5251;
import net.minecraft.class_8646;
import net.minecraft.class_9015;
import net.minecraft.class_9020;
import net.minecraft.class_274.class_275;

public class FakeStats extends Module {
   private static final String OBJECTIVE_NAME = "vie_fakestats";
   private static final String TEAM_PREFIX = "vie_fakestats_team_";
   private static final long UPDATE_INTERVAL_MS = 1000L;
   private final BoolSetting sidebar = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Sidebar",
         "Inject the fake objective into the sidebar display slot.",
         true
      )
   );
   private final StringSetting title = (StringSetting)this.addSetting(
      new StringSetting(
         "Title",
         "Scoreboard title (empty = use NameProtect name)",
         ""
      )
   );
   private final BoolSetting showMoney = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Show Money",
         "Display money line on scoreboard",
         true
      )
   );
   private final StringSetting lineMoney = (StringSetting)this.addSetting(
      new StringSetting(
         "Money",
         "Value after the $$ money icon",
         "Vie"
      )
   );
   private final BoolSetting showShard = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Show Shard",
         "Display shard line on scoreboard",
         true
      )
   );
   private final StringSetting lineShard = (StringSetting)this.addSetting(
      new StringSetting(
         "Shard",
         "Value after the purple star shard icon",
         "Client"
      )
   );
   private final BoolSetting showKills = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Show Kills",
         "Display kills line on scoreboard",
         true
      )
   );
   private final StringSetting lineKills = (StringSetting)this.addSetting(
      new StringSetting(
         "Kills",
         "Value after the red sword kills icon",
         "Is"
      )
   );
   private final BoolSetting showDeaths = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Show Deaths",
         "Display deaths line on scoreboard",
         true
      )
   );
   private final StringSetting lineDeaths = (StringSetting)this.addSetting(
      new StringSetting(
         "Deaths",
         "Value after the orange skull deaths icon",
         "On"
      )
   );
   private final BoolSetting showPlaytime = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Show Playtime",
         "Display playtime line on scoreboard",
         true
      )
   );
   private final StringSetting linePlaytime = (StringSetting)this.addSetting(
      new StringSetting(
         "Playtime",
         "Value after the gold clock playtime icon",
         "Top"
      )
   );
   private long lastUpdateMs = 0L;
   private class_269 currentScoreboard;
   private class_266 customObjective;
   private class_266 originalObjective;
   private String lastServerObjectiveName;
   private final List teamHandles = new ArrayList();
   private static FakeStats INSTANCE;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public FakeStats() {
      super(
         "FakeStats",
         "Client-side sidebar spoof: injects a fake scoreboard objective.",
         Category.PROTECT
      );
      INSTANCE = this;
      this.lineMoney.visibleWhen(this.showMoney::get);
      this.lineShard.visibleWhen(this.showShard::get);
      this.lineKills.visibleWhen(this.showKills::get);
      this.lineDeaths.visibleWhen(this.showDeaths::get);
      this.linePlaytime.visibleWhen(this.showPlaytime::get);
      this.showMoney.onChanged(v -> {
         if (this.isEnabled()) {
            this.rebuildObjective();
         }
      });
      this.showShard.onChanged(v -> {
         if (this.isEnabled()) {
            this.rebuildObjective();
         }
      });
      this.showKills.onChanged(v -> {
         if (this.isEnabled()) {
            this.rebuildObjective();
         }
      });
      this.showDeaths.onChanged(v -> {
         if (this.isEnabled()) {
            this.rebuildObjective();
         }
      });
      this.showPlaytime.onChanged(v -> {
         if (this.isEnabled()) {
            this.rebuildObjective();
         }
      });
      this.lineMoney.onChanged(v -> {
         if (this.isEnabled()) {
            this.rebuildObjective();
         }
      });
      this.lineShard.onChanged(v -> {
         if (this.isEnabled()) {
            this.rebuildObjective();
         }
      });
      this.lineKills.onChanged(v -> {
         if (this.isEnabled()) {
            this.rebuildObjective();
         }
      });
      this.lineDeaths.onChanged(v -> {
         if (this.isEnabled()) {
            this.rebuildObjective();
         }
      });
      this.linePlaytime.onChanged(v -> {
         if (this.isEnabled()) {
            this.rebuildObjective();
         }
      });
   }

   public static boolean isActive() {
      FakeStats var0 = INSTANCE;
      return var0 != null && var0.isEnabled() && (Boolean)var0.sidebar.get();
   }

   public static class_266 getCustomObjective() {
      FakeStats var0 = INSTANCE;
      return var0 != null ? var0.customObjective : null;
   }

   public static void ensureObjectiveReady() {
      FakeStats var0 = INSTANCE;
      if (var0 != null && var0.isEnabled() && (Boolean)var0.sidebar.get()) {
         if (mc != null && mc.field_1687 != null && mc.field_1724 != null) {
            class_269 var1 = mc.field_1687.method_8428();
            if (var1 != null) {
               if (var1 == var0.currentScoreboard && var0.customObjective != null && var0.customObjective.method_1117() == var1) {
                  byte[] var10001 = new byte[13];
                  var10001[0] = (byte)(96);
                  var10001[1] = (byte)(-13);
                  var10001[2] = (byte)(121);
                  var10001[3] = (byte)(-127);
                  var10001[4] = (byte)(90);
                  var10001[5] = (byte)(72);
                  var10001[6] = (byte)(-76);
                  var10001[7] = (byte)(84);
                  var10001[8] = (byte)(13);
                  var10001[9] = (byte)(-42);
                  var10001[10] = (byte)(-123);
                  var10001[11] = (byte)(-126);
                  var10001[12] = (byte)(23);
                  if (var1.method_1170($$v_s(var10001, 1863636497676357683L)) == var0.customObjective) {
                     if (var1.method_1189(class_8646.field_45157) != var0.customObjective) {
                        var1.method_1158(class_8646.field_45157, var0.customObjective);
                     }

                     for (class_8646 var5 : class_8646.values()) {
                        if (var5 != class_8646.field_45157
                           && var5 != class_8646.field_45156
                           && var5 != class_8646.field_45158
                           && var1.method_1189(var5) != null) {
                           var1.method_1158(var5, null);
                        }
                     }

                     return;
                  }
               }

               var0.rebuildObjective();
            }
         }
      }
   }

   public static void requestRebuild() {
      FakeStats var0 = INSTANCE;
      if (var0 != null && var0.isEnabled()) {
         var0.lastUpdateMs = 0L;
         if (mc != null && mc.field_1687 != null && mc.field_1724 != null) {
            var0.rebuildObjective();
         }
      }
   }

   @Override
   public String getInfo() {
      return null;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (mc != null && mc.field_1687 != null && mc.field_1724 != null) {
         class_269 var1 = mc.field_1687.method_8428();
         this.currentScoreboard = var1;
         this.originalObjective = var1.method_1189(class_8646.field_45157);
         if (this.originalObjective != null) {
            this.lastServerObjectiveName = this.originalObjective.method_1113();
         }

         this.rebuildObjective();
      }
   }

   @Override
   public void onDisable() {
      this.restoreObjective();
      this.originalObjective = null;
      this.lastServerObjectiveName = null;
      this.currentScoreboard = null;
      super.onDisable();
   }

   @EventHandler
   public void onReceivePacket(ReceivePacketEvent event) {
      if (this.isEnabled() && (Boolean)this.sidebar.get()) {
         if (event.getPacket() instanceof class_2736 var2) {
            class_8646 var5 = var2.method_11806();
            if (var5 == null) {
               return;
            }

            if (var5 != class_8646.field_45156 && var5 != class_8646.field_45158) {
               String var4 = var2.method_11804();
               if (var5 == class_8646.field_45157 && var4 != null && !var4.isEmpty()) {
                  this.lastServerObjectiveName = var4;
               }

               event.cancel();
               if (mc != null) {
                  mc.execute(FakeStats::ensureObjectiveReady);
               }
            }
         } else if (event.getPacket() instanceof class_2724 && mc != null) {
            mc.execute(() -> {
               this.currentScoreboard = null;
               this.customObjective = null;
               this.teamHandles.clear();
               this.lastUpdateMs = 0L;
               ensureObjectiveReady();
            });
         }
      }
   }

   @EventHandler
   public void onGameJoin(GameJoinEvent event) {
      this.currentScoreboard = null;
      this.customObjective = null;
      this.teamHandles.clear();
      this.lastUpdateMs = 0L;
      if (this.isEnabled() && (Boolean)this.sidebar.get() && mc != null) {
         mc.execute(this::rebuildObjective);
      }
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         if (this.isEnabled()) {
            if (!(Boolean)this.sidebar.get()) {
               if (this.customObjective != null) {
                  this.restoreObjective();
               }
            } else if (mc != null && mc.field_1687 != null && mc.field_1724 != null) {
               ensureObjectiveReady();
               long var2 = System.currentTimeMillis();
               if (var2 - this.lastUpdateMs >= 1000L) {
                  this.rebuildObjective();
                  this.lastUpdateMs = var2;
               }
            }
         }
      }
   }

   private void rebuildObjective() {
      if ((Boolean)this.sidebar.get()) {
         if (mc != null && mc.field_1687 != null && mc.field_1724 != null) {
            class_269 var1 = mc.field_1687.method_8428();
            if (var1 != null) {
               if (var1 != this.currentScoreboard) {
                  this.currentScoreboard = var1;
                  this.teamHandles.clear();
                  this.customObjective = null;
               }

               class_266 var2 = var1.method_1170("vie_fakestats");
               class_5250 var3 = this.buildTitleText();
               if (var2 == null) {
                  var2 = var1.method_1168(
                     "vie_fakestats",
                     class_274.field_1468,
                     var3,
                     class_275.field_1472,
                     false,
                     class_9020.field_47557
                  );
               } else {
                  var2.method_1121(var3);
               }

               this.customObjective = var2;
               var1.method_1158(class_8646.field_45157, this.customObjective);

               for (class_8646 var7 : class_8646.values()) {
                  if (var7 != class_8646.field_45157 && var7 != class_8646.field_45156 && var7 != class_8646.field_45158 && var1.method_1189(var7) != null) {
                     var1.method_1158(var7, null);
                  }
               }

               List var11 = this.generateEntries();

               for (int var12 = 0; var12 < var11.size(); var12++) {
                  String var14 = "vie_fakestats_team_" + var12;
                  if (!this.teamHandles.contains(var14)) {
                     this.teamHandles.add(var14);
                  }

                  class_268 var16 = var1.method_1153(var14);
                  if (var16 == null) {
                     var16 = var1.method_1171(var14);
                  }

                  var16.method_1138((class_2561)var11.get(var12));
                  String var8 = "§" + Integer.toHexString(var12);
                  class_9015 var9 = class_9015.method_55422(var8);
                  var1.method_1180(var9, this.customObjective).method_55410(var11.size() - var12);
                  var1.method_1172(var8, var16);
               }

               for (int var13 = var11.size(); var13 < 16; var13++) {
                  String var15 = "§" + Integer.toHexString(var13);
                  class_9015 var17 = class_9015.method_55422(var15);

                  try {
                     var1.method_1155(var17, this.customObjective);
                     String var18 = "vie_fakestats_team_" + var13;
                     class_268 var19 = var1.method_1153(var18);
                     if (var19 != null) {
                        var1.method_1157(var15, var19);
                        var1.method_1191(var19);
                     }
                  } catch (Exception var10) {
                  }
               }

               while (this.teamHandles.size() > var11.size()) {
                  this.teamHandles.remove(this.teamHandles.size() - 1);
               }
            }
         }
      }
   }

   private void cleanupTeams(class_269 sb) {
      if (sb != null) {
         for (String var3 : this.teamHandles) {
            try {
               class_268 var4 = sb.method_1153(var3);
               if (var4 != null) {
                  sb.method_1191(var4);
               }
            } catch (Exception var5) {
            }
         }

         this.teamHandles.clear();
      }
   }

   private void restoreObjective() {
      if (mc != null && mc.field_1687 != null) {
         try {
            class_269 var1 = mc.field_1687.method_8428();
            if (var1 != null) {
               this.cleanupTeams(var1);
               byte[] var10001 = new byte[13];
               var10001[0] = (byte)(96);
               var10001[1] = (byte)(-13);
               var10001[2] = (byte)(121);
               var10001[3] = (byte)(-127);
               var10001[4] = (byte)(90);
               var10001[5] = (byte)(72);
               var10001[6] = (byte)(-76);
               var10001[7] = (byte)(84);
               var10001[8] = (byte)(13);
               var10001[9] = (byte)(-42);
               var10001[10] = (byte)(-123);
               var10001[11] = (byte)(-126);
               var10001[12] = (byte)(23);
               class_266 var2 = var1.method_1170($$v_s(var10001, 1863636497676357683L));
               if (var2 != null) {
                  try {
                     var1.method_1194(var2);
                  } catch (Exception var4) {
                  }
               }

               this.customObjective = null;
               class_266 var3 = null;
               if (this.lastServerObjectiveName != null && !this.lastServerObjectiveName.isEmpty()) {
                  var3 = var1.method_1170(this.lastServerObjectiveName);
               }

               if (var3 == null && this.originalObjective != null && this.originalObjective.method_1117() == var1) {
                  var3 = this.originalObjective;
               }

               var1.method_1158(class_8646.field_45157, var3);
            }
         } catch (Exception var5) {
         }
      }
   }

   private List generateEntries() {
      ArrayList var1 = new ArrayList();
      if ((Boolean)this.showMoney.get()) {
         byte[] var10001 = new byte[2];
         var10001[0] = (byte)(50);
         var10001[1] = (byte)(-70);
         var1.add(
            colored($$v_s(var10001, 1863636497676357683L), 64512).method_10852(colored(safe((String)this.lineMoney.get()), 16777215))
         );
      }

      if ((Boolean)this.showShard.get()) {
         byte[] var2 = new byte[4];
         var2[0] = (byte)(-12);
         var2[1] = (byte)(2);
         var2[2] = (byte)(-103);
         var2[3] = (byte)(-2);
         var1.add(colored($$v_s(var2, 1863636497676357683L), 10683385).method_10852(colored(safe((String)this.lineShard.get()), 16777215)));
      }

      if ((Boolean)this.showKills.get()) {
         byte[] var3 = new byte[5];
         var3[0] = (byte)(-26);
         var3[1] = (byte)(5);
         var3[2] = (byte)(-117);
         var3[3] = (byte)(127);
         var3[4] = (byte)(28);
         var1.add(colored($$v_s(var3, 1863636497676357683L), 16515072).method_10852(colored(safe((String)this.lineKills.get()), 16777215)));
      }

      if ((Boolean)this.showDeaths.get()) {
         byte[] var4 = new byte[4];
         var4[0] = (byte)(-12);
         var4[1] = (byte)(2);
         var4[2] = (byte)(-68);
         var4[3] = (byte)(-2);
         var1.add(colored($$v_s(var4, 1863636497676357683L), 16348675).method_10852(colored(safe((String)this.lineDeaths.get()), 16777215)));
      }

      if ((Boolean)this.showPlaytime.get()) {
         byte[] var5 = new byte[4];
         var5[0] = (byte)(-12);
         var5[1] = (byte)(22);
         var5[2] = (byte)(-122);
         var5[3] = (byte)(-2);
         var1.add(
            colored($$v_s(var5, 1863636497676357683L), 16573184).method_10852(colored(safe((String)this.linePlaytime.get()), 16777215))
         );
      }

      return var1;
   }

   private static String safe(String s) {
      return s == null ? "" : s.trim();
   }

   public String resolveTitle() {
      String var1 = safe((String)this.title.get());
      if (!var1.isEmpty()) {
         byte[] var10000 = new byte[7];
         var10000[0] = (byte)(93);
         var10000[1] = (byte)(-13);
         var10000[2] = (byte)(114);
         var10000[3] = (byte)(-71);
         var10000[4] = (byte)(111);
         var10000[5] = (byte)(100);
         var10000[6] = (byte)(-113);
         if (!$$v_s(var10000, 1863636497676357683L).equalsIgnoreCase(var1)) {
            return var1;
         }
      }

      String var6;
      if (mc != null && mc.field_1724 != null) {
         var6 = mc.field_1724.method_5477().getString();
      } else {
         byte[] var5 = new byte[3];
         var5[0] = (byte)(64);
         var5[1] = (byte)(-13);
         var5[2] = (byte)(121);
         var6 = $$v_s(var5, 1863636497676357683L);
      }

      String var2 = var6;
      String var3 = NameProtect.staticMaskSelfName(var2);
      if (FakeRoles.isActive()) {
         if (FakeRoles.isMediaRole()) {
            return var3 != null ? FakeRoles.stripMediaPrefix(var3) : "";
         }

         String var4 = FakeRoles.getEffectivePrefix();
         if (var4 != null && !var4.isBlank()) {
            if (var3 != null && !var3.isBlank()) {
               return var4.trim() + " " + var3;
            }

            return var4.trim();
         }
      }

      if (var3 != null) {
         var6 = var3;
      } else {
         byte[] var8 = new byte[3];
         var8[0] = (byte)(64);
         var8[1] = (byte)(-13);
         var8[2] = (byte)(121);
         var6 = $$v_s(var8, 1863636497676357683L);
      }

      return var6;
   }

   public class_5250 buildTitleText() {
      String var1 = safe((String)this.title.get());
      if (!var1.isEmpty()) {
         byte[] var10000 = new byte[7];
         var10000[0] = (byte)(93);
         var10000[1] = (byte)(-13);
         var10000[2] = (byte)(114);
         var10000[3] = (byte)(-71);
         var10000[4] = (byte)(111);
         var10000[5] = (byte)(100);
         var10000[6] = (byte)(-113);
         if (!$$v_s(var10000, 1863636497676357683L).equalsIgnoreCase(var1)) {
            if (FakeRoles.isActive() && mc != null && mc.field_1724 != null) {
               String var6 = mc.field_1724.method_5477().getString();
               if (var6 != null && !var6.isBlank() && var1.contains(var6)) {
                  String var7 = NameProtect.staticMaskSelfName(var6);
                  String var9 = FakeRoles.getEffectivePrefix();
                  String var10;
                  if (var9 != null && !var9.isBlank()) {
                     var10 = var7 != null && !var7.isBlank() ? var9.trim() + " " + var7 : var9.trim();
                  } else {
                     var10 = var7;
                  }

                  return this.gradientTitle(var1.replace(var6, var10));
               }
            }

            return this.gradientTitle(var1);
         }
      }

      String var12;
      if (mc != null && mc.field_1724 != null) {
         var12 = mc.field_1724.method_5477().getString();
      } else {
         byte[] var11 = new byte[3];
         var11[0] = (byte)(64);
         var11[1] = (byte)(-13);
         var11[2] = (byte)(121);
         var12 = $$v_s(var11, 1863636497676357683L);
      }

      String var2 = var12;
      String var3 = NameProtect.staticMaskSelfName(var2);
      if (FakeRoles.isActive()) {
         if (FakeRoles.isMediaRole()) {
            String var8 = FakeRoles.stripMediaPrefix(var3);
            if (var8 != null && !var8.isBlank()) {
               return class_2561.method_43470(var8).method_10862(FakeRoles.getRoleNameStyle());
            }

            return this.gradientTitle(var8);
         }

         String var4 = FakeRoles.getEffectivePrefix();
         if (var4 != null && !var4.isBlank()) {
            class_5250 var5 = class_2561.method_43473();
            var5.method_10852(class_2561.method_43470(var4.trim()).method_10862(FakeRoles.getRolePrefixStyle()));
            if (var3 != null && !var3.isBlank()) {
               var5.method_10852(class_2561.method_43470(" "));
               var5.method_10852(class_2561.method_43470(var3).method_10862(FakeRoles.getRoleNameStyle()));
            }

            return var5;
         }
      }

      return this.gradientTitle(var3);
   }

   private class_5250 gradientTitle(String text) {
      class_5250 var2 = class_2561.method_43473();

      for (int var3 = 0; var3 < text.length(); var3++) {
         var2.method_10852(
            class_2561.method_43470(String.valueOf(text.charAt(var3)))
               .method_10862(class_2583.field_24360.method_27703(class_5251.method_27717(16777215)))
         );
      }

      return var2;
   }

   private static class_5250 colored(String text, int rgb) {
      return class_2561.method_43470(text).method_10862(class_2583.field_24360.method_27703(class_5251.method_27717(rgb)));
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
