package com.myzony.zonynovelreader.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.myzony.zonynovelreader.R;
import com.pnikosis.materialishprogress.ProgressWheel;

/**
 * Created by mo199 on 2016/5/28.
 * 数据加载圆圈
 */
public class TipInfoLayout extends FrameLayout {
    private ProgressWheel mPbProgressBar;
    private ImageView mTvTipState;
    private TextView mTvTipMsg;

    private Context context;

    public TipInfoLayout(Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    public TipInfoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
    }

    public TipInfoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.tip_info_layout, null, false);
        mPbProgressBar = (ProgressWheel) view.findViewById(R.id.tv_tip_loading);
        mTvTipState = (ImageView) view.findViewById(R.id.tv_tip_state);
        mTvTipMsg = (TextView) view.findViewById(R.id.tv_tip_msg);
        setLoading();
        addView(view);
    }

    /**
     * 设置加载状态
     */
    public void setLoading() {
        this.mPbProgressBar.setVisibility(View.VISIBLE);
        this.mTvTipState.setVisibility(View.GONE);
        this.mTvTipMsg.setText(context.getString(R.string.tip_loading));
    }

    /**
     * 加载错误
     * @param message 错误信息
     */
    public void setLoadError(String message){
        setLoadError();
        this.mTvTipMsg.setText(message);
    }

    /**
     * 加载错误
     */
    public void setLoadError() {
        this.mPbProgressBar.setVisibility(View.GONE);
        this.mTvTipState.setVisibility(View.VISIBLE);
        this.mTvTipState.setImageResource(R.drawable.page_icon_loaderror);
        this.mTvTipMsg.setText(context.getString(R.string.tip_load_error));
    }

    /**
     * 没有获得数据
     */
    public void setEmptyData() {
        this.setVisibility(VISIBLE);
        this.mPbProgressBar.setVisibility(View.GONE);
        this.mTvTipState.setVisibility(View.VISIBLE);
        this.mTvTipState.setImageResource(R.drawable.page_icon_empty);
        this.mTvTipMsg.setText(context.getString(R.string.tip_load_empty));
    }
}