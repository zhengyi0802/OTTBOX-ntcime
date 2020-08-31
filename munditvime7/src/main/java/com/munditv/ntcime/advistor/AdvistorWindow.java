package com.munditv.ntcime.advistor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.munditv.ntcime.R;

public class AdvistorWindow {

    private final static String TAG = "AdvistorWindow";

    private final static int ADVISTOR_WINDOW_X = -800;
    private final static int ADVISTOR_WINDOW_Y = 200;
    private final static int ADVISTOR_WINDOW_WIDTH = 410;
    private final static int ADVISTOR_WINDOW_HEIGHT = 512;
    private final static int ADVISTOR_WINDOW_GRAVITY = Gravity.TOP;
    private final static int ADVISTOR_WINDOW_FORNAT = PixelFormat.TRANSLUCENT;
    private final static int ADVISTOR_WINDOW_TYPE = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

    private final static int TEXTBOX_WINDOW_X = -200;
    private final static int TEXTBOX_WINDOW_Y = 100;
    private final static int TEXTBOX_WINDOW_WIDTH = 898;
    private final static int TEXTBOX_WINDOW_HEIGHT = 740;
    private final static int TEXTBOX_WINDOW_GRAVITY = Gravity.TOP;
    private final static int TEXTBOX_WINDOW_FORNAT = PixelFormat.TRANSLUCENT;
    private final static int TEXTBOX_WINDOW_TYPE = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

    private Context                     mContext;
    private WindowManager               mWindowManager = null;
    private WindowManager.LayoutParams  mParams = null;
    private WindowManager.LayoutParams  mParams2 = null;
    private FrameLayout                 mADVISTORFrameLayout = null;
    private FrameLayout                 mTEXTBOXFrameLayout = null;
    private LayoutInflater              mLayoutinflater = null;
    private View                        mADVISTORView = null;
    private View                        mTEXTBOXView = null;
    private TextView                    mTextView = null;
    private ImageView                   mImageView = null;
    private int                         steps = 0;
    private Handler                     mHandler = null;

    private int index = 1;

    public AdvistorWindow(Context context) {
        Log.d(TAG,"Constructor()");
        mContext = context;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mLayoutinflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mADVISTORView = mLayoutinflater.inflate(R.layout.advistor,null);
        mADVISTORFrameLayout = new FrameLayout(mContext);
        mTEXTBOXView = mLayoutinflater.inflate(R.layout.textbox, null);
        mTEXTBOXFrameLayout = new FrameLayout(mContext);
        mParams = new WindowManager.LayoutParams();
        mParams2 = new WindowManager.LayoutParams();
        initialize();
    }

    private void initialize() {
        Log.d(TAG,"initialize()");
        try {
            mParams.width = ADVISTOR_WINDOW_WIDTH;
            mParams.height = ADVISTOR_WINDOW_HEIGHT;
            mParams.gravity = ADVISTOR_WINDOW_GRAVITY;
            mParams.x = ADVISTOR_WINDOW_X;
            mParams.y = ADVISTOR_WINDOW_Y;
            mParams.format = ADVISTOR_WINDOW_FORNAT;
            mParams.type = ADVISTOR_WINDOW_TYPE;
            mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            mParams.token = null;
            mADVISTORFrameLayout.setLayoutParams(mParams);
            mADVISTORFrameLayout.addView(mADVISTORView);
            mWindowManager.addView(mADVISTORFrameLayout, mParams);
            mImageView = (ImageView) mADVISTORView.findViewById(R.id.image);
            mParams2.width = TEXTBOX_WINDOW_WIDTH;
            mParams2.height = TEXTBOX_WINDOW_HEIGHT;
            mParams2.gravity = TEXTBOX_WINDOW_GRAVITY;
            mParams2.x = TEXTBOX_WINDOW_X;
            mParams2.y = TEXTBOX_WINDOW_Y;
            mParams2.format = TEXTBOX_WINDOW_FORNAT;
            mParams2.type = TEXTBOX_WINDOW_TYPE;
            mParams2.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            mParams2.token = null;
            mTextView = (TextView) mTEXTBOXView.findViewById(R.id.textView);
            mTextView.setBackgroundResource(R.drawable.dialog);
            mTextView.setTextColor(Color.BLUE);
            mTextView.setText(R.string.talk_content_1);
            mTEXTBOXFrameLayout.setLayoutParams(mParams2);
            mTEXTBOXFrameLayout.addView(mTEXTBOXView);
            mWindowManager.addView(mTEXTBOXFrameLayout, mParams2);
            switchContext();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext,"initialize error!",Toast.LENGTH_SHORT);
        }
        return;
    }

    public void Destroy() {
        Log.d(TAG,"Destroy()");
        try {
            mADVISTORFrameLayout.removeAllViews();
            mTEXTBOXFrameLayout.removeAllViews();
            mADVISTORView = null;
            mADVISTORFrameLayout = null;
            mLayoutinflater = null;
            mWindowManager = null;
            mContext = null;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext,"Destroy error!",Toast.LENGTH_SHORT);
        }
        return;
    }

    public void switchContext() {
        steps = steps % 7;
        Log.d(TAG, "switchContext() steps = " + steps);
        /*
        if (mHandler == null) {
            mHandler = new Handler();
        } else {
            mHandler.removeCallbacks(hideall);
        }
        */
        switch(steps) {
            case 0 :
                mImageView.setVisibility(View.GONE);
                mTextView.setVisibility(View.GONE);
                break;
            case 1 :
                mImageView.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText(R.string.talk_content_1);
                break;
            case 2 :
                mImageView.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText(R.string.talk_content_2);
                break;
            case 3 :
                mImageView.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText(R.string.talk_content_3);
                break;
            case 4 :
                mImageView.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText(R.string.talk_content_4);
                break;
            case 5 :
                mImageView.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText(R.string.talk_content_5);
                break;
            case 6 :
                mImageView.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText(R.string.talk_content_6);
                break;
        }
        steps++;
        //mHandler.postDelayed(hideall, 10000);
    }
/*
    private Runnable hideall = new Runnable() {
        @Override
        public void run() {
            mImageView.setVisibility(View.GONE);
            mTextView.setVisibility(View.GONE);
            steps = 0;
            mHandler.removeCallbacks(hideall);
            mHandler = null;
        }
    };
*/
}
