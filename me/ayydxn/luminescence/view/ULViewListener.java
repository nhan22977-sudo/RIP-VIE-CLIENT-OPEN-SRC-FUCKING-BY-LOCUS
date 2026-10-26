package me.ayydxn.luminescence.view;

import me.ayydxn.luminescence.console.ULMessageLevel;
import me.ayydxn.luminescence.console.ULMessageSource;
import me.ayydxn.luminescence.geometry.ULIntRect;

public interface ULViewListener {
   default void onTitleChange(String title) {
   }

   default void onURLChange(String url) {
   }

   default void onTooltipChange(String tooltip) {
   }

   default void onCursorChange(ULCursor cursor) {
   }

   default void onConsoleMessageAdded(
      ULMessageSource messageSource, ULMessageLevel messageLevel, String message, int lineNumber, int columnNumber, String sourceID
   ) {
   }

   default ULView onChildViewCreated(String openerURL, String targetURL, boolean isPopup, ULIntRect popupRect) {
      return null;
   }

   default ULView onInspectorViewCreated(boolean isLocal, String inspectedURL) {
      return null;
   }

   default void onLoadingBegin(long frameID, boolean isMainFrame, String url) {
   }

   default void onLoadingFinish(long frameID, boolean isMainFrame, String url) {
   }

   default void onLoadingFail(long frameID, boolean isMainFrame, String url, String description, String errorDomain, int errorCode) {
   }

   default void onWindowObjectReady(long frameID, boolean isMainFrame, String url) {
   }

   default void onDOMReady(long frameID, boolean isMainFrame, String url) {
   }

   default void onUpdateHistory() {
   }
}
