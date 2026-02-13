package com.victor.camera.viewfinderview

import androidx.annotation.IntDef

@IntDef(
    ExampleType.VIEWFINDER_CLASSIC_STYLE,
    ExampleType.VIEWFINDER_POPULAR_STYLE,
    ExampleType.SHOW_RESULT_POINT,
    ExampleType.SHOW_CUSTOM_RESULT_POINT,
)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class ExampleType {
    companion object {
        const val VIEWFINDER_CLASSIC_STYLE = 0
        const val VIEWFINDER_POPULAR_STYLE = 1
        const val SHOW_RESULT_POINT = 2
        const val SHOW_CUSTOM_RESULT_POINT = 3
    }
}
