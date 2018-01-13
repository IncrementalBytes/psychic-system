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

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_main);

    if (findViewById(R.id.fragment_container) != null) {
      if (savedInstanceState != null) {
        return;
      }

      MatchListFragment matchListFragment = new MatchListFragment();
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.replace(R.id.fragment_container, matchListFragment);
      transaction.addToBackStack(null);
      transaction.commit();
    }
  }

  @Override
  public void onMatchSelected(String matchId) {

    Log.d(TAG, "++onMatchSelected(String)");

    // grab updates from the database
    startActivity(new Intent(this, GatheringActivity.class));

    MatchDetailFragment matchDetailFragment = new MatchDetailFragment();
    Bundle args = new Bundle();
    args.putString(MatchDetailFragment.ARG_MATCH_ID, matchId);
    matchDetailFragment.setArguments(args);

    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.fragment_container, matchDetailFragment);
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

    Log.d(TAG, "++onCreateOptionsMenu(Menu)");
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    Log.d(TAG, "++onOptionsItemSelected(MenuItem)");
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

    Log.d(TAG, "++onBackPressed()");
    if (getFragmentManager().getBackStackEntryCount() > 0 ){
      getFragmentManager().popBackStack();
    } else {
      super.onBackPressed();
    }
  }
}
