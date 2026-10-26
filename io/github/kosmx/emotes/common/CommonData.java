package io.github.kosmx.emotes.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonData {
   public static final String MOD_ID = "emotecraft";
   public static final String MOD_NAME = "Emotecraft";
   public static final Logger LOGGER = LoggerFactory.getLogger("Emotecraft");
   public static final byte networkingVersion = 8;
   public static final String playEmoteID = "emote";
   public static final String emoteStreamID = "stream";
   public static final int MAX_PACKET_SIZE = 1048576;

   public static String getIDAsString(String channel) {
      return "emotecraft:" + channel;
   }
}
