package net.frostedbytes.android.trendo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import java.util.ArrayList;
import java.util.List;
import net.frostedbytes.android.trendo.FirebaseRecyclerAdapter;
import net.frostedbytes.android.trendo.MatchCenter;
import net.frostedbytes.android.trendo.MatchPagerActivity;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Match;
import net.frostedbytes.android.trendo.models.Team;

public class MatchListFragment extends Fragment {

  private static final String TAG = "MatchListFragment";

  private static final FirebaseDatabase sDatabaseInstance = FirebaseDatabase.getInstance();

  private RecyclerView mMatchRecyclerView;

  private Query mMatchesQuery;
  private MatchAdapter mMatchesAdapter;
  private ArrayList<Match> mMatchAdapterItems;
  private ArrayList<String> mMatchAdapterKeys;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mMatchesQuery = sDatabaseInstance.getReference("matches");
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++" + TAG + "::onCreateView(LayoutInflater, ViewGroup, Bundle");
    mMatchAdapterItems = new ArrayList<>();
    mMatchAdapterKeys = new ArrayList<>();
    View view = inflater.inflate(R.layout.fragment_list, container, false);
    mMatchRecyclerView = view.findViewById(R.id.recycler_view_list);
    mMatchesAdapter = new MatchAdapter(mMatchesQuery, mMatchAdapterItems, mMatchAdapterKeys);
    mMatchRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    mMatchRecyclerView.setAdapter(mMatchesAdapter);
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();

    Log.d(TAG, "++" + TAG + "::onResume()");
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);

  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    mMatchesAdapter.destroy();
  }

  class MatchAdapter extends FirebaseRecyclerAdapter<ViewHolder, Match> {

    private static final String TAG = "MatchAdapter";

    private List<Team> sTeamList;

    public MatchAdapter(Query query, @Nullable ArrayList<Match> items, @Nullable ArrayList<String> keys) {
      super(query, items, keys);

      Log.d(TAG, "++MatchAdapter(Query, ArrayList<Match>, ArrayList<String>");

      // get team data for matches
      sTeamList = new ArrayList<>();
      sTeamList = MatchCenter.get().getTeams();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

      Log.d(TAG, "++onCreateViewHolder(ViewGroup, int)");
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.match_item, parent, false);
      return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

      Log.d(TAG, "++onBindViewHolder(MatchAdapter.ViewHolder, int)");
      Match match = getItem(position);
      holder.matchId = match.Id;
      holder.mTitleTextView.setText(
          String.format(
              "%1s vs %2s",
              MatchCenter.get().getTeam(match.HomeId).FullName,
              MatchCenter.get().getTeam(match.AwayId).FullName));
      holder.mMatchDateTextView.setText(Match.formatDateForDisplay(match.MatchDate));
      holder.mMatchStatusTextView.setText(match.IsFinal ? "FT" : "In Progress");
    }

    @Override
    protected void itemAdded(Match item, String key, int position) {

      Log.d(TAG, "++itemAdded(Match, String, int)");
    }

    @Override
    protected void itemChanged(Match oldItem, Match newItem, String key, int position) {

      Log.d(TAG, "++itemChanged(Match, Match, String, int)");
    }

    @Override
    protected void itemRemoved(Match item, String key, int position) {

      Log.d(TAG, "++itemRemoved(Match, String, int)");
    }

    @Override
    protected void itemMoved(Match item, String key, int oldPosition, int newPosition) {

      Log.d(TAG, "++itemRemoved(Match, String, int, int)");
    }
  }

  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static final String TAG = "ViewHolder";

    private final TextView mTitleTextView;
    private final TextView mMatchDateTextView;
    private final TextView mMatchStatusTextView;
    private final ImageView mMatchImageView;
    private String matchId;

    public ViewHolder(View view) {
      super(view);

      Log.d(TAG, "++ViewHolder(View)");
      matchId = "";
      mTitleTextView = itemView.findViewById(R.id.match_item_title);
      mMatchDateTextView = itemView.findViewById(R.id.match_item_date);
      mMatchStatusTextView = itemView.findViewById(R.id.match_item_status);
      mMatchImageView = itemView.findViewById(R.id.match_item_delete);
    }

    @Override
    public void onClick(View view) {

      Intent intent = MatchPagerActivity.newIntent(getActivity(), matchId);
      startActivityForResult(intent, MatchFragment.REQUEST_MATCH);
    }
  }
}
