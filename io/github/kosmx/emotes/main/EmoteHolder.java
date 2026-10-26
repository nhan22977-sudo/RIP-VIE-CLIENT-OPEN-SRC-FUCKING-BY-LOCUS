package io.github.kosmx.emotes.main;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zigythebird.playeranim.util.ClientUtil;
import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.animation.ExtraAnimationData;
import com.zigythebird.playeranimcore.loading.UniversalAnimLoader;
import io.github.kosmx.emotes.PlatformTools;
import io.github.kosmx.emotes.api.proxy.INetworkInstance;
import io.github.kosmx.emotes.common.CommonData;
import io.github.kosmx.emotes.common.tools.MathHelper;
import io.github.kosmx.emotes.common.tools.UUIDMap;
import io.github.kosmx.emotes.main.network.ClientEmotePlay;
import io.github.kosmx.emotes.mc.McUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_5244;
import net.minecraft.class_5455;
import net.minecraft.class_742;
import net.minecraft.class_3675.class_306;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmoteHolder implements Supplier<UUID> {
   public final Animation emote;
   public final class_2561 name;
   public final class_2561 description;
   public final class_2561 author;
   public final List<class_2561> folder;
   public final List<class_2561> bages;
   @Nullable
   public final class_2561 fileName;
   public AtomicInteger hash = null;
   public static UUIDMap<EmoteHolder> list = new UUIDMap<>();
   @Nullable
   private class_2960 iconIdentifier = null;
   @Nullable
   private INetworkInstance fromInstance = null;

   public EmoteHolder(Animation emote) {
      this.emote = emote;
      this.name = emote.data().<Object>get("name").map(McUtils::fromJson).orElseThrow();
      this.description = emote.data().<Object>get("description").map(McUtils::fromJson).orElse(class_5244.field_39003);
      this.author = emote.data().<Object>get("author").map(McUtils::fromJson).orElse(class_5244.field_39003);
      this.folder = computeFolderPath((String)emote.data().getRaw("folderpath"));
      this.bages = computeBages((List<String>)emote.data().getRaw("bages"));
      this.fileName = emote.data().<Object>get("fileName").map(McUtils::fromJson).orElse(null);
   }

   private static List<class_2561> computeFolderPath(String folderPath) {
      return StringUtils.isBlank(folderPath)
         ? Collections.emptyList()
         : Arrays.stream(folderPath.split("/")).<class_2561>map(class_2561::method_43470).collect(Collectors.toUnmodifiableList());
   }

   private static List<class_2561> computeBages(List<String> bages) {
      if (bages != null && !bages.isEmpty()) {
         List<class_2561> components = new ArrayList<>(bages.size());

         for (String element : bages) {
            try {
               components.add(McUtils.fromJson(element, class_5455.field_40585));
            } catch (Throwable th) {
               CommonData.LOGGER.warn("Failed to serialize bage!", th);
            }
         }

         return Collections.unmodifiableList(components);
      } else {
         return Collections.emptyList();
      }
   }

   public static void clearEmotes() {
      clearEmotes(null);
   }

   public static void clearEmotes(INetworkInstance networkInstance) {
      list.removeIf(emoteHolder -> {
         if (emoteHolder.fromInstance != networkInstance) {
            return false;
         }

         emoteHolder.closeIcon();
         return true;
      });
   }

   @Nullable
   public class_2960 getIconIdentifier() {
      if (this.emote.data().getBinary("iconData") instanceof ByteBuffer buff && this.iconIdentifier == null) {
         this.registerIcon(buff);
      }

      return this.iconIdentifier;
   }

   private void registerIcon(ByteBuffer buffer) {
      RenderSystem.assertOnRenderThread();

      try (InputStream stream = new ByteArrayInputStream(MathHelper.safeGetBytesFromBuffer(buffer))) {
         this.iconIdentifier = McUtils.newIdentifier("icon" + this.hashCode());
         class_310.method_1551().method_1531().method_4616(this.iconIdentifier, new class_1043(this.iconIdentifier::toString, class_1011.method_4309(stream)));
      } catch (Throwable th) {
         CommonData.LOGGER.warn("Can't open emote {} icon!", this.emote, th);
      }
   }

   private void closeIcon() {
      if (this.iconIdentifier != null) {
         if (RenderSystem.isOnRenderThread()) {
            class_310.method_1551().method_1531().method_4615(this.iconIdentifier);
         } else {
            class_2960 iconIdentifier = this.iconIdentifier;
            class_310.method_1551().method_19537(() -> class_310.method_1551().method_1531().method_4615(iconIdentifier));
         }

         this.iconIdentifier = null;
      }
   }

   public Animation getEmote() {
      return this.emote;
   }

   public static EmoteHolder getEmoteFromUuid(UUID uuid) {
      return list.get(uuid);
   }

   public static EmoteHolder findIfPresent(Animation animation) {
      if (animation == null) {
         return null;
      }

      EmoteHolder fast = getEmoteFromUuid(animation.uuid());
      if (fast != null && fast.emote != null && fast.emote.equals(animation)) {
         return fast;
      }

      for (EmoteHolder holder : list) {
         if (holder.emote != null && holder.emote.equals(animation)) {
            return holder;
         }
      }

      return null;
   }

   public static void addEmoteToList(Iterable<Animation> emotes, @Nullable INetworkInstance fromInstance) {
      for (Animation emote : emotes) {
         addEmoteToList(emote, fromInstance);
      }
   }

   public static EmoteHolder addEmoteToList(Animation emote, @Nullable INetworkInstance fromInstance) {
      EmoteHolder old = findIfPresent(emote);
      if (old != null) {
         return old;
      }

      EmoteHolder newEmote = new EmoteHolder(emote);
      newEmote.fromInstance = fromInstance;
      list.add(newEmote);
      return newEmote;
   }

   public static boolean playEmote(class_742 player, Animation emote) {
      return canPlayEmote(player) && ClientEmotePlay.clientStartLocalEmote(emote);
   }

   private static boolean canPlayEmote(class_742 entity) {
      return !canRunEmote(entity) ? false : entity.isMainAvatar();
   }

   public static boolean canRunEmote(class_742 player) {
      return !(
         new class_243(player.method_23317(), player.method_23318(), player.method_23321())
               .method_1022(
                  new class_243(
                     player.field_6014,
                     class_3532.method_16436(PlatformTools.getConfig().yRatio.get().floatValue(), player.field_6036, player.method_23318()),
                     player.field_5969
                  )
               )
            > PlatformTools.getConfig().stopThreshold.get().floatValue()
      );
   }

   public boolean playEmote() {
      return playEmote(ClientUtil.getClientPlayer(), this.emote);
   }

   @Override
   public int hashCode() {
      if (this.hash == null) {
         this.hash = new AtomicInteger(this.emote.hashCode());
      }

      return this.hash.get();
   }

   public UUID getUuid() {
      return this.emote.uuid();
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof EmoteHolder other && this.emote.equals(other.emote);
   }

   public UUID get() {
      return this.emote.get();
   }

   public static void handleKeyPress(class_306 key) {
      if (canRunEmote(ClientUtil.getClientPlayer())) {
         UUID uuid = PlatformTools.getConfig().emoteKeyMap.getL(key);
         if (uuid != null) {
            EmoteHolder emoteHolder = list.get(uuid);
            if (emoteHolder != null) {
               emoteHolder.playEmote();
            }
         }
      }
   }

   public static EmoteHolder getNonNull(@NotNull UUID emote) {
      EmoteHolder emoteHolder = list.get(emote);
      return emoteHolder == null ? new EmoteHolder.Empty(emote) : emoteHolder;
   }

   public static class Empty extends EmoteHolder {
      public Empty(UUID uuid) {
         super(
            new Animation(
               new ExtraAnimationData("name", "{\"color\":\"red\",\"text\":\"INVALID\"}"),
               0.0F,
               Animation.LoopType.PLAY_ONCE,
               Collections.emptyMap(),
               UniversalAnimLoader.NO_KEYFRAMES,
               new HashMap<>(),
               new HashMap<>()
            )
         );
         this.emote.data().put("uuid", uuid);
      }
   }
}
