package de.javagl.jgltf.model.animation;

import java.util.Objects;

public final class AnimationRunner {
   private final AnimationManager animationManager;
   private boolean running = false;
   private Thread animationThread;
   private final long stepSizeMs = 10L;

   public AnimationRunner(AnimationManager animationManager) {
      Objects.requireNonNull(animationManager, "The animationManager may not be null");
      this.animationManager = animationManager;
   }

   public synchronized void start() {
      if (!this.isRunning()) {
         this.animationThread = new Thread(this::runAnimations, "animationThread");
         this.animationThread.setDaemon(true);
         this.animationThread.start();
         this.running = true;
      }
   }

   public synchronized void stop() {
      if (this.isRunning()) {
         this.running = false;
         this.animationThread = null;
      }
   }

   boolean isRunning() {
      return this.running;
   }

   private void runAnimations() {
      long previousNs = System.nanoTime();

      while (this.isRunning()) {
         long currentNs = System.nanoTime();
         long deltaNs = currentNs - previousNs;
         this.animationManager.performStep(deltaNs);
         previousNs = currentNs;

         try {
            Thread.sleep(10L);
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
         }
      }
   }
}
