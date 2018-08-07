package com.ldh.android.dragslidelayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;


/**
 * Created by ldh on 2017/12/25.
 */
public class DragLayout extends RelativeLayout {
    private static final String TAG = "DragLayout";
    public static final long TIME_TITLE_SHOW_HIDE = 300;//标题栏隐藏和显示动画时间
    private ViewDragHelper mViewDragHelper;
    private View mDragView;

    public static final String POS_MOVING = "POS_MOVING";
    public static final String POS_LEFT_TOP = "POS_LEFT_TOP";
    public static final String POS_RIGHT_TOP = "POS_RIGHT_TOP";
    public static final String POS_LEFT_BOTTOM = "POS_LEFT_BOTTOM";
    public static final String POS_RIGHT_BOTTOM = "POS_RIGHT_BOTTOM";
    public static final String POS_LEFT_TOP_MARGIN_TITLE = "POS_LEFT_TOP_MARGIN_TITLE";
    public static final String POS_RIGHT_TOP_MARGIN_TITLE = "POS_RIGHT_TOP_MARGIN_TITLE";

    private String mCurrentPos = POS_LEFT_TOP_MARGIN_TITLE;

    private int mCurrentLeft;
    private int mCurrentTop;

    private int mCenterX;
    private int mCenterY;

    private int mWidth;
    private int mHeight;

    private View mTitleView;//标题栏

    private boolean isTitleShow = true;

    private boolean isClickable;//若设置为true，可允许该viewGroup拦截点击事件。默认为false

    public DragLayout(Context context) {
        this(context, null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mViewDragHelper = ViewDragHelper.create(this, 1f, new ViewDragCallBack());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h / 2;

        mWidth = w;
        mHeight = h;
    }

    public void setDragedViewPos(String currentPos){
        mCurrentPos = currentPos;
    }

    public void setDragView(@NonNull View view) {
        mDragView = view;
    }

    public void setViewToTop(View view) {//标题隐藏了
        if(mDragView == null)
            throw new IllegalStateException("have not setDragView");
        mTitleView = view;
        isTitleShow = false;
        if (POS_LEFT_TOP_MARGIN_TITLE.equals(mCurrentPos)) {
            mCurrentPos = POS_LEFT_TOP;
            mCurrentLeft = mDragView.getLeft();
            mCurrentTop = mDragView.getTop();
            setTranslate(mCurrentPos);
        } else if (POS_RIGHT_TOP_MARGIN_TITLE.equals(mCurrentPos)) {
            mCurrentPos = POS_RIGHT_TOP;
            mCurrentLeft = mDragView.getLeft();
            mCurrentTop = mDragView.getTop();
            setTranslate(mCurrentPos);
        }

    }

    public void setViewBelowTitle(View view) {//标题显示了
        if(mDragView == null)
            throw new IllegalStateException("have not setDragView");
        mTitleView = view;
        isTitleShow = true;
        if (POS_LEFT_TOP.equals(mCurrentPos)) {
            mCurrentLeft = mDragView.getLeft();
            mCurrentTop = mDragView.getTop();
            mCurrentPos = POS_LEFT_TOP_MARGIN_TITLE;
            setTranslate(mCurrentPos);
        } else if (POS_RIGHT_TOP.equals(mCurrentPos)) {
            mCurrentLeft = mDragView.getLeft();
            mCurrentTop = mDragView.getTop();
            mCurrentPos = POS_RIGHT_TOP_MARGIN_TITLE;
            setTranslate(mCurrentPos);
        }
    }

    public void setClickable(boolean isClickable) {
        this.isClickable = isClickable;
    }

    private class ViewDragCallBack extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return mDragView == child;
        }

