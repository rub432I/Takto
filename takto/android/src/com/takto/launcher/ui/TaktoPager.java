package com.takto.launcher.ui;

import android.content.Context;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * TaktoPager: Robust zero-dependency horizontal matrix pager.
 * Implements proper touch interception, touch slop thresholding,
 * velocity tracking, and smooth scroller physics.
 */
public class TaktoPager extends ViewGroup {

    public interface OnPageChangeListener {
        void onPageSelected(int pageIndex);
    }

    private final Scroller scroller;
    private VelocityTracker velocityTracker;
    private final int touchSlop;
    private final int minFlingVelocity;

    private float downX;
    private float downY;
    private float lastX;
    private boolean isBeingDragged = false;
    private int currentPage = 1;
    private OnPageChangeListener pageChangeListener;

    public TaktoPager(Context context) {
        super(context);
        this.scroller = new Scroller(context);
        ViewConfiguration vc = ViewConfiguration.get(context);
        this.touchSlop = vc.getScaledTouchSlop();
        this.minFlingVelocity = vc.getScaledMinimumFlingVelocity() * 2;
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.pageChangeListener = listener;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            child.measure(
                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            );
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int height = b - t;
        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            child.layout(i * width, 0, (i + 1) * width, height);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();

        if (action == MotionEvent.ACTION_MOVE && isBeingDragged) {
            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                lastX = downX;
                isBeingDragged = !scroller.isFinished();
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain();
                } else {
                    velocityTracker.clear();
                }
                velocityTracker.addMovement(ev);
                break;

            case MotionEvent.ACTION_MOVE:
                float x = ev.getX();
                float y = ev.getY();
                float dx = Math.abs(x - downX);
                float dy = Math.abs(y - downY);

                if (dx > touchSlop && dx > dy * 1.2f) {
                    isBeingDragged = true;
                    lastX = x;
                    if (getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isBeingDragged = false;
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
                break;
        }

        return isBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(ev);

        int action = ev.getActionMasked();
        int width = Math.max(1, getWidth());
        int count = getChildCount();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                downX = ev.getX();
                lastX = downX;
                return true;

            case MotionEvent.ACTION_MOVE:
                float x = ev.getX();
                float deltaX = lastX - x;
                lastX = x;

                float newScrollX = getScrollX() + deltaX;
                float maxScrollX = (count - 1) * width;
                if (newScrollX < 0) {
                    newScrollX = 0;
                } else if (newScrollX > maxScrollX) {
                    newScrollX = maxScrollX;
                }
                scrollTo((int) newScrollX, 0);
                return true;

            case MotionEvent.ACTION_UP:
                velocityTracker.computeCurrentVelocity(1000);
                float xVelocity = velocityTracker.getXVelocity();
                int targetPage = currentPage;

                if (xVelocity > minFlingVelocity) {
                    targetPage = Math.max(0, currentPage - 1);
                } else if (xVelocity < -minFlingVelocity) {
                    targetPage = Math.min(count - 1, currentPage + 1);
                } else {
                    targetPage = Math.round((float) getScrollX() / width);
                    targetPage = Math.max(0, Math.min(count - 1, targetPage));
                }

                snapToPage(targetPage, true);
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
                return true;

            case MotionEvent.ACTION_CANCEL:
                snapToPage(currentPage, true);
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
                return true;
        }

        return super.onTouchEvent(ev);
    }

    public void snapToPage(int page, boolean smooth) {
        int count = getChildCount();
        if (count == 0) return;
        currentPage = Math.max(0, Math.min(count - 1, page));
        int targetX = currentPage * getWidth();
        int dx = targetX - getScrollX();

        if (smooth) {
            scroller.startScroll(getScrollX(), 0, dx, 0, 250);
            postInvalidateOnAnimation();
        } else {
            scrollTo(targetX, 0);
            if (pageChangeListener != null) {
                pageChangeListener.onPageSelected(currentPage);
            }
        }
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidateOnAnimation();
        } else {
            int width = Math.max(1, getWidth());
            int settlingPage = Math.round((float) getScrollX() / width);
            if (settlingPage != currentPage) {
                currentPage = settlingPage;
                if (pageChangeListener != null) {
                    pageChangeListener.onPageSelected(currentPage);
                }
            }
        }
    }
}
