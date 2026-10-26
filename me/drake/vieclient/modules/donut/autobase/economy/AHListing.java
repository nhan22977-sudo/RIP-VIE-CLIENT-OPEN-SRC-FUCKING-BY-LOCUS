package me.drake.vieclient.modules.donut.autobase.economy;

import java.util.List;

public record AHListing(
   int slotId, String rawItemId, String cleanItemId, int amount, long totalPrice, double unitPrice, String sellerName, List<String> loreLines
) {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public boolean isValid() {
      return this.amount > 0 && this.totalPrice > 0L && this.cleanItemId != null && !this.cleanItemId.isEmpty();
   }
}
