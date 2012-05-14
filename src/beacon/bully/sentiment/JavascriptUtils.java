package beacon.bully.sentiment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

public class JavascriptUtils {
    Context mContext;
    Editor prefs;
    Dialog origin;
    /** Instantiate the interface and set the context */
    JavascriptUtils(Context c,Editor in,Dialog d) {
        mContext = c;
        prefs=in;
        origin=d;
    }

    /** Show a toast from the web page */
    public void showToast(String toast) {
        prefs.putString("api_key", toast);
        prefs.commit();
        origin.dismiss();
    }
}