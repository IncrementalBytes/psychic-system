package net.frostedbytes.android.trendo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import net.frostedbytes.android.trendo.utils.LogUtils;

public class BaseActivity extends AppCompatActivity {

  public static final String ARG_COMPARE = "compare";
  public static final String ARG_EMAIL = "email";
  public static final String ARG_MATCH_SUMMARY = "match_summary";
  public static final String ARG_TREND = "trend";
  public static final String ARG_USER_ID = "user_id";
  public static final String ARG_USER_NAME = "user_name";
  public static final String ARG_USER_PREFERENCE = "user_preference";
  public static final String DEFAULT_DATE = "0000-01-01";
  public static final String DEFAULT_ID = "000000000-0000-0000-0000-000000000000";
  public static final int NUM_TRENDS = 2;

  private static final String TAG = BaseActivity.class.getSimpleName();

  private ProgressDialog mProgressDialog;

  @Override
  public void onCreate(Bundle saved) {
    super.onCreate(saved);

    LogUtils.debug(TAG, "++onCreate(Bundle)");
    if (!BuildConfig.DEBUG) {
      Fabric.with(this, new Crashlytics());
    } else {
      LogUtils.debug(TAG, "Skipping Crashlytics setup; debug build.");
    }
  }

  void showProgressDialog(String message) {

    LogUtils.debug(TAG, "++showProgressDialog()");
    if (mProgressDialog == null) {
      mProgressDialog = new ProgressDialog(this);
      mProgressDialog.setCancelable(false);
      mProgressDialog.setMessage(message);
    }

    mProgressDialog.show();
  }

  void hideProgressDialog() {

    LogUtils.debug(TAG, "++hideProgressDialog()");
    if (mProgressDialog != null && mProgressDialog.isShowing()) {
      mProgressDialog.dismiss();
    }
  }
}
