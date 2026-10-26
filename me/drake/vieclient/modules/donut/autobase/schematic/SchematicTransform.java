package me.drake.vieclient.modules.donut.autobase.schematic;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.class_2338;
import net.minecraft.class_2382;
import net.minecraft.class_2415;
import net.minecraft.class_2470;
import net.minecraft.class_2680;

public record SchematicTransform(class_2470 rotation, class_2415 mirror) {
   public static final SchematicTransform IDENTITY = new SchematicTransform(class_2470.field_11467, class_2415.field_11302);
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public class_2338 transformPos(class_2338 pos, class_2382 size) {
      int var3 = pos.method_10263();
      int var4 = pos.method_10264();
      int var5 = pos.method_10260();
      int var6 = size.method_10263();
      int var7 = size.method_10260();
      if (this.mirror == class_2415.field_11301) {
         var3 = var6 - 1 - var3;
      } else if (this.mirror == class_2415.field_11300) {
         var5 = var7 - 1 - var5;
      }
      return switch (<unrepresentable>.$SwitchMap$net$minecraft$util$BlockRotation[this.rotation.ordinal()]) {
         case 1 -> new class_2338(var3, var4, var5);
         case 2 -> new class_2338(var7 - 1 - var5, var4, var3);
         case 3 -> new class_2338(var6 - 1 - var3, var4, var7 - 1 - var5);
         case 4 -> new class_2338(var5, var4, var6 - 1 - var3);
         default -> throw new MatchException(null, null);
      };
   }

   public class_2680 transformState(class_2680 state) {
      if (state == null) {
         return null;
      }

      try {
         class_2680 var2 = state;
         if (this.mirror != class_2415.field_11302) {
            var2 = var2.method_26185(this.mirror);
         }

         if (this.rotation != class_2470.field_11467) {
            var2 = var2.method_26186(this.rotation);
         }

         return var2;
      } catch (Throwable var3) {
         return state;
      }
   }

   public Map transformProperties(Map props) {
      if (props != null && !props.isEmpty()) {
         HashMap var2 = new HashMap(props);
         byte[] var10001 = new byte[6];
         var10001[0] = (byte)(123);
         var10001[1] = (byte)(-5);
         var10001[2] = (byte)(127);
         var10001[3] = (byte)(-73);
         var10001[4] = (byte)(123);
         var10001[5] = (byte)(66);
         if (var2.containsKey($$v_s(var10001, 2583372578675740728L))) {
            var10001 = new byte[6];
            var10001[0] = (byte)(123);
            var10001[1] = (byte)(-5);
            var10001[2] = (byte)(127);
            var10001[3] = (byte)(-73);
            var10001[4] = (byte)(123);
            var10001[5] = (byte)(66);
            String var3 = ((String)var2.get($$v_s(var10001, 2583372578675740728L))).toLowerCase();
            var3 = applyMirrorFacing(var3, this.mirror);
            var3 = applyRotateFacing(var3, this.rotation);
            var10001 = new byte[6];
            var10001[0] = (byte)(123);
            var10001[1] = (byte)(-5);
            var10001[2] = (byte)(127);
            var10001[3] = (byte)(-73);
            var10001[4] = (byte)(123);
            var10001[5] = (byte)(66);
            var2.put($$v_s(var10001, 2583372578675740728L), var3);
         }

         var10001 = new byte[4];
         var10001[0] = (byte)(124);
         var10001[1] = (byte)(-30);
         var10001[2] = (byte)(117);
         var10001[3] = (byte)(-83);
         if (var2.containsKey($$v_s(var10001, 2583372578675740728L))) {
            var10001 = new byte[4];
            var10001[0] = (byte)(124);
            var10001[1] = (byte)(-30);
            var10001[2] = (byte)(117);
            var10001[3] = (byte)(-83);
            String var6 = ((String)var2.get($$v_s(var10001, 2583372578675740728L))).toLowerCase();
            if (this.rotation == class_2470.field_11463 || this.rotation == class_2470.field_11465) {
               if ("x".equals(var6)) {
                  var6 = "z";
               } else if ("z".equals(var6)) {
                  var6 = "x";
               }
            }

            var10001 = new byte[4];
            var10001[0] = (byte)(124);
            var10001[1] = (byte)(-30);
            var10001[2] = (byte)(117);
            var10001[3] = (byte)(-83);
            var2.put($$v_s(var10001, 2583372578675740728L), var6);
         }

         return var2;
      } else {
         return props;
      }
   }

