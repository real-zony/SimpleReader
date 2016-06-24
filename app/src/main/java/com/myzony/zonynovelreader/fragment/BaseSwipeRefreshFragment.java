package com.myzony.zonynovelreader.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.myzony.zonynovelreader.Common.AppContext;
import com.myzony.zonynovelreader.Common.DividerItemDecoration;
import com.myzony.zonynovelreader.Common.FontIconDrawable;
import com.myzony.zonynovelreader.NovelCore.Plug_00ksw;
import com.myzony.zonynovelreader.NovelCore.Plug_Callback_Novel;
import com.myzony.zonynovelreader.R;
import com.myzony.zonynovelreader.UI.BaseActivity;
import com.myzony.zonynovelreader.adapter.BaseStateRecyclerAdapter;
import com.myzony.zonynovelreader.bean.NovelInfo;
import com.myzony.zonynovelreader.widget.TipInfoLayout;

import java.util.List;

import com.getbase.floatingactionbutton.FloatingActionButton;

/**
 * Created by mo199 on 2016/5/28.
 */
public abstract class BaseSwipeRefreshFragment<T> extends Fragment implements SwipeRefreshLayout.OnRefreshListener,Plug_Callback_Novel {
    public static String PLUG_SELECT="plug_select";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TipInfoLayout tipInfoLayout;

    private BaseStateRecyclerAdapter mDataAdapter;
    private LinearLayoutManager linearLayoutManager;
    private int lastVisiableItem;
    private int currentPage;

    private boolean requestingFlag;
    private boolean refreshingFlag;

    private RequestQueue mQueue;
    private StringRequest listRequest;
    private BaseSwipeRefreshFragment baseSwipeRefreshFragment = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentPage = 0;
        requestingFlag = false;
        refreshingFlag = false;

