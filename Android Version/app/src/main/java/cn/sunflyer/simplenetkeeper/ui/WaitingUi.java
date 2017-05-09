package cn.sunflyer.simplenetkeeper.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import cn.sunflyer.simplenetkeeper.R;

/**
 * Created by 陈耀璇 on 2015/4/15.
 */
public class WaitingUi extends Dialog {
    Context context;

    private ImageView mLoading;
    private TextView mText;
    private AnimationDrawable pAnimDraw;

    private CharSequence mInitText ;

    public WaitingUi(Context context) {
        this(context,"请稍候......");
    }

    public WaitingUi(Context c,CharSequence x){
        super(c, R.style.SelfDialog);
        this.context = c;
        this.mInitText = x;
    }

    public void setTips(CharSequence x){
        if(this.mText != null) mText.setText(x);
    }

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.PROGRESS_VISIBILITY_ON);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.waiting_layout);
        this.mText = (TextView)findViewById(R.id.wl_text);
        setTips(this.mInitText);
        this.mLoading = (ImageView)findViewById(R.id.wl_pic);

        mLoading.setBackgroundResource(R.drawable.wait_anim);

        pAnimDraw = (AnimationDrawable)mLoading.getBackground();

        setCanceledOnTouchOutside(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.hide();
            pAnimDraw.stop();
            return true;
        }
        return false;
    }

    @Override
    public void show() {
        this.setTips(mInitText);
        super.show();
        pAnimDraw.start();
    }
}