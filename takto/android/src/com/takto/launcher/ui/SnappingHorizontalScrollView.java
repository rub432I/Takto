package com.takto.launcher.ui;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * SnappingHorizontalScrollView: Native horizontal pager with crisp page-snapping physics.
 * Replaces standard free-scrolling with swipe-to-snap behavior across matrix pages.
 */
public class SnappingHorizontalScrollView extends HorizontalScrollView {

    public interface OnPageChangeListener {
        void onPageSelected(int pageIndex);
    }

    private int pageWidth = 1080;
    private int pageCount = 3;
    private int currentPage = 1;
    private float startX = 0f;
    private OnPageChangeListener pageChangeListener;

    public SnappingHorizontalScrollView(Context context) {
        super(context);
        setHorizontalScrollBarEnabled(false);
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    public void setPageMetrics(int width, int count) {
        this.pageWidth = Math.max(1, width);
        this.pageCount = Math.max(1, count);
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.pageChangeListener = listener;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void snapToPage(int targetPage, boolean smooth) {
        currentPage = Math.max(0, Math.min(pageCount - 1, targetPage));
        int targetX = currentPage * pageWidth;
        if (smooth) {
            smoothScrollTo(targetX, 0);
        } else {
            scrollTo(targetX, 0);
        }
        if (pageChangeListener != null) {
            pageChangeListener.onPageSelected(currentPage);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = ev.getX();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                float deltaX = ev.getX() - startX;
                float threshold = pageWidth * 0.15f;
                int targetPage = currentPage;

                if (deltaX < -threshold) {
                    targetPage = Math.min(pageCount - 1, currentPage + 1);
                } else if (deltaX > threshold) {
                    targetPage = Math.max(0, currentPage - 1);
                } else {
                    targetPage = Math.round((float) getScrollX() / pageWidth);
                    targetPage = Math.max(0, Math.min(pageCount - 1, targetPage));
                }

                snapToPage(targetPage, true);
                return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void fling(int velocityX) {
        // Intercept fling velocity to snap cleanly to adjacent page rather than free-scrolling
        int targetPage = currentPage;
        if (velocityX > 1000) {
            targetPage = Math.min(pageCount - 1, currentPage + 1);
        } else if (velocityX < -1000) {
            targetPage = Math.max(0, currentPage - 1);
        } else {
            targetPage = Math.round((float) getScrollX() / pageWidth);
            targetPage = Math.max(0, Math.min(pageCount - 1, targetPage));
        }
        snapToPage(targetPage, true);
    }
}
