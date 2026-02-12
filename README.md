# ViewFinderView
ViewFinderView 是一个取景视图：主要用于渲染扫描相关的动画效果。其样式主要分为两大类：`classic`：经典样式（带扫描框）、`popular`：流行样式（不带扫描框）；可任意定制。

Usage

- Step 1. Add the JitPack repository to your build file

```
    allprojects {
        repositories {
        ...
        maven { url "https://jitpack.io" }
        }
    }
```

- Step 2. Add the dependency

```
dependencies {
    implementation 'com.github.Victor2018:ViewFinderView:latestVersion'
}
```


- Step 3. Add ViewFinderView in your layout xml

```
 <com.victor.lib.ViewFinderView
     android:id="@+id/viewfinderView"
     android:layout_width="match_parent"
     android:layout_height="match_parent"
     app:vvLaserStyle="line" />
```

# 相关属性说明：
| 属性                       | 属性类型      | 默认值                                  | 属性说明                                                  |
|:-------------------------|:----------|:-------------------------------------|:------------------------------------------------------|
| vfvViewfinderStyle        | enum      | classic                              | 取景框样式；支持`classic`：经典样式（带扫描框）、`popular`：流行样式（不带扫描框）    |
| vfvMaskColor              | color     | <font color=#000000>#60000000</font> | 扫描区外遮罩的颜色                                               |
| vfvFrameColor             | color     | <font color=#1FB3E2>#7F1FB3E2</font> | 扫描框边框的颜色                                                 |
| vfvFrameWidth             | dimension |                                      | 扫描框宽度                                                      |
| vfvFrameHeight            | dimension |                                      | 扫描框高度                                                      |
| vfvFrameRatio             | float     | 0.625f                               | 扫描框与屏幕占比,当未设置扫描框的宽高时，使用占比来计算宽高             |
| vfvFrameLineStrokeWidth   | dimension | 1dp                                  | 边框线宽度                                                      |
| vfvFramePaddingLeft       | dimension | 0                                    | 扫描框左边的内间距                                               |
| vfvFramePaddingTop        | dimension | 0                                    | 扫描框上边的内间距                                               |
| vfvFramePaddingRight      | dimension | 0                                    | 扫描框右边的内间距                                               |
| vfvFramePaddingBottom     | dimension | 0                                    | 扫描框下边的内间距                                               |
| vfvFrameGravity           | enum      | center                               | 扫描框对齐方式                                                   |
| vfvFrameCornerColor       | color     | <font color=#1FB3E2>#FF1FB3E2</font> | 扫描框边角的颜色                                                 |
| vfvFrameCornerSize        | dimension | 16dp                                 | 扫描框边角的大小                                                 |
| vfvFrameCornerStrokeWidth | dimension | 4dp                                  | 扫描框边角的描边宽度                                              |
| vfvFrameCornerRadius      | dimension | 0dp                                  | 扫描框圆角半径                                                   |
| vfvFrameDrawable          | reference |                                      | 扫描框自定义图片                                                 |
| vfvLaserLineHeight        | dimension | 5dp                                  | 激光扫描线高度                                                   |
| vfvLaserMovementSpeed     | dimension | 2dp                                  | 激光扫描线的移动速度                                              |
| vfvLaserAnimationInterval | integer   | 20                                   | 扫描动画延迟间隔时间，单位：毫秒                                    |
| vfvLaserGridColumn        | integer   | 20                                   | 网格激光扫描列数                                                  |
| vfvLaserGridHeight        | dimension | 40dp                                 | 网格激光扫描高度，为0dp时，表示动态铺满                              |
| vfvLaserGridStrokeWidth   | dimension | 1dp                                  | 网格线条的宽                                                     |
| vfvLaserColor             | color     | <font color=#1FB3E2>#FF1FB3E2</font> | 扫描区激光线的颜色                                                |
| vfvLaserStyle             | enum      | line                                 | 激光扫描的样式                                                    |
| vfvLaserDrawable          | reference |                                      | 激光扫描线自定义图片                                               |
| vfvLaserDrawableRatio     | float     | 0.625f                               | 激光扫描图片与屏幕占比                                             |
| vfvLabelText              | string    |                                      | 扫描提示文本信息                                                  |
| vfvLabelTextColor         | color     | <font color=#C0C0C0>#FFC0C0C0</font> | 提示文本字体颜色                                                  |
| vfvLabelTextSize          | dimension | 14sp                                 | 提示文本字体大小                                                  |
| vfvLabelTextPadding       | dimension | 24dp                                 | 提示文本距离扫描框的间距                                           |
| vfvLabelTextWidth         | dimension |                                      | 提示文本的宽度，默认为View的宽度                                    |
| vfvLabelTextLocation      | enum      | bottom                               | 提示文本显示位置                                                  |
| vfvPointColor             | color     | <font color=#1FB3E2>#FF1FB3E2</font> | 结果点的颜色                                                     |
| vfvPointStrokeColor       | color     | <font color=#FFFFFF>#FFFFFFFF</font> | 结果点描边的颜色                                                  |
| vfvPointRadius            | dimension | 15dp                                 | 结果点的半径                                                     |
| vfvPointStrokeRatio       | float     | 1.2                                  | 结果点描边半径与结果点半径的比例                                     |
| vfvPointDrawable          | reference |                                      | 结果点自定义图片                                                  |
| vfvPointAnimation         | boolean   | true                                 | 是否显示结果点的动画                                               |
| vfvPointAnimationInterval | integer   | 3000                                 | 结果点动画间隔时长；单位：毫秒                                       |
| vfvFullRefresh            | boolean   | false                                | 是否完全刷新；适用于`ViewFinderStyle`为`classic`经典样式（带扫描框）时 |
| vfvGradientHeightRatio    | float     | 0.6  f                               | 激光扫描渐变高度                                                   |
| vfvGlowEffectHeight       | dimension | 2dp                                  | 网格激光扫描光晕线高度，为0dp时，表示不绘制 光晕线                      |
| vfvShowTorchIcon          | boolean   | true                                 | 是否显示闪光灯图标位置跟提示正好相反                                  |
| vfvTorchIconPadding       | dimension | 6dp                                  | 闪光灯图标内边距                                                   |
| vfvTorchOnDrawable        | reference |                                      | 闪光灯开启图标                                                    |
| vfvTorchOffDrawable       | reference |                                      | 闪光灯关闭图标                                                    |
| vfvTorchOnText            | string    | 点击关闭手电筒                         | 闪光灯开启提示文字                                                 |
| vfvTorchOffText           | string    | 点击开启手电筒                         | 闪光灯关闭提示文字                                                 |
| vfvTorchTextColor         | color     | <font color=#FFFFFF>#FFFFFFFF</font> | 闪光灯提示文字颜色                                                 |
| vfvTorchTextSize          | dimension | 12sp                                 | 闪光灯提示文字大小                                                 |
| vfvTorchIconTextSpacing   | dimension | 6dp                                  | 闪光灯图标与文字间距                                               |
| vfvTorchIconTextPadding   | dimension | 12dp                                  | 闪光灯图标与扫描框的间距                                           |

# 关注开发者：
- 邮箱： victor423099@gmail.com
- 新浪微博
- ![image](https://github.com/Victor2018/AppUpdateLib/raw/master/SrceenShot/sina_weibo.jpg)

## License

Copyright (c) 2017 Victor

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
