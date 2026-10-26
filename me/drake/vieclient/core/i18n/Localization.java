package me.drake.vieclient.core.i18n;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import me.drake.vieclient.modules.client.ClientSettings;

public final class Localization {
   private static volatile Language currentLanguage = Language.ENG;
   private static final List LISTENERS = new CopyOnWriteArrayList();
   private static final Map MOD_DESC_VIE = new ConcurrentHashMap();
   private static final Map MOD_DESC_ENG = new ConcurrentHashMap();
   private static final Map SET_DESC_VIE = new ConcurrentHashMap();
   private static final Map SET_DESC_ENG = new ConcurrentHashMap();
   private static final Map CAT_VIE = new ConcurrentHashMap();
   private static final Map CAT_ENG = new ConcurrentHashMap();
   private static final Map UI_VIE = new ConcurrentHashMap();
   private static final Map UI_ENG = new ConcurrentHashMap();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private Localization() {
   }

   public static Language getLanguage() {
      return currentLanguage;
   }

   public static boolean isVietnamese() {
      return currentLanguage == Language.VIE;
   }

   public static boolean isEnglish() {
      return currentLanguage == Language.ENG;
   }

   public static void setLanguage(Language language) {
      if (language != null && language != currentLanguage) {
         currentLanguage = language;

         for (Consumer var2 : LISTENERS) {
            try {
               var2.accept(language);
            } catch (Throwable var4) {
            }
         }
      }
   }

   public static Language toggle() {
      Language var0 = currentLanguage.next();
      setLanguage(var0);

      try {
         ClientSettings var1 = ClientSettings.get();
         if (var1 != null) {
            var1.syncLanguage(var0);
         }
      } catch (Throwable var2) {
      }

      return var0;
   }

   public static void addListener(Consumer listener) {
      if (listener != null) {
         LISTENERS.add(listener);
      }
   }

   public static void removeListener(Consumer listener) {
      if (listener != null) {
         LISTENERS.remove(listener);
      }
   }

   private static String normalizeKey(String key) {
      return key == null ? "" : key.trim().toLowerCase(Locale.ROOT).replace(" ", "").replace("_", "").replace("-", "");
   }

