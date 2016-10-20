package com.example.android.booklist;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Chris on 8/29/2016.
 */
public class InputUtils {
    // From user nv__ http://stackoverflow.com/questions/4005728/hide-default-keyboard-on-click-in-android
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
