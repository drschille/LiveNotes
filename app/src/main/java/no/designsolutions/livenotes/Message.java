package no.designsolutions.livenotes;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Daniel on 03.01.2020.
 */

public class Message {
    public static void message(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}