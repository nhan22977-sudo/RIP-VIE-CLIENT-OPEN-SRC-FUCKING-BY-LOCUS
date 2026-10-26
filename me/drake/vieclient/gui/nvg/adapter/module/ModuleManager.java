package me.drake.vieclient.gui.nvg.adapter.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.gui.nvg.adapter.modules.client.BackgroundFX;
import me.drake.vieclient.gui.nvg.adapter.modules.client.Sounds;
import me.drake.vieclient.gui.nvg.adapter.modules.client.VieClient;
import me.drake.vieclient.gui.nvg.adapter.modules.render.HUD;
import me.drake.vieclient.modules.client.ClientSettings;
import me.drake.vieclient.modules.client.MusicHUD;

public class ModuleManager {
   private final Map cache = new IdentityHashMap();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static me.drake.vieclient.core.module.ModuleManager raw() {
      VieClientMod var0 = VieClientMod.getInstance();
      return var0 != null ? var0.getModuleManager() : null;
   }

   public static Category getCategory(me.drake.vieclient.core.module.Category c) {
      return switch (c) {
         case COMBAT -> Category.COMBAT;
         case PLAYER, MOVEMENT -> Category.PLAYER;
         case RENDER -> Category.RENDER;
         case CLIENT -> Category.CLIENT;
         case PROTECT -> Category.PROTECT;
         case DONUT_SMP, SMP_UTILITIES -> Category.DONUT;
         case BASE_FINDING -> Category.BASE_FINDING;
         case CUSTOMIZE -> Category.CUSTOMIZE;
         case WORLD, EXPLOIT -> Category.MISC;
         case CONFIG -> Category.CONFIG;
      };
   }

   static List coreCategoriesFor(Category category) {
      return switch (category) {
         case COMBAT -> List.of(me.drake.vieclient.core.module.Category.COMBAT);
         case PLAYER -> List.of(me.drake.vieclient.core.module.Category.PLAYER, me.drake.vieclient.core.module.Category.MOVEMENT);
         case RENDER -> List.of(me.drake.vieclient.core.module.Category.RENDER);
         case CLIENT -> List.of(me.drake.vieclient.core.module.Category.CLIENT);
         case PROTECT -> List.of(me.drake.vieclient.core.module.Category.PROTECT);
         case MISC -> List.of(me.drake.vieclient.core.module.Category.WORLD, me.drake.vieclient.core.module.Category.EXPLOIT);
         case BASE_FINDING -> List.of(me.drake.vieclient.core.module.Category.BASE_FINDING);
         case CUSTOMIZE -> List.of(me.drake.vieclient.core.module.Category.CUSTOMIZE);
         case DONUT, CONFIG -> List.of();
      };
   }

   public Module wrap(me.drake.vieclient.core.module.Module raw) {
      if (raw == null) {
         return null;
      } else if (raw == HUD.rawModule()) {
         return HUD.instance;
      } else if (raw instanceof MusicHUD) {
         return me.drake.vieclient.gui.nvg.adapter.modules.client.MusicHUD.instance;
      } else {
         return raw instanceof ClientSettings ? VieClient.instance : this.cache.computeIfAbsent(raw, r -> new Module(r));
      }
   }

   public List getModulesByCategory(Category category) {
      me.drake.vieclient.core.module.ModuleManager var2 = raw();
      if (var2 == null) {
         return Collections.emptyList();
      }

      ArrayList var3 = new ArrayList();
      switch (category) {
         case COMBAT:
         case PLAYER:
         case RENDER:
         case CLIENT:
         case PROTECT:
         case MISC:
         case BASE_FINDING:
         case CUSTOMIZE:
            coreCategoriesFor(category).forEach(cat -> this.collect(var2, cat, var3));
            break;
         case DONUT:
            for (me.drake.vieclient.core.module.Module var5 : var2.getModules()) {
               me.drake.vieclient.core.module.Category var6 = var5.getCategory();
               if ((var6 == me.drake.vieclient.core.module.Category.SMP_UTILITIES || var6 == me.drake.vieclient.core.module.Category.DONUT_SMP)
                  && var5.isVisibleInClickGui()) {
                  var3.add(this.wrap(var5));
               }
            }
         case CONFIG:
      }

      return var3;
   }

   private void collect(me.drake.vieclient.core.module.ModuleManager mm, me.drake.vieclient.core.module.Category cat, List out) {
      for (me.drake.vieclient.core.module.Module var5 : mm.getModules(cat)) {
         if (!(var5 instanceof me.drake.vieclient.modules.client.HUD) && !(var5 instanceof MusicHUD) && var5.isVisibleInClickGui()) {
            out.add(this.wrap(var5));
         }
      }
   }

   public Module getModuleByClass(Class clazz) {
      if (clazz == HUD.class) {
         return HUD.instance;
      }

      if (clazz == me.drake.vieclient.gui.nvg.adapter.modules.client.MusicHUD.class) {
         return me.drake.vieclient.gui.nvg.adapter.modules.client.MusicHUD.instance;
      }

      if (clazz == Sounds.class) {
         return Sounds.instance;
      }

      if (clazz == BackgroundFX.class) {
         return BackgroundFX.instance;
      }

      if (clazz == VieClient.class) {
         return VieClient.instance;
      }

      me.drake.vieclient.core.module.ModuleManager var2 = raw();
      if (var2 == null) {
         return null;
      }

      if (clazz == MusicHUD.class) {
         return me.drake.vieclient.gui.nvg.adapter.modules.client.MusicHUD.instance;
      }

      if (clazz == me.drake.vieclient.modules.client.HUD.class) {
         return HUD.instance;
      }

      if (clazz == ClientSettings.class) {
         return VieClient.instance;
      }

      for (me.drake.vieclient.core.module.Module var4 : var2.getModules()) {
         if (clazz.isInstance(var4) || var4.getClass() == clazz) {
            return this.wrap(var4);
         }
      }

      return null;
   }

   public List getModules() {
      me.drake.vieclient.core.module.ModuleManager var1 = raw();
      if (var1 == null) {
         return Collections.emptyList();
      }

      ArrayList var2 = new ArrayList();

      for (me.drake.vieclient.core.module.Module var4 : var1.getModules()) {
         var2.add(this.wrap(var4));
      }

      return var2;
   }
}
