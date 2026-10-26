package me.drake.vieclient.modules.donut.autobase.planner;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2350.class_2351;

public class MiningPlanner {
   private class_2338 corner1;
   private class_2338 corner2;
   private MiningPlanner.MiningPattern pattern = MiningPlanner.MiningPattern.LAYER_BY_LAYER;
   private MiningPlanner.ToolMode toolMode = MiningPlanner.ToolMode.NORMAL;
   private class_2351 stripAxis = class_2351.field_11051;
   private boolean protectContainers = true;
   private boolean protectSpawners = true;
   private int minX;
   private int maxX;
   private int minY;
   private int maxY;
   private int minZ;
   private int maxZ;
   private final List plannedTargets = new ArrayList();
   private final Set minedPositions = Collections.synchronizedSet(new HashSet());
   private final Set pendingPositions = Collections.synchronizedSet(new HashSet());
   private final Map blockedPositions = new ConcurrentHashMap();
   private boolean initialized = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public void setRegion(class_2338 c1, class_2338 c2, MiningPlanner.MiningPattern pattern, boolean protectContainers, boolean protectSpawners) {
      this.setRegion(c1, c2, patternx, MiningPlanner.ToolMode.NORMAL, protectContainersx, protectSpawnersx);
   }

   public void setRegion(
      class_2338 c1, class_2338 c2, MiningPlanner.MiningPattern pattern, MiningPlanner.ToolMode toolMode, boolean protectContainers, boolean protectSpawners
   ) {
      this.corner1 = c1;
      this.corner2 = c2;
      this.pattern = patternx != null ? patternx : MiningPlanner.MiningPattern.LAYER_BY_LAYER;
      this.toolMode = toolModex != null ? toolModex : MiningPlanner.ToolMode.NORMAL;
      this.protectContainers = protectContainersx;
      this.protectSpawners = protectSpawnersx;
      this.minedPositions.clear();
      this.pendingPositions.clear();
      this.blockedPositions.clear();
      this.plannedTargets.clear();
      this.initialized = false;
      if (c1 != null && c2 != null) {
         this.minX = Math.min(c1.method_10263(), c2.method_10263());
         this.maxX = Math.max(c1.method_10263(), c2.method_10263());
         this.minY = Math.min(c1.method_10264(), c2.method_10264());
         this.maxY = Math.max(c1.method_10264(), c2.method_10264());
         this.minZ = Math.min(c1.method_10260(), c2.method_10260());
         this.maxZ = Math.max(c1.method_10260(), c2.method_10260());
         int var7 = this.maxX - this.minX + 1;
         int var8 = this.maxZ - this.minZ + 1;
         this.stripAxis = var7 < (3) && var8 >= (3) ? class_2351.field_11048 : class_2351.field_11051;
         this.buildPlan();
         this.initialized = true;
      }
   }

   private void buildPlan() {
      this.plannedTargets.clear();

      for (int var1 = this.maxY; var1 >= this.minY; var1--) {
         if (this.toolMode == MiningPlanner.ToolMode.DRILL_3X1) {
            this.buildDrillLayer(var1);
         } else if (this.pattern == MiningPlanner.MiningPattern.SPIRAL) {
            this.buildSpiralLayer(var1);
         } else if (this.pattern == MiningPlanner.MiningPattern.TUNNEL_GRID) {
            this.buildTunnelGridLayer(var1);
         } else {
            this.buildLayerByLayer(var1);
         }
      }
   }

