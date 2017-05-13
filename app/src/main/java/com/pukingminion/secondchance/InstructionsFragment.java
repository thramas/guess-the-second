package com.pukingminion.secondchance;

import android.graphics.Typeface;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Samarth on 13/05/17.
 */

public class InstructionsFragment extends Fragment {
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.instructions_layout, null);
        } else {
            if (view.getParent() != null) {
                ((ViewGroup) (view.getParent())).removeView(view);
            }
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    private void initViews() {
        Typeface custom_font = Typeface.createFromAsset(getResources().getAssets(), "fonts/Amatic-Bold.ttf");
        TextView instructionTv = (TextView) view.findViewById(R.id.instructions_det);
        instructionTv.setTypeface(custom_font);
    }

}
