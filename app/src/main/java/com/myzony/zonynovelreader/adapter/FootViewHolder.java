package com.myzony.zonynovelreader.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myzony.zonynovelreader.R;

/**
 * Created by mo199 on 2016/5/31.
 */
public class FootViewHolder extends RecyclerView.ViewHolder {
    TextView foot_hint;
    ProgressBar foot_progressBar;

    public FootViewHolder(View itemView) {
        super(itemView);
        foot_hint = (TextView) itemView.findViewById(R.id.foot_hint);
        foot_progressBar = (ProgressBar) itemView.findViewById(R.id.progressbar);
    }
}