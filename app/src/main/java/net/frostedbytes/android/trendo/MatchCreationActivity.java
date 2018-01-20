package net.frostedbytes.android.trendo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import net.frostedbytes.android.trendo.fragments.MatchCreationFragment;

public class MatchCreationActivity extends BaseActivity implements MatchCreationFragment.MatchCreationListener {

  private static final String TAG = "MatchCreationActivity";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_match_creation);

    if (findViewById(R.id.create_fragment_container) != null) {

      MatchCreationFragment createMatchFragment = new MatchCreationFragment();
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.replace(R.id.create_fragment_container, createMatchFragment);
      transaction.addToBackStack(null);
      transaction.commit();
    }
  }

  @Override
  public void onMatchCreated(String matchId) {

    Log.d(TAG, "++onMatchCreated(String)");
    Intent intent = new Intent();
    intent.putExtra(BaseActivity.ARG_MATCH_ID, matchId);
    setResult(RESULT_OK, intent);
  }

  @Override
  public void onCreatedCancelled() {

    Log.d(TAG, "++onCreatedCancelled()");
    setResult(RESULT_CANCELED);
  }

  public static String getMatchId(Intent result) {

    Log.d(TAG, "++getMatchId(Intent)");
    return result.getStringExtra(BaseActivity.ARG_MATCH_ID);
  }
}
