package com.victor.camera.lib.data

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: FrameGravity
 * Author: Victor
 * Date: 2026/2/12 10:52
 * Description: 扫描框对齐方式
 * -----------------------------------------------------------------
 */

enum class FrameGravity(val value: Int) {
    CENTER(0),
    LEFT(1),
    TOP(2),
    RIGHT(3),
    BOTTOM(4);

    companion object {
        fun fromInt(value: Int): FrameGravity {
            return FrameGravity.entries.find { it.value == value } ?: CENTER
        }
    }
}