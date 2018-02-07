package net.frostedbytes.android.trendo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Match;
import net.frostedbytes.android.trendo.models.MatchSummary;
import net.frostedbytes.android.trendo.models.UserSetting;

public class MatchListFragment extends Fragment {

  private static final String TAG = "MatchListFragment";

  static final String ARG_USER_SETTINGS = "user_settings";

  public interface OnMatchListListener {

    void onPopulated(int size);
    void onSelected(String matchId);
    void onSettingsClicked();
  }

  private UserSetting mSettings;
  private OnMatchListListener mCallback;

  private RecyclerView mRecyclerView;

  List<MatchSummary> mMatchSummaries;

  private Query mMatchSummaryQuery;
  private ValueEventListener mValueEventListener;

  public static MatchListFragment newInstance(UserSetting userSettings) {

    Log.d(TAG, "++newInstance(Settings)");
    MatchListFragment fragment = new MatchListFragment();
    Bundle args = new Bundle();
    args.putSerializable(ARG_USER_SETTINGS, userSettings);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    final View view = inflater.inflate(R.layout.fragment_match_list, container, false);

    mRecyclerView = view.findViewById(R.id.match_list_view);
    FloatingActionButton settingsButton = view.findViewById(R.id.match_list_fab_settings);
    settingsButton.setOnClickListener(buttonView -> mCallback.onSettingsClicked());

    final LinearLayoutManager manager = new LinearLayoutManager(getActivity());
    mRecyclerView.setLayoutManager(manager);

    updateUI();

    return view;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    Log.d(TAG, "++onAttach(Context)");
    try {
      mCallback = (OnMatchListListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString() + " must implement OnDataSent");
    }

    Bundle arguments = getArguments();
    if (arguments != null) {
      mSettings = (UserSetting) arguments.getSerializable(ARG_USER_SETTINGS);
    } else {
      Log.d(TAG, "Arguments were null.");
    }

    if (mSettings != null) {
      String queryPath = MatchSummary.ROOT + "/" + String.valueOf(mSettings.Year) + "/" + mSettings.TeamShortName;
      Log.d(TAG, "Query: " + queryPath);
      mMatchSummaryQuery = FirebaseDatabase.getInstance().getReference().child(queryPath).orderByChild("MatchDate");
      mValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

          mMatchSummaries = new ArrayList<>();
          for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            MatchSummary matchSummary = snapshot.getValue(MatchSummary.class);
            if (matchSummary != null) {
              matchSummary.MatchId = snapshot.getKey();
              mMatchSummaries.add(matchSummary);
            }
          }

          updateUI();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
      };
      mMatchSummaryQuery.addValueEventListener(mValueEventListener);
    } else {
      Log.e(TAG, "Failed to get user settings from arguments.");
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    Log.d(TAG, "++onDestroy()");
    if (mMatchSummaryQuery != null && mValueEventListener != null) {
      mMatchSummaryQuery.removeEventListener(mValueEventListener);
    }
  }

  private void updateUI() {

    if (mMatchSummaries != null) {
      Log.d(TAG, "++updateUI()");
      MatchSummaryAdapter matchAdapter = new MatchSummaryAdapter(mMatchSummaries);
      mRecyclerView.setAdapter(matchAdapter);
      mCallback.onPopulated(matchAdapter.getItemCount());
    }
  }

  private class MatchSummaryAdapter extends RecyclerView.Adapter<MatchSummaryHolder> {

    private List<MatchSummary> mMatchSummaries;

    MatchSummaryAdapter(List<MatchSummary> matchSummaries) {

      mMatchSummaries = matchSummaries;
    }

    @Override
    public MatchSummaryHolder onCreateViewHolder(ViewGroup parent, int viewType) {

      LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
      return new MatchSummaryHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(MatchSummaryHolder holder, int position) {

      MatchSummary matchSummary = mMatchSummaries.get(position);
      holder.bind(matchSummary);
    }

    @Override
    public int getItemCount() { return mMatchSummaries.size(); }

    void setMatchSummaries(List<MatchSummary> matchSummaries) {

      mMatchSummaries = matchSummaries;
    }
  }

  private class MatchSummaryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final TextView mTitleTextView;
    private final TextView mMatchDateTextView;
    private final TextView mMatchScoreTextView;
    private final TextView mMatchStatusTextView;

    private MatchSummary mMatchSummary;

    MatchSummaryHolder(LayoutInflater inflater, ViewGroup parent) {
      super(inflater.inflate(R.layout.match_item, parent, false));

      itemView.setOnClickListener(this);
      mTitleTextView = itemView.findViewById(R.id.match_item_title);
      mMatchDateTextView = itemView.findViewById(R.id.match_item_date);
      mMatchScoreTextView = itemView.findViewById(R.id.match_item_score);
      mMatchStatusTextView = itemView.findViewById(R.id.match_item_status);
    }

    void bind(MatchSummary matchSummary) {

      mMatchSummary = matchSummary;
      mTitleTextView.setText(
        String.format(
          Locale.getDefault(),
          "%1s vs %2s",
          mMatchSummary.HomeTeamName,
          mMatchSummary.AwayTeamName));
      mMatchDateTextView.setText(Match.formatDateForDisplay(mMatchSummary.MatchDate));
      mMatchScoreTextView.setText(
        String.format(
          Locale.getDefault(),
          "%1d - %2d",
          mMatchSummary.HomeScore,
          mMatchSummary.AwayScore));
      mMatchStatusTextView.setText(mMatchSummary.IsFinal ? "FT" : "In Progress");
    }

    @Override
    public void onClick(View view) {

      Log.d(TAG, "++MatchSummaryHolder::onClick(View)");
      mCallback.onSelected(mMatchSummary.MatchId);
    }
  }
}
