# 一、前言

2021年的第一天，这也是我今年发布的第一篇博客，为了达成在元旦当天发布博客的成就，在大家欢庆元旦的时候，我忙忙碌碌的写了一整天的代码，祝愿自己新的一年，一帆风顺，牛气冲天

说远了，聊一下今天这个波形图的自定义view

最近涉及的某个医疗相关的业务，传感器数据传递上来需要实现示波器的效果，心电图的效果，目前交付效果还算理想，于是封装了一下，方便自己以后使用，也给大家分享一下

# 二、效果图

![image](https://upload-images.jianshu.io/upload_images/20395467-201e11915b19b2b8.gif?imageMogr2/auto-orient/strip)

图一是心电图效果，图二是一个滚动的波形图

# 三、功能实现

####（一）绘制背景网格

为了让他看上去像示波器上的数据，我们先绘制一层网格背景，看上去似乎就有那么点意思了

在onLayout函数中获取控件宽高，然后除以默认网格的宽高，得出需要绘制横线和竖线的数量

```
        /** 根据网格的单位长宽，获取能绘制网格横线和竖线的数量*/
        gridHorizontalNum = (int) (mHeight / GRID_WIDTH);
        gridVerticalNum = (int) (mWidth / GRID_WIDTH);
```

在onDraw函数中，通过for循环，来一条条绘制横线和竖线

```
    /**
     * 绘制网格
     *
     * @param canvas
     */
    private void drawGrid(Canvas canvas) {
        /** 设置颜色*/
        mLinePaint.setColor(gridLineColor);
        /** 绘制横线*/
        for (int i = 0; i < gridHorizontalNum + 1; i++) {
            canvas.drawLine(0, i * GRID_WIDTH,
                    mWidth, i * GRID_WIDTH, mLinePaint);
        }
        /** 绘制竖线*/
        for (int i = 0; i < gridVerticalNum + 1; i++) {
            canvas.drawLine(i * GRID_WIDTH, 0,
                    i * GRID_WIDTH, mHeight, mLinePaint);
        }
    }
```

网格是静态的，所以绘制起来比较简单

####（二）绘制折线

折线的绘制有两种模式，也就是效果图上下两种效果的区别

原理也比较简单

######1、首先和绘制网格一样，在onLayout函数中根据每段数据线条的跨度，算出当前view能绘制多少条直线

同时创建一个浮点类型的数组，用于保存每次传进来的数据

```
        /** 根据线条长度，最多能绘制多少个数据点*/
        row = (int) (mWidth / WAVE_LINE_WIDTH);
        dataArray = new float[row + 10];
```

######2、来看数据的保存方式

心电图效果的保存方式是创建了一个索引，每次绘制后自增，索引达到数组的最大值时，赋值为0

也就实现了循环的效果

普通的滚动效果的，就是删除第一个，新增的数据添加至数组的末尾
```
    /**
     * 添加新的数据
     */
    public void showLine(float line) {
        if (draw_index >= row) {
            draw_index = 0;
        }
        switch (drawMode){
            case 0:
                /** 常规模式数据添加至最后一位*/
                dataArray[row - 1] = line;
                break;
            case 1:
                /** 循环模式数据添加至当前绘制的位*/
                dataArray[draw_index] = line;
                break;
        }
        postInvalidate();
    }
```

######3、绘制折线的流程

默认最大值为20，以view高度的一半处为0，下方是-20，上方是+20

x没什么好说的，就是i*单位宽度

y则为高度的一半减去数组中数据占view一半高度的比重

将所有的点坐标传入Path类中，最后使用Canvas的drawPath函数就可以绘制出想要有的效果了
```
    /**
     * 取数组中的指定一段数据来绘制折线
     * @param start 起始数据位
     * @param end 结束数据位
     * */
    private void drawPathFromDatas(Canvas canvas, int start, int end){
        mPath.reset();
        mPath.moveTo(start * WAVE_LINE_WIDTH, mHeight / 2);
        for (int i = start; i < end; i++) {
            nowX = i * WAVE_LINE_WIDTH;
            float dataValue = dataArray[i];
            /** 判断数据为正数还是负数  超过最大值的数据按最大值来绘制*/
            if (dataValue > 0) {
                if (dataValue > MAX_VALUE) {
                    dataValue = MAX_VALUE;
                }
            } else {
                if (dataValue < -MAX_VALUE) {
                    dataValue = -MAX_VALUE;
                }
            }
            nowY = mHeight / 2 - dataValue * (mHeight / (MAX_VALUE * 2));
            mPath.lineTo(nowX, nowY);
        }
        canvas.drawPath(mPath, mWavePaint);
    }
```

# 四、如何使用

####（一）添加库

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
	        implementation 'com.github.Giftedcat:Waveform:1.0'
	}
```

####（二）布局文件

```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".MainActivity">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="波形图1"
        android:textColor="#000000"
        android:textSize="24sp" />

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

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="波形图2"
        android:textColor="#000000"
        android:textSize="24sp" />

    <com.giftedcat.wavelib.view.WaveView
        android:id="@+id/wave_view2"
        android:layout_width="match_parent"
        android:layout_height="1dp"
        android:layout_weight="1"
        app:draw_mode="normal"
        app:grid_visible="false"
        app:wave_line_stroke_width="5" />

</LinearLayout>
```
xml中可使用的参数
```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <declare-styleable name="WaveView">
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
    </declare-styleable>
</resources>
```
####（三）向波形图中添加数据

```
                data = new Random().nextFloat()*(20f)-10f;
                waveShowView.showLine(data);//取得是-10到10间的浮点数
```

# 五、结语

如果对你有所帮助，请记得帮我点一个star，有什么意见和建议可以在评论区给我留言
