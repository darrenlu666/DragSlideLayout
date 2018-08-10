package com.ldh.android.dragslidelayout;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

import static com.ldh.android.dragslidelayout.DragLayout.TIME_TITLE_SHOW_HIDE;

public class MainActivity extends AppCompatActivity implements Handler.Callback {

    public static final int TITLE_SHOW_MSG_WHAT = 0x001;

    private Toolbar mToolbar;

    private Button mButton;

    private DragLayout mDragLayout;

    private Timer mTitleTimer;

    private Handler mTitleShowHandler;

    private boolean mIsTitleShow = true;//标题栏初始状态：显示

    private boolean mAnimating;//是否在动画进行中

    private DragLayout.DragLayoutBuilder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setWindowNoStatusBar(this);
        setContentView(R.layout.activity_main);
        mTitleShowHandler = new Handler(this);
        mDragLayout = findViewById(R.id.drag_layout);
        mButton = findViewById(R.id.sample_text);
        mToolbar = findViewById(R.id.toolBar);
        builder = new DragLayout.DragLayoutBuilder(this,mDragLayout)
                .setDragView(mButton)
                .setViewBelowTitle(mToolbar)
                .setBlankZoneClickable(true)
                .build();

        setTitleTimer();
        mDragLayout.setOnDragLayoutClickListener(()->blackZoneClick());
    }

    public void setTitleTimer() {
        mTitleTimer = new Timer();
        mTitleTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mTitleShowHandler.sendEmptyMessage(TITLE_SHOW_MSG_WHAT);
            }
        }, 3000);
    }

    private void blackZoneClick() {
        if (mAnimating) return;
        if (mIsTitleShow) {
            hideTitle(mToolbar);
            if (mTitleTimer != null) {
                mTitleTimer.cancel();
                mTitleTimer = null;
            }
        } else {
            showTitle(mToolbar);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == TITLE_SHOW_MSG_WHAT) {
            hideTitle(mToolbar);
        }
        return false;
    }

    //显示标题栏动画
    private void showTitle(View view) {
        if (!mIsTitleShow) {
            mIsTitleShow = true;
            ObjectAnimator anim = ObjectAnimator.ofFloat(view, "y", view.getY(), view.getHeight() + view.getY());
            anim.setDuration(TIME_TITLE_SHOW_HIDE);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    showSystemBar(mDragLayout);
                    setTitleTimer();
                    mAnimating = false;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            anim.start();
            builder.getDragLayout().setViewBelowTitle(view);
            mAnimating = true;
        }
    }

    //隐藏标题栏动画
    private void hideTitle(View view) {
        if (mIsTitleShow) {
            mIsTitleShow = false;
            ObjectAnimator anim = ObjectAnimator.ofFloat(view, "y", view.getY(), view.getY() - view.getHeight());
            anim.setDuration(TIME_TITLE_SHOW_HIDE);
            anim.start();
            builder.getDragLayout().setViewToTop(view);
            mAnimating = true;
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    hideSystemBar(mDragLayout);
                    mAnimating = false;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }
    }

    public void hideSystemBar(View fullView) {
        int systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            systemUiVisibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        fullView.setSystemUiVisibility(systemUiVisibility);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) fullView.getLayoutParams();
        layoutParams.topMargin = 0;
        fullView.setLayoutParams(layoutParams);
    }

    public void showSystemBar(View fullView) {
        fullView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
