package net.frostedbytes.android.trendo;

import android.support.v4.app.Fragment;
import net.frostedbytes.android.trendo.fragment.MatchListFragment;

public class MatchListActivity extends SingleFragmentActivity {

  @Override
  protected Fragment createFragment() { return new MatchListFragment(); }
}
