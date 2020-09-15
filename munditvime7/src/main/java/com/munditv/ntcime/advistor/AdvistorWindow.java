package com.munditv.ntcime.advistor;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.munditv.ntcime.R;

public class AdvistorWindow {

    private final static String TAG = "AdvistorWindow";

    private final static int ADVISTOR_WINDOW_X = 700;
    private final static int ADVISTOR_WINDOW_Y = 400;
    private final static int ADVISTOR_WINDOW_WIDTH = 480;
    private final static int ADVISTOR_WINDOW_HEIGHT = 400;
    private final static int ADVISTOR_WINDOW_GRAVITY = Gravity.TOP;
    private final static int ADVISTOR_WINDOW_FORNAT = PixelFormat.TRANSLUCENT;
    private final static int ADVISTOR_WINDOW_TYPE = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

    private Context                     mContext;
    private WindowManager               mWindowManager = null;
    private WindowManager.LayoutParams  mParams = null;
    private LinearLayout                mADVISTORFrameLayout = null;
    private LayoutInflater              mLayoutinflater = null;
    private View                        mADVISTORView = null;
    private TextView                    mTextView = null;
    private ImageView                   mImageView = null;
    private int                         steps = 0;

    private int index = 1;

    public AdvistorWindow(Context context) {
        Log.d(TAG,"Constructor()");
        mContext = context;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mLayoutinflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mADVISTORView = mLayoutinflater.inflate(R.layout.advistor,null);
        mADVISTORFrameLayout = new LinearLayout(mContext);
        mParams = new WindowManager.LayoutParams();
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
            mTextView = (TextView) mADVISTORView.findViewById(R.id.advistor_text);
            mImageView = (ImageView) mADVISTORView.findViewById(R.id.image);
            switchContext();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext,"initialize error!",Toast.LENGTH_SHORT);
        }
        return;
    }

    public void setCoordination(int x, int y, int width, int height) {
        Log.d(TAG,"setCoordination()");
        try {
            mParams.x = x;
            mParams.y = y;
            mParams.width = width;
            mParams.height = height;
            mADVISTORFrameLayout.setLayoutParams(mParams);
            mADVISTORFrameLayout.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext,"setCoordination error!",Toast.LENGTH_SHORT);
        }
        return;
    }


    public void Destroy() {
        Log.d(TAG,"Destroy()");
        try {
            mADVISTORFrameLayout.removeAllViews();
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

    public void showStatus() {
        Log.d(TAG,"showStatus()");
        try {
            mADVISTORFrameLayout.setVisibility(View.VISIBLE);
            mADVISTORFrameLayout.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext,"showStatus error!",Toast.LENGTH_SHORT);
        }
        return;
    }

    public void hideStatus() {
        Log.d(TAG,"hideStatus()");
        try {
            mADVISTORFrameLayout.setVisibility(View.GONE);
            mADVISTORFrameLayout.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext,"hideStatus error!",Toast.LENGTH_SHORT);
        }
        return;
    }

    public void switchContext() {
        steps = (++steps)%3 ;
        switch(steps) {
            case 0 :
                mImageView.setVisibility(View.GONE);
                mTextView.setVisibility(View.GONE);
                break;
            case 1 :
                mImageView.setVisibility(View.GONE);
                mTextView.setVisibility(View.VISIBLE);
                break;
            case 2 :
                mImageView.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.GONE);
                break;
        }
    }

}
