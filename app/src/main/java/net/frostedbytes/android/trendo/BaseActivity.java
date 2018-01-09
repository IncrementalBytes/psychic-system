package net.frostedbytes.android.trendo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.google.firebase.crash.FirebaseCrash;

public class BaseActivity extends AppCompatActivity {

  private static final String TAG = "BaseActivity";

  private ProgressDialog mProgressDialog;

  @Override
  public void onCreate(Bundle saved) {
    super.onCreate(saved);

    FirebaseCrash.setCrashCollectionEnabled(false); // re-enable on release
  }

  public void showProgressDialog() {

    if (mProgressDialog == null) {
      mProgressDialog = new ProgressDialog(this);
      mProgressDialog.setCancelable(false);
      mProgressDialog.setMessage("Loading...");
    }

    mProgressDialog.show();
  }

  public void hideProgressDialog() {

    if (mProgressDialog != null && mProgressDialog.isShowing()) {
      mProgressDialog.dismiss();
    }
  }
}
