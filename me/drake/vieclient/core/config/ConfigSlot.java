package me.drake.vieclient.core.config;

import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;

public class ConfigSlot {
   private final String code;
   private String name;
   private final long createdAt;
   private long updatedAt;
   private JsonObject data;
   private boolean isOwn;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ConfigSlot(String code, String name, JsonObject data, long timestamp, boolean isOwn) {
      this.code = codex;
      this.name = namex;
      this.data = datax;
      this.createdAt = timestamp;
      this.updatedAt = timestamp;
      this.isOwn = isOwnx;
   }

   public ConfigSlot(String code, String name, JsonObject data) {
      this(codex, namex, datax, System.currentTimeMillis(), false);
   }

   public String getCode() {
      return this.code;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String n) {
      this.name = n;
   }

   public long getCreatedAt() {
      return this.createdAt;
   }

   public long getUpdatedAt() {
      return this.updatedAt;
   }

   public void setUpdatedAt(long t) {
      this.updatedAt = t;
   }

   public JsonObject getData() {
      return this.data;
   }

   public void setData(JsonObject d) {
      this.data = d;
      this.updatedAt = System.currentTimeMillis();
   }

   public boolean isOwn() {
      return this.isOwn;
   }

   public void setOwn(boolean b) {
      this.isOwn = b;
   }

   public void updateFrom(ConfigSlot other) {
      this.name = other.name;
      this.data = other.data;
      this.updatedAt = System.currentTimeMillis();
   }

   public JsonObject toJson() {
      JsonObject var1 = new JsonObject();
      byte[] var10001 = new byte[4];
      var10001[0] = (byte)(74);
      var10001[1] = (byte)(-11);
      var10001[2] = (byte)(120);
      var10001[3] = (byte)(-69);
      var1.addProperty($$v_s(var10001, -3707321262197714932L), this.code);
      var10001 = new byte[4];
      var10001[0] = (byte)(71);
      var10001[1] = (byte)(-5);
      var10001[2] = (byte)(113);
      var10001[3] = (byte)(-69);
      var1.addProperty($$v_s(var10001, -3707321262197714932L), this.name);
      var10001 = new byte[9];
      var10001[0] = (byte)(74);
      var10001[1] = (byte)(-24);
      var10001[2] = (byte)(121);
      var10001[3] = (byte)(-65);
      var10001[4] = (byte)(57);
      var10001[5] = (byte)(73);
      var10001[6] = (byte)(-21);
      var10001[7] = (byte)(-91);
      var10001[8] = (byte)(53);
      var1.addProperty($$v_s(var10001, -3707321262197714932L), this.createdAt);
      var10001 = new byte[9];
      var10001[0] = (byte)(92);
      var10001[1] = (byte)(-22);
      var10001[2] = (byte)(120);
      var10001[3] = (byte)(-65);
      var10001[4] = (byte)(57);
      var10001[5] = (byte)(73);
      var10001[6] = (byte)(-21);
      var10001[7] = (byte)(-91);
      var10001[8] = (byte)(53);
      var1.addProperty($$v_s(var10001, -3707321262197714932L), this.updatedAt);
      var10001 = new byte[5];
      var10001[0] = (byte)(64);
      var10001[1] = (byte)(-23);
      var10001[2] = (byte)(83);
      var10001[3] = (byte)(-87);
      var10001[4] = (byte)(35);
      var1.addProperty($$v_s(var10001, -3707321262197714932L), this.isOwn);
      var10001 = new byte[4];
      var10001[0] = (byte)(77);
      var10001[1] = (byte)(-5);
      var10001[2] = (byte)(104);
      var10001[3] = (byte)(-65);
      var1.add($$v_s(var10001, -3707321262197714932L), this.data);
      return var1;
   }

   public static ConfigSlot fromJson(JsonObject obj) {
      String var1 = obj.has("code")
         ? obj.get("code").getAsString()
         : "";
      String var2 = obj.has("name")
         ? obj.get("name").getAsString()
         : var1;
      long var3 = obj.has("createdAt")
         ? obj.get("createdAt").getAsLong()
         : System.currentTimeMillis();
      long var5 = obj.has("updatedAt")
         ? obj.get("updatedAt").getAsLong()
         : var3;
      boolean var7 = obj.has("isOwn")
         ? obj.get("isOwn").getAsBoolean()
         : false;
      JsonObject var8 = obj.has("data")
         ? obj.getAsJsonObject("data")
         : new JsonObject();
      ConfigSlot var9 = new ConfigSlot(var1, var2, var8, var3, var7);
      var9.setUpdatedAt(var5);
      return var9;
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
}
