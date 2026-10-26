package me.drake.vieclient.modules.client;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.BackdropPrepareEvent;
import me.drake.vieclient.core.event.events.PostBlitRenderEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.gui.nvg.animation.AnimationManager;
import me.drake.vieclient.gui.nvg.ui.components.GuiStyle;
import me.drake.vieclient.gui.nvg.ui.components.SoftPalette;
import me.drake.vieclient.gui.nvg.ui.components.ThemeManager;
import me.drake.vieclient.modules.client.music.AppleMusicProvider;
import me.drake.vieclient.modules.client.music.JdkArtworkLoader;
import me.drake.vieclient.modules.client.music.JdkHttpTextClient;
import me.drake.vieclient.modules.client.music.MusicMetadataService;
import me.drake.vieclient.modules.client.music.MusicProviderChain;
import me.drake.vieclient.modules.client.music.MusicSnapshot;
import me.drake.vieclient.modules.client.music.MusicSource;
import me.drake.vieclient.modules.client.music.PowerShellGsmtcBridge;
import me.drake.vieclient.modules.client.music.ProcessMusicProvider;
import me.drake.vieclient.modules.client.music.SoundCloudAppProvider;
import me.drake.vieclient.modules.client.music.SoundCloudMetadataResolver;
import me.drake.vieclient.modules.client.music.SpotifyMetadataResolver;
import me.drake.vieclient.modules.client.music.SpotifyMusicProvider;
import me.drake.vieclient.modules.client.music.WindowScanner;
import me.drake.vieclient.modules.client.music.YouTubeBrowserProvider;
import me.drake.vieclient.modules.client.music.YouTubeMetadataResolver;
import me.drake.vieclient.modules.client.spotify.Lyrics;
import me.drake.vieclient.modules.client.spotify.LyricsQuery;
import me.drake.vieclient.modules.client.spotify.LyricsService;
import me.drake.vieclient.modules.client.spotify.SyncedLyrics;
import me.drake.vieclient.render.FrostedBackground;
import me.drake.vieclient.render.nvg.IconManager;
import me.drake.vieclient.render.nvg.MaterialFX;
import me.drake.vieclient.render.nvg.NVGIcons;
import me.drake.vieclient.render.nvg.NVGImage;
import me.drake.vieclient.render.nvg.NVGRenderer;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.render.nvg.NanoVGResourceGeneration;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

public final class MusicHUD extends Module {
   private static final int[] LYRIC_LINE_COUNT = new int[]{1, 2, 3};
   private static final float LYRIC_FONT_U = 8.0F;
   private static final float LYRIC_ROW_GAP_U = 6.0F;
   private static final float LYRIC_LINE_GAP_U = 6.0F;
   private static final float LYRIC_TOP_PAD_U = 4.0F;
   private static final float LYRIC_BOT_PAD_U = 2.0F;
   private static final float LABEL_SIZE_U = 7.0F;
   private static final float LYRICS_GAP_U = 6.0F;
   private static final float LYRIC_RISE_U = 8.0F;
   private static final float LYRIC_SPRING = 13.0F;
   private static final float LYRIC_SETTLE_TIME = 1.1F;
   private static final float LYRIC_STAGGER = 0.045F;
   private static final int LYRIC_MAX_STAGGERED = 6;
   private static final int LYRIC_MAX_SHIFTS = 8;
   private static final float LYRIC_GLOW_TIME = 0.35F;
   private static final float LYRIC_ARRIVE_TIME = 0.3F;
   private static final float LYRIC_FADE_TIME = 0.32F;
   private static final float LYRIC_RISE = 8.0F;
   private static final int LYRIC_MAX_ROWS = 4;
   private static final float LYRIC_GROW_SPEED = 14.0F;
   private static final float LYRIC_SHRINK_SPEED = 9.0F;
   private static final float LYRIC_FAR_ALPHA = 0.6F;
   private static final float LYRIC_PAUSED_ALPHA = 0.7F;
   private static final float LYRIC_PAUSE_SPEED = 8.0F;
   private static final float LYRIC_BREATH_PERIOD = 1.6F;
   private static final float LYRIC_BREATH_RADIUS = 1.6F;
   private static final float LYRIC_BREATH_DOT_STEP = 6.0F;
   private static final long LYRIC_LEAD_MS = 200L;
   private static final long LYRIC_SWEEP_MS = 180L;
   private static final float LYRIC_SLOW_PACE = 2.0F;
   private static final float LYRIC_CHAR_MS = 90.0F;
   private static final float LYRIC_QUIET_MS_F = 0.7F;
   private static final float LYRIC_MIN_COVERAGE = 0.75F;
   private static final int LYRIC_LOADING_MIN_MS = 700;
   public static final EnumSetting sourcePriority = new EnumSetting(
         "Source Priority",
         "Chooses the only music source MusicHUD may display",
         MusicHUD.SourcePriority.AUTOMATIC
      )
      .withDisplayName(MusicHUD.SourcePriority.AUTOMATIC, "Automatic")
      .withDisplayName(MusicHUD.SourcePriority.SPOTIFY, "Spotify")
      .withDisplayName(MusicHUD.SourcePriority.APPLE_MUSIC, "Apple Music")
      .withDisplayName(MusicHUD.SourcePriority.SOUNDCLOUD, "SoundCloud")
      .withDisplayName(
         MusicHUD.SourcePriority.OTHER_MUSIC_APPS,
         "Other Music Apps"
      )
      .withDisplayName(MusicHUD.SourcePriority.YOUTUBE, "YouTube");
   public static final BoolSetting showArtist = new BoolSetting(
      "Show Artist",
      "Shows the artist name",
      true
   );
   public static final BoolSetting showProgress = new BoolSetting(
      "Show Progress",
      "Shows the progress bar",
      true
   );
   public static final DoubleSetting scale = new DoubleSetting(
      "Scale",
      "Widget scale",
      1.0,
      0.5,
      2.0,
      0.05
   );
   public static final EnumSetting titleMode = new EnumSetting(
      "Title Overflow",
      "Long title behaviour",
      MusicHUD.TitleMode.EXPAND
   );
   public static final BoolSetting trackTransition = new BoolSetting(
      "Track Transition",
      "Animates track changes",
      true
   );
   public static final BoolSetting hideWhenPaused = new BoolSetting(
      "Hide When Paused",
      "Hides MusicHUD when playback is paused or inactive",
      false
   );
   public static final ColorSetting outlineColor = new ColorSetting(
      "Outline Color",
      "Card outline colour",
      -925093196
   );
   public static final BoolSetting showLyrics = new BoolSetting(
      "Show Lyrics",
      "Render synced/plain lyrics under the title",
      true
   );
   public static final EnumSetting lyricLines = new EnumSetting(
         "Line Count",
         "How many lyric lines to render",
         MusicHUD.LyricLines.TWO
      )
      .withDisplayName(MusicHUD.LyricLines.ONE, "1")
      .withDisplayName(MusicHUD.LyricLines.TWO, "2")
      .withDisplayName(MusicHUD.LyricLines.THREE, "3");
   public static final EnumSetting lyricMotion = new EnumSetting(
         "Line Animation",
         "How the active line transitions in",
         MusicHUD.LyricMotion.WAVE
      )
      .withDisplayName(MusicHUD.LyricMotion.WAVE, "Wave")
      .withDisplayName(MusicHUD.LyricMotion.GLIDE, "Slide")
      .withDisplayName(MusicHUD.LyricMotion.FADE, "Fade");
   public static final EnumSetting lyricHighlight = new EnumSetting(
         "Line Style",
         "Active line style",
         MusicHUD.LyricHighlight.KARAOKE
      )
      .withDisplayName(MusicHUD.LyricHighlight.KARAOKE, "Sweep")
      .withDisplayName(MusicHUD.LyricHighlight.LINE, "Block")
      .withDisplayName(MusicHUD.LyricHighlight.OFF, "None");
   public static final BoolSetting lyricsOnline = new BoolSetting(
      "Online Lyrics",
      "Fetch from LRCLib (turn off to use cached results only)",
      true
   );
   public static final ColorSetting lyricsColor = new ColorSetting(
      "Lyrics Color",
      "Upcoming lyric line colour",
      -1052689
   );
   public static final ColorSetting upcomingLyricsColor = new ColorSetting(
      "Dim Color",
      "Faded upcoming lyric colour",
      -7697782
   );
   public static final ColorSetting highlightColor = new ColorSetting(
      "Active Color",
      "Active lyric line colour",
      -14829228
   );
   private static volatile String trackName = "Not Playing";
   private static volatile String artistName = "Open a music player";
   private static volatile boolean isPlaying = false;
   private static volatile MusicHUD.PlaybackState playbackState;
   private static int cachedImageId = -1;
   private long cachedImageGeneration = -1L;
   private static BufferedImage cachedBufferedImage = null;
   private final AtomicReference pendingImage = new AtomicReference();
   private final Object pipelineLock = new Object();
   private final JdkArtworkLoader artworkLoader = new JdkArtworkLoader();
   private volatile MusicProviderChain providerChain;
   private MusicMetadataService metadataService;
   private ScheduledExecutorService pollExecutor;
   private ExecutorService artworkExecutor;
   private volatile long pipelineEpoch;
   private boolean pipelineStarting;
   private volatile String uploadedArtworkIdentity = "";
   private volatile String pendingArtworkIdentity = "";
   private volatile String requestedArtworkUrl = "";
   private volatile boolean clearCachedImageRequested;
   private long lastFrameMs = 0L;
   private float lastDt = 0.0F;
   private long transStartMs = 0L;
   private float transA = 1.0F;
   private float transSlide = 0.0F;
   private static final long TRANS_MS = 560L;
   private int frozenPosMs = 0;
   private long frozenPosAtMs = 0L;
   private boolean trackReportsPosition = false;
   private int consecutiveEmptyPolls = 0;
   private float visibilityAlpha = 1.0F;
   private float discRot = 0.0F;
   private float discSpeed = 0.0F;
   private float marqueeOffset = 0.0F;
   private boolean marqueeScrolling = false;
   private long marqueeTimer = 0L;
   private String lastMarqueeTrack = "";
   private static final long MARQUEE_PAUSE_MS = 10000L;
   private static final float MARQUEE_SPEED = 26.0F;
   private static final int MARQUEE_GAP = 34;
   private static final float TITLE_SIZE_U = 9.0F;
   private static final float SECONDARY_SIZE_U = 8.0F;
   private float animW = 0.0F;
   private final LyricsService lyricsService = new LyricsService();
   private volatile Lyrics currentLyrics = Lyrics.NOT_FOUND;
   private volatile SyncedLyrics.Line[] lyricLinesArr = SyncedLyrics.EMPTY.allLines();
   private volatile int lyricActiveIndex = -1;
   private volatile String lyricTrackKey = "";
   private volatile long lyricActiveStartMs = 0L;
   private volatile long lyricActiveEndMs = 0L;
   private SyncedLyrics lyricSource;
   private int lyricStart;
   private final List lyricShifts = new ArrayList();
   private float lyricClock;
   private List lyricFadeItems;
   private float lyricFade = 1.0F;
   private List lyricLastItems = List.of();
   private List lyricLeaveItems;
   private float lyricLeave;
   private float[] lyricShown = new float[0];
   private float lyricBreath;
   private int lyricCurrentFirst = -1;
   private int lyricCurrentLast = -1;
   private float[] lyricBright = new float[0];
   private float lyricScale = 1.0F;
   private int lyricBrightLo;
   private int lyricBrightHi = -1;
   private float lyricBlockH;
   private long lyricLoadingSince;
   private SyncedLyrics lyricLastSynced;
   private String[] lyricTexts = new String[0];
   private String[][] lyricRows = new String[0][];
   private int lyricRowsWidth;
   private MusicHUD.LyricWords[] lyricWords = new MusicHUD.LyricWords[0];
   private float lyricCharMs = 90.0F;
   private boolean lyricReadable = true;
   private float lyricViewH = -1.0F;
   private int lyricViewLines;
   private float lyricViewScale;
   private String lyricStatus;
   private float lyricBlockHRendered;
   private float bx;
   private float by;
   private float sc;
   private float W_u;
   private float H_u;
   private float widgetW;
   private boolean glassReadyThisFrame;
   private boolean backdropQueuedThisFrame;
   private float pad_u;
   private float discD_u;
   private float upperH_u;
   private float lowerH_u;
   private int nvgPosMs;
   private int nvgDurMs;
   private static final float[] WAVEFORM = new float[]{
      0.3F,
      0.55F,
      0.75F,
      0.45F,
      0.9F,
      0.6F,
      0.8F,
      0.4F,
      0.65F,
      1.0F,
      0.7F,
      0.85F,
      0.5F,
      0.95F,
      0.75F,
      0.45F,
      0.85F,
      0.6F,
      1.0F,
      0.8F,
      0.55F,
      0.9F,
      0.65F,
      0.4F,
      0.85F,
      0.7F,
      0.55F,
      0.8F,
      0.45F,
      0.95F,
      0.6F,
      0.75F,
      0.5F,
      0.35F,
      0.65F,
      0.8F,
      0.45F,
      0.9F,
      0.55F,
      0.7F
   };
   private static final long PROVIDER_CLOSE_GRACE_MS = 30000L;
   private float currentMusicFitScale = 1.0F;
   private float currentMusicFitCx = 0.0F;
   private float currentMusicFitCy = 0.0F;
   private List _lastPlannedItems;
   private long lyricLastFrameMs = 0L;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public MusicHUD() {
      super(
         "MusicHUD",
         "Shows music from native players, desktop apps and YouTube",
         Category.CLIENT,
         -1
      );
      this.setEnabled(true);
      this.addSetting(sourcePriority);
      this.addSetting(showArtist);
      this.addSetting(showProgress);
      this.addSetting(scale);
      this.addSetting(titleMode);
      this.addSetting(trackTransition);
      this.addSetting(hideWhenPaused);
      this.addSetting(outlineColor);
      this.addSetting(showLyrics);
      this.addSetting(lyricLines);
      this.addSetting(lyricMotion);
      this.addSetting(lyricHighlight);
      this.addSetting(lyricsOnline);
      this.addSetting(lyricsColor);
      this.addSetting(upcomingLyricsColor);
      this.addSetting(highlightColor);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.ensurePipeline();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.shutdownPipeline();
      this.resetState();
      this.clearHUDBounds();
   }

