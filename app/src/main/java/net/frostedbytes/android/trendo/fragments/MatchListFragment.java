package net.frostedbytes.android.trendo.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.utils.DateUtils;
import net.frostedbytes.android.trendo.utils.LogUtils;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.MatchSummary;
import net.frostedbytes.android.trendo.models.UserSetting;

public class MatchListFragment extends Fragment {

  private static final String TAG = MatchListFragment.class.getSimpleName();

  public interface OnMatchListListener {

    void onPopulated(int size);
    void onSelected(MatchSummary matchSummary);
  }

  private UserSetting mSettings;
  private OnMatchListListener mCallback;

  private RecyclerView mRecyclerView;

  private List<MatchSummary> mMatchSummaries;

  private Query mMatchSummaryQuery;
  private ValueEventListener mValueEventListener;

  public static MatchListFragment newInstance(UserSetting userSettings) {

    LogUtils.debug(TAG, "++newInstance(Settings)");
    MatchListFragment fragment = new MatchListFragment();
    Bundle args = new Bundle();
    args.putSerializable(BaseActivity.ARG_USER_SETTINGS, userSettings);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    LogUtils.debug(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    final View view = inflater.inflate(R.layout.fragment_match_list, container, false);

    mRecyclerView = view.findViewById(R.id.match_list_view);

    final LinearLayoutManager manager = new LinearLayoutManager(getActivity());
    mRecyclerView.setLayoutManager(manager);

    updateUI();

    return view;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    LogUtils.debug(TAG, "++onAttach(Context)");
    try {
      mCallback = (OnMatchListListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString() + " must implement onPopulated(int) and onSelected(String).");
    }

    Bundle arguments = getArguments();
    if (arguments != null) {
      mSettings = (UserSetting) arguments.getSerializable(BaseActivity.ARG_USER_SETTINGS);
    } else {
      LogUtils.error(TAG, "Arguments were null.");
    }

    if (mSettings != null) {
      String queryPath = MatchSummary.ROOT + "/" + String.valueOf(mSettings.Year) + "/" + mSettings.TeamShortName;
      LogUtils.debug(TAG, "Query: " + queryPath);
      mMatchSummaryQuery = FirebaseDatabase.getInstance().getReference().child(queryPath).orderByChild("MatchDay");
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
      LogUtils.error(TAG, "Failed to get user settings from arguments.");
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    LogUtils.debug(TAG, "++onDestroy()");
    if (mMatchSummaryQuery != null && mValueEventListener != null) {
      mMatchSummaryQuery.removeEventListener(mValueEventListener);
    }

    mMatchSummaries = null;
  }

  private void updateUI() {

    if (mMatchSummaries != null) {
      LogUtils.debug(TAG, "++updateUI()");
      MatchSummaryAdapter matchAdapter = new MatchSummaryAdapter(mMatchSummaries);
      mRecyclerView.setAdapter(matchAdapter);
      mCallback.onPopulated(matchAdapter.getItemCount());
    }
  }

  private class MatchSummaryAdapter extends RecyclerView.Adapter<MatchSummaryHolder> {

    private final List<MatchSummary> mMatchSummaries;

    MatchSummaryAdapter(List<MatchSummary> matchSummaries) {

      mMatchSummaries = matchSummaries;
    }

    @NonNull
    @Override
    public MatchSummaryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

      LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
      return new MatchSummaryHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchSummaryHolder holder, int position) {

      MatchSummary matchSummary = mMatchSummaries.get(position);
      holder.bind(matchSummary);
    }

    @Override
    public int getItemCount() { return mMatchSummaries.size(); }
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
      mMatchDateTextView.setText(DateUtils.formatDateForDisplay(mMatchSummary.MatchDate));
      mMatchScoreTextView.setText(
        String.format(
          Locale.getDefault(),
          "%1d - %2d",
          mMatchSummary.HomeScore,
          mMatchSummary.AwayScore));
      if (mMatchSummary.IsFinal) {
        mMatchStatusTextView.setText(R.string.full_time);
        mMatchStatusTextView.setTypeface(null, Typeface.BOLD);
      } else {
        mMatchStatusTextView.setText(R.string.in_progress);
        mMatchStatusTextView.setTypeface(null, Typeface.ITALIC);
      }
    }

    @Override
    public void onClick(View view) {

      LogUtils.debug(TAG, "++MatchSummaryHolder::onClick(View)");
      mCallback.onSelected(mMatchSummary);
    }
  }
}
