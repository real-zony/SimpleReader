package com.myzony.zonynovelreader.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.myzony.zonynovelreader.Common.AppContext;
import com.myzony.zonynovelreader.Common.DoubleClickExitHelper;
import com.myzony.zonynovelreader.Common.FontIconDrawable;
import com.myzony.zonynovelreader.R;
import com.myzony.zonynovelreader.fragment.BaseSwipeRefreshFragment;
import com.myzony.zonynovelreader.fragment.MyNovelInfoTabFragment;

import butterknife.InjectView;

/**
 * Created by mo199 on 2016/5/26.
 */
public class MainActivity extends BaseActivity{

    private String CURRENT_NAV_VIEW_MENU_ITEM = "current_nav_view_menu_item";

    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @InjectView(R.id.nv_menu)
    NavigationView mNavigationView;

    private int currentNavViewMenuItem; // 当前抽屉菜单条目
    private boolean rightHandOn; // 右手模式是否开启
    private DoubleClickExitHelper doubleClickExitHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            currentNavViewMenuItem = savedInstanceState.getInt(CURRENT_NAV_VIEW_MENU_ITEM);
        } else {
            currentNavViewMenuItem = R.id.menu_item_explore;
        }

        doubleClickExitHelper = new DoubleClickExitHelper(this);

        setupDrawerContent(mNavigationView);
        initMainContent(currentNavViewMenuItem);

        sharedPreferences = getSharedPreferences(BaseSwipeRefreshFragment.PLUG_SELECT,0);
        int flags = sharedPreferences.getInt(BaseSwipeRefreshFragment.PLUG_SELECT,0);
        AppContext.flags = flags;
        AppContext.setPlug(AppContext.plugs[flags]);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_main;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存当前抽屉选择条目
        outState.putInt(CURRENT_NAV_VIEW_MENU_ITEM, currentNavViewMenuItem);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (rightHandOn != sharedPreferences.getBoolean(getString(R.string.right_hand_mode_key), false)) {
            rightHandOn = !rightHandOn;
            if (rightHandOn) {
                setNavigationViewGravity(Gravity.END);
            } else {
                setNavigationViewGravity(Gravity.START);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 使用menu XML文件创建菜单
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // 设置菜单相应
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_about:
                AlertDialog.Builder builder = generateAlterDialog();
                builder.setTitle("关于").setMessage(R.string.about_dialog_message)
                        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.show();
                return true;
            case R.id.action_search:
                startActivity(new Intent(this, SearchActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 配置抽屉内容与响应事件
    private void setupDrawerContent(NavigationView navigationView) {
        Menu menu = navigationView.getMenu();
        MenuItem currentMenuItem = menu.findItem(currentNavViewMenuItem);
        currentMenuItem.setChecked(true);
        currentMenuItem.setIcon(FontIconDrawable.inflate(this,R.xml.icon_novel_my_xml));

        toolbar.setTitle(currentMenuItem.getTitle());
        // 取消掉右上角的返回箭头
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        rightHandOn = sharedPreferences.getBoolean(getString(R.string.right_hand_mode_key), false);
        if (rightHandOn) {
            setNavigationViewGravity(Gravity.END);
        }

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        int menuItemId = menuItem.getItemId();
                        if (menuItemId == R.id.menu_item_settings) {
                            Intent intent3 = new Intent(MainActivity.this, SettingActivity.class);
                            startActivity(intent3);
                            return true;
                        }

                        mDrawerLayout.closeDrawers();
                        if (currentNavViewMenuItem != menuItemId) {
                            toolbar.setTitle(menuItem.getTitle());
                            menuItem.setChecked(true);
                            initMainContent(menuItemId);
                            currentNavViewMenuItem = menuItem.getItemId();
                        }
                        return true;
                    }
                });

    }

    // 初始化抽屉条目对应的content fragment
    private void initMainContent(int navViewMenuItem)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(navViewMenuItem == R.id.menu_item_explore){
            fragmentTransaction.replace(R.id.main_content_layout,new MyNovelInfoTabFragment(),null).commit();
        } else {
        }
    }

    // 设置抽屉gravity
    private void setNavigationViewGravity(int gravity) {
        DrawerLayout.LayoutParams layoutParams = (DrawerLayout.LayoutParams) mNavigationView.getLayoutParams();
        layoutParams.gravity = gravity;
        mNavigationView.setLayoutParams(layoutParams);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
                mDrawerLayout.closeDrawers();
                return true;
            }
            return doubleClickExitHelper.onKeyDown(keyCode, event);
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
