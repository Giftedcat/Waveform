# Waveform - 1.2 更新，支持添加多条数据

# 完整博客地址:
https://juejin.cn/post/6912791803779350536

# 效果图

![效果图](https://upload-images.jianshu.io/upload_images/20395467-0c4914cda75f6aed.gif?imageMogr2/auto-orient/strip)

# 如何使用

## （一）添加库
```
    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
```
	dependencies {
	        implementation 'com.github.Giftedcat:Waveform:1.2.2'
	}
```
## （二）布局文件
波形支持两种滚动模式
通过修改draw_mode
```
    <com.giftedcat.wavelib.view.WaveView
        android:id="@+id/wave_view1"
        android:layout_width="match_parent"
        android:layout_height="1dp"
        android:layout_weight="1"
        app:draw_mode="loop"
        app:max_value="30"
        app:wave_background="#000000"
        app:wave_line_color="#ffff00"
        app:wave_line_width="20" />

    <com.giftedcat.wavelib.view.WaveView
        android:id="@+id/wave_view2"
        android:layout_width="match_parent"
        android:layout_height="1dp"
        android:layout_weight="1"
        app:draw_mode="normal"
        app:grid_visible="false"
        app:wave_line_stroke_width="5" />
```
#### xml可用参数
```
        <!--最大值 默认为20，数据为-20 ~ 20-->
        <attr name="max_value" format="integer" />
        <!--波形图折线单位宽度，通过修改该参数，控制横坐标的单位-->
        <attr name="wave_line_width" format="integer" />
        <!--波形图折线的线宽-->
        <attr name="wave_line_stroke_width" format="integer" />
        <!--波形图折线的颜色-->
        <attr name="wave_line_color" format="string" />
        <!--背景网格图的颜色-->
        <attr name="grid_line_color" format="string" />
        <!--背景颜色，默认为黑-->
        <attr name="wave_background" format="string"/>
        <!--背景网格是否可见-->
        <attr name="grid_visible" format="boolean" />
        <!--波形图绘制模式，常规和循环-->
        <attr name="draw_mode" format="enum">
            <enum name="normal" value="0" />
            <enum name="loop" value="1" />
        </attr>
```
## (三)向控件中添加数据
#### 3.1添加单条数据

```
    float data = new Random().nextFloat()*(20f)-10f;
    waveShowView.showLine(data);//取得是-10到10间的浮点数
```
#### 3.2添加多条数据(1.2版本)

```
    float[] datas = new float[length];
    for (int i = 0; i < datas.length; i++){
        datas[i] = new Random().nextFloat()*(20f)-10f;
    }
    waveShowView.showLines(datas);
```

