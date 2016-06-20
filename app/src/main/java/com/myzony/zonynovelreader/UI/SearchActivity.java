package com.myzony.zonynovelreader.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.myzony.zonynovelreader.R;
import com.myzony.zonynovelreader.fragment.SearchFragment;

import butterknife.InjectView;

/**
 * Created by mo199 on 2016/6/6.
 */
public class SearchActivity extends BaseActivity {

    @InjectView(R.id.custom_searchview)
    EditText customSearchview;

    private InputMethodManager inputMethodManager;
    private FragmentManager fragmentManager;

    @Override
    protected int getLayoutView() {
        return R.layout.search_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        fragmentManager = getSupportFragmentManager();

        customSearchview.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    if(!TextUtils.isEmpty(customSearchview.getText())){
                        fragmentManager.beginTransaction().replace(R.id.main_content, SearchFragment.
                                newInstance(customSearchview.getText().toString()), null).commit();
                        inputMethodManager.hideSoftInputFromWindow(customSearchview.getWindowToken(), 0);
                    }else {
                        AlertDialog.Builder builder = generateAlterDialog();
                        builder.setTitle("搜索字符串不能为空，请重新输入！").setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.show();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        toolbar.setTitle("搜小说");
    }
}
