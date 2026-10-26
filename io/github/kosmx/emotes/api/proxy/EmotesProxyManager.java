package io.github.kosmx.emotes.api.proxy;

import io.github.kosmx.emotes.common.network.EmotePacket;
import java.util.ArrayList;
import java.util.UUID;

public abstract class EmotesProxyManager {
   private static EmotesProxyManager manager = null;
   protected static final ArrayList<INetworkInstance> networkInstances = new ArrayList<>();

   public static boolean registerProxyInstance(INetworkInstance instance) {
      return !networkInstances.contains(instance) ? networkInstances.add(instance) : false;
   }

   public static boolean unregisterProxyInstance(INetworkInstance instance) {
      return networkInstances.remove(instance);
   }

   static void receiveMessage(EmotePacket packet, UUID player, INetworkInstance networkInstance) {
      getManager().dispatchReceive(packet, player, networkInstance);
   }

   static void disconnectInstance(INetworkInstance networkInstance) {
      getManager().onDisconnectFromServer(networkInstance);
   }

   protected static EmotesProxyManager getManager() {
      if (manager == null) {
         throw new IllegalStateException("Emotecraft proxy is NOT loaded. You can only register instances before loading.");
      } else {
         return manager;
      }
   }

   protected static void setManager(EmotesProxyManager newManager) {
      if (manager != null) {
         throw new IllegalArgumentException("You can't set manager twice");
      }

      manager = newManager;
   }

   protected abstract void dispatchReceive(EmotePacket var1, UUID var2, INetworkInstance var3);

   public abstract void onDisconnectFromServer(INetworkInstance var1);
}
