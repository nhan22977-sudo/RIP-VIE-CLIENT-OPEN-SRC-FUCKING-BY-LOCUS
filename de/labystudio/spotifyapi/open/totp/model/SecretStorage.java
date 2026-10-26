package de.labystudio.spotifyapi.open.totp.model;

public class SecretStorage {
   private String validUntil;
   private Secret[] secrets;

   public String getValidUntil() {
      return this.validUntil;
   }

   public Secret[] getSecrets() {
      return this.secrets;
   }

   public Secret getLatestSecret() {
      if (this.secrets != null && this.secrets.length != 0) {
         Secret latestSecret = this.secrets[0];

         for (Secret secret : this.secrets) {
            if (secret.getVersion() > latestSecret.getVersion()) {
               latestSecret = secret;
            }
         }

         return latestSecret;
      } else {
         return null;
      }
   }
}
