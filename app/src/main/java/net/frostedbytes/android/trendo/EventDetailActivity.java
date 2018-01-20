package net.frostedbytes.android.trendo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import net.frostedbytes.android.trendo.fragments.EventDetailFragment;
import net.frostedbytes.android.trendo.models.Team;

public class EventDetailActivity extends BaseActivity implements EventDetailFragment.EventDetailListener {

  private static final String TAG = "EventDetailActivity";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_event_detail);

    if (findViewById(R.id.event_fragment_container) != null) {
      if (savedInstanceState != null) {
        return;
      }

      EventDetailFragment eventDetailFragment = new EventDetailFragment();
      Bundle args = new Bundle();
      args.putString(BaseActivity.ARG_MATCH_ID, getIntent().getStringExtra(BaseActivity.ARG_MATCH_ID));
      args.putSerializable(BaseActivity.ARG_TEAM, getIntent().getSerializableExtra(BaseActivity.ARG_TEAM));
      eventDetailFragment.setArguments(args);

      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.replace(R.id.event_fragment_container, eventDetailFragment);
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
  public void onMatchEventCreated(String matchId) {

    Log.d(TAG, "++onMatchEventCreated(matchId)");
    Intent intent = new Intent();
    intent.putExtra(BaseActivity.ARG_MATCH_ID, matchId);
    setResult(RESULT_OK);
  }

  public static String getMatchId(Intent result) {

    Log.d(TAG, "++getMatchId(Intent)");
    return result.getStringExtra(BaseActivity.ARG_MATCH_ID);
  }
}