   private void clearHUDBounds() {
      HUD var1 = this.hudEditor();
      if (var1 != null) {
         byte[] var10001 = new byte[7];
         var10001[0] = (byte)(24);
         var10001[1] = (byte)(-22);
         var10001[2] = (byte)(115);
         var10001[3] = (byte)(-86);
         var10001[4] = (byte)(-81);
         var10001[5] = (byte)(-111);
         var10001[6] = (byte)(-84);
         var1.removeExternalBounds($$v_s(var10001, 8851307744329977934L));
      }
   }

   private void resetState() {
      playbackState = null;
      byte[] var10000 = new byte[11];
      var10000[0] = (byte)(37);
      var10000[1] = (byte)(-11);
      var10000[2] = (byte)(104);
      var10000[3] = (byte)(-2);
      var10000[4] = (byte)(-106);
      var10000[5] = (byte)(-101);
      var10000[6] = (byte)(-76);
      var10000[7] = (byte)(43);
      var10000[8] = (byte)(106);
      var10000[9] = (byte)(-52);
      var10000[10] = (byte)(-125);
      trackName = $$v_s(var10000, 8851307744329977934L);
      var10000 = new byte[19];
      var10000[0] = (byte)(36);
      var10000[1] = (byte)(-22);
      var10000[2] = (byte)(121);
      var10000[3] = (byte)(-80);
      var10000[4] = (byte)(-26);
      var10000[5] = (byte)(-106);
      var10000[6] = (byte)(-11);
      var10000[7] = (byte)(63);
      var10000[8] = (byte)(118);
      var10000[9] = (byte)(-47);
      var10000[10] = (byte)(-115);
      var10000[11] = (byte)(-107);
      var10000[12] = (byte)(-66);
      var10000[13] = (byte)(95);
      var10000[14] = (byte)(-111);
      var10000[15] = (byte)(75);
      var10000[16] = (byte)(66);
      var10000[17] = (byte)(47);
      var10000[18] = (byte)(-66);
      artistName = $$v_s(var10000, 8851307744329977934L);
      isPlaying = false;
      this.frozenPosMs = 0;
      this.frozenPosAtMs = 0L;
      this.trackReportsPosition = false;
      this.consecutiveEmptyPolls = 0;
      this.pendingImage.set(null);
      this.requestedArtworkUrl = "";
      this.uploadedArtworkIdentity = "";
      this.pendingArtworkIdentity = "";
      this.clearCachedImageRequested = true;
      this.resetMarquee();
      this.currentLyrics = Lyrics.NOT_FOUND;
      this.lyricLinesArr = SyncedLyrics.EMPTY.allLines();
      this.lyricActiveIndex = -1;
      this.lyricTrackKey = "";
   }

   private void ensurePipeline() {
      if (this.isEnabled()) {
         long var1;
         synchronized (this.pipelineLock) {
            if (this.providerChain != null || this.pipelineStarting) {
               return;
            }

            this.pipelineStarting = true;
            var1 = ++this.pipelineEpoch;
         }

         Runnable var10002 = () -> this.startPipeline(var1);
         byte[] var10003 = new byte[15];
         var10003[0] = (byte)(29);
         var10003[1] = (byte)(-13);
         var10003[2] = (byte)(121);
         var10003[3] = (byte)(-13);
         var10003[4] = (byte)(-85);
         var10003[5] = (byte)(-126);
         var10003[6] = (byte)(-90);
         var10003[7] = (byte)(59);
         var10003[8] = (byte)(96);
         var10003[9] = (byte)(-113);
         var10003[10] = (byte)(-105);
         var10003[11] = (byte)(-126);
         var10003[12] = (byte)(-1);
         var10003[13] = (byte)(93);
         var10003[14] = (byte)(-119);
         Thread var6 = new Thread(var10002, $$v_s(var10003, 8851307744329977934L));
         var6.setDaemon(true);
         var6.start();
      }
   }

   private void startPipeline(long epoch) {
      MusicMetadataService var3 = null;
      MusicProviderChain var4 = null;
      ScheduledExecutorService var5 = null;
      ExecutorService var6 = null;

      try {
         JdkHttpTextClient var7 = new JdkHttpTextClient();
         Map var17 = Map.of(
            MusicSource.SPOTIFY_API,
            new SpotifyMetadataResolver(var7),
            MusicSource.YOUTUBE_BROWSER,
            new YouTubeMetadataResolver(var7),
            MusicSource.SOUNDCLOUD_APP,
            new SoundCloudMetadataResolver(var7)
         );
         var3 = MusicMetadataService.createDefault(var17);
         WindowScanner var9 = WindowScanner.system();
         var4 = new MusicProviderChain(
            List.of(
               new SpotifyMusicProvider(var9, var3),
               new AppleMusicProvider(),
               new SoundCloudAppProvider(PowerShellGsmtcBridge.open(), var3),
               new ProcessMusicProvider(var9),
               new YouTubeBrowserProvider(PowerShellGsmtcBridge.openBrowser(), var9, var3)
            )
         );
         var5 = Executors.newSingleThreadScheduledExecutor(runnable -> {
            byte[] var10003 = new byte[14];
            var10003[0] = (byte)(29);
            var10003[1] = (byte)(-13);
            var10003[2] = (byte)(121);
            var10003[3] = (byte)(-13);
            var10003[4] = (byte)(-85);
            var10003[5] = (byte)(-126);
            var10003[6] = (byte)(-90);
            var10003[7] = (byte)(59);
            var10003[8] = (byte)(96);
            var10003[9] = (byte)(-113);
            var10003[10] = (byte)(-108);
            var10003[11] = (byte)(-103);
            var10003[12] = (byte)(-14);
            var10003[13] = (byte)(67);
            Thread var1 = new Thread(runnable, $$v_s(var10003, 8851307744329977934L));
            var1.setDaemon(true);
            return var1;
         });
         var6 = Executors.newSingleThreadExecutor(runnable -> {
            byte[] var10003 = new byte[17];
            var10003[0] = (byte)(29);
            var10003[1] = (byte)(-13);
            var10003[2] = (byte)(121);
            var10003[3] = (byte)(-13);
            var10003[4] = (byte)(-85);
            var10003[5] = (byte)(-126);
            var10003[6] = (byte)(-90);
            var10003[7] = (byte)(59);
            var10003[8] = (byte)(96);
            var10003[9] = (byte)(-113);
            var10003[10] = (byte)(-123);
            var10003[11] = (byte)(-124);
            var10003[12] = (byte)(-22);
            var10003[13] = (byte)(88);
            var10003[14] = (byte)(-110);
            var10003[15] = (byte)(88);
            var10003[16] = (byte)(80);
            Thread var1 = new Thread(runnable, $$v_s(var10003, 8851307744329977934L));
            var1.setDaemon(true);
            return var1;
         });
      } catch (Throwable var14) {
         discardPipeline(var4, var3, var5, var6);
         synchronized (this.pipelineLock) {
            this.pipelineStarting = false;
            return;
         }
      }

      boolean var16 = false;
      synchronized (this.pipelineLock) {
         this.pipelineStarting = false;
         if (this.isEnabled() && this.pipelineEpoch == epoch && this.providerChain == null) {
            this.metadataService = var3;
            this.providerChain = var4;
            this.pollExecutor = var5;
            this.artworkExecutor = var6;
            var16 = true;
         }
      }

      if (!var16) {
         discardPipeline(var4, var3, var5, var6);
      } else {
         MusicProviderChain var19 = var4;

         try {
            var5.scheduleWithFixedDelay(() -> this.pollProviders(epoch, var19), 0L, 200L, TimeUnit.MILLISECONDS);
         } catch (RuntimeException var13) {
         }
      }
   }

   private static void discardPipeline(MusicProviderChain chain, MusicMetadataService metadata, ScheduledExecutorService poll, ExecutorService artwork) {
      if (poll != null) {
         poll.shutdownNow();
      }

      if (artwork != null) {
         artwork.shutdownNow();
      }

      try {
         if (chain != null) {
            chain.close();
         }
      } catch (Throwable var6) {
      }

      try {
         if (metadata != null) {
            metadata.close();
         }
      } catch (Throwable var5) {
      }
   }

   private void shutdownPipeline() {
      ScheduledExecutorService var1;
      ExecutorService var2;
      MusicProviderChain var3;
      MusicMetadataService var4;
      synchronized (this.pipelineLock) {
         this.pipelineEpoch++;
         var1 = this.pollExecutor;
         var2 = this.artworkExecutor;
         var3 = this.providerChain;
         var4 = this.metadataService;
         this.pollExecutor = null;
         this.artworkExecutor = null;
         this.providerChain = null;
         this.metadataService = null;
      }

      if (var2 != null) {
         var2.shutdownNow();
      }

      if (var4 != null) {
         var4.close();
      }

      if (var1 != null) {
         var1.shutdownNow();
      }

      closeProvidersAfterPollStops(var1, var3);
   }

   private void pollProviders(long epoch, MusicProviderChain chain) {
      if (this.isCurrentPipeline(epoch, chain)) {
         MusicHUD.SourcePriority var4 = (MusicHUD.SourcePriority)sourcePriority.get();
         Optional var5 = var4 == MusicHUD.SourcePriority.AUTOMATIC ? chain.poll() : chain.pollOnly(var4.providerId());
         if (this.isCurrentPipeline(epoch, chain)) {
            if (var5.isEmpty()) {
               this.consecutiveEmptyPolls++;
               if (this.consecutiveEmptyPolls >= (4) && playbackState != null) {
                  this.resetState();
               }
            } else {
               this.consecutiveEmptyPolls = 0;
               this.publishSnapshot(epoch, (MusicSnapshot)var5.get());
            }
         }
      }
   }

   private boolean isCurrentPipeline(long epoch, MusicProviderChain chain) {
      return this.isEnabled() && this.pipelineEpoch == epoch && this.providerChain == chain;
   }

   private void publishSnapshot(long epoch, MusicSnapshot snapshot) {
      if (this.pipelineEpoch == epoch && this.isEnabled()) {
         long var4 = System.currentTimeMillis();
         MusicHUD.PlaybackState var6 = playbackState;
         MusicSnapshot var7 = var6 == null ? null : var6.snapshot();
         boolean var8 = var7 == null || !var7.identity().equals(snapshot.identity());
         boolean var9 = var7 != null && var7.playing();
         boolean var10 = !snapshot.playing();
         if (var8) {
            this.trackReportsPosition = snapshot.positionMs() > 0;
            this.frozenPosMs = 0;
            this.frozenPosAtMs = 0L;
         } else if (snapshot.positionMs() > 0) {
            this.trackReportsPosition = true;
         }

         int var11 = var6 != null ? this.positionAt(var6, var4) : 0;
         int var12;
         if (var10) {
            if (var9
               || this.frozenPosAtMs == 0L
               || this.trackReportsPosition && snapshot.positionMs() > 0 && Math.abs(snapshot.positionMs() - this.frozenPosMs) > 2000) {
               this.frozenPosMs = snapshot.positionMs() > 0 ? snapshot.positionMs() : (var9 ? var11 : snapshot.positionMs());
               this.frozenPosAtMs = var4;
            }

            var12 = this.frozenPosMs;
         } else {
            if (!var9 && this.frozenPosAtMs > 0L) {
               var12 = this.frozenPosMs > 0 ? this.frozenPosMs : (snapshot.positionMs() > 0 ? snapshot.positionMs() : var11);
               if (this.trackReportsPosition && snapshot.positionMs() > 0 && Math.abs(snapshot.positionMs() - this.frozenPosMs) > 2000) {
                  var12 = snapshot.positionMs();
               }
            } else if (var8) {
               var12 = snapshot.positionMs();
            } else if (snapshot.positionMs() <= 0) {
               var12 = var11;
            } else if (Math.abs(snapshot.positionMs() - var11) > 3000) {
               var12 = snapshot.positionMs();
            } else if (snapshot.positionMs() < var11) {
               var12 = var11;
            } else {
               var12 = snapshot.positionMs();
            }

            this.frozenPosMs = 0;
            this.frozenPosAtMs = 0L;
         }

         boolean var13 = snapshot.playing();
         if (snapshot.durationMs() > 0 && var12 >= snapshot.durationMs()) {
            var12 = snapshot.durationMs();
            var13 = false;
         }

         MusicSnapshot var14 = snapshot.positionMs() == var12 && snapshot.playing() == var13 ? snapshot : snapshot.withPosition(var12).withPlaying(var13);
         trackName = var14.title();
         artistName = var14.owner();
         isPlaying = var14.playing();
         playbackState = new MusicHUD.PlaybackState(var14, var4);
         if (var8) {
            this.resetMarquee();
         }

         if (snapshot.artwork() != null) {
            this.requestedArtworkUrl = "";
            if (!snapshot.identity().equals(this.uploadedArtworkIdentity) && !snapshot.identity().equals(this.pendingArtworkIdentity)) {
               this.pendingArtworkIdentity = snapshot.identity();
               this.clearCachedImageRequested = false;
               this.pendingImage.set(snapshot.artwork());
            }
         } else {
            String var15 = snapshot.artworkUrl();
            if (!var15.isBlank()) {
               this.requestArtwork(epoch, snapshot.identity(), var15);
            } else if (var8) {
               this.requestedArtworkUrl = "";
               this.pendingArtworkIdentity = "";
               this.pendingImage.set(null);
               this.clearCachedImageRequested = true;
            }
         }
      }
   }

   private void requestArtwork(long epoch, String identity, String url) {
      String var5 = identity + "\n" + url;
      if (!var5.equals(this.requestedArtworkUrl)) {
         this.requestedArtworkUrl = var5;
         ExecutorService var6 = this.artworkExecutor;
         if (var6 != null && !var6.isShutdown()) {
            var6.execute(() -> {
               BufferedImage var6x = this.artworkLoader.load(url);
               if (this.pipelineEpoch == epoch && this.isEnabled()) {
                  if (var6x != null && var5.equals(this.requestedArtworkUrl)) {
                     MusicHUD.PlaybackState var7 = playbackState;
                     MusicSnapshot var8 = var7 == null ? null : var7.snapshot();
                     if (var8 != null && identity.equals(var8.identity())) {
                        this.pendingArtworkIdentity = identity;
                        this.clearCachedImageRequested = false;
                        this.pendingImage.set(var6x);
                     }
                  }
               }
            });
         }
      }
   }

