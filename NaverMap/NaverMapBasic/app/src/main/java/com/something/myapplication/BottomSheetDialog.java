package com.something.myapplication;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by charlie on 2017. 11. 22
 */
public class BottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {
    public static BottomSheetDialog getInstance() {
        return new BottomSheetDialog();
    }
    private LinearLayout lname;
    private LinearLayout Address;
    private LinearLayout Number;
    public TextView NameText;
    public TextView AddText;
    public TextView distText;
    public TextView numberText;
    int pid;
    String nname;
    String addr;
    String numb;
    int dist;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog, container, false);

        return view;
    }
    @Override
    public void onClick(View view) {
        dismiss();
    }
}
