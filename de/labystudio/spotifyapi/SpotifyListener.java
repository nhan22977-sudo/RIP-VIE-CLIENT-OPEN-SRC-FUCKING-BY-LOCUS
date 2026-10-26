package de.labystudio.spotifyapi;

import de.labystudio.spotifyapi.model.Track;

public interface SpotifyListener {
   void onConnect();

   void onTrackChanged(Track var1);

   void onPositionChanged(int var1);

   void onPlayBackChanged(boolean var1);

   void onSync();

   void onDisconnect(Exception var1);
}
