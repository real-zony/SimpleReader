package com.myzony.zonynovelreader.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.myzony.zonynovelreader.Common.BitmapCache;
import com.myzony.zonynovelreader.R;
import com.myzony.zonynovelreader.UI.ChapterActivity;
import com.myzony.zonynovelreader.bean.NovelInfo;

/**
 * Created by mo199 on 2016/5/30.
 */
public class NovelInfoAdapter extends BaseStateRecyclerAdapter<NovelInfo> {
    private ImageLoader mImageLoader;
    private RequestQueue mQueue;

    /**
     * 章节条目点击
     */
    private View.OnClickListener novelInfoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            NovelInfo novelInfo = (NovelInfo)v.getTag();
            Intent intent = new Intent(context, ChapterActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ChapterActivity.VIEW_CHAPTER_INFO,novelInfo);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }
    };

    public NovelInfoAdapter(Context context) {
        super(context);
        mQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mQueue, new BitmapCache());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == BaseStateRecyclerAdapter.TYPE_ITEM) {
            return new NovelInfoViewHolder(LayoutInflater.from(context).inflate(R.layout.recycle_novelinfo_item, parent, false));
        } else {
            return new FootViewHolder(LayoutInflater.from(context).inflate(R.layout.foot_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof NovelInfoViewHolder){
            NovelInfoViewHolder novelInfoViewHolder = (NovelInfoViewHolder) holder;

            NovelInfo novelInfo = mData.get(position);

            novelInfoViewHolder.tv_title.setText(novelInfo.getName());
            novelInfoViewHolder.tv_update.setText("更新时间:" + novelInfo.getUpdate());
            novelInfoViewHolder.tv_author.setText("作者:" + novelInfo.getAuthor());
            novelInfoViewHolder.tv_descript.setText(novelInfo.getDescript());
            // 加载小说封面
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(novelInfoViewHolder.iv_novel,R.drawable.mini_avatar,R.drawable.mini_avatar);
            mImageLoader.get(novelInfo.getImageUrl(),listener);
            mQueue.getCache().clear();
            novelInfoViewHolder.itemView.setTag(novelInfo);
            // 设置条目响应时间
            novelInfoViewHolder.itemView.setOnClickListener(novelInfoClickListener);
        }else if(holder instanceof FootViewHolder){
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            footViewHolder.foot_hint.setText(getStateDescription());
            footViewHolder.foot_progressBar.setVisibility(getProgressBarVisiable());
        }
    }

    class NovelInfoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView iv_novel;

        TextView tv_title;
        TextView tv_author;
        TextView tv_update;
        TextView tv_descript;

        public NovelInfoViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_author = (TextView) itemView.findViewById(R.id.tv_author);
            tv_update = (TextView) itemView.findViewById(R.id.tv_update);
            tv_descript = (TextView) itemView.findViewById(R.id.tv_description);
            iv_novel = (ImageView) itemView.findViewById(R.id.iv_novel);
        }

        @Override
        public void onClick(View v) {
            //Toast.makeText(context,"测试文本",Toast.LENGTH_LONG).show();
        }
    }
}