   private void buildDrillLayer(int y) {
      if (this.stripAxis == class_2351.field_11051) {
         boolean var2 = false;

         for (int var3 = this.minX; var3 <= this.maxX; var3 += 3) {
            int var4;
            if (this.maxX - this.minX < (2)) {
               var4 = this.minX;
            } else {
               var4 = Math.min(var3 + 1, this.maxX - 1);
            }

            if (!var2) {
               for (int var9 = this.minZ; var9 <= this.maxZ; var9++) {
                  this.plannedTargets.add(new class_2338(var4, y, var9));
               }
            } else {
               for (int var5 = this.maxZ; var5 >= this.minZ; var5--) {
                  this.plannedTargets.add(new class_2338(var4, y, var5));
               }
            }

            var2 = !var2;
         }
      } else {
         boolean var6 = false;

         for (int var7 = this.minZ; var7 <= this.maxZ; var7 += 3) {
            int var8;
            if (this.maxZ - this.minZ < (2)) {
               var8 = this.minZ;
            } else {
               var8 = Math.min(var7 + 1, this.maxZ - 1);
            }

            if (!var6) {
               for (int var11 = this.minX; var11 <= this.maxX; var11++) {
                  this.plannedTargets.add(new class_2338(var11, y, var8));
               }
            } else {
               for (int var10 = this.maxX; var10 >= this.minX; var10--) {
                  this.plannedTargets.add(new class_2338(var10, y, var8));
               }
            }

            var6 = !var6;
         }
      }
   }

   private void buildLayerByLayer(int y) {
      boolean var2 = false;

      for (int var3 = this.minX; var3 <= this.maxX; var3++) {
         if (!var2) {
            for (int var5 = this.minZ; var5 <= this.maxZ; var5++) {
               this.plannedTargets.add(new class_2338(var3, y, var5));
            }
         } else {
            for (int var4 = this.maxZ; var4 >= this.minZ; var4--) {
               this.plannedTargets.add(new class_2338(var3, y, var4));
            }
         }

         var2 = !var2;
      }
   }

   private void buildSpiralLayer(int y) {
      int var2 = this.minX;
      int var3 = this.maxX;
      int var4 = this.minZ;
      int var5 = this.maxZ;

      while (var2 <= var3 && var4 <= var5) {
         for (int var6 = var4; var6 <= var5; var6++) {
            this.plannedTargets.add(new class_2338(var2, y, var6));
         }

         for (int var7 = ++var2; var7 <= var3; var7++) {
            this.plannedTargets.add(new class_2338(var7, y, var5));
         }

         var5--;
         if (var2 <= var3) {
            for (int var8 = var5; var8 >= var4; var8--) {
               this.plannedTargets.add(new class_2338(var3, y, var8));
            }

            var3--;
         }

         if (var4 <= var5) {
            for (int var9 = var3; var9 >= var2; var9--) {
               this.plannedTargets.add(new class_2338(var9, y, var4));
            }

            var4++;
         }
      }
   }

   private void buildTunnelGridLayer(int y) {
      for (int var2 = this.minX; var2 <= this.maxX; var2++) {
         boolean var3 = (var2 - this.minX) % (3) == 0;

         for (int var4 = this.minZ; var4 <= this.maxZ; var4++) {
            boolean var5 = (var4 - this.minZ) % (3) == 0;
            if (var3 || var5) {
               this.plannedTargets.add(new class_2338(var2, y, var4));
            }
         }
      }
   }

   public int getHighestActiveY(Function blockIdResolver) {
      if (this.initialized && !this.plannedTargets.isEmpty()) {
         Iterator var2 = this.plannedTargets.iterator();

         class_2338 var3;
         while (true) {
            if (!var2.hasNext()) {
               return -2147483648;
            }

            var3 = (class_2338)var2.next();
            if (!this.minedPositions.contains(var3) && !this.blockedPositions.containsKey(var3)) {
               if (blockIdResolver == null) {
                  break;
               }

               String var4 = (String)blockIdResolver.apply(var3);
               if (var4 == null || !this.isAir(var4)) {
                  break;
               }

               this.minedPositions.add(var3);
            }
         }

         return var3.method_10264();
      } else {
         return -2147483648;
      }
   }

