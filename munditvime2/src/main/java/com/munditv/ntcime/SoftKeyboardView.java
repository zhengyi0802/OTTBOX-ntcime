/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.munditv.ntcime;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Shows a soft keyboard, rendering keys and detecting key presses.
 */
public class SoftKeyboardView extends KeyboardView {
  
  private static final int FULL_WIDTH_OFFSET = 0xFEE0;

  private SoftKeyboard currentKeyboard;
  private boolean capsLock, iscapsLock;
  private static Method invalidateKeyMethod;
  private List <Keyboard.Key> keys;
  private int mykeycode,mykey;

  static {
    try {
      invalidateKeyMethod = KeyboardView.class.getMethod(
              "invalidateKey", new Class[] { int.class } );
    } catch (NoSuchMethodException nsme) {
    }
  }

  public static boolean canRedrawKey() {
    return invalidateKeyMethod != null;
  }

  public SoftKeyboardView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SoftKeyboardView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  private boolean canCapsLock() {
    // Caps-lock can only be toggled on English keyboard.
    return (currentKeyboard != null) && currentKeyboard.isEnglish();
  }

  public boolean toggleCapsLock() {
    if (canCapsLock()) {
      capsLock = !isShifted();
      setShifted(capsLock);
      return true;
    }
    return false;
  }

  public void updateCursorCaps(int caps) {
    if (canCapsLock()) {
      setShifted(capsLock || (caps != 0));
    }
  }

  public boolean hasEscape() {
    return (currentKeyboard != null) && currentKeyboard.hasEscape();
  }

  public boolean isNumberZhuyin() { return currentKeyboard.isNumberZhuyin(); }

  public boolean isNumberEnglish() { return currentKeyboard.isNumberEnglish(); }

  public void setEscape(boolean escape) {
    if ((currentKeyboard != null) && currentKeyboard.setEscape(escape)) {
      invalidateEscapeKey();
    }
  }

  private void invalidateEscapeKey() {
    // invalidateKey method is only supported since 1.6.
    if (invalidateKeyMethod != null) {
      try {
        invalidateKeyMethod.invoke(this, currentKeyboard.getEscapeKeyIndex());
      } catch (IllegalArgumentException e) {
        Log.e("SoftKeyboardView", "exception: ", e);
      } catch (IllegalAccessException e) {
        Log.e("SoftKeyboardView", "exception: ", e);
      } catch (InvocationTargetException e) {
        Log.e("SoftKeyboardView", "exception: ", e);
      }
    }
  }

  @Override
  public void setKeyboard(Keyboard keyboard) {
    if (keyboard instanceof SoftKeyboard) {
      boolean escape = hasEscape();
      currentKeyboard = (SoftKeyboard) keyboard;
      currentKeyboard.updateStickyKeys();
      currentKeyboard.setEscape(escape);
    }
    super.setKeyboard(keyboard);
  }

  @Override
  protected boolean onLongPress(Key key) {
    // 0xFF01~0xFF5E map to the full-width forms of the characters from
    // 0x21~0x7E. Make the long press as producing corresponding full-width
    // forms for these characters by adding the offset (0xff01 - 0x21).
    if (currentKeyboard != null && currentKeyboard.isSymbols() &&
        key.popupResId == 0 && key.codes[0] >= 0x21 && key.codes[0] <= 0x7E) {
      getOnKeyboardActionListener().onKey(
          key.codes[0] + FULL_WIDTH_OFFSET, null);
      return true;
    } else if (key.codes[0] == SoftKeyboard.KEYCODE_MODE_CHANGE_LETTER) {
      getOnKeyboardActionListener().onKey(SoftKeyboard.KEYCODE_OPTIONS, null);
      return true;
    } else {
      return super.onLongPress(key);
    }
  }

  @Override
  public void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    Log.d("LatinKeyboardView", "onDraw");

    int height = 24;
    Paint paint = new Paint();
    paint.setTextAlign(Paint.Align.CENTER);
    paint.setTextSize(height);
    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

    if(currentKeyboard.isNumberZhuyin()) {
      paint.setColor(Color.parseColor("#F08080Ff"));
    } else {
      paint.setColor(Color.parseColor("#F0F080Ff"));
    }
    //get all your keys and draw whatever you want
    keys = getKeyboard().getKeys();
    for(Keyboard.Key key: keys) {
      Log.d("LatinKeyboardView" , "key.label = " + key.label + ", key.popupCharacters = " + key.popupCharacters);
      int h = key.height;
      if(key.popupCharacters != null) {
        String str = key.popupCharacters.toString();
        if(iscapsLock) {
            str = str.toUpperCase();
        } else {
            str = str.toLowerCase();
        }

        if (str != null && str.length() > 0) {
            canvas.drawText(str, key.x + (key.width / 2), key.y + key.height-6, paint);
        }
      }
    }
    return;
  }

  public int getCharactersByKeycode(int keycode) {
    String str="";
    mykeycode = keycode;

    Log.d("SoftwareKeyboardView ", "getCharactersByKeycode keycode = " + keycode);
    switch(keycode) {
      case KeyEvent.KEYCODE_1 :
        str="1";
        break;
      case KeyEvent.KEYCODE_2 :
        str="2";
        break;
      case KeyEvent.KEYCODE_3 :
        str="3";
        break;
      case KeyEvent.KEYCODE_4 :
        str="4";
        break;
      case KeyEvent.KEYCODE_5 :
        str="5";
        break;
      case KeyEvent.KEYCODE_6 :
        str="6";
        break;
      case KeyEvent.KEYCODE_7 :
        str="7";
        break;
      case KeyEvent.KEYCODE_8 :
        str="8";
        break;
      case KeyEvent.KEYCODE_9 :
        str="9";
        break;
      case KeyEvent.KEYCODE_0 :
        if(isNumberEnglish()) {
          if (mykey == '0') {
            mykey = ' ';
          } else {
            mykey = '0';
          }
          return mykey;
        }
        str="0";
        break;
      case 256 :
        str="幫助";
        break;
      default:
        mykeycode=-1;
        return keycode;
    }
    Log.d("SoftwareKeyboardView ", "getCharactersByKeycode keycode = " + keycode + " str=" + str);
    for(Keyboard.Key key: keys) {
      if(key.label != null  && key.label.toString().equals(str)) {
        int[] codes = key.codes;
        for(int i=0; i < codes.length; i++) {
          if(mykey == codes[i]) {
            if ((i + 1) >= codes.length) {
              mykey = codes[0];
              return mykey;
            } else {
              mykey = codes[i + 1];
              return mykey;
            }
          }
        }
        mykey = codes[0];
        return mykey;
      }
    }
    mykeycode=-1;
    mykey = -1;
    return keycode;
  }

  public void setCapsLock(boolean capslock) {
    iscapsLock = capslock;
    redrawKeyboard();
  }

  private void redrawKeyboard() {
    if (isNumberZhuyin()) return;
    invalidate();
  }
}
