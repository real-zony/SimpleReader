package com.myzony.zonynovelreader.fragment;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.myzony.zonynovelreader.R;

/**
 * Created by mo199 on 2016/5/27.
 */
public class BaseFragment extends Fragment {
    public AlertDialog.Builder generateAlertDialog(){
        int dialogTheme = R.style.BlueDialogTheme;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), dialogTheme);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder;
    }
}
