package com.munditv.ntcime.btrfcomm;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.munditv.ntcime.R;

public class BluetoothStatusManager {

    private final static String TAG = "BluetoothStatusManager";

    private final static int BTSTATUS_WINDOW_X = 100;
    private final static int BTSTATUS_WINDOW_Y = 900;
    private final static int BTSTATUS_WINDOW_WIDTH = 600;
    private final static int BTSTATUS_WINDOW_HEIGHT = 80;
    private final static int BTSTATUS_WINDOW_GRAVITY = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
    private final static int BTSTATUS_WINDOW_FORNAT = PixelFormat.TRANSLUCENT;
    private final static int BTSTATUS_WINDOW_TYPE = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

    private Context                     mContext;
    private WindowManager               mWindowManager = null;
    private WindowManager.LayoutParams  mParams = null;
    private FrameLayout                 mBTStatusFrameLayout = null;
    private LayoutInflater              mLayoutinflater = null;
    private View                        mBTStatusView = null;
    private TextView                    mBTStatus;

    public BluetoothStatusManager(Context context) {
        Log.d(TAG,"Constructor()");
        mContext = context;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mLayoutinflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mBTStatusView = mLayoutinflater.inflate(R.layout.btwindow,null);
        mBTStatusFrameLayout = new FrameLayout(mContext);
        mParams = new WindowManager.LayoutParams();
        initialize();
    }

    private void initialize() {
        Log.d(TAG,"initialize()");
        try {
            mParams.width = BTSTATUS_WINDOW_WIDTH;
            mParams.height = BTSTATUS_WINDOW_HEIGHT;
            mParams.gravity = BTSTATUS_WINDOW_GRAVITY;
            if(BTSTATUS_WINDOW_GRAVITY == Gravity.TOP) {
                mParams.x = BTSTATUS_WINDOW_X;
                mParams.y = BTSTATUS_WINDOW_Y;
            }
            mParams.format = BTSTATUS_WINDOW_FORNAT;
            mParams.type = BTSTATUS_WINDOW_TYPE;
            mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            mParams.token = null;
            mBTStatusFrameLayout.setLayoutParams(mParams);
            mBTStatus = (TextView) mBTStatusView.findViewById(R.id.bt_status);
            mBTStatusFrameLayout.addView(mBTStatusView);
            mWindowManager.addView(mBTStatusFrameLayout, mParams);
            //setTextMessage("藍芽狀態");
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
            mBTStatusFrameLayout.setLayoutParams(mParams);
            mBTStatusFrameLayout.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext,"setCoordination error!",Toast.LENGTH_SHORT);
        }
        return;
    }

    public void setTextMessage(CharSequence text) {
        Log.d(TAG,"setTextMessage() message = " + text);
        try {
            //clearTextMessage();
            mBTStatus.setText(text);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext,"setTextMessage (" + text + ") error!",Toast.LENGTH_SHORT);
        }
        return;
    }

    public void clearTextMessage() {
        Log.d(TAG,"clearTextMessage()");
        try {
            mBTStatus.setText("                ");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext,"clearTextMessage error!",Toast.LENGTH_SHORT);
        }
        return;
    }

    public void Destroy() {
        Log.d(TAG,"Destroy()");
        try {
            mBTStatus = null;
            mBTStatusFrameLayout.removeAllViews();
            mBTStatusView = null;
            mBTStatusFrameLayout = null;
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
            mBTStatusFrameLayout.setVisibility(View.VISIBLE);
            mBTStatusFrameLayout.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext,"showStatus error!",Toast.LENGTH_SHORT);
        }
        return;
    }

    public void hideStatus() {
        Log.d(TAG,"hideStatus()");
        try {
            mBTStatusFrameLayout.setVisibility(View.GONE);
            mBTStatusFrameLayout.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext,"hideStatus error!",Toast.LENGTH_SHORT);
        }
        return;
    }


}
