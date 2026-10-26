package io.github.kosmx.emotes.arch.screen.widget.preview.elemets;

import com.mojang.authlib.GameProfile;
import io.github.kosmx.emotes.arch.screen.widget.AbstractFastChooseWidget;
import org.joml.Vector2f;

public class PlayerChooseSquareElement extends PlayerChooseElement {
   protected final int dx;
   protected final int dy;

   public PlayerChooseSquareElement(AbstractFastChooseWidget parent, GameProfile profile, int id, int dx, int dy) {
      super(parent, profile, id);
      this.dx = dx;
      this.dy = dy;
   }

   @Override
   protected void getDirectionVector(Vector2f out) {
      out.set(this.dx, this.dy);
   }

   @Override
   protected void updateRectangle(float easedProgress) {
      int s = this.parent.globalPadding();
      float distance = s * 2.5F * easedProgress;
      int iconX = Math.round(this.parent.method_46426() + this.parent.method_25368() / 2.0F + this.dx * distance) - s;
      int iconY = Math.round(this.parent.method_46427() + this.parent.method_25364() / 2.0F + this.dy * distance) - s;
      this.method_55444(s * 2, s * 2, iconX, iconY);
   }
}
