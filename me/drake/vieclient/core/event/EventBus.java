package me.drake.vieclient.core.event;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import me.drake.vieclient.VieClientMod;
import org.slf4j.Logger;

public class EventBus {
   private static final EventBus.Subscription[] EMPTY_SUBSCRIPTIONS = new EventBus.Subscription[0];
   private final Map subscriptions = new ConcurrentHashMap();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public synchronized void subscribe(Object listener) {
      if (listener != null) {
         HashSet var2 = new HashSet();

         for (Class var3 = listener.getClass(); var3 != null && var3 != Object.class; var3 = var3.getSuperclass()) {
            for (Method var7 : var3.getDeclaredMethods()) {
               EventHandler var8 = var7.getAnnotation(EventHandler.class);
               if (var8 != null) {
                  String var9 = var7.getName() + Arrays.toString(var7.getParameterTypes());
                  if (var2.add(var9)) {
                     Class[] var10 = var7.getParameterTypes();
                     if (var10.length != 1) {
                        VieClientMod.LOGGER
                           .warn(
                              "[EventBus] @EventHandler method {} has {} params, expected 1. Skipping.",
                              var7.getName(),
                              var10.length
                           );
                     } else if (!Event.class.isAssignableFrom(var10[0])) {
                        VieClientMod.LOGGER
                           .warn(
                              "[EventBus] @EventHandler method {} param is not an Event subclass. Skipping.",
                              var7.getName()
                           );
                     } else {
                        Class var11 = var10[0];
                        var7.setAccessible(true);

                        MethodHandle var21;
                        try {
                           var21 = MethodHandles.lookup().unreflect(var7);
                           if (!Modifier.isStatic(var7.getModifiers())) {
                              var21 = var21.bindTo(listener);
                           }

                           var21 = var21.asType(MethodType.methodType(void.class, Event.class));
                        } catch (IllegalAccessException var20) {
                           throw new IllegalArgumentException("Cannot bind event handler " + var7, var20);
                        }

                        EventBus.Subscription var13 = new EventBus.Subscription(listener, var7, var8.priority(), var21);
                        EventBus.Subscription[] var14 = (EventBus.Subscription[])this.subscriptions.get(var11);
                        if (var14 == null) {
                           this.subscriptions.put(var11, new EventBus.Subscription[]{var13});
                        } else {
                           boolean var15 = false;

                           for (EventBus.Subscription var19 : var14) {
                              if (var19.listener() == listener && var19.method().equals(var7)) {
                                 var15 = true;
                                 break;
                              }
                           }

                           if (!var15) {
                              ArrayList var22 = new ArrayList<>(Arrays.asList(var14));
                              var22.add(var13);
                              var22.sort(Comparator.comparingInt(EventBus.Subscription::priority));
                              this.subscriptions.put(var11, var22.toArray(EMPTY_SUBSCRIPTIONS));
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public synchronized void unsubscribe(Object listener) {
      if (listener != null) {
         for (Entry var3 : this.subscriptions.entrySet()) {
            EventBus.Subscription[] var4 = (EventBus.Subscription[])var3.getValue();
            if (var4 != null && var4.length != 0) {
               int var5 = 0;

               for (EventBus.Subscription var9 : var4) {
                  if (var9.listener() != listener) {
                     var5++;
                  }
               }

               if (var5 != var4.length) {
                  if (var5 == 0) {
                     this.subscriptions.remove(var3.getKey());
                  } else {
                     EventBus.Subscription[] var12 = new EventBus.Subscription[var5];
                     int var13 = 0;

                     for (EventBus.Subscription var11 : var4) {
                        if (var11.listener() != listener) {
                           var12[var13++] = var11;
                        }
                     }

                     this.subscriptions.put((Class)var3.getKey(), var12);
                  }
               }
            }
         }
      }
   }

   public Event post(Event event) {
      EventBus.Subscription[] var2 = (EventBus.Subscription[])this.subscriptions.get(event.getClass());
      if (var2 != null && var2.length != 0) {
         for (int var3 = 0; var3 < var2.length; var3++) {
            EventBus.Subscription var4 = var2[var3];

            try {
               var4.dispatch(event);
            } catch (Throwable var6) {
               Logger var10000 = VieClientMod.LOGGER;
               byte[] var10001 = new byte[40];
               var10001[0] = (byte)(99);
               var10001[1] = (byte)(-33);
               var10001[2] = (byte)(106);
               var10001[3] = (byte)(-69);
               var10001[4] = (byte)(43);
               var10001[5] = (byte)(99);
               var10001[6] = (byte)(96);
               var10001[7] = (byte)(-65);
               var10001[8] = (byte)(35);
               var10001[9] = (byte)(-1);
               var10001[10] = (byte)(-60);
               var10001[11] = (byte)(-77);
               var10001[12] = (byte)(111);
               var10001[13] = (byte)(-67);
               var10001[14] = (byte)(101);
               var10001[15] = (byte)(-64);
               var10001[16] = (byte)(72);
               var10001[17] = (byte)(46);
               var10001[18] = (byte)(-91);
               var10001[19] = (byte)(-35);
               var10001[20] = (byte)(-123);
               var10001[21] = (byte)(-122);
               var10001[22] = (byte)(6);
               var10001[23] = (byte)(-7);
               var10001[24] = (byte)(-24);
               var10001[25] = (byte)(123);
               var10001[26] = (byte)(-6);
               var10001[27] = (byte)(33);
               var10001[28] = (byte)(-19);
               var10001[29] = (byte)(-28);
               var10001[30] = (byte)(39);
               var10001[31] = (byte)(98);
               var10001[32] = (byte)(-84);
               var10001[33] = (byte)(85);
               var10001[34] = (byte)(92);
               var10001[35] = (byte)(5);
               var10001[36] = (byte)(-40);
               var10001[37] = (byte)(-103);
               var10001[38] = (byte)(-7);
               var10001[39] = (byte)(87);
               String var7 = $$v_s(var10001, -2152217061488340963L);
               Object[] var10002 = new Object[4];
               var10002[0] = event.getClass().getSimpleName();
               var10002[1] = var4.listener().getClass().getSimpleName();
               var10002[2] = var4.method().getName();
               var10002[3] = var6;
               var10000.error(var7, var10002);
            }
         }

         return event;
      } else {
         return event;
      }
   }

   public boolean hasSubscribers(Class eventType) {
      EventBus.Subscription[] var2 = (EventBus.Subscription[])this.subscriptions.get(eventType);
      return var2 != null && var2.length > 0;
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

   private record Subscription(Object listener, Method method, int priority, MethodHandle invoker) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      void dispatch(Event event) throws Throwable {
         this.invoker.invokeExact((Event)event);
      }
   }
}
