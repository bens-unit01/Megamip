package com.megamip.view;

import com.megamip.voice.R;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;




public class MegamipGui extends FrameLayout {

RelativeLayout megamipGuiView;


public MegamipGui(Context context) {
    super(context);
    init();
    // TODO Auto-generated constructor stub
}

public MegamipGui(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
    // TODO Auto-generated constructor stub
}
public MegamipGui(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    // TODO Auto-generated constructor stub
    init();
}


private void init()
{
   
    LayoutInflater inflator = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    megamipGuiView = (RelativeLayout) inflator.inflate(R.layout.activity_main, this,false);
    setWillNotDraw(false);
    addView(megamipGuiView);
    

}






}
