package com.myzony.zonynovelreader.UI;

import android.os.Bundle;

import com.myzony.zonynovelreader.R;
import com.myzony.zonynovelreader.fragment.SettingsFragment;

/**
 * Created by mo199 on 2016/5/26.
 */
public class SettingActivity extends BaseActivity {

    @Override
    protected int getLayoutView() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        toolbar.setTitle(getString(R.string.menu_item_settings));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(R.id.settings_fragment, new SettingsFragment(), null).commit();
    }
}
