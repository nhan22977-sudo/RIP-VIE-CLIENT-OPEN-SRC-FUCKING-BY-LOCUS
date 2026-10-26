package io.github.kosmx.emotes.arch;

import io.github.kosmx.emotes.PlatformTools;
import io.github.kosmx.emotes.arch.screen.ingame.FastMenuScreen;
import io.github.kosmx.emotes.common.CommonData;
import io.github.kosmx.emotes.main.EmoteHolder;
import io.github.kosmx.emotes.main.network.ClientEmotePlay;
import io.github.kosmx.emotes.main.network.ClientPacketManager;
import io.github.kosmx.emotes.mc.McUtils;
import io.github.kosmx.emotes.server.serializer.UniversalEmoteSerializer;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_156;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_304.class_11900;
import net.minecraft.class_3675.class_307;

public class EmotecraftClientMod {
   protected static final class_11900 KEYBIND_CATEGORY = class_11900.method_74698(McUtils.newIdentifier("keybinding"));
   public static final class_304 OPEN_MENU_KEY = new class_304("key.emotecraft.fastchoose", class_307.field_1668, 66, KEYBIND_CATEGORY);
   public static final class_304 STOP_EMOTE_KEY = new class_304("key.emotecraft.stop", class_307.field_1668, -1, KEYBIND_CATEGORY);
   private static int tick = 0;

   protected void onInitializeClient() {
      loadEmotes();
      ClientPacketManager.init();
   }

   protected void onClientTick(class_310 minecraft) {
      if (tick++ % 21 == 20) {
         ClientEmotePlay.checkQueue();
      }

      if (OPEN_MENU_KEY.method_1436() && (PlatformTools.getConfig().alwaysOpenEmoteScreen.get() || minecraft.field_1724 == minecraft.method_1560())) {
         minecraft.method_1507(new FastMenuScreen(null));
      }

      if (STOP_EMOTE_KEY.method_1436()) {
         ClientEmotePlay.clientStopLocalEmote();
      }
   }

   public static CompletableFuture<Void> loadEmotes() {
      return CompletableFuture.supplyAsync(UniversalEmoteSerializer::loadEmotes, class_156.method_27958()).thenAccept(emotes -> {
         EmoteHolder.clearEmotes();
         EmoteHolder.addEmoteToList(UniversalEmoteSerializer.getLoadedEmotes(), null);
      }).exceptionally(th -> {
         CommonData.LOGGER.error("Failed to reload emotes!", th);
         return null;
      });
   }

   public static int getTick() {
      return tick;
   }
}
