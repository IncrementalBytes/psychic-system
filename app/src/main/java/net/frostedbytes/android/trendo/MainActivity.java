package net.frostedbytes.android.trendo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.google.firebase.auth.FirebaseAuth;
import net.frostedbytes.android.trendo.fragments.MatchDetailFragment;
import net.frostedbytes.android.trendo.fragments.MatchListFragment;
import net.frostedbytes.android.trendo.fragments.SettingsFragment;

public class MainActivity extends BaseActivity implements MatchListFragment.OnMatchSelectedListener {

  private static final String TAG = "MainActivity";

  MatchListFragment mMatchListFragment;
  MatchDetailFragment mMatchDetailFragment;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_main);

    if (findViewById(R.id.fragment_container) != null) {
      if (savedInstanceState != null) {
        return;
      }

      mMatchListFragment = new MatchListFragment();
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.replace(R.id.fragment_container, mMatchListFragment);
      transaction.addToBackStack(null);
      transaction.commit();
    }
  }

  @Override
  public void onMatchSelected(String matchId) {

    Log.d(TAG, "++onMatchSelected(String)");
    mMatchDetailFragment = new MatchDetailFragment();
    Bundle args = new Bundle();
    args.putString(MatchDetailFragment.ARG_MATCH_ID, matchId);
    mMatchDetailFragment.setArguments(args);

    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.fragment_container, mMatchDetailFragment);
    transaction.addToBackStack(matchId);
    transaction.commit();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    Log.d(TAG, "++onDestroy()");
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    int i = item.getItemId();
    switch (i) {
      case R.id.action_logout:
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, SignInActivity.class));
        finish();
        return true;
      case R.id.action_refresh:
        startActivity(new Intent(this, GatheringActivity.class));
        finish();
        return true;
      case R.id.action_settings:
        SettingsFragment settingsFragment = new SettingsFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, settingsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onBackPressed() {

    if (getFragmentManager().getBackStackEntryCount() > 0 ){
      getFragmentManager().popBackStack();
    } else {
      super.onBackPressed();
    }
  }
}
