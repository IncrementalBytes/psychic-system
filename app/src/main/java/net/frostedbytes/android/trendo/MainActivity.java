package net.frostedbytes.android.trendo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.google.firebase.auth.FirebaseAuth;
import net.frostedbytes.android.trendo.fragments.MatchListFragment;

public class MainActivity extends BaseActivity implements MatchListFragment.MatchListListener {

  private static final String TAG = "MainActivity";

  private static final int MATCH_CREATE_RESULT = 0;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_main);

    if (findViewById(R.id.main_fragment_container) != null) {
      if (savedInstanceState != null) {
        return;
      }

      MatchListFragment matchListFragment = new MatchListFragment();
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.replace(R.id.main_fragment_container, matchListFragment);
      transaction.addToBackStack(null);
      transaction.commit();
    }
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
        startActivity(new Intent(this, SettingsActivity.class));
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onCreateMatchRequest() {

    Log.d(TAG, "++onCreateMatchRequest()");
    Intent intent = new Intent(this, MatchCreationActivity.class);
    startActivityForResult(intent, MATCH_CREATE_RESULT);
  }

  @Override
  public void onMatchSelected(String matchId) {

    Log.d(TAG, "++onMatchSelected(String)");
    Intent intent = new Intent(this, MatchDetailActivity.class);
    intent.putExtra(MatchDetailActivity.ARG_MATCH_ID, matchId);
    startActivity(intent);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {

    if (resultCode != RESULT_OK) {
      Log.d(TAG, "Child activity returned cancelled.");
      return;
    }

    if (requestCode == MATCH_CREATE_RESULT) {
      if (data == null) {
        Log.d(TAG, "Result data is null.");
      }

      onMatchSelected(MatchCreationActivity.getMatchId(data));
    }
  }
}