   private static void closeProvidersAfterPollStops(ScheduledExecutorService poll, MusicProviderChain chain) {
      if (chain != null) {
         Runnable var10002 = () -> {
            if (poll != null) {
               long var2x = System.currentTimeMillis() + 30000L;

               while (!awaitTermination(poll, 500L) && System.currentTimeMillis() < var2x) {
               }
            }

            chain.close();
         };
         byte[] var10003 = new byte[24];
         var10003[0] = (byte)(29);
         var10003[1] = (byte)(-13);
         var10003[2] = (byte)(121);
         var10003[3] = (byte)(-13);
         var10003[4] = (byte)(-85);
         var10003[5] = (byte)(-126);
         var10003[6] = (byte)(-90);
         var10003[7] = (byte)(59);
         var10003[8] = (byte)(96);
         var10003[9] = (byte)(-113);
         var10003[10] = (byte)(-108);
         var10003[11] = (byte)(-124);
         var10003[12] = (byte)(-15);
         var10003[13] = (byte)(89);
         var10003[14] = (byte)(-108);
         var10003[15] = (byte)(78);
         var10003[16] = (byte)(94);
         var10003[17] = (byte)(56);
         var10003[18] = (byte)(-31);
         var10003[19] = (byte)(-51);
         var10003[20] = (byte)(26);
         var10003[21] = (byte)(104);
         var10003[22] = (byte)(-10);
         var10003[23] = (byte)(103);
         Thread var2 = new Thread(var10002, $$v_s(var10003, 8851307744329977934L));
         var2.setDaemon(true);
         var2.start();
      }
   }

   private static boolean awaitTermination(ScheduledExecutorService executor, long timeoutMs) {
      try {
         return executor.awaitTermination(timeoutMs, TimeUnit.MILLISECONDS);
      } catch (InterruptedException var4) {
         Thread.currentThread().interrupt();
         return executor.isTerminated();
      }
   }

   private void resetMarquee() {
      this.marqueeOffset = 0.0F;
      this.marqueeScrolling = false;
      this.marqueeTimer = System.currentTimeMillis();
   }

   private void resetImage() {
      if (cachedImageId != (-1)) {
         NVGImage.deleteImage(cachedImageId);
         cachedImageId = -1;
      }

      cachedBufferedImage = null;
      this.uploadedArtworkIdentity = "";
   }

   private void ensureArtworkGeneration() {
      long var1 = NanoVGResourceGeneration.currentGlobal();
      if (this.cachedImageGeneration != var1) {
         if (cachedImageId != (-1)) {
            NVGImage.deleteImage(cachedImageId);
         }

         cachedImageId = -1;
         cachedBufferedImage = null;
         this.cachedImageGeneration = var1;
         this.uploadedArtworkIdentity = "";
      }
   }

   public float getCurrentMusicFitScale() {
      return this.currentMusicFitScale;
   }

   public float getCurrentMusicFitCx() {
      return this.currentMusicFitCx;
   }

   public float getCurrentMusicFitCy() {
      return this.currentMusicFitCy;
   }

   public static String spTrack() {
      return trackName;
   }

   public static String spArtist() {
      return artistName;
   }

   public static boolean spPlaying() {
      return isPlaying;
   }

   public static int spLength() {
      MusicHUD.PlaybackState var0 = playbackState;
      return var0 == null ? 0 : var0.snapshot().durationMs();
   }

   public static int spImageId() {
      return playbackState != null ? cachedImageId : -1;
   }

   public void pollForTitle() {
      if (this.isEnabled()) {
         this.ensureArtworkGeneration();
         this.ensurePipeline();
         this.uploadPendingImage();
      }
   }

   public int spPosition() {
      MusicHUD.PlaybackState var1 = playbackState;
      return this.positionAt(var1, System.currentTimeMillis());
   }

   private int positionAt(MusicHUD.PlaybackState playback, long now) {
      if (playback == null) {
         return 0;
      }

      MusicSnapshot var4 = playback.snapshot();
      if (!var4.playing()) {
         if (this.frozenPosAtMs > 0L) {
            int var7 = Math.max(0, this.frozenPosMs);
            if (var4.durationMs() > 0) {
               var7 = Math.min(var7, var4.durationMs());
            }

            return var7;
         } else {
            return var4.positionMs();
         }
      } else {
         long var5 = var4.positionMs();
         if (playback.publishedAtMs() > 0L) {
            var5 += Math.max(0L, now - playback.publishedAtMs());
         }

         if (var4.durationMs() > 0) {
            var5 = Math.min(var5, var4.durationMs());
         }

         return (int)Math.min(2147483647L, var5);
      }
   }

   private void updateLayout(long now, float dt) {
      this.ensurePipeline();
      this.uploadPendingImage();
      this.sc = (float)((Double)scale.get()).doubleValue();
      this.pad_u = 7.0F;
      this.discD_u = 40.0F;
      this.upperH_u = this.discD_u + 2.0F * this.pad_u;
      this.lowerH_u = showProgress.get() ? 16.0F : 0.0F;
      int var4 = LYRIC_LINE_COUNT[((MusicHUD.LyricLines)lyricLines.get()).ordinal()];
      boolean var5 = (Boolean)showLyrics.get() && this.currentLyrics != null && !this.currentLyrics.isMissing();
      float var6 = var5 ? 4.0F + var4 * 8.0F + Math.max(0, var4 - 1) * 6.0F + 2.0F : 0.0F;
      float var7 = var5 ? 6.0F : 0.0F;
      this.H_u = this.upperH_u + this.lowerH_u + var7 + var6;
      this.W_u = showLyrics.get() ? 190.0F : 165.0F;
      boolean var8 = isPlaying
         && (playbackState == null || playbackState.snapshot().durationMs() <= 0 || this.positionAt(playbackState, now) < playbackState.snapshot().durationMs());
      float var9 = var8 ? 2.6F : 0.0F;
      this.discSpeed = AnimationManager.damp(this.discSpeed, var9, 3.5F, dt);
      this.discRot = this.discRot + this.discSpeed * dt;
      if (this.discRot > (float) (Math.PI * 2)) {
         this.discRot -= (float) (Math.PI * 2);
      }

      float var10 = this.pad_u * this.sc;
      float var11 = this.discD_u * this.sc;
      float var12 = 9.0F * this.sc;
      float var13 = (this.discD_u + 7.0F + this.pad_u) * this.sc;
      float var14 = NanoVGRenderer.getTextWidth(trackName, var12, true);
      boolean var15 = titleMode.get() == MusicHUD.TitleMode.SCROLL;
      float var16 = 17.0F * this.sc;
      float var17 = this.W_u * this.sc;
      float var18 = var13 + var14 + var16 + var10 + 14.0F * this.sc;
      if ((Boolean)showLyrics.get() && var5) {
         var18 = Math.max(var18, 200.0F * this.sc);
      }

      float var19 = var15 ? var17 : Math.max(var17, Math.min(380.0F * this.sc, var18));
      if (this.animW <= 0.0F) {
         this.animW = var19;
      }

      this.animW = AnimationManager.damp(this.animW, var19, 12.0F, dt);
      this.widgetW = this.animW;
      HUD var20 = this.hudEditor();
      float var21 = var20 == null ? 1.0F : var20.getWidgetScale("spotify");
      float[] var22 = this.getPositionFromEditor(this.widgetW * var21, this.H_u * this.sc * var21);
      this.bx = var22[0];
      this.by = var22[1];
      this.lastDt = dt;
   }

   private void renderCard() {
      this.pollLyrics();
      this.cacheHUDBounds();
      HUD var1 = this.hudEditor();

      try (HUD.WidgetRenderScope var2 = var1 == null
            ? null
            : var1.beginWidgetRender("spotify", this.bx, this.by)) {
         long var3 = System.currentTimeMillis();
         float var5 = this.lastDt;
         this.sc = (float)((Double)scale.get()).doubleValue();
         this.pad_u = 7.0F;
         this.discD_u = 40.0F;
         this.upperH_u = this.discD_u + 2.0F * this.pad_u;
         this.lowerH_u = showProgress.get() ? 16.0F : 0.0F;
         int var6 = LYRIC_LINE_COUNT[((MusicHUD.LyricLines)lyricLines.get()).ordinal()];
         boolean var7 = (Boolean)showLyrics.get() && this.currentLyrics != null && !this.currentLyrics.isMissing();
         float var8 = var7 ? 4.0F + var6 * 8.0F + Math.max(0, var6 - 1) * 6.0F + 2.0F : 0.0F;
         float var9 = var7 ? 6.0F : 0.0F;
         this.H_u = this.upperH_u + this.lowerH_u + var9 + var8;
         this.W_u = showLyrics.get() ? 190.0F : 165.0F;
         float var10 = this.pad_u * this.sc;
         float var11 = this.discD_u * this.sc;
         float var12 = this.upperH_u * this.sc;
         float var13 = this.lowerH_u * this.sc;
         float var14 = 9.0F * this.sc;
         float var15 = 8.0F * this.sc;
         float var16 = NanoVGRenderer.getTextHeight(var14);
         float var17 = NanoVGRenderer.getTextHeight(var15);
         if (!trackName.equals(this.lastMarqueeTrack)) {
            this.lastMarqueeTrack = trackName;
            this.marqueeOffset = 0.0F;
            this.marqueeScrolling = false;
            this.marqueeTimer = var3;
            this.transStartMs = var3;
            int var18 = playbackState != null ? playbackState.snapshot().durationMs() : 0;
            this.requestLyrics(trackName, artistName, var18);
         }

         if ((Boolean)trackTransition.get()) {
            float var70 = Math.min(1.0F, (float)(var3 - this.transStartMs) / 560.0F);
            float var19 = 1.0F - (float)Math.pow(1.0F - var70, 3.0);
            this.transA = var19;
            this.transSlide = (1.0F - var19) * 16.0F * this.sc;
         } else {
            this.transA = 1.0F;
            this.transSlide = 0.0F;
         }

         float var71 = Math.max(0.0F, Math.min(1.0F, this.visibilityAlpha));
         boolean var72 = (Boolean)showArtist.get() && !artistName.isEmpty();
         float var20 = 3.5F * this.sc;
         float var21 = var16 + (var72 ? var20 + var17 : 0.0F);
         float var22 = (this.discD_u + 7.0F + this.pad_u) * this.sc;
         float var23 = NanoVGRenderer.getTextWidth(trackName, var14, true);
         boolean var24 = titleMode.get() == MusicHUD.TitleMode.SCROLL;
         float var25 = 17.0F * this.sc;
         float var26 = this.widgetW;
         MusicHUD.PlaybackState var27 = playbackState;
         MusicSource var28 = var27 != null ? var27.snapshot().source() : MusicSource.SPOTIFY_API;
         Color var29 = new Color((Integer)outlineColor.get(), true);
         Color var30 = getSourceAccent(var28, var29);
         int var31 = showProgress.get() ? this.positionAt(var27, var3) : 0;
         int var32 = var27 == null ? 0 : var27.snapshot().durationMs();
         int var33 = var32 > 0 ? var32 : 1;
         this.nvgPosMs = var31;
         this.nvgDurMs = var33;
         float var34 = this.bx + var26 - var10 - var25;
         float var35 = this.by + var10 + 1.5F * this.sc;
         float var36 = this.bx + var22;
         float var37 = var34 - 6.0F * this.sc;
         float var38 = Math.max(10.0F, var37 - var36);
         boolean var39 = var24 && var23 > var38;
         float var40 = var23 + 34.0F * this.sc;
         if (var39) {
            if (!isPlaying) {
               this.marqueeOffset = 0.0F;
               this.marqueeScrolling = false;
               this.marqueeTimer = var3;
            } else if (!this.marqueeScrolling) {
               if (var3 - this.marqueeTimer >= 10000L) {
                  this.marqueeScrolling = true;
               }
            } else {
               this.marqueeOffset = this.marqueeOffset + 26.0F * this.sc * var5;
               if (this.marqueeOffset >= var40) {
                  this.marqueeOffset = 0.0F;
                  this.marqueeScrolling = false;
                  this.marqueeTimer = var3;
               }
            }
         } else {
            this.marqueeOffset = 0.0F;
            this.marqueeScrolling = false;
         }

         float var41 = this.H_u * this.sc;
         float var42 = Math.max(1, Math.round(4.0F * this.sc));
         int var43 = Math.round(this.bx);
         int var44 = Math.round(this.by);
         float var45 = Math.round(this.bx + var26) - var43;
         float var46 = Math.round(this.by + var41) - var44;
         int[] var47 = ThemeManager.bodyTop();
         int[] var48 = ThemeManager.bodyBot();
         int var49 = (int)(ThemeManager.bodyAlpha(1.0F) * MaterialFX.bodyTintMul() * var71);
         if (ThemeManager.isCleanStyle()) {
            float var50 = 8.0F * this.sc;
            NanoVGRenderer.drawSquircleGradient(
               var43,
               var44,
               var45,
               var46,
               var50,
               SoftPalette.rgba(SoftPalette.BODY_TOP, (int)(235.0F * var71)),
               SoftPalette.rgba(SoftPalette.BODY_BOT, (int)(245.0F * var71))
            );
            NanoVGRenderer.drawSquircleOutline(var43, var44, var45, var46, var50, 1.0F, SoftPalette.white((int)(22.0F * var71)));
         } else {
            MaterialFX.glassBody(var43, var44, var45, var46, var42, 1.0F);
            NanoVGRenderer.drawRoundedRectGradient(
               var43, var44, var45, var46, var42, new Color(var47[0], var47[1], var47[2], var49), new Color(var48[0], var48[1], var48[2], var49)
            );
            GuiStyle.gloss(var43, var44, var45, var46, var42, var71);
         }

         this.drawAlbumArt(this.bx + var10, this.by + var10, var11, var30, this.transA * var71);
         this.drawSourceLogo(var28, var34, var35, var25, this.transA * var71);
         float var73 = this.by + var10 + (var11 - var21) / 2.0F;
         float var51 = var73 + var16 + var20;
         Color var52 = withAlpha(ThemeManager.text(255), this.transA * var71);
         Color var53 = withAlpha(ThemeManager.textDim(255), this.transA * var71);
         if (var39) {
            NanoVGRenderer.save();

            try {
               NanoVGRenderer.scissor(var36, this.by, var38, var12);
               NanoVGRenderer.drawText(trackName, var36 - this.marqueeOffset, var73, var14, var52, true);
               NanoVGRenderer.drawText(trackName, var36 - this.marqueeOffset + var40, var73, var14, var52, true);
            } finally {
               NanoVGRenderer.restore();
            }
         } else {
            String var54 = var24 ? trackName : truncateNVG(trackName, var38, var14, true);
            NanoVGRenderer.drawText(var54, var36 + this.transSlide, var73, var14, var52, true);
         }

         if (var72) {
            float var74 = 10.6F * this.sc;
            float var55 = 7.5F * this.sc;
            float var56 = var51 + (var17 - var55) / 2.0F;
            if (isPlaying) {
               this.drawMiniEqualizer(var36 + this.transSlide, var56, var74, var55, var30, this.transA * var71);
            } else {
               this.drawMiniPause(var36 + this.transSlide, var56, 6.0F * this.sc, var55, var53);
            }

            float var57 = var74 + 4.5F * this.sc;
            NanoVGRenderer.drawText(truncateNVG(artistName, var38 - var57, var15, false), var36 + this.transSlide + var57, var51, var15, var53);
         }

         if (var13 > 0.0F) {
            String var75 = formatTime(this.nvgPosMs);
            String var78 = var32 > 0 ? formatTime(this.nvgDurMs) : "--:--";
            float var80 = this.by + var12 + (var13 - var17) / 2.0F;
            float var82 = NanoVGRenderer.getTextWidth(var75, var15);
            float var58 = NanoVGRenderer.getTextWidth(var78, var15);
            NanoVGRenderer.drawText(var75, this.bx + var10, var80, var15, var53);
            NanoVGRenderer.drawText(var78, this.bx + var26 - var10 - var58, var80, var15, var53);
            float var59 = this.bx + var10 + var82 + 7.0F * this.sc;
            float var60 = var26 - 2.0F * var10 - var82 - var58 - 14.0F * this.sc;
            float var61 = 10.0F * this.sc;
            float var62 = var80 + (var17 - var61) / 2.0F;
            float var63 = var32 > 0 ? Math.min(1.0F, (float)this.nvgPosMs / this.nvgDurMs) : 0.0F;
            this.drawWaveform(var59, var62, Math.max(8.0F, var60), var61, var63, var32 > 0, var30, this.transA * var71);
         }

         if (var8 > 0.0F) {
            float var76 = this.by + (this.upperH_u + this.lowerH_u + var9) * this.sc;
            float var79 = var76 + var8 * this.sc;
            float var81 = this.bx + var10;
            float var83 = var26 - 2.0F * var10;
            this.drawLyrics(var81, var76, var83, var79, this.sc, this.transA * var71);
         }

         if (!ThemeManager.isCleanStyle()) {
            Color var77 = new Color(var29.getRed(), var29.getGreen(), var29.getBlue(), Math.min(65, (int)(var29.getAlpha() * var71)));
            NanoVGRenderer.drawRoundedRectOutline(var43, var44, var45, var46, var42, 1.0F, var77);
            NanoVGRenderer.drawRoundedRectOutline(var43, var44, var45, var46, var42, 1.0F, new Color(255, 255, 255, (int)(24.0F * var71)));
         }
      }

      this.drawEditorOutline();
   }