   public class_2338 getNextTarget(class_243 playerPos, double maxReach, Function blockIdResolver) {
      if (this.initialized && !this.plannedTargets.isEmpty()) {
         int var5 = this.getHighestActiveY(blockIdResolver);
         if (var5 == Integer.MIN_VALUE) {
            return null;
         }

         class_2338 var6 = playerPos != null ? class_2338.method_49637(playerPos.field_1352, playerPos.field_1351, playerPos.field_1350) : null;
         class_2338 var7 = var6 != null ? var6.method_10074() : null;
         class_2338 var8 = null;
         double var9 = Double.MAX_VALUE;
         class_2338 var11 = null;
         double var12 = Double.MAX_VALUE;

         for (class_2338 var15 : this.plannedTargets) {
            if (var15.method_10264() == var5
               && !this.minedPositions.contains(var15)
               && !this.pendingPositions.contains(var15)
               && !this.blockedPositions.containsKey(var15)) {
               if (this.toolMode == MiningPlanner.ToolMode.DRILL_3X1) {
                  List var25 = this.getDrillFootprint(var15);
                  boolean var26 = true;
                  class_2338 var27 = null;

                  for (class_2338 var20 : var25) {
                     if (!this.minedPositions.contains(var20)) {
                        String var21 = blockIdResolver != null ? (String)blockIdResolver.apply(var20) : "";
                        if (var21 == null) {
                           var21 = "";
                        }

                        if (this.isAir(var21)) {
                           this.minedPositions.add(var20);
                        } else {
                           var26 = false;
                           if (var27 == null) {
                              var27 = var20;
                           }
                        }
                     }
                  }

                  if (var26) {
                     this.minedPositions.add(var15);
                  } else {
                     boolean var28 = false;
                     boolean var29 = false;
                     Iterator var30 = var25.iterator();

                     while (true) {
                        if (var30.hasNext()) {
                           class_2338 var22 = (class_2338)var30.next();
                           if (this.minedPositions.contains(var22)) {
                              continue;
                           }

                           String var23 = blockIdResolver != null ? (String)blockIdResolver.apply(var22) : "";
                           if (var23 == null) {
                              var23 = "";
                           }

                           if (var7 != null && var22.equals(var7)) {
                              var29 = true;
                           }

                           if (this.isIndestructible(var23)) {
                              this.blockedPositions.put(var15, "Footprint indestructible: " + var23);
                              this.blockedPositions.put(var22, "Indestructible: " + var23);
                              var28 = true;
                           } else if (this.protectContainers && this.isContainer(var23)) {
                              this.blockedPositions.put(var15, "Footprint protected container: " + var23);
                              this.blockedPositions.put(var22, "Protected container: " + var23);
                              var28 = true;
                           } else {
                              if (!this.protectSpawners || !this.isSpawner(var23)) {
                                 continue;
                              }

                              this.blockedPositions.put(var15, "Footprint protected spawner: " + var23);
                              this.blockedPositions.put(var22, "Protected spawner: " + var23);
                              var28 = true;
                           }
                        }

                        if (!var28) {
                           class_2338 var31 = var15;
                           String var32 = blockIdResolver != null ? (String)blockIdResolver.apply(var15) : "";
                           if (var32 == null) {
                              var32 = "";
                           }

                           if (this.isAir(var32) && var27 != null) {
                              var31 = var27;
                           }

                           if (playerPos == null) {
                              return var31;
                           }

                           double var33 = playerPos.method_1028(var31.method_10263() + 0.5, var31.method_10264() + 0.5, var31.method_10260() + 0.5);
                           if (!(var33 > maxReach * maxReach)) {
                              if (var29) {
                                 var11 = var31;
                              } else if (var33 < var9) {
                                 var9 = var33;
                                 var8 = var31;
                              }
                           }
                        }
                        break;
                     }
                  }
               } else {
                  String var16 = blockIdResolver != null ? (String)blockIdResolver.apply(var15) : "";
                  if (var16 == null) {
                     var16 = "";
                  }

                  if (this.isAir(var16)) {
                     this.minedPositions.add(var15);
                  } else if (this.isIndestructible(var16)) {
                     this.blockedPositions.put(var15, "Indestructible: " + var16);
                  } else if (this.protectContainers && this.isContainer(var16)) {
                     this.blockedPositions.put(var15, "Protected container: " + var16);
                  } else if (this.protectSpawners && this.isSpawner(var16)) {
                     this.blockedPositions.put(var15, "Protected spawner: " + var16);
                  } else {
                     boolean var17 = var7 != null && var15.equals(var7);
                     if (playerPos == null) {
                        return var15;
                     }

                     double var18 = playerPos.method_1028(var15.method_10263() + 0.5, var15.method_10264() + 0.5, var15.method_10260() + 0.5);
                     if (!(var18 > maxReach * maxReach)) {
                        if (var17) {
                           var11 = var15;
                        } else if (var18 < var9) {
                           var9 = var18;
                           var8 = var15;
                        }
                     }
                  }
               }
            }
         }

         return var8 == null && var11 != null && var7 != null && this.isSafeFloorBelow(var7, blockIdResolver) ? var11 : var8;
      } else {
         return null;
      }
   }