   private static String applyMirrorFacing(String facing, class_2415 m) {
      if (m == class_2415.field_11300) {
         byte[] var10000 = new byte[5];
         var10000[0] = (byte)(115);
         var10000[1] = (byte)(-11);
         var10000[2] = (byte)(110);
         var10000[3] = (byte)(-86);
         var10000[4] = (byte)(125);
         if ($$v_s(var10000, 2583372578675740728L).equals(facing)) {
            var10000 = new byte[5];
            var10000[0] = (byte)(110);
            var10000[1] = (byte)(-11);
            var10000[2] = (byte)(105);
            var10000[3] = (byte)(-86);
            var10000[4] = (byte)(125);
            return $$v_s(var10000, 2583372578675740728L);
         }

         var10000 = new byte[5];
         var10000[0] = (byte)(110);
         var10000[1] = (byte)(-11);
         var10000[2] = (byte)(105);
         var10000[3] = (byte)(-86);
         var10000[4] = (byte)(125);
         if ($$v_s(var10000, 2583372578675740728L).equals(facing)) {
            var10000 = new byte[5];
            var10000[0] = (byte)(115);
            var10000[1] = (byte)(-11);
            var10000[2] = (byte)(110);
            var10000[3] = (byte)(-86);
            var10000[4] = (byte)(125);
            return $$v_s(var10000, 2583372578675740728L);
         }
      } else if (m == class_2415.field_11301) {
         byte[] var5 = new byte[4];
         var5[0] = (byte)(120);
         var5[1] = (byte)(-5);
         var5[2] = (byte)(111);
         var5[3] = (byte)(-86);
         if ($$v_s(var5, 2583372578675740728L).equals(facing)) {
            var5 = new byte[4];
            var5[0] = (byte)(106);
            var5[1] = (byte)(-1);
            var5[2] = (byte)(111);
            var5[3] = (byte)(-86);
            return $$v_s(var5, 2583372578675740728L);
         }

         var5 = new byte[4];
         var5[0] = (byte)(106);
         var5[1] = (byte)(-1);
         var5[2] = (byte)(111);
         var5[3] = (byte)(-86);
         if ($$v_s(var5, 2583372578675740728L).equals(facing)) {
            var5 = new byte[4];
            var5[0] = (byte)(120);
            var5[1] = (byte)(-5);
            var5[2] = (byte)(111);
            var5[3] = (byte)(-86);
            return $$v_s(var5, 2583372578675740728L);
         }
      }

      return facing;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static String applyRotateFacing(String facing, class_2470 r) {
      return switch (<unrepresentable>.$SwitchMap$net$minecraft$util$BlockRotation[r.ordinal()]) {
         case 1 -> facing;
         case 2 -> {
            switch (facing) {
               case "north":
                  yield "east";
               case "east":
                  yield "south";
               case "south":
                  yield "west";
               case "west":
                  yield "north";
               default:
                  yield facing;
            }
         }
         case 3 -> {
            switch (facing) {
               case "north":
                  yield "south";
               case "south":
                  yield "north";
               case "east":
                  yield "west";
               case "west":
                  yield "east";
               default:
                  yield facing;
            }
         }
         case 4 -> {
            switch (facing) {
               case "north":
                  yield "west";
               case "west":
                  yield "south";
               case "south":
                  yield "east";
               case "east":
                  yield "north";
               default:
                  yield facing;
            }
         }
         default -> throw new MatchException(null, null);
      };
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
