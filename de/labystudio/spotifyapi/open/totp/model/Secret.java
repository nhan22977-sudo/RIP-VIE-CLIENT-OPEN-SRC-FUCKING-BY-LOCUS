package de.labystudio.spotifyapi.open.totp.model;

public class Secret {
   private final int[] secret;
   private final int version;

   private Secret(int[] secret, int version) {
      this.secret = secret;
      this.version = version;
   }

   public String getSecretAsString() {
      StringBuilder sb = new StringBuilder();

      for (int i : this.secret) {
         sb.append((char)i);
      }

      return sb.toString();
   }

   public byte[] getSecretAsBytes() {
      StringBuilder xorResults = new StringBuilder();

      for (int i = 0; i < this.secret.length; i++) {
         int result = this.secret[i] ^ i % 33 + 9;
         xorResults.append(result);
      }

      StringBuilder hexResult = new StringBuilder();

      for (int i = 0; i < xorResults.length(); i++) {
         hexResult.append(String.format("%02x", Integer.valueOf(xorResults.charAt(i))));
      }

      byte[] byteArray = new byte[hexResult.length() / 2];

      for (int i = 0; i < hexResult.length(); i += 2) {
         int byteValue = Integer.parseInt(hexResult.substring(i, i + 2), 16);
         byteArray[i / 2] = (byte)byteValue;
      }

      return byteArray;
   }

   public int getVersion() {
      return this.version;
   }

   public static Secret fromNumbers(int[] secret, int version) {
      return new Secret(secret, version);
   }

   public static Secret fromString(String secret, int version) {
      int[] array = new int[secret.length()];

      for (int i = 0; i < secret.length(); i++) {
         array[i] = secret.charAt(i);
      }

      return new Secret(array, version);
   }
}