   @EventHandler
   public void onBackdropPrepare(BackdropPrepareEvent event) {
      this.backdropQueuedThisFrame = false;
      if (mc.field_1724 != null && mc.field_1687 != null) {
         if (this.isEnabled()) {
            HUD var2 = this.hudEditor();
            boolean var3 = var2 != null && var2.isEditorMode();
            boolean var4 = (Boolean)hideWhenPaused.get()
               && (!isPlaying || trackName.equals("Not Playing"));
            if (!var4 || var3 || !(this.visibilityAlpha <= 0.05F)) {
               float var5 = (float)((Double)scale.get()).doubleValue();
               float var6 = this.widgetW > 0.0F ? this.widgetW : 165.0F * var5;
               int var7 = LYRIC_LINE_COUNT[((MusicHUD.LyricLines)lyricLines.get()).ordinal()];
               boolean var8 = (Boolean)showLyrics.get() && this.currentLyrics != null && !this.currentLyrics.isMissing();
               float var9 = var8 ? 4.0F + var7 * 8.0F + Math.max(0, var7 - 1) * 6.0F + 2.0F : 0.0F;
               float var10 = var8 ? 6.0F : 0.0F;
               float var11 = (54.0F + (showProgress.get() ? 16.0F : 0.0F) + var10 + var9) * var5;
               HUD var12 = this.hudEditor();
               float var13 = var12 == null ? 1.0F : var12.getWidgetScale("spotify");
               float var14 = var6 * var13;
               float var15 = var11 * var13;
               float[] var16 = this.getPositionFromEditor(var14, var15);
               float var17 = Math.max(1.0F, Math.round(4.0F * var5)) * var13;
               FrostedBackground.queueLogicalRect(
                  var16[0], var16[1], var14, var15, ThemeManager.isCleanStyle() ? 8.0F * var5 : var17, ThemeManager.isCleanStyle()
               );
               this.backdropQueuedThisFrame = true;
            }
         }
      }
   }

   @EventHandler
   public void onPostBlit(PostBlitRenderEvent event) {
      if (mc.field_1724 != null && mc.field_1687 != null) {
         boolean var3;
         boolean var10000;
         label87: {
            label86: {
               HUD var2 = this.hudEditor();
               var3 = var2 != null && var2.isEditorMode();
               if ((Boolean)hideWhenPaused.get()) {
                  if (!isPlaying) {
                     break label86;
                  }

                  byte[] var10001 = new byte[11];
                  var10001[0] = (byte)(37);
                  var10001[1] = (byte)(-11);
                  var10001[2] = (byte)(104);
                  var10001[3] = (byte)(-2);
                  var10001[4] = (byte)(-106);
                  var10001[5] = (byte)(-101);
                  var10001[6] = (byte)(-76);
                  var10001[7] = (byte)(43);
                  var10001[8] = (byte)(106);
                  var10001[9] = (byte)(-52);
                  var10001[10] = (byte)(-125);
                  if (trackName.equals($$v_s(var10001, 8851307744329977934L))) {
                     break label86;
                  }
               }

               var10000 = false;
               break label87;
            }

            var10000 = true;
         }

         boolean var4 = var10000;
         float var5 = var4 && !var3 ? 0.0F : 1.0F;
         long var6 = System.currentTimeMillis();
         float var8 = this.lastFrameMs > 0L ? Math.min(0.05F, (float)(var6 - this.lastFrameMs) / 1000.0F) : 0.0F;
         this.lastFrameMs = var6;
         this.visibilityAlpha = AnimationManager.damp(this.visibilityAlpha, var5, 9.0F, var8);
         if (!(this.visibilityAlpha <= 0.005F) || !(var5 <= 0.0F)) {
            this.glassReadyThisFrame = this.backdropQueuedThisFrame && event.backdropDrawn();

            try (NanoVGFrame var9 = NanoVGRenderer.frame()) {
               if (!var9.isActive()) {
                  return;
               }

               this.ensureArtworkGeneration();
               this.updateLayout(var6, var8);
               this.renderCard();
            }
         }
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static Color getSourceAccent(MusicSource source, Color fallback) {
      if (source == null) {
         return fallback;
      }

      return switch (<unrepresentable>.$SwitchMap$me$drake$vieclient$modules$client$music$MusicSource[source.ordinal()]) {
         case 1 -> new Color(30, 215, 96);
         case 2 -> new Color(255, 34, 48);
         case 3 -> new Color(255, 85, 0);
         case 4 -> new Color(252, 60, 104);
         case 5 -> fallback;
         default -> throw new MatchException(null, null);
      };
   }

   private void drawSourceLogo(MusicSource source, float x, float y, float size, float anim) {
      if (!(anim <= 0.005F)) {
         if (source == MusicSource.SPOTIFY_API) {
            this.drawSpotifyLogo(x, y, size, anim);
         } else {
            float var6 = x + size * 0.5F;
            float var7 = y + size * 0.5F;
            if (source == MusicSource.YOUTUBE_BROWSER) {
               float var8 = size * 0.72F;
               float var9 = y + (size - var8) * 0.5F;
               float var10 = 3.5F * this.sc;
               NanoVGRenderer.drawRoundedRect(x, var9, size, var8, var10, new Color(255, 0, 51, (int)(245.0F * anim)));
               NanoVGRenderer.drawRoundedRectGradient(
                  x, var9, size, var8 * 0.45F, var10, new Color(255, 255, 255, (int)(65.0F * anim)), new Color(255, 255, 255, 0)
               );
               float var11 = size * 0.2F;
               float var12 = var6 + 0.6F * this.sc;
               NanoVGRenderer.drawTriangle(
                  var12 - var11 * 0.7F, var7 - var11, var12 - var11 * 0.7F, var7 + var11, var12 + var11, var7, new Color(255, 255, 255, (int)(255.0F * anim))
               );
               if (!ThemeManager.isCleanStyle()) {
                  NanoVGRenderer.drawRoundedRectOutline(x, var9, size, var8, var10, 0.8F * this.sc, new Color(255, 255, 255, (int)(42.0F * anim)));
               }
            } else if (source == MusicSource.SOUNDCLOUD_APP) {
               float var13 = size * 0.5F;
               NanoVGRenderer.drawCircle(var6, var7, var13, new Color(255, 85, 0, (int)(245.0F * anim)));
               float var16 = 1.2F * this.sc;
               float var18 = 1.0F * this.sc;
               Color var19 = new Color(255, 255, 255, (int)(245.0F * anim));
               NanoVGRenderer.drawRoundedRect(var6 - var16 * 2.0F - var18 * 1.5F, var7 - 2.0F * this.sc, var16, 4.0F * this.sc, var16 * 0.5F, var19);
               NanoVGRenderer.drawRoundedRect(var6 - var16 * 1.0F - var18 * 0.5F, var7 - 4.0F * this.sc, var16, 8.0F * this.sc, var16 * 0.5F, var19);
               NanoVGRenderer.drawRoundedRect(var6 + var18 * 0.5F, var7 - 5.5F * this.sc, var16, 11.0F * this.sc, var16 * 0.5F, var19);
               NanoVGRenderer.drawRoundedRect(var6 + var16 + var18 * 1.5F, var7 - 3.5F * this.sc, var16, 7.0F * this.sc, var16 * 0.5F, var19);
               if (!ThemeManager.isCleanStyle()) {
                  NanoVGRenderer.drawCircleOutline(var6, var7, var13, 0.8F * this.sc, new Color(255, 255, 255, (int)(42.0F * anim)));
               }
            } else if (source == MusicSource.APPLE_MUSIC_API) {
               float var14 = size * 0.5F;
               NanoVGRenderer.drawCircle(var6, var7, var14, new Color(252, 60, 104, (int)(245.0F * anim)));
               this.drawMusicNote(var6 - size * 0.25F, var7 - size * 0.25F, size * 0.5F, size * 0.5F, Color.WHITE);
               if (!ThemeManager.isCleanStyle()) {
                  NanoVGRenderer.drawCircleOutline(var6, var7, var14, 0.8F * this.sc, new Color(255, 255, 255, (int)(42.0F * anim)));
               }
            } else {
               float var15 = size * 0.5F;
               Color var17 = new Color((Integer)outlineColor.get(), true);
               NanoVGRenderer.drawCircle(var6, var7, var15, new Color(var17.getRed(), var17.getGreen(), var17.getBlue(), (int)(220.0F * anim)));
               this.drawMusicNote(var6 - size * 0.25F, var7 - size * 0.25F, size * 0.5F, size * 0.5F, Color.WHITE);
            }
         }
      }
   }

   private void drawSpotifyLogo(float x, float y, float size, float anim) {
      int var5 = (int)(255.0F * Math.max(0.0F, Math.min(1.0F, anim)));
      if (var5 > 0) {
         float var6 = x + size * 0.5F;
         float var7 = y + size * 0.5F;
         float var8 = size * 0.5F;
         byte[] var10000 = new byte[7];
         var10000[0] = (byte)(24);
         var10000[1] = (byte)(-22);
         var10000[2] = (byte)(115);
         var10000[3] = (byte)(-86);
         var10000[4] = (byte)(-81);
         var10000[5] = (byte)(-111);
         var10000[6] = (byte)(-84);
         if (IconManager.hasColored($$v_s(var10000, 8851307744329977934L))) {
            var10000 = new byte[7];
            var10000[0] = (byte)(24);
            var10000[1] = (byte)(-22);
            var10000[2] = (byte)(115);
            var10000[3] = (byte)(-86);
            var10000[4] = (byte)(-81);
            var10000[5] = (byte)(-111);
            var10000[6] = (byte)(-84);
            IconManager.drawColored($$v_s(var10000, 8851307744329977934L), x, y, size, var5);
         } else {
            NVGIcons.spotifyGreen(x, y, size, var5);
         }

         if (!ThemeManager.isCleanStyle()) {
            NanoVGRenderer.drawCircleOutline(
               var6, var7, var8, 0.8F * this.sc, new Color(255, 255, 255, (int)(40.0F * anim))
            );
         }
      }
   }

   private void drawMiniEqualizer(float x, float y, float w, float h, float anim) {
      this.drawMiniEqualizer(x, y, w, h, new Color(30, 215, 96), anim);
   }

   private void drawMiniEqualizer(float x, float y, float w, float h, Color color, float anim) {
      long var7 = System.currentTimeMillis();
      float var9 = 1.6F * this.sc;
      float var10 = 1.4F * this.sc;
      int var11 = (int)(245.0F * anim);
      Color var12 = new Color(color.getRed(), color.getGreen(), color.getBlue(), var11);
      float var13 = Math.max(2.0F * this.sc, h * (0.3F + 0.65F * Math.abs((float)Math.sin(var7 / 160.0))));
      float var14 = Math.max(2.0F * this.sc, h * (0.2F + 0.75F * Math.abs((float)Math.sin(var7 / 220.0 + 1.1))));
      float var15 = Math.max(2.0F * this.sc, h * (0.35F + 0.6F * Math.abs((float)Math.sin(var7 / 180.0 + 2.3))));
      float var16 = Math.max(2.0F * this.sc, h * (0.25F + 0.7F * Math.abs((float)Math.sin(var7 / 250.0 + 3.4))));
      NanoVGRenderer.drawRoundedRect(x, y + h - var13, var9, var13, var9 * 0.5F, var12);
      NanoVGRenderer.drawRoundedRect(x + var9 + var10, y + h - var14, var9, var14, var9 * 0.5F, var12);
      NanoVGRenderer.drawRoundedRect(x + (var9 + var10) * 2.0F, y + h - var15, var9, var15, var9 * 0.5F, var12);
      NanoVGRenderer.drawRoundedRect(x + (var9 + var10) * 3.0F, y + h - var16, var9, var16, var9 * 0.5F, var12);
   }

   private void drawMiniPause(float x, float y, float w, float h, Color color) {
      float var6 = 1.8F * this.sc;
      float var7 = 2.2F * this.sc;
      NanoVGRenderer.drawRoundedRect(x, y, var6, h, var6 * 0.5F, color);
      NanoVGRenderer.drawRoundedRect(x + var6 + var7, y, var6, h, var6 * 0.5F, color);
   }

   private void drawAlbumArt(float ax, float ay, float aSize, Color accent, float anim) {
      long var6 = NVGRenderer.getContext();
      if (var6 != 0L && !(anim <= 0.005F)) {
         float var8 = Math.max(1.0F, Math.round(4.0F * this.sc));
         float var9 = ax + aSize / 2.0F;
         float var10 = ay + aSize / 2.0F;
         float var11 = 0.92F + 0.08F * anim;
         int var12 = (int)(255.0F * anim);
         NanoVG.nvgSave(var6);
         NanoVG.nvgTranslate(var6, var9, var10);
         NanoVG.nvgScale(var6, var11, var11);
         NanoVG.nvgTranslate(var6, -var9, -var10);

         try {
            if (cachedImageId != -1) {
               NVGPaint var13 = NVGPaint.calloc();

               try {
                  float var14 = 0.2F;
                  float var15 = aSize / (1.0F - var14);
                  float var16 = ax - (var15 - aSize) / 2.0F;
                  NanoVG.nvgImagePattern(var6, var16, ay, var15, var15, 0.0F, cachedImageId, anim, var13);
                  NanoVG.nvgBeginPath(var6);
                  NanoVG.nvgRoundedRect(var6, ax, ay, aSize, aSize, var8);
                  NanoVG.nvgFillPaint(var6, var13);
                  NanoVG.nvgFill(var6);
               } finally {
                  var13.free();
               }

               NanoVGRenderer.drawRoundedRectGradient(
                  ax, ay, aSize, aSize * 0.45F, var8, new Color(255, 255, 255, (int)(32.0F * anim)), new Color(255, 255, 255, 0)
               );
               if (!isPlaying && playbackState != null) {
                  NanoVGRenderer.drawRoundedRect(ax, ay, aSize, aSize, var8, new Color(12, 14, 20, (int)(95.0F * anim)));
                  float var26 = 2.4F * this.sc;
                  float var28 = 9.0F * this.sc;
                  float var30 = 2.4F * this.sc;
                  Color var17 = new Color(255, 255, 255, (int)(210.0F * anim));
                  NanoVGRenderer.drawRoundedRect(var9 - var26 - var30 * 0.5F, var10 - var28 * 0.5F, var26, var28, var26 * 0.5F, var17);
                  NanoVGRenderer.drawRoundedRect(var9 + var30 * 0.5F, var10 - var28 * 0.5F, var26, var28, var26 * 0.5F, var17);
               }
            } else {
               float var25 = aSize * 0.5F;
               NanoVGRenderer.drawCircle(var9, var10, var25, new Color(17, 18, 22, var12));
               NanoVGRenderer.drawCircleOutline(var9, var10, var25 - 0.5F * this.sc, 1.0F * this.sc, new Color(255, 255, 255, (int)(32.0F * anim)));
               NanoVGRenderer.drawCircleOutline(var9, var10, var25 * 0.86F, 0.75F * this.sc, new Color(38, 40, 48, (int)(220.0F * anim)));
               NanoVGRenderer.drawCircleOutline(var9, var10, var25 * 0.74F, 0.75F * this.sc, new Color(48, 50, 60, (int)(200.0F * anim)));
               NanoVGRenderer.drawCircleOutline(var9, var10, var25 * 0.62F, 0.75F * this.sc, new Color(38, 40, 48, (int)(220.0F * anim)));
               NanoVGRenderer.drawCircleOutline(var9, var10, var25 * 0.5F, 0.75F * this.sc, new Color(48, 50, 60, (int)(200.0F * anim)));
               float var27 = this.discRot - 0.42F;
               float var29 = this.discRot + 0.42F;
               NanoVGRenderer.drawArc(var9, var10, var25 * 0.68F, var27, var29, 3.2F * this.sc, new Color(255, 255, 255, (int)(26.0F * anim)));
               NanoVGRenderer.drawArc(
                  var9, var10, var25 * 0.68F, var27 + (float) Math.PI, var29 + (float) Math.PI, 3.2F * this.sc, new Color(255, 255, 255, (int)(26.0F * anim))
               );
               float var31 = var25 * 0.38F;
               Color var32 = new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), Math.min(235, (int)(190.0F * anim)));
               NanoVGRenderer.drawCircle(var9, var10, var31, var32);
               NanoVGRenderer.drawCircleOutline(var9, var10, var31, 1.0F * this.sc, new Color(255, 255, 255, (int)(65.0F * anim)));
               NanoVGRenderer.drawCircle(var9, var10, var25 * 0.1F, new Color(12, 13, 16, var12));
               NanoVGRenderer.drawCircleOutline(var9, var10, var25 * 0.1F, 0.8F * this.sc, new Color(210, 215, 225, (int)(190.0F * anim)));
            }

            if (!ThemeManager.isCleanStyle()) {
               NanoVGRenderer.drawRoundedRectOutline(ax, ay, aSize, aSize, var8, 1.0F, new Color(255, 255, 255, (int)(32.0F * anim)));
            }
         } finally {
            NanoVG.nvgRestore(var6);
         }
      }
   }

