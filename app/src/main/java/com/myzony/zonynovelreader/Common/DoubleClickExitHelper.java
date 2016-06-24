package com.myzony.zonynovelreader.Common;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.Toast;

import com.myzony.zonynovelreader.NovelCore.Plug_00ksw;
import com.myzony.zonynovelreader.fragment.BaseSwipeRefreshFragment;

/**
 * Created by mo199 on 2016/6/13.
 */
public class DoubleClickExitHelper {
    private Activity context;
    private boolean firstKeyDown = true;

    private Handler handler;

    private Toast exitToast;

    private Runnable onBackTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if(exitToast != null){
                exitToast.cancel();
            }
            firstKeyDown = true;
        }
    };

    public DoubleClickExitHelper(Activity context){
        this.context = context;
        handler = new Handler();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode != KeyEvent.KEYCODE_BACK){
            return false;
        }
        if(firstKeyDown){
            if(exitToast == null){
                exitToast = Toast.makeText(context, "再按一次返回键退出应用", Toast.LENGTH_SHORT);
            }
            exitToast.show();
            handler.postDelayed(onBackTimeRunnable,2000);
            firstKeyDown = false;
        }else{
            handler.removeCallbacks(onBackTimeRunnable);
            if(exitToast != null){
                exitToast.cancel();
            }

            SharedPreferences sharedPreferences = context.getSharedPreferences(BaseSwipeRefreshFragment.PLUG_SELECT,0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(AppContext.getPlug().getClass() == Plug_00ksw.class){
                editor.putInt(BaseSwipeRefreshFragment.PLUG_SELECT,0);
            }else{
                editor.putInt(BaseSwipeRefreshFragment.PLUG_SELECT,1);
            }
            editor.commit();

            context.finish();
        }
        return true;
    }
}
