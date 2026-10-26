package com.zigythebird.playeranimcore.animation.keyframe;

public record KeyframeLocation<T extends Keyframe>(T keyframe, float startTick) {
}