        mQueue = Volley.newRequestQueue(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycle_view_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tipInfoLayout = (TipInfoLayout) view.findViewById(R.id.tip_info);
        tipInfoLayout.setVisibility(View.GONE);

        // 初始化浮动按钮
        final FloatingActionButton actionButton = (FloatingActionButton) view.findViewById(R.id.action_a);
        final FloatingActionsMenu actionsMenu = (FloatingActionsMenu) view.findViewById(R.id.multiple_actions);
        actionButton.setIconDrawable(fixIconFontDrawable(FontIconDrawable.inflate(getActivity(),R.xml.icon_replace_source)));
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppContext.getPlug().getClass() == Plug_00ksw.class){
                    AppContext.setPlug(AppContext.plugs[1]);
                }else{
                    AppContext.setPlug(AppContext.plugs[0]);
                }
                mDataAdapter.clear();
                actionsMenu.toggle();
                hideRecyclerView(true);
                tipInfoLayout.setLoading();
                onRefresh();
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresher);
        if (getActivity() instanceof BaseActivity) {
            swipeRefreshLayout.setColorSchemeColors(((BaseActivity) getActivity()).getColorPrimary());
        }
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        mDataAdapter = getRecyclerAdapter();
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mDataAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        requestData(currentPage + 1, false);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (lastVisiableItem + 1 == mDataAdapter.getItemCount()) {
                        if (checkReLoadingAbility()) {
                            if (swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                                if (listRequest != null) {
                                    listRequest.cancel();
                                }
                            }
                            requestData(currentPage + 1, false);
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisiableItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });

    }

    /**
     * 请求数据
     * @param page 页码
     * @param refreshFlag 刷新标识
     */
    private void requestData(int page, boolean refreshFlag) {
        swipeRefreshLayout.setEnabled(false);
        if (requestingFlag) {
            return;
        }
        requestingFlag = true;

        if (mDataAdapter.getItemCount() == 1) {
            if (refreshFlag) {
                if (swipeRefreshLayout.isRefreshing()) {
                    recyclerView.setVisibility(View.GONE);
                    tipInfoLayout.setVisibility(View.GONE);
                } else {
                    hideRecyclerView(true);
                    tipInfoLayout.setLoading();
                }
            } else {
                hideRecyclerView(true);
                tipInfoLayout.setLoading();
            }
        } else {
            if (!refreshFlag) {
                mDataAdapter.setState(BaseStateRecyclerAdapter.STATE_MORE);
                mDataAdapter.notifyDataSetChanged();
            }
        }

        requestDataFromNetwork(page);
    }

    /**
     * 页面读取成功回调
     * @param list 返回读取成功的list
     */
    @Override
    public void call_Novel(List<NovelInfo> list) {
        loadDataComplete((List<T>) list);
    }

    /**
     * 请求网络数据
     * @param page 页码
     */
    private void requestDataFromNetwork(final int page) {
        AppContext.log("requestDataFromNetwork:" + getItemURL(page));

        listRequest = new StringRequest(getItemURL(page),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppContext.getPlug().bindCB_Novel(baseSwipeRefreshFragment);
                        AppContext.getPlug().getNovelUrl(response,mQueue);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadDataComplete(null);
                    }
                });
        mQueue.add(listRequest);
    }

    @Override
    public void onRefresh() {
        refreshingFlag = true;
        swipeRefreshLayout.setEnabled(false);
        requestData(1, true);
    }

    /**
     * 数据加载成功
     * @param list NovelInfo列表
     */
    public void loadDataComplete(List<T> list) {
        requestingFlag = false;

        if (list == null) {
            Toast.makeText(getActivity(), getString(R.string.request_data_error_hint),Toast.LENGTH_LONG).show();
            if (mDataAdapter.getItemCount() == 1) {
                hideRecyclerView(true);
                tipInfoLayout.setLoadError();
            } else {
                hideRecyclerView(false);
                mDataAdapter.setState(BaseStateRecyclerAdapter.STATE_ERROR);
                mDataAdapter.notifyDataSetChanged();
            }

        } else {
            if (list.size() > 0) {
                hideRecyclerView(false);
                if (currentPage == 0) { // 如果是第一页
                    if (list.size() < AppContext.PAGE_SIZE) { // 如果结果小于10，已加载全部
                        mDataAdapter.setState(BaseStateRecyclerAdapter.STATE_FULL);
                        mDataAdapter.resetDataSet(list);
                    } else {
                        for (int i = 0; i < list.size(); i++) {
                            if (itemCompareTo(mDataAdapter.getDataSet(), list.get(i))) {
                                list.remove(i);
                                i--;
                            }
                        }
                        mDataAdapter.setState(BaseStateRecyclerAdapter.STATE_MORE);
                        mDataAdapter.addDataSetToStart(list);
                    }
                } else {
                    if (mDataAdapter.getItemCount() > AppContext.PAGE_SIZE) {
                        if (list.size() < AppContext.PAGE_SIZE) {
                            mDataAdapter.setState(BaseStateRecyclerAdapter.STATE_FULL);
                            mDataAdapter.addDataSetToEnd(list);
                        } else {
                            mDataAdapter.setState(BaseStateRecyclerAdapter.STATE_MORE);
                            mDataAdapter.addDataSetToEnd(list);
                        }
                    } else {
                        mDataAdapter.resetDataSet(list);
                    }
                }
                if (!refreshingFlag) {
                    currentPage++;
                }
                // 异步存储
            } else {
                if (mDataAdapter.getItemCount() == 1) {
                    hideRecyclerView(true);
                    tipInfoLayout.setEmptyData();
                } else {
                    hideRecyclerView(false);
                    mDataAdapter.setState(BaseStateRecyclerAdapter.STATE_FULL);
                    mDataAdapter.notifyDataSetChanged();
                }
            }
        }
        refreshingFlag = false;
        swipeRefreshLayout.setEnabled(true);

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * 隐藏列表，显示tipinfo
     * @param visiable
     */
    private void hideRecyclerView(boolean visiable) {
        if (visiable) {
            recyclerView.setVisibility(View.GONE);
            tipInfoLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tipInfoLayout.setVisibility(View.GONE);
        }
    }

    private boolean checkReLoadingAbility() {
        return mDataAdapter.getItemCount() >= AppContext.PAGE_SIZE + 1 && (mDataAdapter.getState() == BaseStateRecyclerAdapter.STATE_MORE ||
                mDataAdapter.getState() == BaseStateRecyclerAdapter.STATE_ERROR);
    }

    @Override
    public void onDestroy() {
        if (listRequest != null) {
            listRequest.cancel();
        }
        super.onDestroy();
    }

    /**
     * 解决Drawable资源在floatingbutton内显示错位的问题
     * @param iconDrawable 源资源文件
     * @return 正确的资源文件
     */
    private Drawable fixIconFontDrawable(Drawable iconDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(iconDrawable.getIntrinsicWidth(), iconDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        iconDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        iconDrawable.draw(canvas);
        return new BitmapDrawable(getResources(), bitmap);
    }

    protected abstract boolean itemCompareTo(List<T> list, T item);

    protected abstract BaseStateRecyclerAdapter getRecyclerAdapter();

    protected abstract String getItemURL(int page);
}
