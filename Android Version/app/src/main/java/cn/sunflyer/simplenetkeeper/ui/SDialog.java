package cn.sunflyer.simplenetkeeper.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.sunflyer.simplenetkeeper.R;

/**
 * Created by 陈耀璇 on 2015/5/24.<br/>
 * 继承自Dialog类，由于个人水平问题，因此这个对话框在消失后会被释放(dismiss)掉。除非手动指定每个按钮的监听事件处理过程。<br/>
 * 为了修改这个窗口的标题和文本内容，请使用 setWindowTitle 和 setWindowContent 方法。<br/>
 * 请注意：请在调用show()方法后再设置文本内容，否则会提示空指针错误，这个操作与onCreate方法调用时机有关<br/>
 */
public class SDialog extends Dialog{

    Context context;

    private TextView mTvTitle;
    private TextView mTvContent;

    private Button mButOk;
    private Button mButNo;

    public SDialog(Context c) {
        super(c , R.style.SDialog);
        this.context = c;
        super.onStart();
    }

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.PROGRESS_VISIBILITY_ON);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ques_dialog);
        /**私有变量初始化*/
        this.initComponentVar();
    }

    /**初始化组件变量*/
    private void initComponentVar(){
        this.mTvTitle = (TextView)findViewById(R.id.qd_title);
        this.mTvContent = (TextView)findViewById(R.id.qd_con);

        this.mButNo = (Button)findViewById(R.id.qd_but_no);
        this.mButOk = (Button)findViewById(R.id.qd_but_ok);

        this.setOnTouchOutsideCloseWindow(false);
    }

    private final View.OnClickListener DEFAULT_LISTENER = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            hide();
            dismiss();
        }
    };

    public SDialog setPositiveListener(View.OnClickListener i){
        this.mButOk.setOnClickListener(i == null ? DEFAULT_LISTENER : i);
        return this;
    }

    public SDialog setNegativeListener(View.OnClickListener i){
        this.mButNo.setOnClickListener(i == null ? DEFAULT_LISTENER : i);
        return this;
    }

    public SDialog setWindowTitle(CharSequence t){
        if(t != null)
            this.mTvTitle.setText(t);
        return this;
    }

    public SDialog setWindowContent(CharSequence t){
        if(t != null)
            this.mTvContent.setText(t);
        return this;
    }

    public SDialog setOnTouchOutsideCloseWindow(boolean i){
        this.setCanceledOnTouchOutside(i);
        return this;
    }

    public SDialog setPositiveText(CharSequence i){
        if( i != null && !i.equals("")){
            this.mButOk.setText(i);
            this.mButOk.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public SDialog setNegativeText(CharSequence i){
        if( i != null && !i.equals("")){
            this.mButNo.setText(i);
            this.mButNo.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public SDialog setPositiveButton(CharSequence i,View.OnClickListener c){
        this.setPositiveText(i);
        this.setPositiveListener(c);
        return this;
    }

    public SDialog setNegativeButton(CharSequence i,View.OnClickListener c){
        this.setNegativeText(i);
        this.setNegativeListener(c);
        return this;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 按下了键盘上返回按钮
            this.hide();
            return true;
        }
        return false;
    }

    public void show(){
        super.show();
    }

    public void hide(){
        super.hide();
    }

}
