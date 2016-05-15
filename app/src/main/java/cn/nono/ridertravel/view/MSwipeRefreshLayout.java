package cn.nono.ridertravel.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.widget.AbsListView;

/**
 * Created by Administrator on 2016/5/15.
 */
//解决SwipeRefreshLayout 下只能允许挂载多个View而导致的 冲突。
public class MSwipeRefreshLayout extends SwipeRefreshLayout {

    //下拉刷新基准的listView
    private AbsListView mScrollableChild = null;

    public void setBaseListView(AbsListView absListView) {
        mScrollableChild = absListView;
    }

    public MSwipeRefreshLayout(Context context) {
        super(context);
    }

    public MSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canChildScrollUp() {

        if(null != mScrollableChild) {
            if (android.os.Build.VERSION.SDK_INT < 14) {
                if (mScrollableChild instanceof AbsListView) {
                    final AbsListView absListView = (AbsListView) mScrollableChild;
                    return absListView.getChildCount() > 0
                            && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                            .getTop() < absListView.getPaddingTop());
                } else {
                    return mScrollableChild.getScrollY() > 0;
                }
            } else {
                return ViewCompat.canScrollVertically(mScrollableChild, -1);
            }
        }

        return super.canChildScrollUp();
    }
}
