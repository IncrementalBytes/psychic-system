package net.frostedbytes.android.trendo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.firebase.crash.FirebaseCrash;

public class BaseActivity extends AppCompatActivity {

  public static final String ARG_VALUES_DOUBLE = "values_double";
  public static final String ARG_VALUES_LONG = "values_long";
  public static final String ARG_MATCH_DATE = "match_date";
  public static final String ARG_USER = "user";
  public static final String ARG_USER_SETTINGS = "user_settings";

  public static final String DEFAULT_ID = "000000000-0000-0000-0000-000000000000";
  public static final int NUM_TRENDS = 5;

  private static final String TAG = "BaseActivity";

  private ProgressDialog mProgressDialog;


  @Override
  public void onCreate(Bundle saved) {
    super.onCreate(saved);

    Log.d(TAG, "++onCreate(Bundle)");
    FirebaseCrash.setCrashCollectionEnabled(false); // re-enable on release
  }

  void showProgressDialog(String message) {

    Log.d(TAG, "++showProgressDialog()");
    if (mProgressDialog == null) {
      mProgressDialog = new ProgressDialog(this);
      mProgressDialog.setCancelable(false);
      mProgressDialog.setMessage(message);
    }

    mProgressDialog.show();
  }

  void hideProgressDialog() {

    Log.d(TAG, "++hideProgressDialog()");
    if (mProgressDialog != null && mProgressDialog.isShowing()) {
      mProgressDialog.dismiss();
    }
  }
}
