package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.core.setting.StringSetting;

public class AHSniper extends Module {
   private static final Pattern PRICE_PATTERN;
   private static final Pattern BUY_CMD_PATTERN;
   private final StringSetting targetItem = (StringSetting)this.addSetting(
      new StringSetting(
         "ItemFilter",
         "Item ID to snipe (partial match)",
         "diamond"
      )
   );
   private final IntSetting maxPrice = (IntSetting)this.addSetting(
      new IntSetting(
         "MaxPrice",
         "Buy if price is below this",
         1000,
         1,
         100000
      )
   );
   private final IntSetting reactionDelay = (IntSetting)this.addSetting(
      new IntSetting(
         "ReactionDelay",
         "Delay before buying (ms)",
         100,
         0,
         500
      )
   );
   private final StringSetting buyCommand = (StringSetting)this.addSetting(
      new StringSetting(
         "BuyCommand",
         "Buy command template (%id% = listing ID)",
         "/ah buy %id%"
      )
   );
   private long pendingBuyAt = -1L;
   private String pendingCmd = null;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AHSniper() {
      super(
         "AHSniper",
         "Snipers cheap AH listings on DonutSMP",
         Category.SMP_UTILITIES
      );
      this.buyCommand.setVisibleInGui(false);
   }

   @Override
   public void onTick() {
      if (this.isInGame()) {
         String var1;
         while ((var1 = AHSniper.ChatMessageQueue.pollStatic()) != null) {
            this.processMessage(var1);
         }

         if (this.pendingBuyAt > 0L && System.currentTimeMillis() >= this.pendingBuyAt && this.pendingCmd != null) {
            mc.field_1724.field_3944.method_45729(this.pendingCmd);
            this.pendingBuyAt = -1L;
            this.pendingCmd = null;
         }
      }
   }

   private void processMessage(String msg) {
      String var2 = msg.toLowerCase();
      byte[] var10001 = new byte[4];
      var10001[0] = (byte)(113);
      var10001[1] = (byte)(-5);
      var10001[2] = (byte)(116);
      var10001[3] = (byte)(-125);
      if (!var2.contains($$v_s(var10001, -7266628735624556529L))) {
         var10001 = new byte[7];
         var10001[0] = (byte)(75);
         var10001[1] = (byte)(-17);
         var10001[2] = (byte)(127);
         var10001[3] = (byte)(-86);
         var10001[4] = (byte)(122);
         var10001[5] = (byte)(14);
         var10001[6] = (byte)(74);
         if (!var2.contains($$v_s(var10001, -7266628735624556529L))) {
            return;
         }
      }

      if (var2.contains(((String)this.targetItem.get()).toLowerCase())) {
         int var3 = this.extractPrice(msg);
         if (var3 > 0 && var3 <= (Integer)this.maxPrice.get()) {
            String var4 = this.extractBuyCommand(msg);
            if (var4 == null) {
               String var10000 = (String)this.buyCommand.get();
               var10001 = new byte[4];
               var10001[0] = (byte)(15);
               var10001[1] = (byte)(-13);
               var10001[2] = (byte)(120);
               var10001[3] = (byte)(-5);
               var4 = var10000.replace($$v_s(var10001, -7266628735624556529L), "");
            }

            this.pendingCmd = var4;
            this.pendingBuyAt = System.currentTimeMillis() + ((Integer)this.reactionDelay.get()).intValue();
         }
      }
   }

   private int extractPrice(String msg) {
      Matcher var2 = PRICE_PATTERN.matcher(msg);
      if (var2.find()) {
         try {
            return Integer.parseInt(var2.group(1).replace(",", ""));
         } catch (NumberFormatException var4) {
         }
      }

      return -1;
   }

   private String extractBuyCommand(String msg) {
      Matcher var2 = BUY_CMD_PATTERN.matcher(msg);
      return var2.find() ? var2.group(1) : null;
   }

   @Override
   public String getInfo() {
      return (String)this.targetItem.get();
   }

   static {
      byte[] var10000 = new byte[18];
      var10000[0] = (byte)(118);
      var10000[1] = (byte)(-66);
      var10000[2] = (byte)(52);
      var10000[3] = (byte)(-126);
      var10000[4] = (byte)(119);
      var10000[5] = (byte)(74);
      var10000[6] = (byte)(12);
      var10000[7] = (byte)(-116);
      var10000[8] = (byte)(120);
      var10000[9] = (byte)(-114);
      var10000[10] = (byte)(-72);
      var10000[11] = (byte)(-110);
      var10000[12] = (byte)(48);
      var10000[13] = (byte)(-118);
      var10000[14] = (byte)(113);
      var10000[15] = (byte)(-30);
      var10000[16] = (byte)(80);
      var10000[17] = (byte)(99);
      PRICE_PATTERN = Pattern.compile($$v_s(var10000, -7266628735624556529L));
      var10000 = new byte[13];
      var10000[0] = (byte)(2);
      var10000[1] = (byte)(-75);
      var10000[2] = (byte)(125);
      var10000[3] = (byte)(-74);
      var10000[4] = (byte)(51);
      var10000[5] = (byte)(3);
      var10000[6] = (byte)(81);
      var10000[7] = (byte)(-54);
      var10000[8] = (byte)(98);
      var10000[9] = (byte)(-2);
      var10000[10] = (byte)(-73);
      var10000[11] = (byte)(-35);
      var10000[12] = (byte)(98);
      BUY_CMD_PATTERN = Pattern.compile($$v_s(var10000, -7266628735624556529L));
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

   private static final class ChatMessageQueue {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      static String pollStatic() {
         return me.drake.vieclient.util.ChatMessageQueue.poll();
      }
   }
}
