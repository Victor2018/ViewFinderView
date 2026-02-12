package com.victor.viewfinderview

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.victor.lib.ViewFinderView
import com.victor.lib.data.ViewFinderStyle
import com.victor.lib.interfaces.OnScanPointClickListener
import com.victor.lib.interfaces.OnTorchStateChangeListener

class ViewFinderViewActivity : AppCompatActivity() {

    companion object {
        fun intentStart (activity: AppCompatActivity, exampleType: Int,data: Int?) {
            val intent = Intent(activity, ViewFinderViewActivity::class.java)
            intent.putExtra(Constant.EXAMPLE_TYPE,exampleType)
            intent.putExtra(Constant.LAYOUT_ID,data)
            activity.startActivity(intent)
        }
    }

    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        initData(intent)
    }

    fun initData(intent: Intent?) {
        val layoutId = intent?.getIntExtra(Constant.LAYOUT_ID,0) ?: 0
        val exampleType = intent?.getIntExtra(Constant.EXAMPLE_TYPE,0) ?: 0
        setContentView(layoutId)

        val viewfinderView = findViewById<ViewFinderView>(R.id.viewfinderView)
        viewfinderView.setTiledVectorBackground(R.drawable.ic_vector_bg)

        when(exampleType) {
            ExampleType.VIEWFINDER_CLASSIC_STYLE -> {
                viewfinderView.setViewfinderStyle(ViewFinderStyle.CLASSIC)

                viewfinderView.mOnTorchStateChangeListener = object : OnTorchStateChangeListener {
                    override fun onTorchStateChanged(isOn: Boolean) {
                        showToast("isOn: $isOn")
                    }
                }
            }

            ExampleType.VIEWFINDER_POPULAR_STYLE -> {
                viewfinderView.setViewfinderStyle(ViewFinderStyle.POPULAR)
            }

            ExampleType.SHOW_RESULT_POINT -> {
                // 显示结果点
                viewfinderView.showResultPoints(arrayListOf(Point(400, 750), Point(650, 750)))
                // 结果点Item点击监听
                viewfinderView.mOnScanPointClickListener = object : OnScanPointClickListener {
                    override fun OnScanPointClick(position: Int) {
                        showToast("position: $position")
                    }
                }
            }

            ExampleType.SHOW_CUSTOM_RESULT_POINT -> {
                viewfinderView.setPointDrawable(R.mipmap.ic_result_point)
                // 显示结果点
                viewfinderView.showResultPoints(arrayListOf(Point(400, 750), Point(650, 750)))
                // 结果点Item点击监听
                viewfinderView.mOnScanPointClickListener = object : OnScanPointClickListener {
                    override fun OnScanPointClick(position: Int) {
                        showToast("position: $position")
                    }
                }
            }
        }
    }

    private fun showToast(text: String) {
        toast?.cancel()
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        toast?.show()
    }

    private fun View.setTiledVectorBackground(@DrawableRes vectorResId: Int) {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId) as VectorDrawable
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        ).apply {
            val canvas = Canvas(this)
            vectorDrawable.setBounds(0, 0, width, height)
            vectorDrawable.draw(canvas)
        }

        val tiledDrawable = BitmapDrawable(context.resources, bitmap).apply {
            tileModeX = Shader.TileMode.REPEAT
            tileModeY = Shader.TileMode.REPEAT
        }

        background = tiledDrawable
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        initData(intent)
    }

}