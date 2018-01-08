package net.frostedbytes.android.trendo;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

  private static final String TAG = "BaseActivity";

  private ProgressDialog mProgressDialog;

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
