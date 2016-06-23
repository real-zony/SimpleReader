package com.myzony.zonynovelreader.adapter;

import android.content.Context;
import android.view.View;

import com.myzony.zonynovelreader.R;

/**
 * Created by mo199 on 2016/5/28.
 */
public abstract class BaseStateRecyclerAdapter<T> extends BaseRecyclerAdapter<T> {
    /**
     * 错误标志
     */
    public static final int STATE_ERROR = -1;
    /**
     * 空标志
     */
    public static final int STATE_EMPTY = 0;
    /**
     * 正在加载数据
     */
    public static final int STATE_MORE = 1;
    /**
     * 所有数据条目已经加载完成
     */
    public static final int STATE_FULL = 2;

    public static int TYPE_ITEM = 0;
    public static int TYPE_FOOT = 1;

    private int state;

    public BaseStateRecyclerAdapter(Context context) {
        super(context);
        state = STATE_FULL;
    }

    // 判断条目是否在最后
    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOT;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    /**
     * 获得当前状态
     * @return 状态值
     */
    public int getState() {
        return state;
    }

    /**
     * 设置状态
     * @param state 状态值
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * 获得状态描述信息
     * @return 描述信息
     */
    protected String getStateDescription() {
        String stateDescription;
        switch (state) {
            case STATE_ERROR:
                stateDescription = context.getString(R.string.foot_state_error);
                break;
            case STATE_EMPTY:
                stateDescription = context.getString(R.string.foot_state_empty);
                break;
            case STATE_MORE:
                stateDescription = context.getString(R.string.foot_state_more);
                break;
            case STATE_FULL:
                stateDescription = context.getString(R.string.foot_state_full);
                break;
            default:
                stateDescription = "unknow error";
                break;
        }
        return stateDescription;
    }

    /**
     * 获得加载小圆圈进度条显示状态
     * @return 显示OR隐藏状态
     */
    protected int getProgressBarVisiable() {
        if (state == STATE_MORE) {
            return View.VISIBLE;
        } else {
            return View.GONE;
        }
    }
}