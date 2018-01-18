package net.frostedbytes.android.trendo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.google.firebase.auth.FirebaseAuth;
import net.frostedbytes.android.trendo.fragments.EventDetailFragment;
import net.frostedbytes.android.trendo.fragments.MatchCreationFragment;
import net.frostedbytes.android.trendo.fragments.MatchDetailFragment;
import net.frostedbytes.android.trendo.fragments.MatchListFragment;
import net.frostedbytes.android.trendo.fragments.SettingsFragment;
import net.frostedbytes.android.trendo.models.Team;

public class MainActivity extends BaseActivity implements
  MatchListFragment.MatchListListener,
  MatchCreationFragment.MatchCreationListener,
  MatchDetailFragment.MatchDetailListener,
  EventDetailFragment.EventDetailListener {

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
  public void onCreateMatchRequest() {

    Log.d(TAG, "++onCreateMatchRequest()");
    MatchCreationFragment createMatchFragment = new MatchCreationFragment();

    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.fragment_container, createMatchFragment);
    transaction.addToBackStack(null);
    transaction.commit();
  }

  @Override
  public void onMatchSelected(String matchId) {

    Log.d(TAG, "++onMatchSelected(String)");
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
  public void onMatchCreated(String matchId) {

    Log.d(TAG, "++onMatchCreated(String)");
    onMatchSelected(matchId);
  }

  @Override
  public void onCreateMatchEventRequest(String matchId, Team team) {

    Log.d(TAG, "++onCreateMatchEventRequest()");
    EventDetailFragment eventDetailFragment = new EventDetailFragment();
    Bundle args = new Bundle();
    args.putString(MatchDetailFragment.ARG_MATCH_ID, matchId);
    args.putSerializable(MatchDetailFragment.ARG_TEAM, team);
    eventDetailFragment.setArguments(args);

    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.fragment_container, eventDetailFragment);
    transaction.addToBackStack(null);
    transaction.commit();
  }

  @Override
  public void onEditMatchEventRequest(String matchId, String teamShortName) {

    Log.d(TAG, "++onCreateMatchEventRequest()");
    // TODO: implement
  }

  @Override
  public void onMatchEventCreated(String matchId) {

    Log.d(TAG, "++onMatchEventCreated(String)");
    onMatchSelected(matchId);
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
    if (getFragmentManager().getBackStackEntryCount() > 0) {
      getFragmentManager().popBackStack();
    } else {
      super.onBackPressed();
    }
  }
}
