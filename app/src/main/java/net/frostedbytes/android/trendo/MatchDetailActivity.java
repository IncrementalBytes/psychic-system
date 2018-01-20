package net.frostedbytes.android.trendo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import net.frostedbytes.android.trendo.fragments.MatchDetailFragment;
import net.frostedbytes.android.trendo.models.Team;

public class MatchDetailActivity extends BaseActivity  implements MatchDetailFragment.MatchDetailListener {

  private static final String TAG = "MatchDetailActivity";

  private static final int EVENT_CREATE_RESULT = 0;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_match_detail);

    if (findViewById(R.id.detail_fragment_container) != null) {

      String matchId = getIntent().getStringExtra(BaseActivity.ARG_MATCH_ID);

      MatchDetailFragment matchDetailFragment = new MatchDetailFragment();
      Bundle args = new Bundle();
      args.putString(BaseActivity.ARG_MATCH_ID, matchId);
      matchDetailFragment.setArguments(args);

      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.replace(R.id.detail_fragment_container, matchDetailFragment);
      transaction.addToBackStack(matchId);
      transaction.commit();
    }
  }

  @Override
  public void onCreateMatchEventRequest(String matchId, Team team) {

    Log.d(TAG, "++onCreateMatchEventRequest(String, Team)");
    Intent intent = new Intent(this, EventDetailActivity.class);
    intent.putExtra(BaseActivity.ARG_MATCH_ID, matchId);
    intent.putExtra(BaseActivity.ARG_TEAM, team);
    startActivityForResult(intent, EVENT_CREATE_RESULT);
  }

  @Override
  public void onEditMatchEventRequest(String matchId, Team team) {

    Log.d(TAG, "++onEditMatchEventRequest(String, Team)");
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {

    if (resultCode != RESULT_OK) {
      Log.d(TAG, "Child activity returned cancelled.");
      return;
    }

    if (requestCode == EVENT_CREATE_RESULT) {
      if (data == null) {
        Log.d(TAG, "Result data is null.");
      }

      // TODO: refresh to pick up new event
    }
  }
}
