package com.hss01248.unscroll;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.hss01248.lib.Refreshable;
import com.hss01248.lib.SuperRcvAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/8/29.
 */
public class UnScrolledRecycleView extends RecyclerView  implements Refreshable{
    public UnScrolledRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public UnScrolledRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    float startX;
    float startY;

    SuperRcvAdapter mSuperAdapter;






    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int actionMasked = ev.getActionMasked() & MotionEvent.ACTION_MASK;

        if (actionMasked == MotionEvent.ACTION_DOWN) {
            // 记录手指按下时的位置
            startX = ev.getX();
            startY = ev.getY();
            return super.dispatchTouchEvent(ev);
        }

        if (actionMasked == MotionEvent.ACTION_MOVE) {
            // 最关键的地方，忽略MOVE 事件
            // ListView onTouch获取不到MOVE事件所以不会发生滚动处理
            return true;
        }

        // 手指抬起时
        if (actionMasked == MotionEvent.ACTION_UP
                || actionMasked == MotionEvent.ACTION_CANCEL) {
            // 无需判断点击事件还是长按事件,只需要排除滚动事件就可以.

            float dx = Math.abs(ev.getX() - startX);
            float dy = Math.abs(ev.getY() - startY) ;

            if (dy < ViewConfiguration.get(getContext()).getScaledDoubleTapSlop()){//屏蔽y轴方向上的滑动
                super.dispatchTouchEvent(ev);
            }else {
                // 如果手指y轴方向上已经划出一段距离,那么让父类去处理
                setPressed(false);
                invalidate();
                return true;
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof SuperRcvAdapter){
            mSuperAdapter = (SuperRcvAdapter) adapter;
        }
    }

    @Override
    public void refresh(List newData) {
        mSuperAdapter.refresh(newData);
        measureListViewHeight(this);

    }

    @Override
    public void addAll(List newData) {
        mSuperAdapter.addAll(newData);
        measureListViewHeight(this);

    }

    @Override
    public void clear() {
        mSuperAdapter.clear();
        measureListViewHeight(this);

    }

    @Override
    public void delete(int position) {
        mSuperAdapter.delete(position);
        measureListViewHeight(this);
    }

    @Override
    public void add(Object object) {
        mSuperAdapter.add(object);
        measureListViewHeight(this);
    }



   // todo


    protected static void measureListViewHeight(final RecyclerView listView) {
        final Adapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            return;
        }
        listView.post(new Runnable() {
            @Override
            public void run() {
               /* int totalHeight = 0;
                int count = listAdapter.getItemCount();
                //TODO 这里可以去获取每一列最高的一个
                View listItem = listAdapter.getView(0, null, listView);

                listItem.measure(0, 0);
                if (listView instanceof GridView) {
                    int columns = ((GridView) listView).getNumColumns();
                    int rows = count % columns != 0 ? 1 : 0;
                    rows += count / columns;
                    totalHeight += listItem.getMeasuredHeight() * rows;
                } else if (listView instanceof ListView) {
                    for (int i = 0; i < count; i++) {
                        listItem = listAdapter.getView(i, null, listView);
                        listItem.measure(0, 0);
                        totalHeight += listItem.getMeasuredHeight() + ((ListView) listView).getDividerHeight() * (listAdapter.getCount() - 1);
                    }
                }
                ViewGroup.LayoutParams params = listView.getLayoutParams();

                params.height = totalHeight;

                listView.setLayoutParams(params);*/
            }
        });
    }
}