   private void drawWaveform(float x, float y, float w, float h, float progress, boolean hasDuration, Color accent) {
      this.drawWaveform(x, y, w, h, progress, hasDuration, accent, 1.0F);
   }

   private void drawWaveform(float x, float y, float w, float h, float progress, boolean hasDuration, Color accent, float anim) {
      if (!(anim <= 0.01F) && !(w <= 4.0F)) {
         long var9 = System.currentTimeMillis();
         float var11 = Math.max(1.4F, 1.8F * this.sc);
         float var12 = Math.max(1.0F, 1.4F * this.sc);
         int var13 = (int)((w + var12) / (var11 + var12));
         var13 = Math.max(12, Math.min(36, var13));
         float var14 = var13 * var11 + (var13 - 1) * var12;
         float var15 = x + (w - var14) * 0.5F;
         float var16 = y + h * 0.5F;
         NanoVGRenderer.drawRoundedRect(var15, var16 - 0.75F * this.sc, var14, 1.5F * this.sc, 0.75F * this.sc, new Color(255, 255, 255, (int)(18.0F * anim)));
         float var17 = var15 + var14 * (hasDuration ? progress : 0.5F);

         for (int var18 = 0; var18 < var13; var18++) {
            float var19 = var15 + var18 * (var11 + var12);
            int var20 = var18 * WAVEFORM.length / var13;
            float var21 = WAVEFORM[Math.min(WAVEFORM.length - 1, var20)];
            float var22;
            if (isPlaying) {
               float var23 = (float)(Math.sin(var9 * 0.006 + var18 * 0.42) * 0.22 + Math.cos(var9 * 0.009 + var18 * 0.68) * 0.16);
               var22 = Math.max(0.2F, Math.min(1.0F, var21 + var23));
            } else {
               var22 = Math.max(0.18F, var21 * 0.72F);
            }

            float var32 = Math.max(2.5F * this.sc, h * var22);
            float var24 = var16 - var32 * 0.5F;
            float var25 = var11 * 0.5F;
            float var26 = (float)var18 / Math.max(1, var13 - 1);
            boolean var27 = hasDuration ? var26 <= progress || var19 + var11 <= var17 : var18 % 2 == 0;
            if (var27) {
               Color var28 = withAlpha(new Color(255, 255, 255), anim * 0.95F);
               Color var29 = withAlpha(accent, anim * 0.95F);
               NanoVGRenderer.drawRoundedRectGradient(var19, var24, var11, var32, var25, var28, var29);
            } else {
               Color var33 = new Color(255, 255, 255, (int)(42.0F * anim));
               NanoVGRenderer.drawRoundedRect(var19, var24, var11, var32, var25, var33);
            }
         }

         if (hasDuration && progress > 0.0F && progress <= 1.0F) {
            float var31 = Math.min(var15 + var14, Math.max(var15, var17));
            NanoVGRenderer.drawCircle(var31, var16, 2.6F * this.sc, withAlpha(accent, anim));
            NanoVGRenderer.drawCircle(var31, var16, 1.4F * this.sc, withAlpha(Color.WHITE, anim));
         }
      }
   }

   private void drawMusicNote(float ax, float ay, float aSize, Color accent) {
      this.drawMusicNote(ax, ay, aSize, aSize, accent);
   }

