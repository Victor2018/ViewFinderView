package com.victor.lib.data

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: LaserStyle
 * Author: Victor
 * Date: 2026/2/12 10:51
 * Description: 扫描线样式
 * -----------------------------------------------------------------
 */

enum class LaserStyle(val value: Int) {
    NONE(0),
    LINE(1),
    GRID(2),
    IMAGE(3),
    GRADIENT(4);

    companion object {
        fun fromInt(value: Int): LaserStyle {
            return LaserStyle.entries.find { it.value == value } ?: LINE
        }
    }
}