package com.myzony.zonynovelreader.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.text.TextUtils;
import android.widget.Toast;

import com.myzony.zonynovelreader.Common.AppContext;
import com.myzony.zonynovelreader.R;
import com.myzony.zonynovelreader.UI.SettingActivity;
import com.jenzz.materialpreference.Preference;
import com.jenzz.materialpreference.SwitchPreference;
import com.myzony.zonynovelreader.cache.DataCleanManager;
import com.myzony.zonynovelreader.utils.FileUtils;

import java.io.File;

/**
 * Created by mo199 on 2016/5/27.
 */
public class SettingsFragment extends PreferenceFragment {
    private Context context;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean rightHandMode;

    private SwitchPreference rightHandModeSwitch;
    private Preference clearCachePreference;
    private Preference feedbackPreference;

    public SettingsFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化偏好设置资源
        addPreferencesFromResource(R.xml.prefs);

        context = getActivity();
        sharedPreferences = context.getSharedPreferences(SettingActivity.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        rightHandMode = sharedPreferences.getBoolean(getString(R.string.right_hand_mode_key), false);
        rightHandModeSwitch = (SwitchPreference) findPreference(getString(R.string.right_hand_mode_key));
        rightHandModeSwitch.setChecked(rightHandMode);

        feedbackPreference = (Preference) findPreference(getString(R.string.advice_feedback_key));

        clearCachePreference = (Preference) findPreference(getString(R.string.clear_cache_data_key));
        initCachePreference();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, android.preference.Preference preference) {
        if(preference == null){
            return super.onPreferenceTreeClick(preferenceScreen,preference);
        }

        String key = preference.getKey();
        if(TextUtils.equals(key,getString(R.string.clear_cache_data_key))){
            clearAppCache();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    /**
     * 初始化缓存首选项
     */
    private void initCachePreference(){
        long fileSize = 0;
        String cacheSize = "0KB";
        File filesDir = getActivity().getFilesDir();
        File cacheDir = getActivity().getCacheDir();

        fileSize += FileUtils.getDirSize(filesDir);
        fileSize += FileUtils.getDirSize(cacheDir);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (AppContext.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            File externalCacheDir = getActivity().getExternalCacheDir();
            fileSize += FileUtils.getDirSize(externalCacheDir);
        }
        if (fileSize > 0)
            cacheSize = FileUtils.formatFileSize(fileSize);
        clearCachePreference.setSummary(cacheSize);
    }
    private void clearAppCache(){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    clearCachePreference.setSummary("0KB");
                    Toast.makeText(getActivity(),getString(R.string.clear_cache_success),Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getActivity(),getString(R.string.clear_cache_fail),Toast.LENGTH_LONG).show();
                }
            }
        };

        new Thread(){
            @Override
            public void run() {
                Message msg = new Message();
                try{
                    DataCleanManager.cleanDatabases(getActivity());
                    DataCleanManager.cleanInternalCache(getActivity());
                    // 2.2版本才有将应用缓存转移到sd卡的功能
                    if (AppContext.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
                        DataCleanManager.cleanCustomCache(getActivity().getExternalCacheDir());
                    }
                    msg.what =1;
                }catch (Exception exp){
                    exp.printStackTrace();
                    msg.what = -1;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }
}
