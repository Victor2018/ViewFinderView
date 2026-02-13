package com.victor.camera.viewfinderview

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        findViewById<Button>(R.id.mBtnLineLaserStyle).setOnClickListener { view ->
            ViewFinderViewActivity.intentStart(this,
                ExampleType.VIEWFINDER_CLASSIC_STYLE,
                R.layout.activity_line_style_viewfinder)
        }
        findViewById<Button>(R.id.mBtnGridLaserStyle1).setOnClickListener { view ->
            ViewFinderViewActivity.intentStart(this,
                ExampleType.VIEWFINDER_CLASSIC_STYLE,
                R.layout.activity_grid_style_viewfinder)
        }
        findViewById<Button>(R.id.mBtnGridLaserStyle2).setOnClickListener { view ->
            ViewFinderViewActivity.intentStart(this,
                ExampleType.VIEWFINDER_CLASSIC_STYLE,
                R.layout.activity_full_grid_style_viewfinder)
        }
        findViewById<Button>(R.id.mBtnGradientStyle).setOnClickListener { view ->
            ViewFinderViewActivity.intentStart(this,
                ExampleType.VIEWFINDER_CLASSIC_STYLE,
                R.layout.activity_full_gradient_style_viewfinder)
        }
        findViewById<Button>(R.id.mBtnImageStyle).setOnClickListener { view ->
            ViewFinderViewActivity.intentStart(this,
                ExampleType.VIEWFINDER_CLASSIC_STYLE,
                R.layout.activity_image_style_viewfinder)
        }
        findViewById<Button>(R.id.mBtnPopularViewFinderStyle).setOnClickListener { view ->
            ViewFinderViewActivity.intentStart(this,
                ExampleType.VIEWFINDER_POPULAR_STYLE,
                R.layout.activity_image_style_viewfinder)
        }
        findViewById<Button>(R.id.mBtnShowResultPointsStyle).setOnClickListener { view ->
            ViewFinderViewActivity.intentStart(this,
                ExampleType.SHOW_RESULT_POINT,
                R.layout.activity_line_style_viewfinder)
        }
        findViewById<Button>(R.id.mBtnShowDiyResultPointsStyle).setOnClickListener { view ->
            ViewFinderViewActivity.intentStart(this,
                ExampleType.SHOW_CUSTOM_RESULT_POINT,
                R.layout.activity_line_style_viewfinder)
        }
    }
}