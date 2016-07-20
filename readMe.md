## CircleProgressButton

![progressButton](https://raw.githubusercontent.com/fantianwen/MarkDown/master/commonView/progressButton1.gif)


## how to use

```
 <van.tian.wen.circleprogressbutton.CircleProgressButton
         android:layout_width="72dp"
         android:layout_height="72dp"
         van:circleColor="@android:color/holo_blue_light"
         van:progressColor="@android:color/holo_red_dark"
         van:progressWidth="2dp"
         van:textColor="@android:color/black"
         van:textSize="16sp" />
```
### setText

```
circleProgressButton.setText("文字");
```

### getProgressStatus

you can get progressStatus by implement `CircleProgressButton.CircleProcessListener`

```
circleProgressButton.setCircleProcessListener(new CircleProgressButton.CircleProcessListener() {
            @Override
            public void onFinished() {
                //长按结束了
                LogUtil.e("van====>onFinished");
            }

            @Override
            public void onCancel() {
                //长按取消了
                LogUtil.e("van====>onCancel");
            }

            @Override
            public void onCancelOk() {
                //长按取消了，并且进度到了0
                LogUtil.e("van====>onCancelOk");
            }

            @Override
            public void onReStart() {
                //长按结束了，在进度到0之前再次按了下去
                LogUtil.e("van====>onReStart");
            }
        });
```






