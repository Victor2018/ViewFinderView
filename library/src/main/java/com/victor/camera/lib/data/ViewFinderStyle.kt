package com.victor.camera.lib.data

import androidx.annotation.IntDef

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ViewFinderStyle
 * Author: Victor
 * Date: 2026/2/12 10:53
 * Description: 取景框样式
 * -----------------------------------------------------------------
 */

@IntDef(ViewFinderStyle.CLASSIC, ViewFinderStyle.POPULAR)
@Retention(AnnotationRetention.SOURCE)
annotation class ViewFinderStyle {
    companion object {
        /**
         * 经典样式：经典的扫描风格（带扫描框）
         */
        const val CLASSIC = 0
        /**
         * 流行样式：类似于新版的微信全屏扫描（不带扫描框）
         */
        const val POPULAR = 1
    }
}