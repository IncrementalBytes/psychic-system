package net.frostedbytes.android.trendo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.firebase.crash.FirebaseCrash;

public class BaseActivity extends AppCompatActivity {

  public static final String ARG_MATCH_ID = "match_id";
  public static final String ARG_TEAM = "team";
  public static final String DEFAULT_ID = "000000000-0000-0000-0000-000000000000";

  private static final String TAG = "BaseActivity";

  private ProgressDialog mProgressDialog;

  @Override
  public void onCreate(Bundle saved) {
    super.onCreate(saved);

    Log.d(TAG, "++onCreate(Bundle)");
    FirebaseCrash.setCrashCollectionEnabled(false); // re-enable on release
  }

  public void showProgressDialog() {

    Log.d(TAG, "++showProgressDialog()");
    if (mProgressDialog == null) {
      mProgressDialog = new ProgressDialog(this);
      mProgressDialog.setCancelable(false);
      mProgressDialog.setMessage("Loading...");
    }

    mProgressDialog.show();
  }

  public void hideProgressDialog() {

    Log.d(TAG, "++hideProgressDialog()");
    if (mProgressDialog != null && mProgressDialog.isShowing()) {
      mProgressDialog.dismiss();
    }
  }
}
