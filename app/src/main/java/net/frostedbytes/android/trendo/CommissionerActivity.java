package net.frostedbytes.android.trendo;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import net.frostedbytes.android.trendo.fragments.CommissionerFragment;
import net.frostedbytes.android.trendo.fragments.MatchSummaryDataFragment;
import net.frostedbytes.android.trendo.fragments.TeamDataFragment;
import net.frostedbytes.android.trendo.models.MatchSummary;
import net.frostedbytes.android.trendo.models.Team;
import net.frostedbytes.android.trendo.utils.LogUtils;

import java.util.ArrayList;

public class CommissionerActivity extends BaseActivity {

  private static final String TAG = BASE_TAG + CommissionerActivity.class.getSimpleName();

  private ProgressBar mProgressBar;

  private ArrayList<MatchSummary> mRemoteMatchSummaries;
  private int mSeason;
  private ArrayList<Team> mRemoteTeams;

  /*
      AppCompatActivity Override(s)
   */
  @Override
  public void onBackPressed() {

    LogUtils.debug(TAG, "++onBackPressed()");
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    LogUtils.debug(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_commissioner);

    mProgressBar = findViewById(R.id.commissioner_progress);

    Toolbar toolbar = findViewById(R.id.commissioner_toolbar);
    setSupportActionBar(toolbar);

    mSeason = getIntent().getIntExtra(BaseActivity.ARG_SEASON, 0);
    mRemoteTeams = getIntent().getParcelableArrayListExtra(BaseActivity.ARG_TEAMS);
    mRemoteMatchSummaries = getIntent().getParcelableArrayListExtra(BaseActivity.ARG_MATCH_SUMMARIES);
    replaceFragment(CommissionerFragment.newInstance(mSeason, mRemoteTeams, mRemoteMatchSummaries));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    LogUtils.debug(TAG, "++onCreateOptionsMenu(Menu)");
    getMenuInflater().inflate(R.menu.menu_commissioner, menu);
    return true;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    LogUtils.debug(TAG, "++onDestroy()");
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    LogUtils.debug(TAG, "++onOptionsItemSelected(MenuItem)");
    if (item.getItemId() == R.id.action_back) {
      setResult(RESULT_CANCELED, null);
      finish();
    }

    return super.onOptionsItemSelected(item);
  }

  /*
      Fragment Override(s)
   */

  /*
    Private Method(s)
   */
  private void replaceFragment(Fragment fragment) {

    LogUtils.debug(TAG, "++replaceFragment()");
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.commissioner_fragment_container, fragment);
    fragmentTransaction.commitAllowingStateLoss();
  }
}
