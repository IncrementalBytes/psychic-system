package net.frostedbytes.android.trendo;

import android.os.Bundle;
import android.util.Log;

public class SettingsActivity extends BaseActivity {

  private static final String TAG = "SettingsActivity";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_settings);
  }

  @Override
  public void onResume() {
    super.onResume();

    Log.d(TAG, "++onResume()");
  }
}
