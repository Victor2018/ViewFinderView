package com.victor.camera.lib.data

enum class TextLocation(private val mValue: Int) {
    TOP(0), BOTTOM(1);

    companion object {
        fun getFromInt(value: Int): TextLocation {
            for (location in TextLocation.entries) {
                if (location.mValue == value) {
                    return location
                }
            }
            return TOP
        }
    }
}