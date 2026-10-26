package me.drake.vieclient.modules.client.music;

import java.io.IOException;
import java.util.List;

@FunctionalInterface
interface GsmtcProcessLauncher {
   Process start(List var1) throws IOException;
}
