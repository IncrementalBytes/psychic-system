package net.frostedbytes.android.trendo;

import android.support.v4.app.Fragment;

public class ListActivity extends SingleFragmentActivity {

  @Override
  protected Fragment createFragment() { return new ListFragment(); }
}
