package van.tian.wen.circleprogressbutton;

import android.os.Bundle;

/**
 * Created by RadAsm on 16/7/20.
 */
public class BounceY {

    /**
     * 从第一次回弹到落地的时间（单位：毫秒）
     */
    public static final float TIME = 250;
    private final float mBouncedWidth;

    /**
     * 回落回来的时间
     */
    private float mTime1;


    public BounceY(float bouncedWidth) {
        mTime1 = 1.2071F * TIME;
        this.mBouncedWidth = bouncedWidth;
    }

    public float getTime1() {
        return mTime1;
    }

    public float calulateY(int x) {
        if (x <= mTime1) {
            return (float) ( mBouncedWidth - ((2 * mBouncedWidth * Math.pow((x - TIME), 2)) / Math.pow(TIME, 2)));
        }
        return 0;
    }

}
