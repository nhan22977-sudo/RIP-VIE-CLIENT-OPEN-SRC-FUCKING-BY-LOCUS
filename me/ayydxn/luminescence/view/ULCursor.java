package me.ayydxn.luminescence.view;

import me.ayydxn.luminescence.util.NativeEnum;
import me.ayydxn.luminescence.util.UsedByNative;

public enum ULCursor implements NativeEnum {
   POINTER(0),
   CROSS(1),
   HAND(2),
   IBEAM(3),
   WAIT(4),
   HELP(5),
   EAST_RESIZE(6),
   NORTH_RESIZE(7),
   NORTH_EAST_RESIZE(8),
   NORTH_WEST_RESIZE(9),
   SOUTH_RESIZE(10),
   SOUTH_EAST_RESIZE(11),
   SOUTH_WEST_RESIZE(12),
   WEST_RESIZE(13),
   NORTH_SOUTH_RESIZE(14),
   EAST_WEST_RESIZE(15),
   NORTH_EAST_SOUTH_WEST_RESIZE(16),
   NORTH_WEST_SOUTH_EAST_RESIZE(17),
   COLUMN_RESIZE(18),
   ROW_RESIZE(19),
   MIDDLE_PANNING(20),
   EAST_PANNING(21),
   NORTH_PANNING(22),
   NORTH_EAST_PANNING(23),
   NORTH_WEST_PANNING(24),
   SOUTH_PANNING(25),
   SOUTH_EAST_PANNING(26),
   SOUTH_WEST_PANNING(27),
   WEST_PANNING(28),
   MOVE(29),
   VERTICAL_TEXT(30),
   CELL(31),
   CONTEXT_MENU(32),
   ALIAS(33),
   PROGRESS(34),
   NO_DROP(35),
   COPY(36),
   NONE(37),
   NOT_ALLOWED(38),
   ZOOM_IN(39),
   ZOOM_OUT(40),
   GRAB(41),
   GRABBING(42),
   CUSTOM(43);

   private final int value;

   ULCursor(int value) {
      this.value = value;
   }

   @Override
   public int getValue() {
      return this.value;
   }

   @UsedByNative("ViewCallbackAdapter.cpp")
   public static ULCursor fromNativeValue(int value) {
      return NativeEnum.resolve(ULCursor.class, value, POINTER);
   }

   // $VF: synthetic method
   private static ULCursor[] $values() {
      return new ULCursor[]{
         POINTER,
         CROSS,
         HAND,
         IBEAM,
         WAIT,
         HELP,
         EAST_RESIZE,
         NORTH_RESIZE,
         NORTH_EAST_RESIZE,
         NORTH_WEST_RESIZE,
         SOUTH_RESIZE,
         SOUTH_EAST_RESIZE,
         SOUTH_WEST_RESIZE,
         WEST_RESIZE,
         NORTH_SOUTH_RESIZE,
         EAST_WEST_RESIZE,
         NORTH_EAST_SOUTH_WEST_RESIZE,
         NORTH_WEST_SOUTH_EAST_RESIZE,
         COLUMN_RESIZE,
         ROW_RESIZE,
         MIDDLE_PANNING,
         EAST_PANNING,
         NORTH_PANNING,
         NORTH_EAST_PANNING,
         NORTH_WEST_PANNING,
         SOUTH_PANNING,
         SOUTH_EAST_PANNING,
         SOUTH_WEST_PANNING,
         WEST_PANNING,
         MOVE,
         VERTICAL_TEXT,
         CELL,
         CONTEXT_MENU,
         ALIAS,
         PROGRESS,
         NO_DROP,
         COPY,
         NONE,
         NOT_ALLOWED,
         ZOOM_IN,
         ZOOM_OUT,
         GRAB,
         GRABBING,
         CUSTOM
      };
   }
}
