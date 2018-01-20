package net.frostedbytes.android.trendo;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import net.frostedbytes.android.trendo.fragments.SettingsFragment;

public class SettingsActivity extends BaseActivity {

  private static final String TAG = "SettingsActivity";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_settings);

    if (findViewById(R.id.setting_fragment_container) != null) {
      if (savedInstanceState != null) {
        return;
      }

      SettingsFragment settingsFragment = new SettingsFragment();
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.replace(R.id.setting_fragment_container, settingsFragment);
      transaction.addToBackStack(null);
      transaction.commit();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    Log.d(TAG, "++onDestroy()");
  }
}