   public class_2338 getNextNavTarget(class_243 playerPos, Function blockIdResolver) {
      if (this.initialized && !this.plannedTargets.isEmpty()) {
         int var3 = this.getHighestActiveY(blockIdResolver);
         if (var3 == (-2147483648)) {
            return null;
         }

         class_2338 var4 = null;
         double var5 = Double.MAX_VALUE;

         for (class_2338 var8 : this.plannedTargets) {
            if (var8.method_10264() == var3 && !this.minedPositions.contains(var8) && !this.blockedPositions.containsKey(var8)) {
               String var9 = blockIdResolver != null ? (String)blockIdResolver.apply(var8) : "";
               if (var9 == null) {
                  var9 = "";
               }

               if (this.isAir(var9)) {
                  this.minedPositions.add(var8);
               } else if (!this.isIndestructible(var9)
                  && (!this.protectContainers || !this.isContainer(var9))
                  && (!this.protectSpawners || !this.isSpawner(var9))) {
                  class_2338 var10 = var8;
                  if (this.toolMode == MiningPlanner.ToolMode.DRILL_3X1) {
                     for (class_2338 var12 : this.getDrillFootprint(var8)) {
                        if (!this.minedPositions.contains(var12)) {
                           String var13 = blockIdResolver != null ? (String)blockIdResolver.apply(var12) : "";
                           if (var13 != null && !this.isAir(var13)) {
                              var10 = var12;
                              break;
                           }
                        }
                     }
                  }

                  if (playerPos == null) {
                     return var10;
                  }

                  double var14 = playerPos.method_1028(var10.method_10263() + 0.5, var10.method_10264() + 0.5, var10.method_10260() + 0.5);
                  if (var14 < var5) {
                     var5 = var14;
                     var4 = var10;
                  }
               }
            }
         }

         return var4;
      } else {
         return null;
      }
   }