        /**
         * 处理水平方向上的拖动
         *
         * @param child 拖动的View
         * @param left  移动到x轴的距离
         * @param dx    建议的移动的x距离
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //两个if主要是让view在ViewGroup中
            if (left < getPaddingLeft()) {
                mCurrentLeft = getPaddingLeft();
                return mCurrentLeft;
            }

            if (left > getWidth() - child.getMeasuredWidth() - getPaddingRight()) {
                mCurrentLeft = getWidth() - child.getMeasuredWidth() - getPaddingRight();
                return mCurrentLeft;
            }
            mCurrentLeft = left;
            return left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            //两个if主要是让view在ViewGroup中
            if (top < getPaddingTop()) {
                mCurrentTop = getPaddingTop();
                return mCurrentTop;
            }

            if (top > getHeight() - child.getMeasuredHeight() - getPaddingBottom()) {
                mCurrentTop = getHeight() - child.getMeasuredHeight() - getPaddingBottom();
                return mCurrentTop;
            }
            mCurrentTop = top;
            return top;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            switch (state) {
                case ViewDragHelper.STATE_DRAGGING://正在拖动过程中
                    mCurrentPos = POS_MOVING;
                    break;
                case ViewDragHelper.STATE_IDLE://view没有被拖动，或者正在进行fling
                    if (mCurrentLeft + mDragView.getMeasuredWidth() / 2 <= mCenterX & mCurrentTop + mDragView.getMeasuredHeight() / 2 <= mCenterY) {//左上角
                        mCurrentPos = isTitleShow ? POS_LEFT_TOP_MARGIN_TITLE : POS_LEFT_TOP;
                    } else if (mCurrentLeft + mDragView.getMeasuredWidth() / 2 >= mCenterX & mCurrentTop + mDragView.getMeasuredHeight() / 2 <= mCenterY) {//右上角
                        mCurrentPos = isTitleShow ? POS_RIGHT_TOP_MARGIN_TITLE : POS_RIGHT_TOP;
                    } else if (mCurrentLeft + mDragView.getMeasuredWidth() / 2 <= mCenterX & mCurrentTop + mDragView.getMeasuredHeight() / 2 >= mCenterY) {//左下角
                        mCurrentPos = POS_LEFT_BOTTOM;
                    } else if (mCurrentLeft + mDragView.getMeasuredWidth() / 2 >= mCenterX & mCurrentTop + mDragView.getMeasuredHeight() / 2 >= mCenterY) {//右下角
                        mCurrentPos = POS_RIGHT_BOTTOM;
                    } else {//右上角
                        mCurrentPos = POS_RIGHT_TOP;
                    }
                    setTranslate(mCurrentPos);
                    break;
                case ViewDragHelper.STATE_SETTLING://fling完毕后被放置到一个位置
                    break;
            }
            super.onViewDragStateChanged(state);
        }


        @Override
        public int getViewHorizontalDragRange(View child) {
            return getMeasuredWidth() - child.getMeasuredWidth();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight() - child.getMeasuredHeight();
        }
    }

    private float dx = 0f;
    private float dy = 0f;

    private void setTranslate(final String posStyle) {
        switch (posStyle) {
            case POS_LEFT_TOP:
                dx = -mCurrentLeft;
                dy = -mCurrentTop;
                break;
            case POS_RIGHT_TOP:
                dx = mWidth - mCurrentLeft - mDragView.getMeasuredWidth();
                dy = -mCurrentTop;
                break;
            case POS_LEFT_BOTTOM:
                dx = -mCurrentLeft;
                dy = mHeight - mCurrentTop - mDragView.getMeasuredHeight();
                break;
            case POS_RIGHT_BOTTOM:
                dx = mWidth - mCurrentLeft - mDragView.getMeasuredWidth();
                dy = mHeight - mCurrentTop - mDragView.getMeasuredHeight();
                break;
            case POS_LEFT_TOP_MARGIN_TITLE:
                dx = -mCurrentLeft;
                dy = -mCurrentTop + (mTitleView == null ? 0 : mTitleView.getMeasuredHeight());
                break;
            case POS_RIGHT_TOP_MARGIN_TITLE:
                dx = mWidth - mCurrentLeft - mDragView.getMeasuredWidth();
                dy = -mCurrentTop + (mTitleView == null ? 0 : mTitleView.getMeasuredHeight());
                break;
        }
        TranslateAnimation mTranslateAnimation = new TranslateAnimation(0, dx, 0, dy);
        mTranslateAnimation.setDuration(TIME_TITLE_SHOW_HIDE);
        mTranslateAnimation.setFillAfter(true);
        mTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDragView.clearAnimation();
                LayoutParams params = (LayoutParams) mDragView.getLayoutParams();
                removeRule(params);
                params.setMargins(0, 0, 0, 0);
                switch (posStyle) {
                    case POS_LEFT_TOP:
                        //mDragView.layout(0, 0, mDragView.getMeasuredWidth(), mDragView.getMeasuredHeight());
                        //params.setMargins(0,0,mWidth - mDragView.getMeasuredWidth() ,mHeight - mDragView.getMeasuredHeight());
                        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        break;
                    case POS_RIGHT_TOP:
                        //mDragView.layout(mWidth - mDragView.getMeasuredWidth(), 0, mWidth, mDragView.getMeasuredHeight());
                        //params.setMargins(mWidth - mDragView.getMeasuredWidth() ,0,0,mHeight - mDragView.getMeasuredHeight());
                        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        break;
                    case POS_LEFT_BOTTOM:
                        //mDragView.layout(0, mHeight - mDragView.getMeasuredHeight(), mDragView.getMeasuredWidth(), mHeight);
                        //params.setMargins(0,mHeight - mDragView.getMeasuredHeight(),mWidth - mDragView.getMeasuredWidth(),0);
                        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        break;
                    case POS_RIGHT_BOTTOM:
                        //mDragView.layout(mWidth - mDragView.getMeasuredWidth(), mHeight - mDragView.getMeasuredHeight(), mWidth, mHeight);
                        //params.setMargins(mWidth - mDragView.getMeasuredWidth(),mHeight - mDragView.getMeasuredHeight(),0,0);
                        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        break;
                    case POS_LEFT_TOP_MARGIN_TITLE:
                        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        params.setMargins(0, mTitleView == null ? 0 : mTitleView.getMeasuredHeight(), 0, 0);
                        break;
                    case POS_RIGHT_TOP_MARGIN_TITLE:
                        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        params.setMargins(0, mTitleView == null ? 0 : mTitleView.getMeasuredHeight(), 0, 0);
                        break;
                }
                mDragView.setLayoutParams(params);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mDragView.startAnimation(mTranslateAnimation);
    }

    private void removeRule(LayoutParams params) {
        params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mViewDragHelper.cancel();
                break;
        }
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && isClickable)
            if (mOnDragLayoutClickListener != null) {
                mOnDragLayoutClickListener.layoutClick();
            }
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    public interface OnDragLayoutClickListener {//点击可移动的view之外的区域

        void layoutClick();
    }

    private OnDragLayoutClickListener mOnDragLayoutClickListener;

    public void setOnDragLayoutClickListener(OnDragLayoutClickListener listener) {
        mOnDragLayoutClickListener = listener;
    }

}
