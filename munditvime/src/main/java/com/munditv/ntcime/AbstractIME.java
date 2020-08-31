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
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;

/**
 * Abstract class extended by ZhuyinIME
 */
public abstract class AbstractIME extends InputMethodService implements 
    KeyboardView.OnKeyboardActionListener, CandidateView.CandidateViewListener {

  protected SoftKeyboardView inputView;
  private CandidatesContainer candidatesContainer;
  private KeyboardSwitch keyboardSwitch;
  private Editor editor;
  private WordDictionary wordDictionary;
  private PhraseDictionary phraseDictionary;
  private SoundMotionEffect effect;
  private int orientation;
  private int hardwarekey;
  private int prevKeyCode;
  private boolean isCapslock;

  private Handler mHandler = new Handler();
  protected abstract KeyboardSwitch createKeyboardSwitch(Context context);
  protected abstract Editor createEditor();
  protected abstract WordDictionary createWordDictionary(Context context);
  
  @Override
  public void onCreate() {
    super.onCreate();
    keyboardSwitch = createKeyboardSwitch(this);
    editor = createEditor();
    wordDictionary = createWordDictionary(this);
    phraseDictionary = new PhraseDictionary(this);
    effect = new SoundMotionEffect(this);
    prevKeyCode = -2;
    isCapslock = false;
    orientation = getResources().getConfiguration().orientation;
    startTVService();
  }

  private void startTVService() {
    PackageInfo packageInfo = null;

    try{
      packageInfo = getPackageManager().getPackageInfo("com.munditv.tvservice",
              PackageManager.GET_SERVICES);
      if(packageInfo != null) {
        Intent mIntent = new Intent();
        mIntent.setClassName("com.munditv.tvservice",
                "com.mundirv.tvservice.TVService");
        mIntent.setAction("com.munditv.tvservice.ACTION_START_SERVICE");
        startService(mIntent);
        Toast.makeText(this,R.string.toast_text_tvservice_start,Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(this,R.string.toast_text_tvservice_not_found,Toast.LENGTH_SHORT).show();
      }
    } catch (Exception e) {
      e.printStackTrace();
      Toast.makeText(this,R.string.toast_text_tvservice_error,Toast.LENGTH_SHORT).show();
    }
  }


  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    if (orientation != newConfig.orientation) {
      // Clear composing text and candidates for orientation change.
      escape();
      orientation = newConfig.orientation;
    }

    super.onConfigurationChanged(newConfig);
  }

  @Override
  public void onUpdateSelection(int oldSelStart, int oldSelEnd, int newSelStart,
      int newSelEnd, int candidatesStart, int candidatesEnd) {
    super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, 
        candidatesStart, candidatesEnd);
    if ((candidatesEnd != -1) &&
        ((newSelStart != candidatesEnd) || (newSelEnd != candidatesEnd))) {
      // Clear composing text and its candidates for cursor movement.
      escape();
    }
    // Update the caps-lock status for the current cursor position.
    updateCursorCapsToInputView();
  }

  @Override
  public void onComputeInsets(InputMethodService.Insets outInsets) {
    super.onComputeInsets(outInsets);
    outInsets.contentTopInsets = outInsets.visibleTopInsets;
  }

  @Override
  public void onConfigureWindow(Window win, boolean isFullscreen, boolean isCandidatesOnly) {
    super.onConfigureWindow(win, false, isCandidatesOnly);
  }

  @Override
  public void onStartInput(EditorInfo attribute, boolean restarting) {
    attribute.imeOptions = EditorInfo.IME_FLAG_NO_FULLSCREEN;
    super.onStartInput(attribute, restarting);
  }

  @Override
  public View onCreateInputView() {
    inputView = (SoftKeyboardView) getLayoutInflater().inflate(
        R.layout.input, null);
    inputView.setOnKeyboardActionListener(this);
    InputConnection ic = getCurrentInputConnection();
    if (ic != null) {
      EditorInfo ei = getCurrentInputEditorInfo();
    }
    return inputView;
  }

  @Override
  public View onCreateCandidatesView() {
    candidatesContainer = (CandidatesContainer) getLayoutInflater().inflate(
        R.layout.candidates, null);
    candidatesContainer.setCandidateViewListener(this);
    return candidatesContainer;
  }

  @Override
  public void onStartInputView(EditorInfo attribute, boolean restarting) {
    super.onStartInputView(attribute, restarting);

    // Reset editor and candidates when the input-view is just being started.
    editor.start(attribute.inputType);
    clearCandidates();
    effect.reset();

    keyboardSwitch.initializeKeyboard(getMaxWidth());
    // Select a keyboard based on the input type of the editing field.
    keyboardSwitch.onStartInput(attribute.inputType);
    bindKeyboardToInputView();
  }

  @Override
  public void onFinishInput() {
    // Clear composing as any active composing text will be finished, same as in
    // onFinishInputView, onFinishCandidatesView, and onUnbindInput.
    editor.clearComposingText(getCurrentInputConnection());
    super.onFinishInput();
  }

  @Override
  public void onFinishInputView(boolean finishingInput) {
    editor.clearComposingText(getCurrentInputConnection());
    super.onFinishInputView(finishingInput);
    // Dismiss any pop-ups when the input-view is being finished and hidden.
    inputView.closing();
  }

  @Override
  public void onFinishCandidatesView(boolean finishingInput) {
    editor.clearComposingText(getCurrentInputConnection());
    super.onFinishCandidatesView(finishingInput);
  }

  @Override
  public void onUnbindInput() {
    editor.clearComposingText(getCurrentInputConnection());
    super.onUnbindInput();
  }

  private void bindKeyboardToInputView() {
    if (inputView != null) {
      // Bind the selected keyboard to the input view.
      inputView.setKeyboard(keyboardSwitch.getCurrentKeyboard());
      updateCursorCapsToInputView();
    }
  }

  private void updateCursorCapsToInputView() {
    InputConnection ic = getCurrentInputConnection();
    if ((ic != null) && (inputView != null)) {
      int caps = 0;
      EditorInfo ei = getCurrentInputEditorInfo();
      if ((ei != null) && (ei.inputType != EditorInfo.TYPE_NULL)) {
        caps = ic.getCursorCapsMode(ei.inputType);
      }

      inputView.updateCursorCaps(caps);
    }
  }

  private void commitText(CharSequence text) {
    if (editor.commitText(getCurrentInputConnection(), text)) {
      // Clear candidates after committing any text.
      clearCandidates();
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    Log.d("AbstractIME ", "onKeyDown() keyCode = " + Integer.toString(keyCode));
    if (keyCode == 165) {
        return true;
    }

    if(inputView == null) return super.onKeyDown(keyCode, event);
    if(!inputView.isShown()) return super.onKeyDown(keyCode, event);

    if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
      // Handle the back-key to close the pop-up keyboards.
      if ((inputView != null) && inputView.handleBack()) {
        return true;
      }
    }

    if (keyCode ==75) {
      return true;
    }

    if (keyCode == 82) {
      hardwarekey = -200;
      handleKeyCode(hardwarekey);
      return true;
    }

    if (keyCode == 172 || keyCode == 68) {
      if (inputView.isNumberEnglish()) {
        isCapslock = !isCapslock;
        inputView.setCapsLock(isCapslock);
        return true;
      } else {
        keyCode = 256;
      }
    }

    if (keyCode == 67) {
      hardwarekey = -5;
      handleKeyCode(hardwarekey);
      return true;
    }

    if ((candidatesContainer != null) && candidatesContainer.isShown()) {
      Log.d("AbstractIME ", "onKeyDown() candidatesContainer is not null!");
      CandidateView candidateview = candidatesContainer.getCandidateView();
      int i = candidateview.getHightlightIndex();
      int count = candidatesContainer.remainWords();
      if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT ||
          keyCode == KeyEvent.KEYCODE_DPAD_UP) {
        if (i > 0) {
          i = i - 1;
        } else {
          candidatesContainer.previewPage();
          count = candidatesContainer.remainWords();
          i = count-1;
        }
        candidateview.setHightlightIndex(i);
        return true;
      } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ||
                 keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
        if (i < count-1) {
          i = i + 1;
        } else {
          candidatesContainer.nextPage();
          i = 0;
        }
        candidateview.setHightlightIndex(i);
        return true;
      } else if (keyCode == 23 || keyCode == 66) {
        if(i >= 0){
          hardwarekey = ' ';
          handleKeyCode(hardwarekey);
        } else {
          sendKeyChar('\n');
        }
        return true;
      }
    }

    if (keyCode == 23 || keyCode == 66) {
      handleKeyCode(10);
      return true;
    }

    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT ||
            keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ||
            keyCode == KeyEvent.KEYCODE_DPAD_UP ||
            keyCode == KeyEvent.KEYCODE_DPAD_DOWN ||
            keyCode == KeyEvent.KEYCODE_BACK ||
            keyCode == KeyEvent.KEYCODE_ESCAPE) {
      return super.onKeyDown(keyCode, event);
    }

    if(inputView != null) {
      hardwarekey = inputView.getCharactersByKeycode(keyCode);
      if(inputView.isNumberEnglish()) {
        if (isCapslock) {
          hardwarekey = Character.toUpperCase(hardwarekey);
        }
        if(mHandler != null) mHandler.removeCallbacks(delayCheck);
        if ((keyCode >= KeyEvent.KEYCODE_2 && keyCode <= KeyEvent.KEYCODE_9)
            || keyCode == KeyEvent.KEYCODE_0) {
          if(prevKeyCode == keyCode) {
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL);
          }
        }
        mHandler.postDelayed(delayCheck, 2000);
        prevKeyCode = keyCode;
        mHandler.postDelayed(postCharacter,300);
        return true;
      }
      handleKeyCode(hardwarekey);
      //if (hardwarekey > 255)
      return true;
    }

    return super.onKeyDown(keyCode, event);
  }

  final Runnable postCharacter = new Runnable() {
    public void run() {
      handleKeyCode(hardwarekey);
      mHandler.removeCallbacks(postCharacter);
    }
  };

  final Runnable delayCheck = new Runnable() {
    public void run() {
      prevKeyCode = -2;
      mHandler.removeCallbacks(delayCheck);
    }
  };

  public void onKey(int primaryCode, int[] keyCodes) {
    String str="{";
    for (int i=0; i<keyCodes.length; i++) {
        str += Integer.toString(keyCodes[i]) + ", ";
    }
    str +="}";
    Log.d("AbstractIME ", "onKey(primaryCode,keyCodes) primaryCode = " + primaryCode + " keyCodes = " + str);
    if (inputView.isNumberEnglish()) {
      if (primaryCode == 172) {
        isCapslock = !isCapslock;
        inputView.setCapsLock(isCapslock);
        return;
      }
    }
    handleKeyCode(primaryCode);
  }

  public void handleKeyCode(int primaryCode) {
    Log.d("AbstractIME ", "handleKeyCode() primaryCode = " + primaryCode);
    if (keyboardSwitch.onKey(primaryCode)) {
      escape();
      bindKeyboardToInputView();
      return;
    }
    if (handleOption(primaryCode) || handleCapsLock(primaryCode)
            || handleEnter(primaryCode) || handleSpace(primaryCode)
            || handleDelete(primaryCode) || handleComposing(primaryCode)) {
      return;
    }
    handleKey(primaryCode);
  }

  public void onText(CharSequence text) {
    commitText(text);
  }

  public void onPress(int primaryCode) {
    effect.playSound();
  }

  public void onRelease(int primaryCode) {
    // no-op
  }

  public void onPickCandidate(String candidate) {
    // Commit the picked candidate and suggest its following words.
    commitText(candidate);
    setCandidates(
        phraseDictionary.getFollowingWords(candidate.charAt(0)), false);
  }

  private void clearCandidates() {
    setCandidates("", false);
  }

  private void setCandidates(String words, boolean highlightDefault) {
    if (candidatesContainer != null) {
      candidatesContainer.setCandidates(words, highlightDefault);
      setCandidatesViewShown((words.length() > 0) || editor.hasComposingText());
      if (inputView != null) {
        inputView.setEscape(candidatesContainer.isShown());
      }
    }
  }

  private boolean handleOption(int keyCode) {
    if (keyCode == SoftKeyboard.KEYCODE_OPTIONS) {
      // TODO: Do voice input here.
      return true;
    }
    return false;
  }

  private boolean handleCapsLock(int keyCode) {
    return (keyCode == Keyboard.KEYCODE_SHIFT) && inputView.toggleCapsLock();
  }

  private boolean handleEnter(int keyCode) {
    if (keyCode == '\n') {
      if (inputView.hasEscape()) {
        escape();
      } else if (editor.treatEnterAsLinkBreak()) {
        commitText("\n");
      } else {
        sendKeyChar('\n');
      }
      return true;
    }
    return false;
  }

  private boolean handleSpace(int keyCode) {
    if (keyCode == ' ') {
      if ((candidatesContainer != null) && candidatesContainer.isShown()) {
        // The space key could either pick the highlighted candidate or escape
        // if there's no highlighted candidate and no composing-text.
        if (!candidatesContainer.pickHighlighted()
            && !editor.hasComposingText()) {
          escape();
        }
      } else {
        commitText(" ");
      }
      return true;
    }
    return false;
  }

  private boolean handleDelete(int keyCode) {
    // Handle delete-key only when no composing text.
    Log.d("AbstractIME ", "handleDelete() ");
    if ((keyCode == Keyboard.KEYCODE_DELETE) && !editor.hasComposingText()) {
      if (inputView.hasEscape()) {
        Log.d("AbstractIME ", "handleDelete() escape()");
        escape();
      } else {
        Log.d("AbstractIME ", "handleDelete() sendDownUpKeyEvents()");
        sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL);
      }
      return true;
    }
    Log.d("AbstractIME ", "handleDelete() no Process");
    return false;
  }

  private boolean handleComposing(int keyCode) {
    if (editor.compose(getCurrentInputConnection(), keyCode)) {
      // Set the candidates for the updated composing-text and provide default
      // highlight for the word candidates.
      setCandidates(wordDictionary.getWords(editor.composingText()), true);
      return true;
    }
    return false;
  }

  /**
   * Handles input of SoftKeybaord key code that has not been consumed by
   * other handling-methods.
   */
  private void handleKey(int keyCode) {
    Log.d("AbstractIME ", "handleKey keyCode = " + keyCode);
    if (isInputViewShown() && inputView.isShifted()) {
      keyCode = Character.toUpperCase(keyCode);
    }
    commitText(String.valueOf((char) keyCode));
  }

  /**
   * Simulates PC Esc-key function by clearing all composing-text or candidates.
   */
  protected void escape() {
    editor.clearComposingText(getCurrentInputConnection());
    clearCandidates();
  }

  @Override
  public void swipeLeft() {

  }

  @Override
  public void swipeRight() {

  }

  @Override
  public void swipeDown() {

  }

  @Override
  public void swipeUp() {

  }

}
