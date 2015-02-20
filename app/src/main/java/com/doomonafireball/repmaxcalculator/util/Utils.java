package com.doomonafireball.repmaxcalculator.util;

import android.content.Context;
import android.widget.Toast;

import com.doomonafireball.repmaxcalculator.R;

/**
 * Created by derek on 11/19/14.
 */
public class Utils {

    public static void copyStringToClipboard(Context context, String textToClip) {
        if (textToClip.length() > 0) {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(
                        Context.CLIPBOARD_SERVICE);
                clipboard.setText(textToClip);
            } else {
                android.content.ClipboardManager clipboard
                        = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData
                        .newPlainText("weight", textToClip);
                clipboard.setPrimaryClip(clip);
            }
            Toast.makeText(context, String.format(context.getString(R.string.copied_to_clipboard), textToClip), Toast.LENGTH_SHORT).show();
        }
    }
}