   public static boolean containsVietnamese(String text) {
      if (text != null && !text.isEmpty()) {
         for (int var1 = 0; var1 < text.length(); var1++) {
            char var2 = text.charAt(var1);
            if (var2 >= (192) && var2 <= (591)
               || var2 >= (7840) && var2 <= (7929)
               || var2 == (273)
               || var2 == (272)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static String getModuleDescription(String moduleName, String fallback) {
      String var2 = normalizeKey(moduleName);
      Map var3 = isVietnamese() ? MOD_DESC_VIE : MOD_DESC_ENG;
      String var4 = (String)var3.get(var2);
      if (var4 != null) {
         return var4;
      }

      if (fallback != null) {
         if (isEnglish() && containsVietnamese(fallback)) {
            String var5 = (String)MOD_DESC_ENG.get(var2);
            return var5 != null ? var5 : moduleName;
         } else {
            return fallback;
         }
      } else {
         return "";
      }
   }

   public static String getSettingDescription(String settingName, String fallback) {
      String var2 = normalizeKey(settingName);
      Map var3 = isVietnamese() ? SET_DESC_VIE : SET_DESC_ENG;
      String var4 = (String)var3.get(var2);
      if (var4 != null) {
         return var4;
      }

      if (fallback != null) {
         if (isEnglish() && containsVietnamese(fallback)) {
            String var5 = (String)SET_DESC_ENG.get(var2);
            return var5 != null ? var5 : settingName;
         } else {
            return fallback;
         }
      } else {
         return "";
      }
   }

   public static String getCategoryName(String rawCategoryName) {
      if (rawCategoryName == null) {
         return "";
      }

      String var1 = normalizeKey(rawCategoryName);
      Map var2 = isVietnamese() ? CAT_VIE : CAT_ENG;
      String var3 = (String)var2.get(var1);
      return var3 != null ? var3 : rawCategoryName;
   }

   public static String get(String key, String fallback) {
      if (key == null) {
         return fallback != null ? fallback : "";
      } else {
         String var2 = key.toLowerCase(Locale.ROOT);
         Map var3 = isVietnamese() ? UI_VIE : UI_ENG;
         String var4 = (String)var3.get(var2);
         if (var4 != null) {
            return var4;
         } else {
            return fallback != null ? fallback : key;
         }
      }
   }

   public static String get(String key) {
      return get(key, key);
   }

   public static String getLanguageHint() {
      return isVietnamese()
         ? "Ngôn ngữ: Tiếng Việt (Bấm để đổi sang English)"
         : "Language: English (Click to switch to Tiếng Việt)";
   }

   private static void regMod(String name, String vie, String eng) {
      String var3 = normalizeKey(name);
      MOD_DESC_VIE.put(var3, vie);
      MOD_DESC_ENG.put(var3, eng);
   }

   private static void regSet(String name, String vie, String eng) {
      String var3 = normalizeKey(name);
      SET_DESC_VIE.put(var3, vie);
      SET_DESC_ENG.put(var3, eng);
   }

   private static void regCat(String name, String vie, String eng) {
      String var3 = normalizeKey(name);
      CAT_VIE.put(var3, vie);
      CAT_ENG.put(var3, eng);
   }

   private static void regUI(String key, String vie, String eng) {
      String var3 = key.toLowerCase(Locale.ROOT);
      UI_VIE.put(var3, vie);
      UI_ENG.put(var3, eng);
   }

   private static void registerCategories() {
      regCat(
         "Combat",
         "Chiến đấu",
         "Combat"
      );
      regCat(
         "Player",
         "Người chơi",
         "Player"
      );
      regCat(
         "Render",
         "Hiển thị",
         "Render"
      );
      regCat(
         "Movement",
         "Di chuyển",
         "Movement"
      );
      regCat(
         "World",
         "Thế giới",
         "World"
      );
      regCat(
         "Exploit",
         "Khai thác",
         "Exploit"
      );
      regCat(
         "DonutSMP",
         "Donut SMP",
         "Donut SMP"
      );
      regCat(
         "Donut",
         "Donut SMP",
         "Donut SMP"
      );
      regCat(
         "SMPUtilities",
         "Tiện ích SMP",
         "SMP Utilities"
      );
      regCat(
         "BaseFinding",
         "Tìm Base",
         "Base Finding"
      );
      regCat(
         "Customize",
         "Tùy biến",
         "Customize"
      );
      regCat(
         "Protect",
         "Bảo mật",
         "Protect"
      );
      regCat(
         "Config",
         "Cấu hình",
         "Config"
      );
      regCat(
         "Client",
         "Hệ thống",
         "Client"
      );
      regCat(
         "Misc",
         "Tiện ích",
         "Misc"
      );
   }

   private static void registerModules() {
      registerCombatModules();
      registerMovementAndPlayerModules();
      registerRenderModules();
      registerWorldAndDonutModules();
   }

   private static void registerCombatModules() {
      regMod(
         "AimAssist",
         "Hỗ trợ lia tâm mượt mà tự nhiên hướng về phía mục tiêu.",
         "Smooth and natural aim assistance towards targets."
      );
      regMod(
         "AnchorMacro",
         "Tự động đặt và kích nổ Respawn Anchor cực nhanh.",
         "Rapid automated Respawn Anchor placement & detonation macro."
      );
      regMod(
         "AutoCrystal",
         "Tự động đặt và phá End Crystal trong tầm tối ưu.",
         "Automatically place and break End Crystals with optimized range."
      );
      regMod(
         "AutoDoubleHand",
         "Tự động quản lý totem và vũ khí hai tay khi giao chiến.",
         "Automatically manage totem and weapon hands during combat."
      );
      regMod(
         "AutoHitCrystal",
         "Tự động kích nổ crystal ngay khi có cơ hội gây sát thương.",
         "Automatically detonate crystals when damage opportunities arise."
      );
      regMod(
         "AutoInvTotem",
         "Tự động lấy Totem từ kho đồ vào tay phụ khi hết totem.",
         "Automatically move Totems from inventory to offhand when depleted."
      );
      regMod(
         "AutoJumpReset",
         "Tự động nhảy để giảm giật lùi (knockback) khi nhận sát thương.",
         "Automatically jump to reset knockback when taking damage."
      );
      regMod(
         "AutoTotem",
         "Luôn giữ Totem of Undying trên tay phụ khi máu xuống thấp.",
         "Keep Totem of Undying in offhand when health drops low."
      );
      regMod(
         "CartMacro",
         "Macro đặt và kích nổ Minecart TNT siêu tốc.",
         "High-speed TNT Minecart placement and detonation macro."
      );
      regMod(
         "DoubleAnchor",
         "Đặt hai Anchor liên tiếp để tối đa hóa sát thương diện rộng.",
         "Place double Anchors in quick succession for maximum burst damage."
      );
      regMod(
         "ElytraSwap",
         "Hoán đổi nhanh giữa áo giáp ngực và cánh Elytra.",
         "Quick swap between chestplate and Elytra wings."
      );
      regMod(
         "Hitbox",
         "Mở rộng kích thước hitbox của mục tiêu để dễ đánh trúng hơn.",
         "Expand target hitboxes for easier hit registration."
      );
      regMod(
         "HoverTotem",
         "Tự động giữ totem khi di chuột trong kho đồ.",
         "Automatically hold totem when hovering in inventory."
      );
      regMod(
         "MaceSwap",
         "Tự động chuyển sang chùy Mace ngay trước khoảnh khắc tiếp đất.",
         "Automatically switch to Mace weapon just before landing impact."
      );
      regMod(
         "SafeAnchor",
         "Chỉ kích nổ Respawn Anchor khi người chơi ở khoảng cách an toàn.",
         "Only detonate Respawn Anchors when at a safe distance."
      );
      regMod(
         "ShieldDisabler",
         "Tự động dùng rìu phá khiên đối thủ khi đối phương đang đỡ.",
         "Automatically use axe to disable enemy shields when blocking."
      );
      regMod(
         "SpearSwap",
         "Macro hoán đổi giáo và vũ khí linh hoạt theo cự ly.",
         "Flexible spear/weapon swap macro based on distance."
      );
      regMod(
         "StaticHitboxes",
         "Giữ kích thước hitbox cố định bất kể chuyển động đối thủ.",
         "Maintain fixed hitbox size regardless of target movement."
      );
      regMod(
         "TriggerBot",
         "Tự động tấn công ngay khi mục tiêu lọt vào tâm ngắm.",
         "Automatically attack as soon as a target enters your crosshair."
      );
      regMod(
         "WTap",
         "Tự động nhả phím W để reset sprint và tối đa knockback đối thủ.",
         "Automatically reset sprint with W-tap for maximum knockback."
      );
      regMod(
         "STap",
         "Tự động lùi S-tap để combo giữ khoảng cách đối thủ hoàn hảo.",
         "Automatically S-tap to maintain ideal combo spacing."
      );
      regMod(
         "AutoSwordBotting",
         "Tự động vung kiếm farm quái hoặc AFK botting an toàn.",
         "Automatically swing sword for mob farming or AFK botting."
      );
   }

   private static void registerMovementAndPlayerModules() {
      regMod(
         "AutoSprint",
         "Tự động chạy nhanh khi di chuyển.",
         "Automatically sprints when moving."
      );
      regMod(
         "AutoFireworkFlight",
         "Tự động bắn pháo hoa duy trì độ cao bay bằng cánh Elytra.",
         "Sustain Elytra flight with automatic firework rockets."
      );
      regMod(
         "FreeLook",
         "Tự do xoay góc nhìn quan sát 360 độ mà không làm đổi hướng di chuyển.",
         "Look freely in 360 degrees without altering your movement direction."
      );
      regMod(
         "TridentBoost",
         "Phóng đinh ba Riptide không cần nước/mưa, gồng nhanh tức thì và gia tăng lực phóng.",
         "Use riptide tridents out of water, charge faster, and boost launch velocity."
      );
      regMod(
         "AutoTool",
         "Tự động đổi công cụ đào hợp lý nhất kèm độ trễ tự nhiên và bảo vệ độ bền.",
         "Automatically switch to best tool with humanized delay & durability safeguard."
      );
      regMod(
         "SwingSpeed",
         "Điều chỉnh tốc độ vung tay mượt mà hoặc làm chậm theo phong cách slow-motion điện ảnh.",
         "Adjust hand swing speed smoothly or create slow-motion cinematic swings."
      );
      regMod(
         "Freecam",
         "Tách rời camera để tự do bay lượn quan sát xung quanh.",
         "Detach camera to freely fly and inspect surroundings."
      );
      regMod(
         "AutoRelog",
         "Tự động kết nối lại máy chủ khi bị ngắt kết nối.",
         "Automatically reconnect to server after getting disconnected."
      );
      regMod(
         "AutoEat",
         "Tự động ăn thức ăn khi thanh thức ăn hoặc máu giảm.",
         "Automatically consume food when hunger or health drops."
      );
      regMod(
         "HomeSetter",
         "Hỗ trợ lưu và điều hướng tọa độ điểm đặt /home nhanh.",
         "Quickly save and navigate /home coordinates."
      );
      regMod(
         "FakeRoles",
         "Tùy biến hiển thị danh hiệu và role giả lập trên giao diện client.",
         "Customize simulated player titles and roles in client UI."
      );
      regMod(
         "FakeStats",
         "Hiển thị chỉ số K/D và thông số giả lập trên màn hình.",
         "Display simulated K/D ratios and player stats on screen."
      );
      regMod(
         "NameProtect",
         "Ẩn tên của bạn và bạn bè trên màn hình để bảo vệ danh tính khi stream.",
         "Conceal usernames on screen to protect identity while streaming."
      );
      regMod(
         "SkinProtect",
         "Ẩn skin người chơi bằng skin mặc định hoặc skin tùy chỉnh.",
         "Conceal player skins with default or custom skins."
      );
   }

   private static void registerRenderModules() {
      regMod(
         "CustomFOV",
         "Căn chỉnh góc nhìn FOV mở rộng vượt giới hạn vanilla, chống giật góc nhìn và hỗ trợ zoom.",
         "Adjust field of view beyond vanilla limits with static locking and zoom support."
      );
      regMod(
         "EdgeGlow",
         "Hiệu ứng viền phát sáng hào quang neon kèm vệt quét sheen cho tay, người chơi và item rơi.",
         "Neon glowing aura and shimmering sheen sweep for hands, players, and items."
      );
      regMod(
         "FillGlow",
         "Tô màu phát sáng rực rỡ và hào quang neon cho item rơi và item cầm tay.",
         "Vibrant color fill overlay and neon glow aura for dropped items and held items."
      );
      regMod(
         "ColorCorrection",
         "Hiệu chỉnh dải màu điện ảnh, độ tương phản và tối góc viền màn hình.",
         "Cinematic color grading, vibrance, contrast, and screen vignetting."
      );
      regMod(
         "MotionBlur",
         "Tạo vệt mờ chuyển động mượt mà khi xoay camera bằng tích lũy khung hình.",
         "Smooth motion blur accumulation when panning camera."
      );
      regMod(
         "TargetESP",
         "Khóa tiêu điểm mục tiêu combat với vòng sáng công nghệ Cyber và thẻ trạng thái 2D.",
         "Futuristic Cyber target lock-on with rotating rings and 2D status badge."
      );
      regMod(
         "CustomCrosshair",
         "Thay thế tâm ngắm mặc định bằng biểu tượng Vie Client kèm hiệu ứng hồi chiêu và nhắm mục tiêu.",
         "Replace vanilla crosshair with Vie Client logo, cooldown gauge, and target glow."
      );
      regMod(
         "WorldParticles",
         "Hạt tinh thể di tích cổ đại và tinh cầu trôi nổi xoay chuyển phát sáng trong không gian.",
         "Floating ancient relics and celestial motes drifting in the world."
      );
      regMod(
         "FullBright",
         "Làm sáng toàn bộ thế giới trong bóng tối và trong hang động.",
         "Illuminate world gamma in dark areas and caves."
      );
      regMod(
         "PlayerESP",
         "Hiển thị khung viền và vị trí người chơi qua tường.",
         "Highlight players through walls with bounding boxes."
      );
      regMod(
         "MobESP",
         "Hiển thị vị trí quái vật và sinh vật xung quanh qua tường.",
         "Highlight mobs and creatures through walls."
      );
      regMod(
         "StorageESP",
         "Hiển thị rương, shulker box và thùng chứa đồ qua tường.",
         "Highlight chests, shulker boxes, and containers through walls."
      );
      regMod(
         "BlockESP",
         "Đánh dấu và tô sáng các khối block được chọn trong lòng đất.",
         "Highlight selected blocks and ores underground."
      );
      regMod(
         "BedrockHoleESP",
         "Phát hiện các hố bedrock an toàn trong thế giới Nether.",
         "Detect safe bedrock holes in the Nether."
      );
      regMod(
         "HoleESP",
         "Hiển thị các hố an toàn obsidian và bedrock phục vụ giao chiến crystal.",
         "Highlight obsidian and bedrock safe holes for crystal combat."
      );
      regMod(
         "Tracers",
         "Vẽ đường chỉ dẫn từ tâm màn hình đến vị trí người chơi khác.",
         "Draw tracer lines from screen center to other players."
      );
      regMod(
         "NameTags",
         "Thẻ tên người chơi sắc nét hiển thị lượng máu, khoảng cách và giáp.",
         "Enhanced nametags displaying player health, distance, and armor."
      );
      regMod(
         "Trajectories",
         "Dự đoán quỹ đạo bay của ngọc Ender Pearl, cung tên và bình thuốc ném.",
         "Predict projectile paths for Ender Pearls, arrows, and splash potions."
      );
      regMod(
         "JumpCircle",
         "Tạo vòng tròn sóng năng lượng lan tỏa dưới chân mỗi khi nhảy.",
         "Emit expanding energy shockwave rings under feet when jumping."
      );
      regMod(
         "CombatFX",
         "Hiệu ứng tia sét, vòng năng lượng và vệt chém khi tấn công trúng đích.",
         "Lightning sparks and energy rings when hitting targets."
      );
      regMod(
         "CrystalFX",
         "Hiệu ứng nổ và vụn pha lê rực rỡ khi kích nổ End Crystal.",
         "Vibrant crystal shard bursts and explosion effects for End Crystals."
      );
      regMod(
         "SwingAnimation",
         "Tùy biến hoạt ảnh vung kiếm/vật phẩm theo các phong cách 1.7 và 1.8.",
         "Customize sword swing animations with 1.7 and 1.8 styles."
      );
      regMod(
         "NonLa",
         "Đội chiếc nón lá truyền thống Việt Nam độc quyền trên đầu người chơi.",
         "Wear the iconic Vietnamese traditional conical hat on player head."
      );
      regMod(
         "PlayerModels",
         "Tùy biến tai thỏ, cánh rồng và phụ kiện 3D trên nhân vật.",
         "Custom 3D player cosmetic models, wings, and accessories."
      );
      regMod(
         "Accessories",
         "Tùy biến các phụ kiện làm đẹp gắn kèm nhân vật.",
         "Customize cosmetic accessories attached to player model."
      );
      regMod(
         "AnimationFX",
         "Tùy chỉnh hiệu ứng chuyển động và hoạt ảnh trên giao diện.",
         "Customize UI motion effects and render animations."
      );
      regMod(
         "BlockSelection",
         "Tô viền phát sáng khối block đang được nhắm tới.",
         "Glowing highlight outline on currently targeted block."
      );
      regMod(
         "WeatherNotifier",
         "Thông báo khi trời bắt đầu mưa hoặc tạnh mưa.",
         "Notify when rain starts or clears up."
      );
      regMod(
         "BlockNotifier",
         "Phát thông báo âm thanh và toast khi tìm thấy khối block quý giá.",
         "Alert with audio and toasts when valuable blocks are found."
      );
      regMod(
         "SuspiciousESP",
         "Phát hiện và đánh dấu các khối cát/sỏi khảo cổ đáng ngờ.",
         "Highlight suspicious archaeological sand and gravel blocks."
      );
   }

   private static void registerWorldAndDonutModules() {
      regMod(
         "HUD",
         "Tùy chỉnh giao diện hiển thị: Watermark, FPS, Tọa độ, Danh sách module và Keystrokes.",
         "Customize HUD overlay: Watermark, FPS, Coords, Active modules, and Keystrokes."
      );
      regMod(
         "MusicHUD",
         "Widget hiển thị trình phát nhạc Spotify và Local media sắc nét trên màn hình.",
         "Sleek on-screen Spotify & media player HUD widget."
      );
      regMod(
         "ClientSettings",
         "Cá nhân hóa client: Đổi ngôn ngữ VIE/ENG, theme kính, Discord RPC và phím tắt ClickGUI.",
         "Personalize client: Language VIE/ENG, glass theme, Discord RPC, and ClickGUI bind."
      );
      regMod(
         "InventoryProtect",
         "Ẩn item nhạy cảm trong kho đồ và rương để chống lộ vị trí/regear khi livestream.",
         "Conceal sensitive inventory and container items to prevent stream leaks."
      );
      regMod(
         "DiscordPresence",
         "Hiển thị trạng thái đang chơi Vie Client trên Discord Rich Presence.",
         "Show Vie Client status on Discord Rich Presence."
      );
      regMod(
         "Emote",
         "Vòng xoay biểu cảm Emote Wheel với cử chỉ mượt mà.",
         "Emote Wheel with smooth player gestures."
      );
      regMod(
         "AutoBuild",
         "Tự động xây dựng công trình theo file schematics litematica.",
         "Automatically build structures from litematica schematics."
      );
      regMod(
         "AutoMine",
         "Tự động đào khu vực đã chọn theo tọa độ 3D.",
         "Automatically excavate selected 3D regions."
      );
      regMod(
         "AutoPlace",
         "Tự động đặt block khi di chuyển hoặc xây cầu nối nhanh.",
         "Automatically place blocks while moving or bridging."
      );
      regMod(
         "AHSniper",
         "Tự động canh mua vật phẩm giá rẻ trên chợ đấu giá Auction House.",
         "Automatically snipe underpriced items on Auction House."
      );
      regMod(
         "AutoSell",
         "Tự động bán vật phẩm nhanh khi rương đầy.",
         "Automatically sell items when inventory fills up."
      );
      regMod(
         "InventorySell",
         "Tự động gõ /sell và xả sạch toàn bộ đồ inventory, armor và offhand lên panel bán.",
         "Automatically runs /sell and dumps all inventory, equipped armor & offhand onto sell panel."
      );
      regMod(
         "EmergencySeller",
         "Tự động xả bán vật phẩm khẩn cấp khi gặp nguy hiểm.",
         "Emergency rapid item sell trigger when endangered."
      );
      regMod(
         "BoneDropper",
         "Tự động thả xương hoặc vật phẩm thừa khi farm.",
         "Automatically drop bones and junk items while farming."
      );
      regMod(
         "AutoTreeFarmer",
         "Tự động chặt cây và trồng lại mầm cây trong nông trại gỗ.",
         "Automatically harvest and replant trees in tree farms."
      );
      regMod(
         "AutoCrop",
         "Tự động thu hoạch và gieo trồng lại lúa, cà rốt, khoai tây.",
         "Automatically harvest and replant wheat, carrots, and potatoes."
      );
      regMod(
         "BlockEntitiesDebug",
         "Hiển thị thông số phân tích khối tile entity.",
         "Display block entity debug analysis."
      );
      regMod(
         "ChunkDebug",
         "Phân tích và phát hiện chunk bất thường.",
         "Analyze and flag abnormal chunks."
      );
      regMod(
         "SusChunkFinder",
         "Quét tìm các chunk khả nghi có hoạt động của người chơi.",
         "Scan for suspicious player-activity chunks."
      );
      regMod(
         "TunnelBaseFinder",
         "Quét đường hầm và dấu vết căn cứ ngầm dưới lòng đất.",
         "Scan underground tunnels and hidden base trails."
      );
      regMod(
         "Vie Debug",
         "Phân tích và phát hiện căn cứ ẩn dưới lòng đất.",
         "Analyze and detect hidden underground bases."
      );
      regMod(
         "VieDebug",
         "Phân tích và phát hiện căn cứ ẩn dưới lòng đất.",
         "Analyze and detect hidden underground bases."
      );
      regMod(
         "SpawnerBreaker",
         "Tự động đào phá lồng spawner đối thủ siêu tốc.",
         "Rapidly break enemy mob spawners automatically."
      );
      regMod(
         "SpawnersNotifier",
         "Thông báo tọa độ khi phát hiện lồng spawner trong tầm quét.",
         "Notify coordinates when mob spawners are detected nearby."
      );
      regMod(
         "SpawnersProtect",
         "Cảnh báo khi spawner của bạn bị tiếp cận hoặc tấn công.",
         "Alert when your spawners are approached or damaged."
      );
      regMod(
         "SpawnerTags",
         "Gắn nhãn và khoảng cách đến các lồng spawner qua tường.",
         "Display tags and distance to mob spawners through walls."
      );
      regMod(
         "StaffLists",
         "Theo dõi và thông báo (Staff Notifier) khi ban quản trị server online/offline.",
         "Track and notify (Staff Notifier) online/offline status of server staff members."
      );
      regMod(
         "/rtp debug",
         "Tự động sethome và gõ /rtp theo khu vực (region).",
         "Automatically set home and execute /rtp for the selected region."
      );
      regMod(
         "RtpDebug",
         "Tự động sethome và gõ /rtp theo khu vực (region).",
         "Automatically set home and execute /rtp for the selected region."
      );
      regMod(
         "Auto Pearl Method",
         "Tự động sethome, ném ngọc Ender Pearl lên trời và gõ /rtp.",
         "Automatically sethome, throw Ender Pearl skyward, and execute /rtp."
      );
      regMod(
         "AutoPearlMethod",
         "Tự động quay mặt lên trời, ném Ender Pearl và gõ /rtp <region>.",
         "Automatically aim skyward, throw Ender Pearl, and execute /rtp <region>."
      );
      regMod(
         "KeyboardSFX",
         "Hiệu ứng âm thanh bàn phím cơ và click chuột chân thực.",
         "Realistic mechanical keyboard switch and mouse click audio feedback."
      );
      regMod(
         "Item Replacement",
         "Thay thế mô hình và kết cấu vật phẩm phía client tùy chỉnh.",
         "Client-side cosmetic item model and texture replacement."
      );
      regMod(
         "ItemReplacement",
         "Thay thế mô hình và kết cấu vật phẩm phía client tùy chỉnh.",
         "Client-side cosmetic item model and texture replacement."
      );
      regMod(
         "AntiAdminSpectator",
         "Cảnh báo khẩn cấp khi Admin/Staff đang Spectator hoặc theo dõi bạn.",
         "Emergency alert when Admin/Staff is spectating or tracking you."
      );
      regMod(
         "AutoBaseBuilding",
         "Tự động xây dựng căn cứ theo bản vẽ Litematica.",
         "Automated structural base construction from Litematica schematics."
      );
      regMod(
         "AutoBaseMining",
         "Tự động đào căn cứ an toàn và bảo vệ độ bền công cụ.",
         "Automated base excavation with safety checks and tool preservation."
      );
      regMod(
         "FastUse",
         "Sử dụng vật phẩm với tốc độ cực cao.",
         "Use items at very high speeds."
      );
      regMod(
         "NoSlow",
         "Không bị giảm tốc độ di chuyển khi đang dùng vật phẩm.",
         "No slowdown while using items."
      );
      regMod(
         "BreakingProgress",
         "Tùp biến hoạt ảnh và hiệu ứng phá block đẹp mắt.",
         "Customizes block breaking progress overlay with futuristic animations."
      );
      regMod(
         "ItemSpin",
         "Xoay tròn vật phẩm đang cầm trên tay theo trục cố định.",
         "Spins the held item on a fixed axis."
      );
      regMod(
         "Optimization",
         "Tối ưu hóa hiệu năng client và giảm tải đồ họa.",
         "Scientific client performance optimizations and visual overhead culling."
      );
      regMod(
         "SkyCustom",
         "Tùy biến bầu trời Nuit 3D với hiệu ứng chuyển cảnh mượt mà.",
         "Nuit custom skybox engine with smooth transitions and 3D rotation."
      );
      regMod(
         "True Stretched",
         "Kéo giãn góc nhìn thế giới 3D tương tự game bắn súng (4:3, 5:4).",
         "Stretches 3D world perspective like FPS games (4:3, 5:4, etc.)."
      );
   }

   private static void registerSettings() {
      registerGeneralSettings();
      registerModuleSpecificSettings();
   }

   private static void registerGeneralSettings() {
      regSet(
         "Creator Mode",
         "Bấm chuột giữa lấy block tức thì trong chế độ sáng tạo",
         "Instant pick-block on demand before placing in creative mode"
      );
      regSet(
         "Delay",
         "Độ trễ thực thi (ms)",
         "Execution delay (ms)"
      );
      regSet(
         "SwitchDelay",
         "Độ trễ đổi công cụ tự nhiên (ms)",
         "Humanized tool switch delay (ms)"
      );
      regSet(
         "ReturnDelay",
         "Độ trễ quay lại slot ban đầu (ms)",
         "Delay before returning to origin slot (ms)"
      );
      regSet(
         "SwapBack",
         "Tự động quay về slot ban đầu sau khi đào",
         "Return to origin hotbar slot after mining"
      );
      regSet(
         "SwitchBack",
         "Tự động quay về slot ban đầu sau khi đào",
         "Return to origin hotbar slot after mining"
      );
      regSet(
         "DuraSafe",
         "Ngưỡng bảo vệ công cụ sắp gãy",
         "Threshold to safeguard nearly broken tools"
      );
      regSet(
         "MinDura",
         "Ngưỡng độ bền tối thiểu để bảo vệ",
         "Minimum durability threshold for tool protection"
      );
      regSet(
         "Prefer3x3",
         "Ưu tiên cúp hoặc mũi khoan 3x3",
         "Prioritize 3x3 Shard Pickaxes / Drills"
      );
      regSet(
         "OnlyWhileMining",
         "Chỉ đổi công cụ khi đang chủ động đào",
         "Only switch tools while actively mining blocks"
      );
      regSet(
         "Threshold",
         "Ngưỡng số lượng vật phẩm tối thiểu để nạp bù",
         "Minimum stack threshold to trigger replenishment"
      );
      regSet(
         "RefillOffhand",
         "Bổ sung vật phẩm cho cả tay phụ (offhand)",
         "Replenish items in offhand slot"
      );
      regSet(
         "SkipTotems",
         "Không nạp bù Totem để nhường quyền ưu tiên",
         "Do not refill Totems to prioritize AutoTotem"
      );
      regSet(
         "PauseWhileUsing",
         "Tạm dừng nạp bù khi đang dùng thức ăn/cung/nỏ",
         "Pause replenishment while eating or using items"
      );
      regSet(
         "Preset",
         "Cấu hình thiết lập mẫu",
         "Profile configuration preset"
      );
      regSet(
         "Multiplier",
         "Hệ số điều chỉnh tốc độ",
         "Speed adjustment multiplier"
      );
      regSet(
         "No Water",
         "Cho phép phóng Riptide không cần nước hoặc trời mưa",
         "Allow using riptide out of water or without rain"
      );
      regSet(
         "Charge Scale",
         "Tỷ lệ thời gian gồng đinh ba tối thiểu (0 = tức thì, 1 = mặc định)",
         "Scale minimum charge time before riptide launches (0 = instant, 1 = vanilla)"
      );
      regSet(
         "Boost Multiplier",
         "Hệ số nhân lực đẩy khi phóng Riptide",
         "Velocity multiplier when launching with riptide"
      );
      regSet(
         "Theme",
         "Chủ đề màu sắc hiển thị",
         "Visual color theme preset"
      );
      regSet(
         "Smoothness",
         "Độ mượt mà của hiệu ứng đồ họa",
         "Smoothness level of graphical effects"
      );
      regSet(
         "Blend",
         "Độ hòa trộn màu sắc bầu trời",
         "Sky color blend ratio"
      );
      regSet(
         "TopColor",
         "Màu đỉnh vòm trời (Zenith)",
         "Sky zenith top dome color"
      );
      regSet(
         "HorizonColor",
         "Màu đường chân trời & sương mù",
         "Horizon line and atmospheric fog color"
      );
      regSet(
         "AuroraBottom",
         "Màu chân dải cực quang",
         "Bottom edge color of the aurora ribbon"
      );
      regSet(
         "AuroraTop",
         "Màu đỉnh dải cực quang",
         "Top crest color of the aurora ribbon"
      );
      regSet(
         "AuroraSpeed",
         "Tốc độ lượn sóng của dải cực quang",
         "Wave animation speed of aurora ribbons"
      );
      regSet(
         "Nebula",
         "Hiển thị dải mây không gian sâu",
         "Render deep space galaxy nebula clouds"
      );
      regSet(
         "SunMoonGlow",
         "Vòng hào quang rực rỡ quanh Mặt Trời & Mặt Trăng",
         "Vibrant radiant bloom aura around Sun and Moon"
      );
      regSet(
         "AffectFog",
         "Đồng bộ sương mù thế giới theo màu chân trời",
         "Synchronize world fog with horizon colors"
      );
      regSet(
         "KeepInRain",
         "Giữ bầu trời cực quang huyền ảo ngay cả khi trời mưa",
         "Maintain ethereal sky effects even during rain"
      );
      regSet(
         "TimeIndependent",
         "Giữ độ sáng bầu trời liên tục không phụ thuộc ngày đêm",
         "Keep sky bright continuously regardless of day/night"
      );
      regSet(
         "RotateSky",
         "Xoay bầu trời nhẹ nhàng theo thời gian",
         "Gently rotate sky celestial dome over time"
      );
      regSet(
         "RotationSpeed",
         "Tốc độ xoay bầu trời",
         "Celestial dome rotation speed"
      );
      regSet(
         "Range",
         "Phạm vi tác động (block)",
         "Action range (blocks)"
      );
      regSet(
         "Mode",
         "Chế độ hoạt động",
         "Operating mode"
      );
      regSet(
         "Speed",
         "Tốc độ xử lý",
         "Processing speed"
      );
      regSet(
         "Color",
         "Màu sắc hiển thị",
         "Display color"
      );
      regSet(
         "ColorMode",
         "Chế độ màu sắc",
         "Color mode"
      );
      regSet(
         "CustomColor",
         "Màu sắc tùy chỉnh",
         "Custom color"
      );
      regSet(
         "Keybind",
         "Phím tắt kích hoạt",
         "Toggle keybind"
      );
      regSet(
         "Reset",
         "Đặt lại toàn bộ cài đặt về mặc định",
         "Reset all settings to default"
      );
      regSet(
         "BlurFactor",
         "Cường độ làm mờ chuyển động",
         "Motion blur persistence factor"
      );
      regSet(
         "Strength",
         "Cường độ vệt mờ chuyển động",
         "Motion blur persistence and trail strength"
      );
      regSet(
         "Contrast",
         "Độ tương phản màu sắc",
         "Color contrast level"
      );
      regSet(
         "Vibrance",
         "Độ bão hòa màu sắc",
         "Color vibrance level"
      );
      regSet(
         "Brightness",
         "Độ sáng / phơi sáng tổng thể",
         "Overall screen brightness and exposure"
      );
      regSet(
         "Exposure",
         "Độ phơi sáng màn hình",
         "Screen exposure level"
      );
      regSet(
         "Warmth",
         "Nhiệt độ màu sắc (ấm/lạnh)",
         "Color temperature warmth"
      );
      regSet(
         "Vignette",
         "Cường độ tối góc viền màn hình",
         "Screen vignette darkness"
      );
      regSet(
         "Aurora",
         "Hiển thị dải cực quang 3D",
         "Render 3D aurora ribbons"
      );
      regSet(
         "Stars",
         "Hiển thị bầu trời sao lấp lánh",
         "Render sparkling starfield"
      );
      regSet(
         "Meteors",
         "Hiệu ứng sao băng lướt qua",
         "Shooting meteor streaks"
      );
      regSet(
         "Hand",
         "Áp dụng cho tay và vật phẩm đang cầm",
         "Apply to first-person hand & held items"
      );
      regSet(
         "HandHeldItem",
         "Áp dụng cho tay và vật phẩm đang cầm",
         "Apply to first-person hand & held items"
      );
      regSet(
         "Players",
         "Áp dụng cho người chơi khác",
         "Apply to other player models"
      );
      regSet(
         "Items",
         "Áp dụng cho vật phẩm rơi dưới đất",
         "Apply to dropped item entities"
      );
      regSet(
         "DroppedItems",
         "Áp dụng cho vật phẩm rơi dưới đất",
         "Apply to dropped item entities"
      );
      regSet(
         "Bloom",
         "Hào quang tỏa sáng phát quang",
         "Radial bloom glow aura"
      );
      regSet(
         "GlowBloom",
         "Hào quang tỏa sáng phát quang",
         "Radial bloom glow aura"
      );
      regSet(
         "GlowColor",
         "Màu sắc viền sáng tùy chỉnh",
         "Custom glow aura color"
      );
      regSet(
         "SheenColor",
         "Màu sắc vệt quét lấp lánh",
         "Shimmering sheen sweep highlight color"
      );
      regSet(
         "Aura",
         "Độ đậm của vầng sáng phát quang",
         "Radiant bloom aura intensity"
      );
      regSet(
         "Reach",
         "Bán kính lan tỏa của vầng sáng (pixels)",
         "Glow bloom spread radius (pixels)"
      );
      regSet(
         "RimStroke",
         "Độ sắc nét của viền bao quanh vật thể",
         "Edge outline sharpness around objects"
      );
      regSet(
         "Sheen",
         "Cường độ vệt quét ánh sáng lấp lánh",
         "Shimmering sheen sweep brightness"
      );
      regSet(
         "Breathe",
         "Hiệu ứng nhịp thở phát quang mượt mà",
         "Smooth breathing pulsation glow animation"
      );
      regSet(
         "Style",
         "Kiểu dáng hiển thị",
         "Visual display style"
      );
      regSet(
         "Size",
         "Kích thước hiển thị (pixels)",
         "Display size in pixels"
      );
      regSet(
         "Scale",
         "Kích thước tỷ lệ hiệu ứng",
         "Overall scale size of visual effect"
      );
      regSet(
         "Spin",
         "Xoay vòng tròn liên tục",
         "Continuously rotate the crosshair in a circle"
      );
      regSet(
         "SpinSpeed",
         "Tốc độ xoay vòng tròn",
         "Crosshair rotation speed"
      );
      regSet(
         "TargetRed",
         "Đổi màu đỏ rực khi nhắm trúng mục tiêu",
         "Flash red when crosshair is hovering over a living target"
      );
      regSet(
         "AttackPulse",
         "Hiệu ứng rung động khi tấn công",
         "Slight pulse and expansion feedback on attack"
      );
      regSet(
         "CooldownBar",
         "Hiển thị thanh đo tiến trình hồi đòn",
         "Display attack cooldown meter arc below crosshair"
      );
      regSet(
         "Cooldown",
         "Hiển thị vòng cung hồi chiêu",
         "Display attack cooldown meter"
      );
      regSet(
         "HitColor",
         "Màu sắc chớp sáng khi mục tiêu dính đòn",
         "Flash color when target takes damage"
      );
      regSet(
         "HitFlash",
         "Hiệu ứng chớp đỏ khi mục tiêu bị tấn công",
         "Flash red impact burst when target is struck"
      );
      regSet(
         "Opacity",
         "Độ đậm nhạt của hiệu ứng hiển thị",
         "Display opacity and brightness"
      );
      regSet(
         "Memory",
         "Thời gian lưu mục tiêu (giây)",
         "Target retention duration in seconds"
      );
      regSet(
         "PlayersOnly",
         "Chỉ khóa mục tiêu là người chơi",
         "Only lock onto other player characters"
      );
      regSet(
         "ShowInfo",
         "Hiển thị khoảng cách và lượng máu",
         "Display target distance and health badge"
      );
      regSet(
         "ToastAlert",
         "Hiển thị thông báo Toast ở góc màn hình",
         "Display on-screen Vie Client toast notification"
      );
      regSet(
         "SoundAlert",
         "Phát âm thanh cảnh báo khi phát hiện",
         "Play audio alert when detected"
      );
      regSet(
         "ChatAlert",
         "Gửi thông báo chi tiết trong khung chat",
         "Send detailed alert message in chat"
      );
      regSet(
         "GhostSounds",
         "Phát hiện âm thanh ma không thấy người",
         "Detect phantom sounds with no visible player"
      );
      regSet(
         "SpectatorMode",
         "Phát hiện chuyển sang chế độ Spectator",
         "Detect players switching into Spectator mode"
      );
      regSet(
         "TabVanish",
         "Phát hiện biến mất khỏi Tablist khi đang gần",
         "Detect players vanishing from tablist while nearby"
      );
      regSet(
         "CooldownS",
         "Thời gian chờ giữa các lần cảnh báo lặp lại (giây)",
         "Cooldown seconds between repeat alert triggers"
      );
      regSet(
         "FillEmpty",
         "Ngụy trang cả các ô trống để che giấu số lượng item",
         "Camouflage empty slots to conceal item stack counts"
      );
      regSet(
         "HideTooltips",
         "Ẩn thông tin chi tiết (enchant, lore, độ bền) khi rê chuột",
         "Hide hovered item tooltips (enchants, lore, durability)"
      );
      regSet(
         "HideItemName",
         "Ẩn tên vật phẩm hiển thị trên thanh hotbar",
         "Hide item name overlay when cycling hotbar slots"
      );
      regSet(
         "MaskHotbar",
         "Áp dụng che giấu cho cả thanh Hotbar ngoài màn hình",
         "Apply camouflage to on-screen hotbar HUD"
      );
      regSet(
         "Containers",
         "Áp dụng che giấu trong rương, Shulker, Enderchest",
         "Apply camouflage in chests, shulker boxes, and ender chests"
      );
      regSet(
         "Sidebar",
         "Chèn bảng điểm giả lập vào thanh bên (sidebar)",
         "Inject the fake objective into the sidebar display slot"
      );
      regSet(
         "Title",
         "Tiêu đề bảng điểm (để trống = tự lấy tên)",
         "Scoreboard title (empty = use NameProtect name)"
      );
      regSet(
         "LineMoney",
         "Nội dung sau biểu tượng tiền $$",
         "Text after the $$ money icon"
      );
      regSet(
         "LineShard",
         "Nội dung sau biểu tượng sao tím Shard",
         "Text after the purple star shard icon"
      );
      regSet(
         "LineKills",
         "Nội dung sau biểu tượng kiếm đỏ Kills",
         "Text after the red sword kills icon"
      );
      regSet(
         "LineDeaths",
         "Nội dung sau biểu tượng đầu lâu cam Deaths",
         "Text after the orange skull deaths icon"
      );
      regSet(
         "LinePlaytime",
         "Nội dung sau biểu tượng đồng hồ vàng Playtime",
         "Text after the gold clock playtime icon"
      );
      regSet(
         "Language",
         "Ngôn ngữ hiển thị client (VIE / ENG)",
         "Client display language (VIE / ENG)"
      );
      regSet(
         "ThemePreset",
         "Giao diện chủ đề kính thủy tinh",
         "Glass theme preset"
      );
      regSet(
         "DiscordRPC",
         "Bật hiển thị Discord Rich Presence",
         "Enable Discord Rich Presence"
      );
      regSet(
         "Notifier",
         "Thông báo khi có staff vào hoặc thoát game",
         "Notify when staff joins or leaves the game"
      );
      regSet(
         "Chat",
         "Gửi thông báo vào tin nhắn chat",
         "Send notifications to chat"
      );
      regSet(
         "Toast",
         "Hiển thị thẻ thông báo pop-up trên màn hình",
         "Show on-screen toast popup notifications"
      );
      regSet(
         "Sound",
         "Phát âm thanh chuông cảnh báo",
         "Play alert sound chime"
      );
      regSet(
         "CustomStaff",
         "Tên staff tùy chỉnh (cách nhau bởi dấu phẩy)",
         "Custom staff names (comma-separated)"
      );
      regSet(
         "TestNotification",
         "Thử nghiệm âm thanh và thông báo",
         "Test sound and notification preview"
      );
      regSet(
         "Region",
         "Khu vực RTP (Asia, EU Central, EU West, East, West, Oceania)",
         "RTP region (Asia, EU Central, EU West, East, West, Oceania)"
      );
      regSet(
         "HomeSlot",
         "Số thứ tự slot home (1-10)",
         "Home slot number (1-10)"
      );
      regSet(
         "DelayMs",
         "Độ trễ giữa các lệnh (ms)",
         "Delay between commands (ms)"
      );
      regSet(
         "Sound Pack",
         "Bộ âm thanh switch bàn phím",
         "Mechanical switch sound pack profile"
      );
      regSet(
         "Mouse Clicks",
         "Phát âm thanh khi click chuột trái/phải",
         "Play switch sounds on left and right mouse clicks"
      );
      regSet(
         "Target Item",
         "Vật phẩm cần thay thế hiển thị",
         "Target item to replace visually"
      );
      regSet(
         "Replacement Item",
         "Vật phẩm hiển thị thay thế",
         "Replacement item to render instead"
      );
   }

   private static void registerModuleSpecificSettings() {
      regSet(
         "Disconnect on Chunk",
         "Tự động ngắt kết nối khi phát hiện chunk chứa base",
         "Automatically disconnect when a base chunk is detected"
      );
      regSet(
         "Unflag Out Of Range",
         "Bỏ đánh dấu khi ra khỏi render distance",
         "Automatically unflag columns outside render distance"
      );
      regSet(
         "Anchor Palette",
         "Bảng màu hạt hội tụ cho Respawn Anchor",
         "Color palette for anchor convergence particles"
      );
      regSet(
         "Apply To",
         "Đối tượng áp dụng thay đổi ngoại trang / hoạt ảnh",
         "Whose skin or animation gets replaced"
      );
      regSet(
         "Armor Durability",
         "Chế độ hiển thị độ bền giáp",
         "Durability display mode"
      );
      regSet(
         "Armor Layout",
         "Hướng sắp xếp thanh hiển thị giáp",
         "Orientation of armor widget"
      );
      regSet(
         "Aura Preset",
         "Mẫu hiệu ứng hào quang quanh người chơi",
         "Clean and eye-catching personal aura preset"
      );
      regSet(
         "Axis",
         "Trục xoay cố định của vật phẩm",
         "Fixed item rotation axis"
      );
      regSet(
         "Blend Mode",
         "Phương thức hòa trộn OpenGL cho bầu trời Nuit",
         "Nuit OpenGL blending equation for sky overlays"
      );
      regSet(
         "ClickGuiMode",
         "Chế độ hiển thị ClickGUI",
         "ClickGUI display mode"
      );
      regSet(
         "Crit Mode",
         "Chiến lược nhảy đánh chí mạng",
         "Crit strategy: SMART, AGGRESSIVE, or FALL_ONLY"
      );
      regSet(
         "Easing",
         "Đường cong mượt mà cho hiệu ứng phóng to/thu nhỏ",
         "Animation curve for the zoom-out effect"
      );
      regSet(
         "EffectStyle",
         "Kiểu hiệu ứng viền chọn khối",
         "Visual effect style for the block selection"
      );
      regSet(
         "FillStyle",
         "Kiểu ánh sáng và hoạt ảnh lấp đầy mặt khối",
         "Lighting & animation mode for face fills"
      );
      regSet(
         "Filter",
         "Bộ lọc đối tượng hiển thị",
         "Which entities or mobs to show"
      );
      regSet(
         "Footprint Style",
         "Kiểu dấu chân hoặc logo VIE phát sáng",
         "Select footprint design or custom VIE logo"
      );
      regSet(
         "Glow Style",
         "Kiểu phát sáng thẩm mỹ cho crystal",
         "Aesthetic styling for crystal glow"
      );
      regSet(
         "GuiStyle",
         "Phong cách hình ảnh ClickGUI",
         "ClickGUI visual style"
      );
      regSet(
         "KillStyle",
         "Hiệu ứng bùng nổ khi tiêu diệt mục tiêu",
         "Kill burst visual look"
      );
      regSet(
         "Line Animation",
         "Hiệu ứng xuất hiện dòng lời bài hát",
         "How the active lyric line transitions in"
      );
      regSet(
         "Line Count",
         "Số lượng dòng lời bài hát hiển thị",
         "How many lyric lines to render"
      );
      regSet(
         "Line Style",
         "Kiểu dáng dòng lời bài hát đang phát",
         "Active lyric line style"
      );
      regSet(
         "Logo Style",
         "Kiểu hiển thị hạt logo Vie",
         "Rendering style for Vie Logo particles"
      );
      regSet(
         "MarkerStyle",
         "Kiểu đánh dấu mục tiêu giao chiến",
         "Target marker look"
      );
      regSet(
         "Mirror",
         "Chế độ lật đối xứng bản vẽ",
         "Blueprint mirror mode"
      );
      regSet(
         "Model",
         "Mẫu nhân vật 3D biến hình",
         "Character model to transform into"
      );
      regSet(
         "Module Style",
         "Kiểu hiển thị danh sách module",
         "Module list style (GUI tab)"
      );
      regSet(
         "ModuleList",
         "Vị trí danh sách module trên màn hình",
         "Module list position"
      );
      regSet(
         "NumberStyle",
         "Kiểu hoạt ảnh số sát thương gây ra",
         "Damage number animation style"
      );
      regSet(
         "Particle Type",
         "Kiểu hạt phân tán trong thế giới",
         "Style of particles scattered across the world"
      );
      regSet(
         "Pattern",
         "Quy luật đào quặng và đào căn cứ",
         "Excavation pattern"
      );
      regSet(
         "Pop Style",
         "Kiểu nổ thẩm mỹ của crystal",
         "Detonation aesthetic: clean, minimal, or rich"
      );
      regSet(
         "Priority",
         "Mức độ ưu tiên lựa chọn mục tiêu",
         "Target selection priority"
      );
      regSet(
         "RegionMap Mode",
         "Bố cục bản đồ vùng lãnh thổ cũ hoặc mới",
         "Choose between Old and New region map layout"
      );
      regSet(
         "Reset Mode",
         "Chế độ ngắt chạy bộ lấy lại đà đánh",
         "Sprint reset mode (W-Tap / S-Tap)"
      );
      regSet(
         "Role",
         "Danh hiệu hoặc vai trò giả lập hiển thị",
         "Fake role to display"
      );
      regSet(
         "Rotation",
         "Góc xoay hướng bản vẽ",
         "Blueprint rotation angle"
      );
      regSet(
         "ShapeMode",
         "Cách vẽ các khối và hình học",
         "How the shapes are rendered"
      );
      regSet(
         "Source",
         "Nguồn sát thương hiển thị",
         "Show all damage or only what you deal"
      );
      regSet(
         "Source Priority",
         "Ưu tiên nguồn nhạc MusicHUD",
         "Chooses the only music source MusicHUD may display"
      );
      regSet(
         "SparkStyle",
         "Kiểu tia lửa khi đánh trúng mục tiêu",
         "Hit particle spark look"
      );
      regSet(
         "Strafe",
         "Tự động di chuyển lượn vòng quanh đối thủ",
         "Strafe around the opponent in combat"
      );
      regSet(
         "Target",
         "Bộ phận trên cơ thể mục tiêu để lia tâm",
         "What part of the entity to target"
      );
      regSet(
         "TargetSlot",
         "Vị trí ô hotbar để đặt Totem",
         "Where to put the totem"
      );
      regSet(
         "Targets",
         "Đối tượng mục tiêu kích hoạt hiệu ứng",
         "Which entities produce effects"
      );
      regSet(
         "TextColorMode",
         "Chế độ màu chữ",
         "Text color mode"
      );
      regSet(
         "TextStyle",
         "Định dạng huy hiệu phần trăm đào khối 3D",
         "Format of the floating 3D percentage badge"
      );
      regSet(
         "Title Overflow",
         "Xử lý khi tên bài hát quá dài",
         "Long title overflow behaviour"
      );
      regSet(
         "Tool Type",
         "Chế độ công cụ đào: Cuốc đơn hoặc Mũi khoan 3x1",
         "Mining tool mode: Normal Pickaxe or Drill"
      );
      regSet(
         "Watermark",
         "Vị trí hiển thị logo Watermark",
         "Watermark position"
      );
      regSet(
         "WingType",
         "Kiểu cánh 3D hiển thị sau lưng",
         "Style of wings to render"
      );
      regSet(
         "InteractionSFX",
         "Hiệu ứng âm thanh khi nhấp và tương tác giao diện",
         "Play sound effects on UI clicks and interactions"
      );
      regSet(
         "Interaction SFX",
         "Hiệu ứng âm thanh khi nhấp và tương tác giao diện",
         "Play sound effects on UI clicks and interactions"
      );
   }

   private static void registerUIStrings() {
      regUI(
         "search.prompt",
         "Tìm kiếm module...",
         "Search modules..."
      );
      regUI(
         "search.hint",
         "Tìm kiếm toàn bộ module (Ctrl + F)",
         "Search all modules (Ctrl + F)"
      );
      regUI(
         "toolbar.active_pill",
         "%d đang bật",
         "%d active"
      );
      regUI(
         "toolbar.filter_enabled",
         "Lọc: Chỉ hiện các module đang bật",
         "Filter: show only enabled modules"
      );
      regUI(
         "toolbar.show_all",
         "Hiện toàn bộ module",
         "Show all modules"
      );
      regUI(
         "toolbar.enabled",
         "Module đang bật (%d)",
         "Enabled Modules (%d)"
      );
      regUI(
         "toolbar.search_results",
         "Kết quả tìm kiếm (%d)",
         "Search Results (%d)"
      );
      regUI(
         "gear.hint",
         "Cài đặt Client (Giao diện & Phong cách)",
         "Client Settings (GUI & Style)"
      );
      regUI(
         "grid.no_enabled",
         "Chưa bật module nào",
         "No modules are enabled"
      );
      regUI(
         "grid.no_in_category",
         "Không có module trong mục này",
         "No modules in this category"
      );
      regUI(
         "grid.no_results",
         "Không tìm thấy kết quả cho \"%s\"",
         "No results for \"%s\""
      );
      regUI(
         "traffic.close",
         "Đóng ClickGUI (ESC)",
         "Close ClickGUI (ESC)"
      );
      regUI(
         "traffic.reset",
         "Đặt lại tìm kiếm & danh mục",
         "Reset search & category"
      );
      regUI(
         "traffic.filter",
         "Chuyển lọc module đang bật",
         "Toggle enabled modules filter"
      );
      regUI(
         "sec.animations",
         "Hoạt ảnh",
         "Animations"
      );
      regUI(
         "sec.fps",
         "HUD Settings",
         "HUD Settings"
      );
      regUI(
         "sec.hud",
         "HUD Settings",
         "HUD Settings"
      );
      regUI(
         "sec.gui",
         "Giao diện & Phong cách",
         "GUI & Style"
      );
      regUI(
         "sec.music",
         "Trình phát nhạc",
         "Music HUD"
      );
      regUI(
         "sec.macros",
         "Phím tắt Chat",
         "Chat Macros"
      );
      regUI(
         "cfg.profiles",
         "Cấu hình",
         "Profiles"
      );
      regUI(
         "cfg.community",
         "Community Hub",
         "Community Hub"
      );
      regUI(
         "cfg.new",
         "Tạo cấu hình",
         "New Profile"
      );
      regUI(
         "cfg.share",
         "Chia sẻ Cloud",
         "Share Cloud"
      );
      regUI(
         "cfg.import",
         "Nhập mã Cloud",
         "Import Code"
      );
      regUI(
         "cfg.folder",
         "Thư mục",
         "Folder"
      );
      regUI(
         "cfg.load",
         "Tải",
         "Load"
      );
      regUI(
         "cfg.copy",
         "Sao chép",
         "Copy"
      );
      regUI(
         "cfg.copied",
         "Đã chép!",
         "Copied!"
      );
      regUI(
         "cfg.delete",
         "Xóa?",
         "Delete?"
      );
      regUI(
         "cfg.yes",
         "Có",
         "Yes"
      );
      regUI(
         "cfg.no",
         "Không",
         "No"
      );
      regUI(
         "cfg.default",
         "Cấu hình mặc định",
         "Default profile"
      );
      regUI(
         "title.singleplayer",
         "Chơi đơn",
         "Singleplayer"
      );
      regUI(
         "title.multiplayer",
         "Chơi mạng",
         "Multiplayer"
      );
      regUI(
         "title.mods",
         "Mods & Addons",
         "Mods & Addons"
      );
      regUI(
         "title.options",
         "Cài đặt",
         "Options"
      );
      regUI(
         "title.quit",
         "Thoát game",
         "Quit Game"
      );
      regUI(
         "hint.lang_switch",
         "Đổi ngôn ngữ (VIE / ENG)",
         "Switch Language (VIE / ENG)"
      );
      regUI(
         "hint.settings",
         "Cài đặt Giao diện",
         "Client Settings (GUI & Style)"
      );
      regUI(
         "hint.client_settings",
         "Cài đặt Hệ thống",
         "Client Settings"
      );
      regUI(
         "hint.search",
         "Nhấp để tìm / Esc để xóa",
         "Click to search / Esc to clear"
      );
   }

   static {
      registerCategories();
      registerModules();
      registerSettings();
      registerUIStrings();
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
