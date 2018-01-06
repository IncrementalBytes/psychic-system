package net.frostedbytes.android.trendo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import java.util.List;
import net.frostedbytes.android.trendo.fragment.MatchFragment;
import net.frostedbytes.android.trendo.models.Match;

public class MatchPagerActivity extends AppCompatActivity {

  private static final String EXTRA_MATCH_ID = "match_id";

  private List<Match> mMatches;

  public static Intent newIntent(Context packageContext, String matchId) {

    Intent intent = new Intent(packageContext, MatchPagerActivity.class);
    intent.putExtra(EXTRA_MATCH_ID, matchId);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_match_pager);

    String matchId = (String) getIntent().getSerializableExtra(EXTRA_MATCH_ID);
    ViewPager viewPager = findViewById(R.id.match_view_pager);

    mMatches = MatchCenter.get().getMatches();
    FragmentManager fragmentManager = getSupportFragmentManager();
    viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

      @Override
      public Fragment getItem(int position) {

        Match match = mMatches.get(position);
        return MatchFragment.newInstance(match.Id);
      }

      @Override
      public int getCount() {
        return mMatches.size();
      }
    });

    for (int i = 0; i < mMatches.size(); i++) {
      if (mMatches.get(i).Id.equals(matchId)) {
        viewPager.setCurrentItem(i);
        break;
      }
    }
  }
}
