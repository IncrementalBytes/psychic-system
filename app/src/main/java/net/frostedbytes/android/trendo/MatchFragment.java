package net.frostedbytes.android.trendo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.UUID;

public class MatchFragment extends Fragment {

  private static final String TAG = "MatchFragment";

  private static final String ARG_MATCH_ID = "match_id";

  public static MatchFragment newInstance(UUID matchId) {

    System.out.println("++" + TAG + "::newInstance(UUID)");
    Bundle args = new Bundle();
    args.putSerializable(ARG_MATCH_ID, matchId);

    MatchFragment fragment = new MatchFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    System.out.println("++" + TAG + "::onCreate(Bundle)");
  }

  @Override
  public void onPause() {
    super.onPause();

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    System.out.println("++" + TAG + "::onCreateView(LayoutInflater,ViewGroup, Bundle)");
    View v = inflater.inflate(R.layout.activity_match, container, false);

    // TODO: expand on match view

    return v;
  }
}
