package com.hss01248.unscroll;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.hss01248.lib.Refreshable;
import com.hss01248.lib.SuperLvAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/7/13 0013.
 */
public class UnScrolledListView extends ListView implements Refreshable {

    private int mPosition;

    protected SuperLvAdapter mSuperAdapter;

    public UnScrolledListView(Context context) {
        super(context);
    }

    public UnScrolledListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UnScrolledListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int actionMasked = ev.getActionMasked() & MotionEvent.ACTION_MASK;

        if (actionMasked == MotionEvent.ACTION_DOWN) {
            // 记录手指按下时的位置
            mPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
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
            // 手指按下与抬起都在同一个视图内，交给父控件处理，这是一个点击事件
            if (pointToPosition((int) ev.getX(), (int) ev.getY()) == mPosition) {
                super.dispatchTouchEvent(ev);
            } else {
                // 如果手指已经移出按下时的Item，说明是滚动行为，清理Item pressed状态
                setPressed(false);
                invalidate();
                return true;
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof  SuperLvAdapter){
            mSuperAdapter = (SuperLvAdapter) adapter;
        }
        measureListViewHeight(this);
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


    protected static void measureListViewHeight(final AbsListView listView) {
        final ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            return;
        }
        listView.post(new Runnable() {
            @Override
            public void run() {
                int totalHeight = 0;
                int count = listAdapter.getCount();
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

                listView.setLayoutParams(params);
            }
        });
    }
}