   private void drawMusicNote(float ax, float ay, float aw, float ah, Color accent) {
      float var6 = ax + aw / 2.0F;
      float var7 = ay + ah / 2.0F + (isPlaying ? (float)(Math.sin(System.currentTimeMillis() / 300.0) * 2.0 * this.sc) : 0.0F);
      float var8 = 4.0F * this.sc;
      NanoVGRenderer.drawCircle(var6 - 3.0F * this.sc, var7 + 4.0F * this.sc, var8, new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 220));
      NanoVGRenderer.drawLine(
         var6 + var8 - 4.0F * this.sc,
         var7 + 4.0F * this.sc,
         var6 + var8 - 4.0F * this.sc,
         var7 - 6.0F * this.sc,
         1.5F * this.sc,
         new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 220)
      );
      NanoVGRenderer.drawLine(
         var6 + var8 - 4.0F * this.sc,
         var7 - 6.0F * this.sc,
         var6 + var8 + 2.0F * this.sc,
         var7 - 2.0F * this.sc,
         1.5F * this.sc,
         new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 180)
      );
      if (!isPlaying && !trackName.equals("Not Playing")) {
         NanoVGRenderer.drawLine(
            var6 - 4.0F * this.sc, var7 - 5.0F * this.sc, var6 - 4.0F * this.sc, var7 + 5.0F * this.sc, 2.5F * this.sc, new Color(200, 200, 200, 180)
         );
         NanoVGRenderer.drawLine(
            var6 + 1.0F * this.sc, var7 - 5.0F * this.sc, var6 + 1.0F * this.sc, var7 + 5.0F * this.sc, 2.5F * this.sc, new Color(200, 200, 200, 180)
         );
      }
   }

   private Color lyricsTextColor() {
      return new Color((Integer)lyricsColor.get(), true);
   }

   private Color lyricsUpcomingColor() {
      return new Color((Integer)upcomingLyricsColor.get(), true);
   }

   private Color highlightTextColor() {
      return new Color((Integer)highlightColor.get(), true);
   }

   public static int spDuration() {
      return spLength();
   }

   private void requestLyrics(String title, String artist, int durationMs) {
      if (title != null && !title.isBlank()) {
         byte[] var10000 = new byte[11];
         var10000[0] = (byte)(37);
         var10000[1] = (byte)(-11);
         var10000[2] = (byte)(104);
         var10000[3] = (byte)(-2);
         var10000[4] = (byte)(-106);
         var10000[5] = (byte)(-101);
         var10000[6] = (byte)(-76);
         var10000[7] = (byte)(43);
         var10000[8] = (byte)(106);
         var10000[9] = (byte)(-52);
         var10000[10] = (byte)(-125);
         if (!$$v_s(var10000, 8851307744329977934L).equals(title)) {
            if (artist == null) {
               artist = "";
            }

            String var4 = title + artist + durationMs;
            if (var4.equals(this.lyricTrackKey) && this.currentLyrics != null && !this.currentLyrics.isLoading()) {
               return;
            }

            this.lyricTrackKey = var4;
            this.currentLyrics = Lyrics.LOADING;
            this.lyricActiveIndex = -1;
            this.lyricLinesArr = SyncedLyrics.EMPTY.allLines();
            if (this.lyricsService == null) {
               this.currentLyrics = Lyrics.NOT_FOUND;
               return;
            }

            try {
               this.lyricsService.setOnline((Boolean)lyricsOnline.get());
            } catch (Throwable var9) {
               this.currentLyrics = Lyrics.NOT_FOUND;
               return;
            }

            LyricsQuery var5 = new LyricsQuery(title, artist, "", durationMs);
            Lyrics var6 = null;

            try {
               var6 = this.lyricsService.lookup(var5);
            } catch (Throwable var8) {
            }

            this.currentLyrics = var6 != null ? var6 : Lyrics.NOT_FOUND;
            this.applyLyricsResult();
            return;
         }
      }

      this.currentLyrics = Lyrics.NOT_FOUND;
      this.lyricLinesArr = SyncedLyrics.EMPTY.allLines();
      this.lyricActiveIndex = -1;
      this.lyricTrackKey = "";
   }

   private void pollLyrics() {
      if ((Boolean)showLyrics.get() && this.isEnabled()) {
         if (trackName != null && !trackName.isBlank()) {
            byte[] var10000 = new byte[11];
            var10000[0] = (byte)(37);
            var10000[1] = (byte)(-11);
            var10000[2] = (byte)(104);
            var10000[3] = (byte)(-2);
            var10000[4] = (byte)(-106);
            var10000[5] = (byte)(-101);
            var10000[6] = (byte)(-76);
            var10000[7] = (byte)(43);
            var10000[8] = (byte)(106);
            var10000[9] = (byte)(-52);
            var10000[10] = (byte)(-125);
            if (!$$v_s(var10000, 8851307744329977934L).equals(trackName)) {
               if (artistName == null) {
                  artistName = "";
               }

               if (this.lyricsService == null) {
                  return;
               }

               try {
                  this.lyricsService.setOnline((Boolean)lyricsOnline.get());
               } catch (Throwable var6) {
                  return;
               }

               int var1 = spLength();
               LyricsQuery var2 = new LyricsQuery(trackName, artistName, "", var1);

               Lyrics var3;
               try {
                  var3 = this.lyricsService.lookup(var2);
               } catch (Throwable var5) {
                  return;
               }

               if (var3 == null) {
                  return;
               }

               if (var3 == this.currentLyrics) {
                  this.updateActiveLineIndex();
                  return;
               }

               this.currentLyrics = var3;
               this.applyLyricsResult();
               return;
            }
         }
      }
   }

   private void applyLyricsResult() {
      if (this.currentLyrics == null) {
         this.lyricLinesArr = SyncedLyrics.EMPTY.allLines();
      } else {
         SyncedLyrics var1 = this.currentLyrics.synced();
         if (var1 != null && !var1.isEmpty()) {
            this.lyricLinesArr = var1.allLines();
         } else {
            this.lyricLinesArr = SyncedLyrics.EMPTY.allLines();
         }

         this.updateActiveLineIndex();
      }
   }

   private void updateActiveLineIndex() {
      SyncedLyrics.Line[] var1 = this.lyricLinesArr;
      if (var1 != null && var1.length != 0) {
         MusicHUD.PlaybackState var2 = playbackState;
         if (var2 == null) {
            this.lyricActiveIndex = -1;
         } else {
            long var3 = this.positionAt(var2, System.currentTimeMillis());
            int var5 = -1;
            int var6 = 0;

            while (var6 < var1.length && var1[var6].timeMs() <= var3) {
               var5 = var6++;
            }

            if (var5 != this.lyricActiveIndex) {
               this.lyricActiveIndex = var5;
               if (var5 >= 0) {
                  this.lyricActiveStartMs = var1[var5].timeMs();
                  this.lyricActiveEndMs = var5 + 1 < var1.length ? var1[var5 + 1].timeMs() : this.lyricActiveStartMs + 4000L;
               }
            }
         }
      } else {
         if (this.lyricActiveIndex != (-1)) {
            this.lyricActiveIndex = -1;
         }
      }
   }

   private static float smooth(float value) {
      float var1 = clamp01(value);
      return var1 * var1 * (3.0F - 2.0F * var1);
   }

   private static float clamp01(float v) {
      return Math.max(0.0F, Math.min(1.0F, v));
   }

   private static float springLeft(float time) {
      float var1 = 13.0F * time;
      return (1.0F + var1) * (float)Math.exp(-var1);
   }

   private static float topFade(float top, float s) {
      return top >= 0.0F ? 1.0F : smooth(1.0F + top / (3.0F * s));
   }

   private static float bottomFade(float below, float px) {
      return below <= 0.5F ? 1.0F : smooth(1.0F - below / (px * 0.6F));
   }

   private static int letterCount(String text) {
      int var1 = text.length();

      for (int var2 = 0; var2 < text.length(); var2++) {
         if (text.charAt(var2) == (32)) {
            var1--;
         }
      }

      return var1;
   }

   private float lineHeight(String[] rows, float rowGap) {
      float var3 = 8.0F * this.lyricScale;
      return rows.length <= 1 ? var3 : rows.length * var3 + (rows.length - 1) * rowGap;
   }

   private static Color lerpColor(Color a, Color b, float t) {
      t = clamp01(t);
      int var3 = Math.round(a.getRed() + (b.getRed() - a.getRed()) * t);
      int var4 = Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t);
      int var5 = Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * t);
      int var6 = Math.round(a.getAlpha() + (b.getAlpha() - a.getAlpha()) * t);
      return new Color(var3, var4, var5, var6);
   }

   private float ascent() {
      return NanoVGRenderer.getTextHeight(8.0F * this.lyricScale) * 0.78F;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private MusicHUD.LyricView planLyrics(float innerW, float s, float dt, long nowMs) {
      boolean var6 = (Boolean)showLyrics.get();
      long var7 = this.positionAt(playbackState, nowMs);
      int var9 = LYRIC_LINE_COUNT[((MusicHUD.LyricLines)lyricLines.get()).ordinal()];
      MusicHUD.LyricMotion var10 = (MusicHUD.LyricMotion)lyricMotion.get();
      MusicHUD.LyricHighlight var11 = (MusicHUD.LyricHighlight)lyricHighlight.get();
      boolean var12 = (Boolean)lyricsOnline.get();
      boolean var13 = false;
      float var14 = -1.0F;
      if (!var6) {
         this.lyricLoadingSince = 0L;
         var14 = 0.0F;
      } else {
         if (this.currentLyrics == null || this.currentLyrics.status() != Lyrics.Status.LOADING) {
            this.lyricLoadingSince = 0L;
         } else if (this.lyricLoadingSince == 0L) {
            this.lyricLoadingSince = nowMs;
         }

         this.lyricStatus = null;
         if (this.currentLyrics != null) {
            switch (<unrepresentable>.$SwitchMap$me$drake$vieclient$modules$client$spotify$Lyrics$Status[this.currentLyrics.status().ordinal()]) {
               case 1:
                  this.lyricStatus = nowMs - this.lyricLoadingSince < 700L
                     ? null
                     : "Loading lyrics";
                  break;
               case 2:
                  SyncedLyrics var15 = this.currentLyrics.synced();
                  this.prepareLyrics(var15);
                  if (this.lyricReadable) {
                     this._lastPlannedItems = new ArrayList(16);
                     float var16 = this.lyricViewHeight(var15, var9, innerW, s);
                     this.syncedItems(this._lastPlannedItems, var15, var7, var16, var10, var11, innerW, s, dt);
                     var14 = var16 - 8.0F * s + this.ascent();
                     var13 = true;
                  } else {
                     this.lyricStatus = "Lyrics in a script the HUD font lacks";
                  }
                  break;
               case 3:
                  this.lyricStatus = "No synced lyrics";
                  break;
               case 4:
                  this.lyricStatus = "Instrumental";
                  break;
               case 5:
                  this.lyricStatus = var12
                     ? "No lyrics found"
                     : "No saved lyrics";
                  break;
               case 6:
                  this.lyricStatus = "Lyrics unavailable";
            }
         }
      }

      List var18;
      if (var13) {
         this.lyricLastItems = this._lastPlannedItems;
         this.lyricLeaveItems = null;
         var18 = this._lastPlannedItems;
      } else {
         var18 = this.leavingItems(this.lyricStatus == null, s, dt);
      }

      if (this.lyricStatus != null) {
         var14 = (8.0F * s - 7.0F * s) * 0.5F + this.ascent();
      }

      float var19 = var14 < 0.0F ? this.lyricBlockH : (var14 > 0.0F ? 6.0F * s + Math.round(var14) : 0.0F);
      float var17 = this.easeLyricBlock(var19, dt);
      this.lyricBlockHRendered = var17;
      return new MusicHUD.LyricView(var18, this.lyricStatus, var17);
   }

   private List leavingItems(boolean quiet, float s, float dt) {
      this.lyricSource = null;
      if (!this.lyricLastItems.isEmpty()) {
         this.lyricLeaveItems = new ArrayList(this.lyricLastItems);
         this.lyricLeave = 0.0F;
         this.lyricLastItems = List.of();
      }

      if (this.lyricLeaveItems == null) {
         return List.of();
      } else {
         this.lyricLeave = Math.min(1.0F, this.lyricLeave + dt / 0.32F);
         if (quiet && !(this.lyricLeave >= 1.0F)) {
            ArrayList var4 = new ArrayList(this.lyricLeaveItems.size());
            addFaded(var4, this.lyricLeaveItems, smooth(this.lyricLeave), 8.0F * s);
            return var4;
         } else {
            this.lyricLeaveItems = null;
            return List.of();
         }
      }
   }

   private static void addFaded(List items, List frozen, float fade, float rise) {
      for (MusicHUD.LyricItem var5 : frozen) {
         float var6 = var5.alpha() * (1.0F - fade);
         if (!(var6 <= 0.004F)) {
            items.add(new MusicHUD.LyricItem(var5.text(), var5.top() - rise * fade, var5.highlight(), var6, var5.progress(), var5.fill()));
         }
      }
   }

   private void syncedItems(
      List items,
      SyncedLyrics synced,
      long position,
      float viewH,
      MusicHUD.LyricMotion motion,
      MusicHUD.LyricHighlight highlight,
      float innerW,
      float s,
      float dt
   ) {
      float var11 = 6.0F * s;
      int var12 = synced.indexAt(position + 200L);
      int var13 = synced.firstWithTimeOf(var12);
      int var14 = Math.max(0, var13);
      if (synced != this.lyricSource) {
         this.lyricSource = synced;
         this.lyricStart = var14;
         this.lyricShifts.clear();
         this.lyricClock = 0.0F;
         this.lyricFadeItems = null;
         this.lyricFade = 1.0F;
         this.lyricShown = new float[synced.size() * 2];
         this.lyricBright = new float[synced.size()];
         this.lyricBrightLo = 0;
         this.lyricBrightHi = -1;

         for (int var15 = Math.max(0, var13); var15 <= var12; var15++) {
            this.lyricBright[var15] = 1.0F;
         }

         if (var12 >= 0) {
            this.lyricBrightLo = Math.max(0, var13);
            this.lyricBrightHi = var12;
         }
      }

      this.lyricClock += dt;
      this.lyricBreath = (this.lyricBreath + dt) % 1.6F;
      this.lyricCurrentFirst = var13;
      this.lyricCurrentLast = var12;
      float var19 = motion == MusicHUD.LyricMotion.WAVE ? 0.045F : 0.0F;
      if (var14 != this.lyricStart) {
         float var16 = var14 > this.lyricStart ? this.restDistance(this.lyricStart, var14, innerW, s) : -1.0F;
         if (motion != MusicHUD.LyricMotion.FADE && var16 > 0.0F && var16 <= viewH + var11 + 0.5F) {
            if (this.lyricShifts.size() == 8) {
               this.lyricShifts.remove(0);
            }

            this.lyricShifts.add(new MusicHUD.LyricShift(var16, this.lyricClock, var14, var19));
         } else {
            this.lyricFadeItems = new ArrayList(this.lyricLastItems);
            this.lyricFade = 0.0F;
            this.lyricShifts.clear();
            this.showView(synced, var14, viewH, innerW, s);
         }

         this.lyricStart = var14;
      }

      this.updateBrightness(var13, var12, dt);
      this.lyricFade = Math.min(1.0F, this.lyricFade + dt / 0.32F);
      if (this.lyricFade >= 1.0F) {
         this.lyricFadeItems = null;
      }

      float var20 = 1.37F;
      this.lyricShifts.removeIf(shift -> this.lyricClock - shift.at() > var20);
      if (this.lyricShifts.isEmpty()) {
         this.lyricClock = 0.0F;
      }

      float var17 = 8.0F * s;
      if (this.lyricFadeItems != null) {
         float var18 = smooth(this.lyricFade);
         addFaded(items, this.lyricFadeItems, var18, var17);
         this.addLyricView(items, synced, this.lyricStart, var18, var17 * (1.0F - var18), viewH, position, highlight, innerW, s, dt);
      } else {
         this.addLyricView(items, synced, this.lyricStart, 1.0F, 0.0F, viewH, position, highlight, innerW, s, dt);
      }
   }

   private void addLyricView(
      List items,
      SyncedLyrics synced,
      int from,
      float alpha,
      float rise,
      float viewH,
      long position,
      MusicHUD.LyricHighlight mode,
      float innerW,
      float s,
      float dt
   ) {
      float var13 = 6.0F * s;
      float var14 = 6.0F * s;
      float var15 = 8.0F * s + var14;
      boolean var16 = mode == MusicHUD.LyricHighlight.KARAOKE;
      float var17 = 0.0F;

      for (int var18 = from; var18 < synced.size() && var17 <= viewH + var15 * 2.0F; var18++) {
         String[] var19 = this.rowsFor(var18, innerW);
         int var20 = Math.max(1, var19.length);
         float var21 = var17 + this.lyricOffset(var18) + rise;
         float var22 = 1.0F - 0.4F * clamp01(var21 / var15 - 1.0F);
         float var23 = this.lyricGlow(var18, mode);
         float var24 = var19.length == 0 ? this.breakProgress(synced, var18, position) : 1.0F;
         float var25 = this.lineHeight(var19, var13);

         for (int var26 = 0; var26 < var20; var26++) {
            float var27 = var17 + var26 * (8.0F * s + var13);
            boolean var28 = var27 + 8.0F * s <= viewH + 0.5F;
            float var29 = this.updateShown(var18, var26, var28, dt);
            float var30 = var21 + var26 * (8.0F * s + var13);
            float var31 = alpha * var22 * smooth(var29) * topFade(var30, s) * bottomFade(var30 + 8.0F * s - viewH, 8.0F * s);
            if (!(var31 <= 0.004F)) {
               float var32 = var16 && var19.length > 0 ? this.rowFill(synced, var18, var19, var26, innerW, position) : 0.0F;
               items.add(new MusicHUD.LyricItem(var19.length == 0 ? null : var19[var26], var30, var23, var31, var24, var32));
            }
         }

         var17 += var25 + var14;
      }

      float var33 = 0.0F;

      for (int var34 = from - 1; var34 >= 0; var34--) {
         String[] var35 = this.rowsFor(var34, innerW);
         int var36 = Math.max(1, var35.length);
         float var37 = (var33 -= this.lineHeight(var35, var13) + var14) + this.lyricOffset(var34) + rise;
         if (topFade(var37 + (var36 - 1) * (8.0F * s + var13), s) <= 0.0F) {
            break;
         }

         float var38 = this.lyricGlow(var34, mode);
         float var39 = var35.length == 0 ? this.breakProgress(synced, var34, position) : 1.0F;

         for (int var40 = 0; var40 < var36; var40++) {
            float var41 = var37 + var40 * (8.0F * s + var13);
            float var42 = alpha * smooth(this.lyricShown[var34 * 2 + var40]) * topFade(var41, s);
            if (!(var42 <= 0.004F)) {
               float var43 = var16 && var35.length > 0 ? this.rowFill(synced, var34, var35, var40, innerW, position) : 0.0F;
               items.add(new MusicHUD.LyricItem(var35.length == 0 ? null : var35[var40], var41, var38, var42, var39, var43));
            }
         }
      }
   }

   private float lyricOffset(int index) {
      float var2 = 0.0F;

      for (int var3 = 0; var3 < this.lyricShifts.size(); var3++) {
         MusicHUD.LyricShift var4 = (MusicHUD.LyricShift)this.lyricShifts.get(var3);
         float var5 = Math.max(0, Math.min(6, index - var4.start())) * var4.stagger();
         float var6 = this.lyricClock - var4.at() - var5;
         var2 += var6 <= 0.0F ? var4.distance() : var4.distance() * springLeft(var6);
      }

      return var2;
   }

   private float restDistance(int from, int to, float innerW, float s) {
      float var5 = 6.0F * s;
      float var6 = 0.0F;

      for (int var7 = from; var7 < to; var7++) {
         var6 += this.lineHeight(this.rowsFor(var7, innerW), var5) + 6.0F * s;
      }

      return var6;
   }

   private void showView(SyncedLyrics synced, int from, float viewH, float innerW, float s) {
      Arrays.fill(this.lyricShown, 0.0F);
      float var6 = 6.0F * s;
      float var7 = 0.0F;

      for (int var8 = from; var8 < synced.size() && var7 < viewH; var8++) {
         String[] var9 = this.rowsFor(var8, innerW);
         int var10 = Math.max(1, var9.length);

         for (int var11 = 0; var11 < var10; var11++) {
            if (var7 + var11 * (8.0F * s + var6) + 8.0F * s <= viewH + 0.5F) {
               this.lyricShown[var8 * (2) + var11] = 1.0F;
            }
         }

         var7 += this.lineHeight(var9, var6) + 6.0F * s;
      }
   }

   private float updateShown(int index, int row, boolean fits, float dt) {
      int var5 = index * (2) + row;
      float var6 = dt / 0.3F;
      float var7 = fits ? Math.min(1.0F, this.lyricShown[var5] + var6) : Math.max(0.0F, this.lyricShown[var5] - var6);
      this.lyricShown[var5] = var7;
      return var7;
   }

   private void updateBrightness(int currentFirst, int currentLast, float dt) {
      int var4 = this.lyricBrightHi >= 0 ? this.lyricBrightLo : 2147483647;
      int var5 = this.lyricBrightHi;
      if (currentLast >= 0) {
         var4 = Math.min(var4, Math.max(0, currentFirst));
         var5 = Math.max(var5, currentLast);
      }

      float var6 = dt / 0.35F;
      int var7 = 2147483647;
      int var8 = -1;

      for (int var9 = Math.max(0, var4); var9 <= var5 && var9 < this.lyricBright.length; var9++) {
         boolean var10 = var9 >= currentFirst && var9 <= currentLast;
         float var11 = var10 ? Math.min(1.0F, this.lyricBright[var9] + var6) : Math.max(0.0F, this.lyricBright[var9] - var6);
         this.lyricBright[var9] = var11;
         if (var11 > 0.0F) {
            var7 = Math.min(var7, var9);
            var8 = var9;
         }
      }

      this.lyricBrightLo = var8 < 0 ? 0 : var7;
      this.lyricBrightHi = var8;
   }

   private float lyricGlow(int index, MusicHUD.LyricHighlight mode) {
      if (mode != MusicHUD.LyricHighlight.OFF && index >= 0 && index < this.lyricCurrentFirst) {
         return 1.0F;
      } else {
         return index >= 0 && index < this.lyricBright.length ? smooth(this.lyricBright[index]) : 0.0F;
      }
   }

   private float breakProgress(SyncedLyrics synced, int index, long position) {
      if (index >= this.lyricCurrentFirst && index <= this.lyricCurrentLast) {
         int var5 = synced.lastWithTimeOf(index) + 1;
         if (var5 >= synced.size()) {
            return 1.0F;
         }

         long var6 = synced.line(index).timeMs();
         long var8 = synced.line(var5).timeMs() - 200L;
         return var8 <= var6 ? 1.0F : clamp01((float)(position - var6) / (float)(var8 - var6));
      } else {
         return 1.0F;
      }
   }

   private float rowFill(SyncedLyrics synced, int index, String[] rows, int row, float innerW, long position) {
      if (index < this.lyricCurrentFirst) {
         return Float.POSITIVE_INFINITY;
      }

      if (index > this.lyricCurrentLast) {
         return 0.0F;
      }

      String var8 = rows[row];
      int var9 = 0;
      if (row > 0) {
         String var10 = this.lyricTexts[index];
         var9 = rows[0].length();

         while (var9 < var10.length() && var10.charAt(var9) == (32)) {
            var9++;
         }
      }

      float var15 = this.litChars(synced, index, position) - var9;
      if (var15 <= 0.0F) {
         return 0.0F;
      }

      if (var15 >= var8.length()) {
         return Float.POSITIVE_INFINITY;
      }

      int var11 = (int)var15;
      if (var11 > 0 && Character.isLowSurrogate(var8.charAt(var11))) {
         var11--;
      }

      int var12 = var11 + Character.charCount(var8.codePointAt(var11));
      float var13 = NanoVGRenderer.getTextWidth(var8.substring(0, var11), 8.0F * this.lyricScale);
      float var14 = NanoVGRenderer.getTextWidth(var8.substring(0, var12), 8.0F * this.lyricScale);
      return var13 + (var14 - var13) * clamp01((var15 - var11) / (var12 - var11));
   }

   private float litChars(SyncedLyrics synced, int index, long position) {
      MusicHUD.LyricWords var5 = this.lyricWords[index];
      if (var5 == null) {
         this.timeWords(synced, synced.firstWithTimeOf(index));
         var5 = this.lyricWords[index];
      }

      float var6 = 0.0F;
      long[] var7 = var5.start();

      for (int var8 = 0; var8 < var7.length && position >= var7[var8]; var8++) {
         float var9 = smooth((float)(position - var7[var8]) / (float)var5.sweep()[var8]);
         var6 = var5.from()[var8] + (var5.to()[var8] - var5.from()[var8]) * var9;
      }

      return var6;
   }

   private void timeWords(SyncedLyrics synced, int first) {
      int var3 = synced.lastWithTimeOf(first);
      int var4 = var3 + 1;
      long var5 = synced.line(first).timeMs();
      long var7 = var4 < synced.size() ? synced.line(var4).timeMs() - 200L : Long.MAX_VALUE;
      int var9 = 0;

      for (int var10 = first; var10 <= var3; var10++) {
         var9 += letterCount(this.lyricTexts[var10]);
      }

      long var28 = var4 < synced.size() ? Math.min(var7 - var5, Math.round(var9 * this.lyricCharMs * 2.0F)) : Math.round(var9 * this.lyricCharMs);
      if (var28 < 0L) {
         var28 = 0L;
      }

      int var12 = 0;

      for (int var13 = first; var13 <= var3; var13++) {
         String var14 = this.lyricTexts[var13];
         int var15 = var14.isEmpty() ? 0 : var14.length() - letterCount(var14) + 1;
         int[] var16 = new int[var15];
         int[] var17 = new int[var15];
         long[] var18 = new long[var15];
         long[] var19 = new long[var15];
         List var20 = synced.line(var13).wordTimes();
         boolean var21 = var20.size() == var15;
         int var29 = 0;

         for (int var23 = 0; var23 < var15; var23++) {
            var16[var23] = var29;
            if ((var29 = var14.indexOf(32, var29)) < 0) {
               var29 = var14.length();
            }

            var17[var23] = var29++;
            long var24 = var21 ? (Long)var20.get(var23) : var5 + var28 * var12 / Math.max(1, var9);
            long var26 = var21
               ? (var23 + 1 < var15 ? (Long)var20.get(var23 + 1) : Math.min(var7, var24 + 180L))
               : var5 + var28 * (var12 += var17[var23] - var16[var23]) / Math.max(1, var9);
            var18[var23] = var24;
            var19[var23] = Math.max(1L, Math.min(180L, var26 - var24));
         }

         this.lyricWords[var13] = new MusicHUD.LyricWords(var16, var17, var18, var19);
      }
   }

   private float lyricPace(SyncedLyrics synced) {
      float[] var2 = new float[synced.size()];
      int var3 = 0;
      int var4 = 0;

      while (var4 < synced.size()) {
         int var5 = synced.lastWithTimeOf(var4);
         int var6 = 0;

         for (int var7 = var4; var7 <= var5; var7++) {
            var6 += letterCount(this.lyricTexts[var7]);
         }

         if (var6 > 0 && var5 + 1 < synced.size()) {
            var2[var3++] = (float)(synced.line(var5 + 1).timeMs() - synced.line(var4).timeMs()) / var6;
         }

         var4 = var5 + 1;
      }

      if (var3 == 0) {
         return 90.0F;
      }

      Arrays.sort(var2, 0, var3);
      return var2[var3 / (2)];
   }

   private float lyricViewHeight(SyncedLyrics synced, int lines, float innerW, float s) {
      this.prepareRows(innerW);
      if (this.lyricViewH >= 0.0F && this.lyricViewLines == lines && Math.abs(this.lyricViewScale - s) < 0.001F) {
         return this.lyricViewH;
      }

      int var5 = lines;
      int var6 = 0;

      while (var6 < synced.size()) {
         int var7 = 0;
         int var8 = synced.lastWithTimeOf(var6);

         for (int var9 = var6; var9 <= var8; var9++) {
            var7 += Math.max(1, this.rowsFor(var9, innerW).length);
         }

         var5 = Math.max(var5, Math.min(4, Math.min(var7, lines)));
         var6 = synced.lastWithTimeOf(var6) + 1;
      }

      this.lyricViewH = var5 * 8.0F * s + (var5 - 1) * 6.0F * s;
      this.lyricViewLines = lines;
      this.lyricViewScale = s;
      return this.lyricViewH;
   }

   private float easeLyricBlock(float target, float dt) {
      float var3 = target - this.lyricBlockH;
      if (Math.abs(var3) < 0.5F) {
         this.lyricBlockH = target;
      } else {
         float var4 = var3 > 0.0F ? 14.0F : 9.0F;
         this.lyricBlockH = this.lyricBlockH + var3 * (1.0F - (float)Math.exp(-var4 * dt));
      }

      return this.lyricBlockH;
   }

   private void prepareLyrics(SyncedLyrics synced) {
      if (synced != this.lyricLastSynced) {
         this.lyricLastSynced = synced;
         this.lyricViewH = -1.0F;
         if (synced != null && synced.size() != 0) {
            this.lyricTexts = new String[synced.size()];
            this.lyricRows = new String[synced.size()][];
            this.lyricWords = new MusicHUD.LyricWords[synced.size()];

            for (int var2 = 0; var2 < synced.size(); var2++) {
               String var3 = synced.line(var2).text();
               if (var3 == null) {
                  var3 = "";
               }

               this.lyricTexts[var2] = var3;
            }

            this.lyricReadable = true;
            this.lyricCharMs = this.lyricPace(synced);
         } else {
            this.lyricTexts = new String[0];
            this.lyricRows = new String[0][];
            this.lyricWords = new MusicHUD.LyricWords[0];
            this.lyricReadable = true;
         }
      }
   }

   private void prepareRows(float innerW) {
      int var2 = (int)innerW;
      if (var2 != this.lyricRowsWidth) {
         Arrays.fill(this.lyricRows, null);
         this.lyricRowsWidth = var2;
         this.lyricViewH = -1.0F;
      }
   }

   private String[] rowsFor(int index, float innerW) {
      this.prepareRows(innerW);
      String[] var3 = this.lyricRows[index];
      if (var3 == null) {
         var3 = this.wrapLyric(this.lyricTexts[index], innerW);
         this.lyricRows[index] = var3;
      }

      return var3;
   }

   private String[] wrapLyric(String text, float maxW) {
      if (text.isEmpty()) {
         return new String[0];
      }

      if (NanoVGRenderer.getTextWidth(text, 8.0F * this.lyricScale) <= maxW) {
         return new String[]{text};
      }

      int var3 = -1;
      float var4 = Float.MAX_VALUE;

      for (int var5 = text.indexOf(32); var5 > 0; var5 = text.indexOf(32, var5 + 1)) {
         String var6 = text.substring(0, var5).stripTrailing();
         float var7 = NanoVGRenderer.getTextWidth(var6, 8.0F * this.lyricScale);
         if (var7 > maxW) {
            break;
         }

         String var8 = text.substring(var5 + 1).stripLeading();
         float var9 = NanoVGRenderer.getTextWidth(var8, 8.0F * this.lyricScale);
         if (var9 <= maxW && Math.max(var7, var9) < var4) {
            var4 = Math.max(var7, var9);
            var3 = var5;
         }
      }

      if (var3 > 0) {
         String[] var15 = new String[2];
         var15[0] = text.substring(0, var3).stripTrailing();
         var15[1] = text.substring(var3 + 1).stripLeading();
         return var15;
      } else {
         int var10 = this.fitPrefix(text, maxW);
         String var11 = text.substring(0, var10).stripTrailing();
         String var12 = text.substring(var10).stripLeading();
         if (var12.isEmpty()) {
            return new String[]{var11};
         } else {
            float var13 = NanoVGRenderer.getTextWidth(var12, 8.0F * this.lyricScale);
            if (var13 <= maxW) {
               String[] var14 = new String[2];
               var14[0] = var11;
               var14[1] = var12;
               return var14;
            } else {
               String[] var10000 = new String[2];
               var10000[0] = var11;
               var10000[1] = this.ellipsize(var12, maxW);
               return var10000;
            }
         }
      }
   }

   private int fitPrefix(String text, float maxW) {
      int var3 = -1;

      for (int var4 = text.indexOf(32);
         var4 > 0 && !(NanoVGRenderer.getTextWidth(text.substring(0, var4), 8.0F * this.lyricScale) > maxW);
         var4 = text.indexOf(32, var4 + 1)
      ) {
         var3 = var4;
      }

      if (var3 > 0) {
         return var3 + 1;
      }

      int var5 = 1;
      int var6 = text.length();

      while (var5 < var6) {
         int var7 = var5 + var6 + 1 >>> 1;
         if (NanoVGRenderer.getTextWidth(text.substring(0, var7), 8.0F * this.lyricScale) <= maxW) {
            var5 = var7;
         } else {
            var6 = var7 - 1;
         }
      }

      if (var5 > 1 && var5 < text.length() && Character.isHighSurrogate(text.charAt(var5 - 1))) {
         var5--;
      }

      return var5;
   }

   private String ellipsize(String text, float maxW) {
      float var3 = maxW - NanoVGRenderer.getTextWidth("…", 8.0F * this.lyricScale);
      int var4 = 0;
      int var5 = text.length();

      while (var4 < var5) {
         int var6 = var4 + var5 + 1 >>> 1;
         if (NanoVGRenderer.getTextWidth(text.substring(0, var6), 8.0F * this.lyricScale) <= var3) {
            var4 = var6;
         } else {
            var5 = var6 - 1;
         }
      }

      return text.substring(0, var4).stripTrailing() + "…";
   }

   private void drawLyrics(float left, float top, float width, float bottom, float s, float alpha) {
      this.sc = s;
      this.lyricScale = s;
      long var7 = System.currentTimeMillis();
      float var9 = this.lyricLastFrameMs == 0L ? 0.0F : Math.min(0.05F, (float)(var7 - this.lyricLastFrameMs) / 1000.0F);
      this.lyricLastFrameMs = var7;
      MusicHUD.LyricView var10 = this.planLyrics(width, s, var9, var7);
      if (!(bottom <= top - 4.0F * s + Math.max(1.0F, 2.0F * s)) && !(alpha <= 0.001F)) {
         float var11 = top - 4.0F * s + Math.max(1.0F, 2.0F * s);
         float var12 = (float)Math.floor(left - 2.0F);
         float var13 = (float)Math.ceil(left + width + 2.0F);
         float var14 = bottom;
         if (!(var14 <= var11) && !(var13 <= var12)) {
            NanoVGRenderer.save();

            try {
               NanoVGRenderer.scissor(var12, var11, var13 - var12, var14 - var11);
               if (var10.status() != null) {
                  float var15 = Math.round(top + (8.0F * s - 7.0F * s) * 0.5F);
                  Color var16 = withAlpha(this.lyricsUpcomingColor(), alpha * 0.85F);
                  NanoVGRenderer.drawText(var10.status(), left, var15, 7.0F * s, var16);
               }

               for (MusicHUD.LyricItem var37 : var10.items()) {
                  float var17 = alpha * var37.alpha();
                  if (!(var17 <= 0.004F)) {
                     float var18 = top + var37.top();
                     if (var37.text() == null) {
                        float var19 = var18 + this.ascent();
                        this.drawLyricBreak(left, var19, s, var37.highlight(), var37.progress(), var17);
                     } else {
                        Color var38 = withAlpha(lerpColor(this.lyricsUpcomingColor(), this.lyricsTextColor(), var37.highlight()), var17);
                        float var20 = Math.min(var13, (float)Math.floor(left) + Math.round(var37.fill()));
                        if (var37.fill() <= 0.0F) {
                           NanoVGRenderer.drawText(var37.text(), left, Math.round(var18), 8.0F * s, var38);
                        } else if (var20 >= var13) {
                           NanoVGRenderer.drawText(var37.text(), left, Math.round(var18), 8.0F * s, withAlpha(this.highlightTextColor(), var17));
                        } else {
                           NanoVGRenderer.save();

                           try {
                              NanoVGRenderer.scissor(var12, var11, var20 - var12, var14 - var11);
                              NanoVGRenderer.drawText(var37.text(), left, Math.round(var18), 8.0F * s, withAlpha(this.highlightTextColor(), var17));
                           } finally {
                              NanoVGRenderer.restore();
                           }

                           NanoVGRenderer.save();

                           try {
                              NanoVGRenderer.scissor(var20, var11, var13 - var20, var14 - var11);
                              NanoVGRenderer.drawText(var37.text(), left, Math.round(var18), 8.0F * s, var38);
                           } finally {
                              NanoVGRenderer.restore();
                           }
                        }
                     }
                  }
               }
            } finally {
               NanoVGRenderer.restore();
            }
         }
      }
   }

   private void drawLyricBreak(float left, float centerY, float s, float highlight, float progress, float alpha) {
      float var7 = Math.max(1.0F, 1.6F * s);
      float var8 = Math.max(4.0F, Math.round(6.0F * s));
      float var9 = 0.5F - 0.5F * (float)Math.cos(this.lyricBreath / 1.6F * 2.0 * Math.PI);

      for (int var10 = 0; var10 < (3); var10++) {
         float var11 = smooth(progress * 3.0F - var10) * highlight;
         Color var12 = withAlpha(lerpColor(this.lyricsUpcomingColor(), this.highlightTextColor(), var11), alpha);
         float var13 = var7 * (1.0F + 0.3F * var11 + 0.12F * var9 * highlight);
         NanoVGRenderer.drawCircle(left + var7 + var10 * var8, centerY, var13, var12);
      }
   }

   private void cacheHUDBounds() {
      HUD var1 = this.hudEditor();
      if (var1 != null) {
         byte[] var10001 = new byte[7];
         var10001[0] = (byte)(24);
         var10001[1] = (byte)(-22);
         var10001[2] = (byte)(115);
         var10001[3] = (byte)(-86);
         var10001[4] = (byte)(-81);
         var10001[5] = (byte)(-111);
         var10001[6] = (byte)(-84);
         var1.cacheExternalBounds($$v_s(var10001, 8851307744329977934L), this.bx, this.by, this.widgetW, this.H_u * this.sc);
      }
   }

   private void drawEditorOutline() {
      HUD var1 = this.hudEditor();
      if (var1 != null) {
         byte[] var10001 = new byte[7];
         var10001[0] = (byte)(24);
         var10001[1] = (byte)(-22);
         var10001[2] = (byte)(115);
         var10001[3] = (byte)(-86);
         var10001[4] = (byte)(-81);
         var10001[5] = (byte)(-111);
         var10001[6] = (byte)(-84);
         var1.drawExternalEditorOutline($$v_s(var10001, 8851307744329977934L), this.bx, this.by, this.widgetW, this.H_u * this.sc);
      }
   }

   private float[] getPositionFromEditor(float W, float H) {
      HUD var3 = this.hudEditor();
      int var4 = mc.method_22683().method_4486();
      int var5 = mc.method_22683().method_4502();
      float var6 = Math.max(1.0F, Math.min(var4 - W - 1.0F, var4 / 2.0F - W / 2.0F));
      float var7 = Math.max(1.0F, Math.min(var5 - H - 1.0F, 30.0F));
      if (var3 != null) {
         byte[] var10001 = new byte[7];
         var10001[0] = (byte)(24);
         var10001[1] = (byte)(-22);
         var10001[2] = (byte)(115);
         var10001[3] = (byte)(-86);
         var10001[4] = (byte)(-81);
         var10001[5] = (byte)(-111);
         var10001[6] = (byte)(-84);
         float var8 = var3.wx($$v_s(var10001, 8851307744329977934L), var6);
         var10001 = new byte[7];
         var10001[0] = (byte)(24);
         var10001[1] = (byte)(-22);
         var10001[2] = (byte)(115);
         var10001[3] = (byte)(-86);
         var10001[4] = (byte)(-81);
         var10001[5] = (byte)(-111);
         var10001[6] = (byte)(-84);
         float var9 = var3.wy($$v_s(var10001, 8851307744329977934L), var7);
         float[] var10 = new float[2];
         var10[0] = var8;
         var10[1] = var9;
         return var10;
      } else {
         float[] var10000 = new float[2];
         var10000[0] = var6;
         var10000[1] = var7;
         return var10000;
      }
   }

   private HUD hudEditor() {
      try {
         return (HUD)VieClientMod.getInstance().getModuleManager().getModule(HUD.class);
      } catch (RuntimeException var2) {
         return null;
      }
   }

   private void uploadPendingImage() {
      BufferedImage var1 = (BufferedImage)this.pendingImage.get();
      String var2 = this.pendingArtworkIdentity;
      if (var1 == null) {
         if (this.clearCachedImageRequested) {
            this.resetImage();
            this.clearCachedImageRequested = false;
         }
      } else {
         int var3 = NVGImage.createFromBufferedImage(var1);
         if (var3 > 0) {
            if (!this.pendingImage.compareAndSet(var1, null)) {
               NVGImage.deleteImage(var3);
            } else {
               if (cachedImageId != (-1)) {
                  NVGImage.deleteImage(cachedImageId);
               }

               cachedImageId = var3;
               cachedBufferedImage = var1;
               this.uploadedArtworkIdentity = var2;
               this.pendingArtworkIdentity = "";
               this.clearCachedImageRequested = false;
            }
         }
      }
   }

   private static Color withAlpha(Color color, float alphaMultiplier) {
      int var2 = Math.round(color.getAlpha() * Math.max(0.0F, Math.min(1.0F, alphaMultiplier)));
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), var2);
   }

   private static String truncateNVG(String text, float maxWidth, float size, boolean bold) {
      if (NanoVGRenderer.getTextWidth(text, size, bold) <= maxWidth) {
         return text;
      }

      byte[] var10000 = new byte[2];
      var10000[0] = (byte)(69);
      var10000[1] = (byte)(-76);
      String var4 = $$v_s(var10000, 8851307744329977934L);
      int var5 = text.length();

      while (var5 > 1 && NanoVGRenderer.getTextWidth(text.substring(0, var5) + var4, size, bold) > maxWidth) {
         var5--;
      }

      return text.substring(0, var5) + var4;
   }

   private static String formatTime(int ms) {
      int var1 = Math.max(0, ms) / (1000);
      int var2 = var1 / (3600);
      int var3 = var1 % (3600) / (60);
      int var4 = var1 % (60);
      if (var2 > 0) {
         byte[] var6 = new byte[12];
         var6[0] = (byte)(78);
         var6[1] = (byte)(-2);
         var6[2] = (byte)(38);
         var6[3] = (byte)(-5);
         var6[4] = (byte)(-10);
         var6[5] = (byte)(-59);
         var6[6] = (byte)(-79);
         var6[7] = (byte)(104);
         var6[8] = (byte)(38);
         var6[9] = (byte)(-110);
         var6[10] = (byte)(-42);
         var6[11] = (byte)(-110);
         String var7 = $$v_s(var6, 8851307744329977934L);
         Object[] var8 = new Object[3];
         var8[0] = var2;
         var8[1] = var3;
         var8[2] = var4;
         return String.format(var7, var8);
      } else {
         byte[] var10000 = new byte[7];
         var10000[0] = (byte)(78);
         var10000[1] = (byte)(-2);
         var10000[2] = (byte)(38);
         var10000[3] = (byte)(-5);
         var10000[4] = (byte)(-10);
         var10000[5] = (byte)(-59);
         var10000[6] = (byte)(-79);
         String var5 = $$v_s(var10000, 8851307744329977934L);
         Object[] var10001 = new Object[2];
         var10001[0] = var3;
         var10001[1] = var4;
         return String.format(var5, var10001);
      }
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

   public enum LyricHighlight {
      KARAOKE,
      LINE,
      OFF;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static MusicHUD.LyricHighlight[] $values() {
         MusicHUD.LyricHighlight[] var10000 = new MusicHUD.LyricHighlight[3];
         var10000[0] = KARAOKE;
         var10000[1] = LINE;
         var10000[2] = OFF;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[7];
         var10002[0] = (byte)(115);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-97);
         var10002[4] = (byte)(124);
         var10002[5] = (byte)(49);
         var10002[6] = (byte)(22);
         KARAOKE = new MusicHUD.LyricHighlight();
         var10002 = new byte[4];
         var10002[0] = (byte)(116);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-101);
         LINE = new MusicHUD.LyricHighlight();
         var10002 = new byte[3];
         var10002[0] = (byte)(119);
         var10002[1] = (byte)(-36);
         var10002[2] = (byte)(90);
         OFF = new MusicHUD.LyricHighlight();
      }

      // $VF: synthetic method
      private static String $$v_s(byte[] var0, long var1) {
         byte[] var3 = new byte[var0.length];

         for (int var4 = 0; var4 < var0.length; var4++) {
            var3[var4] = (byte)(
               var0[var4]
                  ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                  ^ (var4 + 1) * (37)
            );
         }

         return new String(var3, StandardCharsets.UTF_8);
      }
   }

   public record LyricItem(String text, float top, float highlight, float alpha, float progress, float fill) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public enum LyricLines {
      ONE,
      TWO,
      THREE;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static MusicHUD.LyricLines[] $values() {
         MusicHUD.LyricLines[] var10000 = new MusicHUD.LyricLines[3];
         var10000[0] = ONE;
         var10000[1] = TWO;
         var10000[2] = THREE;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[3];
         var10002[0] = (byte)(119);
         var10002[1] = (byte)(-44);
         var10002[2] = (byte)(89);
         ONE = new MusicHUD.LyricLines();
         var10002 = new byte[3];
         var10002[0] = (byte)(108);
         var10002[1] = (byte)(-51);
         var10002[2] = (byte)(83);
         TWO = new MusicHUD.LyricLines();
         var10002 = new byte[5];
         var10002[0] = (byte)(108);
         var10002[1] = (byte)(-46);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-101);
         var10002[4] = 1;
         THREE = new MusicHUD.LyricLines();
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

   public enum LyricMotion {
      WAVE,
      GLIDE,
      FADE;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static MusicHUD.LyricMotion[] $values() {
         MusicHUD.LyricMotion[] var10000 = new MusicHUD.LyricMotion[3];
         var10000[0] = WAVE;
         var10000[1] = GLIDE;
         var10000[2] = FADE;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[4];
         var10002[0] = (byte)(111);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(74);
         var10002[3] = (byte)(-101);
         WAVE = new MusicHUD.LyricMotion();
         var10002 = new byte[5];
         var10002[0] = (byte)(127);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-102);
         var10002[4] = (byte)(-12);
         GLIDE = new MusicHUD.LyricMotion();
         var10002 = new byte[4];
         var10002[0] = (byte)(126);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(88);
         var10002[3] = (byte)(-101);
         FADE = new MusicHUD.LyricMotion();
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

   public record LyricShift(float distance, float at, int start, float stagger) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public record LyricView(List<MusicHUD.LyricItem> items, String status, float height) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public record LyricWords(int[] from, int[] to, long[] start, long[] sweep) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record PlaybackState(MusicSnapshot snapshot, long publishedAtMs) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public enum SourcePriority {
      AUTOMATIC(null),
      SPOTIFY("spotify-api"),
      APPLE_MUSIC("apple-music-api"),
      SOUNDCLOUD("soundcloud-app"),
      OTHER_MUSIC_APPS("process"),
      YOUTUBE("browser");

      private final String providerId;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      SourcePriority(String providerId) {
         this.providerId = providerIdx;
      }

      public String providerId() {
         return this.providerId;
      }

      // $VF: synthetic method
      private static MusicHUD.SourcePriority[] $values() {
         MusicHUD.SourcePriority[] var10000 = new MusicHUD.SourcePriority[6];
         var10000[0] = AUTOMATIC;
         var10000[1] = SPOTIFY;
         var10000[2] = APPLE_MUSIC;
         var10000[3] = SOUNDCLOUD;
         var10000[4] = OTHER_MUSIC_APPS;
         var10000[5] = YOUTUBE;
         return var10000;
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

   public enum TitleMode {
      EXPAND,
      SCROLL;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static MusicHUD.TitleMode[] $values() {
         MusicHUD.TitleMode[] var10000 = new MusicHUD.TitleMode[2];
         var10000[0] = EXPAND;
         var10000[1] = SCROLL;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[6];
         var10002[0] = (byte)(124);
         var10002[1] = (byte)(-62);
         var10002[2] = (byte)(76);
         var10002[3] = (byte)(-97);
         var10002[4] = (byte)(17);
         var10002[5] = (byte)(127);
         EXPAND = new MusicHUD.TitleMode();
         var10002 = new byte[6];
         var10002[0] = (byte)(106);
         var10002[1] = (byte)(-39);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-111);
         var10002[4] = (byte)(19);
         var10002[5] = (byte)(119);
         SCROLL = new MusicHUD.TitleMode();
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
}