   private boolean isSafeFloorBelow(class_2338 pos, Function blockIdResolver) {
      if (pos != null && blockIdResolver != null) {
         class_2338 var3 = pos.method_10074();
         String var4 = (String)blockIdResolver.apply(var3);
         if (var4 == null) {
            return false;
         }

         if (!this.isAir(var4)) {
            byte[] var10001 = new byte[4];
            var10001[0] = (byte)(67);
            var10001[1] = (byte)(-5);
            var10001[2] = (byte)(106);
            var10001[3] = (byte)(-65);
            if (!var4.contains($$v_s(var10001, -6731441450804588534L))) {
               var10001 = new byte[4];
               var10001[0] = (byte)(73);
               var10001[1] = (byte)(-13);
               var10001[2] = (byte)(110);
               var10001[3] = (byte)(-69);
               if (!var4.contains($$v_s(var10001, -6731441450804588534L))) {
                  var10001 = new byte[4];
                  var10001[0] = (byte)(89);
                  var10001[1] = (byte)(-11);
                  var10001[2] = (byte)(117);
                  var10001[3] = (byte)(-70);
                  if (!var4.contains($$v_s(var10001, -6731441450804588534L))) {
                     return true;
                  }
               }
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public List getDrillFootprint(class_2338 center) {
      if (center == null) {
         return Collections.emptyList();
      }

      ArrayList var2 = new ArrayList(3);
      var2.add(center);
      if (this.stripAxis == class_2351.field_11051) {
         class_2338 var3 = center.method_10069(-1, 0, 0);
         class_2338 var4 = center.method_10069(1, 0, 0);
         if (var3.method_10263() >= this.minX && var3.method_10263() <= this.maxX) {
            var2.add(var3);
         }

         if (var4.method_10263() >= this.minX && var4.method_10263() <= this.maxX) {
            var2.add(var4);
         }
      } else {
         class_2338 var5 = center.method_10069(0, 0, -1);
         class_2338 var6 = center.method_10069(0, 0, 1);
         if (var5.method_10260() >= this.minZ && var5.method_10260() <= this.maxZ) {
            var2.add(var5);
         }

         if (var6.method_10260() >= this.minZ && var6.method_10260() <= this.maxZ) {
            var2.add(var6);
         }
      }

      return var2;
   }

   public float getRouteYaw(class_2338 target, class_243 playerPos) {
      if (target != null && playerPos != null) {
         double var3 = target.method_10263() + 0.5 - playerPos.field_1352;
         double var5 = target.method_10260() + 0.5 - playerPos.field_1350;
         if (Math.abs(var3) > Math.abs(var5)) {
            return var3 >= 0.0 ? -90.0F : 90.0F;
         } else {
            return var5 >= 0.0 ? 0.0F : 180.0F;
         }
      } else {
         return this.stripAxis == class_2351.field_11051 ? 0.0F : -90.0F;
      }
   }

   public void markMined3x1(class_2338 center) {
      if (center != null) {
         for (class_2338 var4 : this.getDrillFootprint(center)) {
            this.markMined(var4);
         }
      }
   }

   public boolean isAir(String blockId) {
      byte[] var10001 = new byte[13];
      var10001[0] = (byte)(66);
      var10001[1] = (byte)(-13);
      var10001[2] = (byte)(114);
      var10001[3] = (byte)(-69);
      var10001[4] = (byte)(112);
      var10001[5] = (byte)(-79);
      var10001[6] = (byte)(-9);
      var10001[7] = (byte)(-20);
      var10001[8] = (byte)(51);
      var10001[9] = (byte)(-104);
      var10001[10] = (byte)(-123);
      var10001[11] = (byte)(-97);
      var10001[12] = (byte)(57);
      if (!blockId.equals($$v_s(var10001, -6731441450804588534L))) {
         var10001 = new byte[18];
         var10001[0] = (byte)(66);
         var10001[1] = (byte)(-13);
         var10001[2] = (byte)(114);
         var10001[3] = (byte)(-69);
         var10001[4] = (byte)(112);
         var10001[5] = (byte)(-79);
         var10001[6] = (byte)(-9);
         var10001[7] = (byte)(-20);
         var10001[8] = (byte)(51);
         var10001[9] = (byte)(-104);
         var10001[10] = (byte)(-121);
         var10001[11] = (byte)(-105);
         var10001[12] = (byte)(61);
         var10001[13] = (byte)(126);
         var10001[14] = (byte)(-31);
         var10001[15] = (byte)(-109);
         var10001[16] = (byte)(22);
         var10001[17] = (byte)(56);
         if (!blockId.equals($$v_s(var10001, -6731441450804588534L))) {
            var10001 = new byte[18];
            var10001[0] = (byte)(66);
            var10001[1] = (byte)(-13);
            var10001[2] = (byte)(114);
            var10001[3] = (byte)(-69);
            var10001[4] = (byte)(112);
            var10001[5] = (byte)(-79);
            var10001[6] = (byte)(-9);
            var10001[7] = (byte)(-20);
            var10001[8] = (byte)(51);
            var10001[9] = (byte)(-104);
            var10001[10] = (byte)(-110);
            var10001[11] = (byte)(-103);
            var10001[12] = (byte)(34);
            var10001[13] = (byte)(127);
            var10001[14] = (byte)(-31);
            var10001[15] = (byte)(-109);
            var10001[16] = (byte)(22);
            var10001[17] = (byte)(56);
            if (!blockId.equals($$v_s(var10001, -6731441450804588534L))) {
               return false;
            }
         }
      }

      return true;
   }

   public boolean isContainer(String blockId) {
      byte[] var10001 = new byte[5];
      var10001[0] = (byte)(76);
      var10001[1] = (byte)(-14);
      var10001[2] = (byte)(121);
      var10001[3] = (byte)(-83);
      var10001[4] = (byte)(103);
      if (!blockId.contains($$v_s(var10001, -6731441450804588534L))) {
         var10001 = new byte[11];
         var10001[0] = (byte)(92);
         var10001[1] = (byte)(-14);
         var10001[2] = (byte)(105);
         var10001[3] = (byte)(-78);
         var10001[4] = (byte)(120);
         var10001[5] = (byte)(-90);
         var10001[6] = (byte)(-28);
         var10001[7] = (byte)(-43);
         var10001[8] = (byte)(37);
         var10001[9] = (byte)(-51);
         var10001[10] = (byte)(-100);
         if (!blockId.contains($$v_s(var10001, -6731441450804588534L))) {
            var10001 = new byte[6];
            var10001[0] = (byte)(77);
            var10001[1] = (byte)(-5);
            var10001[2] = (byte)(110);
            var10001[3] = (byte)(-84);
            var10001[4] = (byte)(118);
            var10001[5] = (byte)(-81);
            if (!blockId.contains($$v_s(var10001, -6731441450804588534L))) {
               var10001 = new byte[6];
               var10001[0] = (byte)(71);
               var10001[1] = (byte)(-11);
               var10001[2] = (byte)(108);
               var10001[3] = (byte)(-82);
               var10001[4] = (byte)(118);
               var10001[5] = (byte)(-79);
               if (!blockId.contains($$v_s(var10001, -6731441450804588534L))) {
                  var10001 = new byte[9];
                  var10001[0] = (byte)(75);
                  var10001[1] = (byte)(-13);
                  var10001[2] = (byte)(111);
                  var10001[3] = (byte)(-82);
                  var10001[4] = (byte)(118);
                  var10001[5] = (byte)(-83);
                  var10001[6] = (byte)(-27);
                  var10001[7] = (byte)(-17);
                  var10001[8] = (byte)(53);
                  if (!blockId.contains($$v_s(var10001, -6731441450804588534L))) {
                     var10001 = new byte[7];
                     var10001[0] = (byte)(75);
                     var10001[1] = (byte)(-24);
                     var10001[2] = (byte)(115);
                     var10001[3] = (byte)(-82);
                     var10001[4] = (byte)(99);
                     var10001[5] = (byte)(-90);
                     var10001[6] = (byte)(-28);
                     if (!blockId.contains($$v_s(var10001, -6731441450804588534L))) {
                        return false;
                     }
                  }
               }
            }
         }
      }

      return true;
   }

   public boolean isSpawner(String blockId) {
      byte[] var10001 = new byte[7];
      var10001[0] = (byte)(92);
      var10001[1] = (byte)(-22);
      var10001[2] = (byte)(125);
      var10001[3] = (byte)(-87);
      var10001[4] = (byte)(125);
      var10001[5] = (byte)(-90);
      var10001[6] = (byte)(-28);
      return blockId.endsWith($$v_s(var10001, -6731441450804588534L));
   }

   public boolean isIndestructible(String blockId) {
      return blockId.endsWith("bedrock")
         || blockId.endsWith("barrier")
         || blockId.endsWith("structure_void")
         || blockId.endsWith("end_portal")
         || blockId.endsWith("end_portal_frame")
         || blockId.endsWith("nether_portal");
   }

   public void markPending(class_2338 pos) {
      this.pendingPositions.add(pos);
   }

   public void unmarkPending(class_2338 pos) {
      this.pendingPositions.remove(pos);
   }

   public void markMined(class_2338 pos) {
      this.pendingPositions.remove(pos);
      this.blockedPositions.remove(pos);
      this.minedPositions.add(pos);
   }

   public void markBlocked(class_2338 pos, String reason) {
      this.pendingPositions.remove(pos);
      String var10002;
      if (reason != null) {
         var10002 = reason;
      } else {
         byte[] var3 = new byte[14];
         var3[0] = (byte)(98);
         var3[1] = (byte)(-13);
         var3[2] = (byte)(114);
         var3[3] = (byte)(-73);
         var3[4] = (byte)(125);
         var3[5] = (byte)(-92);
         var3[6] = (byte)(-74);
         var3[7] = (byte)(-24);
         var3[8] = (byte)(43);
         var3[9] = (byte)(-51);
         var3[10] = (byte)(-121);
         var3[11] = (byte)(-99);
         var3[12] = (byte)(46);
         var3[13] = (byte)(127);
         var10002 = $$v_s(var3, -6731441450804588534L);
      }

      this.blockedPositions.put(pos, var10002);
   }

   public int getTotalCount() {
      return this.initialized ? (this.maxX - this.minX + 1) * (this.maxY - this.minY + 1) * (this.maxZ - this.minZ + 1) : this.plannedTargets.size();
   }

   public int getMinedCount() {
      return this.minedPositions.size();
   }

   public int getBlockedCount() {
      return this.blockedPositions.size();
   }

   public int getPendingCount() {
      return this.pendingPositions.size();
   }

   public int getRemainingCount() {
      return Math.max(0, this.getTotalCount() - this.getMinedCount() - this.getBlockedCount());
   }

   public boolean isFinished() {
      return this.initialized && this.getMinedCount() + this.getBlockedCount() >= this.getTotalCount();
   }

   public Map getBlockedPositions() {
      return Collections.unmodifiableMap(this.blockedPositions);
   }

   public boolean isMined(class_2338 pos) {
      return pos != null && this.minedPositions.contains(pos);
   }

   public Set getMinedPositions() {
      return Collections.unmodifiableSet(this.minedPositions);
   }

   public List getPlannedTargets() {
      return Collections.unmodifiableList(this.plannedTargets);
   }

   public class_2338 getCorner1() {
      return this.corner1;
   }

   public class_2338 getCorner2() {
      return this.corner2;
   }

   public MiningPlanner.ToolMode getToolMode() {
      return this.toolMode;
   }

   public class_2351 getStripAxis() {
      return this.stripAxis;
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

   public enum MiningPattern {
      LAYER_BY_LAYER,
      SPIRAL,
      TUNNEL_GRID;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static MiningPlanner.MiningPattern[] $values() {
         MiningPlanner.MiningPattern[] var10000 = new MiningPlanner.MiningPattern[3];
         var10000[0] = LAYER_BY_LAYER;
         var10000[1] = SPIRAL;
         var10000[2] = TUNNEL_GRID;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[14];
         var10002[0] = (byte)(116);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(69);
         var10002[3] = (byte)(-101);
         var10002[4] = (byte)(101);
         var10002[5] = (byte)(-13);
         var10002[6] = (byte)(57);
         var10002[7] = (byte)(-4);
         var10002[8] = (byte)(15);
         var10002[9] = (byte)(-18);
         var10002[10] = (byte)(-91);
         var10002[11] = (byte)(-81);
         var10002[12] = (byte)(42);
         var10002[13] = (byte)(38);
         LAYER_BY_LAYER = new MiningPlanner.MiningPattern();
         var10002 = new byte[6];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-54);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-116);
         var10002[4] = (byte)(118);
         var10002[5] = (byte)(-32);
         SPIRAL = new MiningPlanner.MiningPattern();
         var10002 = new byte[11];
         var10002[0] = (byte)(108);
         var10002[1] = (byte)(-49);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-112);
         var10002[4] = (byte)(114);
         var10002[5] = (byte)(-32);
         var10002[6] = (byte)(36);
         var10002[7] = (byte)(-30);
         var10002[8] = (byte)(2);
         var10002[9] = (byte)(-21);
         var10002[10] = (byte)(-96);
         TUNNEL_GRID = new MiningPlanner.MiningPattern();
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

   public enum ToolMode {
      NORMAL,
      DRILL_3X1;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static MiningPlanner.ToolMode[] $values() {
         MiningPlanner.ToolMode[] var10000 = new MiningPlanner.ToolMode[2];
         var10000[0] = NORMAL;
         var10000[1] = DRILL_3X1;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[6];
         var10002[0] = (byte)(119);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-109);
         var10002[4] = (byte)(77);
         var10002[5] = (byte)(24);
         NORMAL = new MiningPlanner.ToolMode();
         var10002 = new byte[9];
         var10002[0] = (byte)(125);
         var10002[1] = (byte)(-56);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-110);
         var10002[4] = (byte)(64);
         var10002[5] = (byte)(11);
         var10002[6] = (byte)(71);
         var10002[7] = (byte)(-27);
         var10002[8] = (byte)(96);
         DRILL_3X1 = new MiningPlanner.ToolMode();
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
