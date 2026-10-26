package me.drake.vieclient.modules.client.music;

import java.util.List;
import java.util.Optional;

public final class MusicProviderChain implements AutoCloseable {
   private final List providers;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public MusicProviderChain(List providers) {
      this.providers = List.copyOf(providersx);
   }

   public Optional poll() {
      for (MusicProvider var2 : this.providers) {
         try {
            Optional var3 = var2.poll();
            if (var3.isPresent()) {
               return var3;
            }
         } catch (Throwable var4) {
         }
      }

      return Optional.empty();
   }

   public Optional pollOnly(String providerId) {
      if (providerId != null && !providerId.isBlank()) {
         for (MusicProvider var3 : this.providers) {
            if (providerId.equals(var3.id())) {
               try {
                  return var3.poll();
               } catch (Throwable var5) {
                  return Optional.empty();
               }
            }
         }

         return Optional.empty();
      } else {
         return Optional.empty();
      }
   }

   @Override
   public void close() {
      for (MusicProvider var2 : this.providers) {
         try {
            var2.close();
         } catch (Throwable var4) {
         }
      }
   }
}
