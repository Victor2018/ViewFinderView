package com.victor.viewfinderview

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import com.victor.viewfinderview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)


        binding.content.mBtnLineLaserStyle.setOnClickListener { view ->
            ViewFinderViewActivity.intentStart(this,
                ExampleType.VIEWFINDER_CLASSIC_STYLE,
                R.layout.activity_line_style_viewfinder)
        }
        binding.content.mBtnGridLaserStyle1.setOnClickListener { view ->
            ViewFinderViewActivity.intentStart(this,
                ExampleType.VIEWFINDER_CLASSIC_STYLE,
                R.layout.activity_grid_style_viewfinder)
        }
        binding.content.mBtnGridLaserStyle2.setOnClickListener { view ->
            ViewFinderViewActivity.intentStart(this,
                ExampleType.VIEWFINDER_CLASSIC_STYLE,
                R.layout.activity_full_grid_style_viewfinder)
        }
        binding.content.mBtnGradientStyle.setOnClickListener { view ->
            ViewFinderViewActivity.intentStart(this,
                ExampleType.VIEWFINDER_CLASSIC_STYLE,
                R.layout.activity_full_gradient_style_viewfinder)
        }
        binding.content.mBtnImageStyle.setOnClickListener { view ->
            ViewFinderViewActivity.intentStart(this,
                ExampleType.VIEWFINDER_CLASSIC_STYLE,
                R.layout.activity_image_style_viewfinder)
        }
        binding.content.mBtnPopularViewFinderStyle.setOnClickListener { view ->
            ViewFinderViewActivity.intentStart(this,
                ExampleType.VIEWFINDER_POPULAR_STYLE,
                R.layout.activity_image_style_viewfinder)
        }
        binding.content.mBtnShowResultPointsStyle.setOnClickListener { view ->
            ViewFinderViewActivity.intentStart(this,
                ExampleType.SHOW_RESULT_POINT,
                R.layout.activity_line_style_viewfinder)
        }
        binding.content.mBtnShowDiyResultPointsStyle.setOnClickListener { view ->
            ViewFinderViewActivity.intentStart(this,
                ExampleType.SHOW_CUSTOM_RESULT_POINT,
                R.layout.activity_line_style_viewfinder)
        }
    }

}