package io.github.kosmx.emotes;

import com.zigythebird.playeranim.PlayerAnimLibService;
import io.github.kosmx.emotes.main.config.ClientConfig;
import io.github.kosmx.emotes.mc.McUtils;
import io.github.kosmx.emotes.server.config.Serializer;
import io.github.kosmx.emotes.server.services.InstanceService;
import java.util.UUID;
import net.minecraft.class_11890;
import net.minecraft.class_156;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_370;
import net.minecraft.class_5498;
import net.minecraft.class_638;
import net.minecraft.class_370.class_9037;
import org.jetbrains.annotations.Nullable;

public final class PlatformTools {
   public static final boolean HAS_SEARCHABLES = PlayerAnimLibService.INSTANCE.isModLoaded("searchables");

   @Nullable
   public static class_11890 getAvatarFromUUID(UUID uuid) {
      class_638 level = class_310.method_1551().field_1687;
      return level == null ? null : (class_11890)level.method_66347(uuid);
   }

   public static void openExternalEmotesDir() {
      class_156.method_668().method_60932(InstanceService.INSTANCE.getExternalEmoteDir());
   }

   public static ClientConfig getConfig() {
      return (ClientConfig)Serializer.getConfig();
   }

   public static class_5498 getCameraType() {
      return class_310.method_1551().field_1690.method_31044();
   }

   public static void setCameraType(class_5498 p) {
      class_310.method_1551().field_1690.method_31043(p);
   }

   public static void addToast(class_2561 title, class_2561 message) {
      class_370 toast = class_370.method_29047(class_310.method_1551(), class_9037.field_47584, title, message);
      class_310.method_1551().method_1566().method_1999(toast);
   }

   public static void addToast(class_2561 message) {
      addToast(McUtils.MOD_NAME, message);
   }
}
