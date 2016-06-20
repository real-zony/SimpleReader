package com.myzony.zonynovelreader.Common;

import android.app.Activity;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.Toast;

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
            context.finish();
        }
        return true;
    }
}
