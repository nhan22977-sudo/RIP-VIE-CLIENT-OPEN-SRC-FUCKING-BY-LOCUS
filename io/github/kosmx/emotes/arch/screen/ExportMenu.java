package io.github.kosmx.emotes.arch.screen;

import com.zigythebird.playeranimcore.animation.Animation;
import io.github.kosmx.emotes.PlatformTools;
import io.github.kosmx.emotes.common.CommonData;
import io.github.kosmx.emotes.main.EmoteHolder;
import io.github.kosmx.emotes.server.serializer.EmoteWriter;
import io.github.kosmx.emotes.server.serializer.UniversalEmoteSerializer;
import io.github.kosmx.emotes.server.serializer.type.IWriter;
import io.github.kosmx.emotes.server.services.InstanceService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.class_2561;
import net.minecraft.class_339;
import net.minecraft.class_410;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_5244;
import net.minecraft.class_7845;
import net.minecraft.class_8132;
import net.minecraft.class_8667;
import net.minecraft.class_7845.class_7939;

public class ExportMenu extends class_437 {
   private static final class_2561 TITLE = class_2561.method_43471("emotecraft.options.export");
   private static final class_2561 DATA_LOSS_MSG = class_2561.method_43471("emotecraft.dataloss.msg");
   private static final class_2561 DATA_LOSS_DESCR = class_2561.method_43471("emotecraft.dataloss.descr");
   private final class_8132 layout = new class_8132(this);
   protected final class_437 parent;

   public ExportMenu(class_437 parent) {
      super(TITLE);
      this.parent = parent;
   }

   public void method_25426() {
      this.layout.method_57726(this.method_25440(), this.field_22793);
      class_7845 gridLayout = new class_7845();
      gridLayout.method_46458().method_46477(8).method_46475(4).method_46467();
      class_7939 rowHelper = gridLayout.method_47610(2);

      for (IWriter serializer : UniversalEmoteSerializer.WRITERS) {
         rowHelper.method_47612(class_4185.method_46430(class_2561.method_43469("emotecraft.export", new Object[]{serializer.getExtension()}), button -> {
            if (!serializer.possibleDataLoss()) {
               this.exportEmotesInFormat(serializer);
            } else {
               this.field_22787.method_1507(new class_410(choice -> {
                  if (choice) {
                     this.exportEmotesInFormat(serializer);
                  }

                  this.field_22787.method_1507(this);
               }, DATA_LOSS_MSG, DATA_LOSS_DESCR) {
                  protected void method_37051(class_8667 layout) {
                     super.method_37051(layout);
                     this.method_2125(100);
                  }
               });
            }
         }).method_46432(200).method_46431());
      }

      this.layout.method_48999(gridLayout);
      class_8667 footer = (class_8667)this.layout.method_48996(class_8667.method_52742().method_52735(8));
      footer.method_52736(class_4185.method_46430(class_5244.field_24334, button -> this.method_25419()).method_46431());
      footer.method_52736(class_4185.method_46430(EmoteMenu.OPEN_FOLDER, button -> PlatformTools.openExternalEmotesDir()).method_46431());
      this.layout.method_48206(x$0 -> {
         class_339 var10000 = (class_339)this.method_37063(x$0);
      });
      this.method_48640();
   }

   private void exportEmotesInFormat(IWriter format) {
      Path exportDir = InstanceService.INSTANCE.getExternalEmoteDir().resolve(format.getExtension() + "_export");

      try {
         Files.createDirectories(exportDir);
      } catch (IOException var8) {
      }

      for (EmoteHolder emoteHolder : EmoteHolder.list) {
         Animation emote = emoteHolder.getEmote();
         if (!emote.data().has("isBuiltin")) {
            CommonData.LOGGER.debug("Saving {} into {}", emoteHolder.name.getString(), format.getExtension());

            try {
               EmoteWriter.writeAnimationInFormat(emote, exportDir, format);
            } catch (Exception e) {
               CommonData.LOGGER.warn("Failed to export!", e);
               PlatformTools.addToast(class_2561.method_43469("emotecraft.export.error", new Object[]{format.getExtension()}), emoteHolder.name);
            }
         }
      }

      String exportPath = InstanceService.INSTANCE.getGameDirectory().relativize(exportDir).normalize().toString().replace(File.separator, "/");
      PlatformTools.addToast(class_2561.method_43469("emotecraft.export.done", new Object[]{format.getExtension()}), class_2561.method_43470(exportPath));
      CommonData.LOGGER.info("All emotes are saved in {} format!", format.getExtension());
   }

   protected void method_48640() {
      this.layout.method_48222();
   }

   public void method_25419() {
      this.field_22787.method_1507(this.parent);
   }
}
