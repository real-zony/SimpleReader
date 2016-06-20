package com.myzony.zonynovelreader.UI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.myzony.zonynovelreader.R;
import com.myzony.zonynovelreader.Common.AppContext;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by mo199 on 2016/5/26.
 */
public abstract class BaseActivity extends AppCompatActivity{
    public static final String PREFERENCE_FILE_NAME = "mygitosc_settings";
    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor editor;

    @InjectView(R.id.toolbar)
    protected Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(AppContext.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        setContentView(getLayoutView());
        ButterKnife.inject(this);

        sharedPreferences = getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        initToolbar();
    }

    protected abstract int getLayoutView();

    protected void initToolbar() {
        toolbar.setTitle("");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public int getColorPrimary() {
        return getResources().getColor(R.color.blue);
    }

    // 生成指定主题的对话框
    public AlertDialog.Builder generateAlterDialog() {
        int dialogTheme;
        dialogTheme = R.style.BlueDialogTheme;

        AlertDialog.Builder builder = new AlertDialog.Builder(this, dialogTheme);
        return builder;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
