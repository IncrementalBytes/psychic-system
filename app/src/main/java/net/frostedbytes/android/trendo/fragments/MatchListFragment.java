package net.frostedbytes.android.trendo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import java.util.Locale;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Match;
import net.frostedbytes.android.trendo.models.MatchSummary;
import net.frostedbytes.android.trendo.models.Settings;

public class MatchListFragment extends Fragment {

  private static final String TAG = "MatchListFragment";

  static final String ARG_USER_SETTINGS = "user_settings";

  public interface OnMatchListListener {

    void onPopulated(int size);
    void onSelected(String matchId);
  }

  private RecyclerView.Adapter mRecyclerViewAdapter;
  private Settings mSettings;
  private OnMatchListListener mCallback;

  private RecyclerView mRecyclerView;
  private TextView mErrorMessage;

  private Query mMatchSummaryQuery; /* mMatchQuery gets cleaned up by FirebaseRecyclerAdapter */

  public static MatchListFragment newInstance(Settings userSettings) {

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

    Bundle arguments = getArguments();
    if (arguments != null) {
      mSettings = (Settings) arguments.getSerializable(ARG_USER_SETTINGS);
    } else {
      Log.d(TAG, "Arguments were null.");
    }

    mRecyclerView = view.findViewById(R.id.match_list_view);
    mErrorMessage = view.findViewById(R.id.match_list_text_error_message);

    final LinearLayoutManager manager = new LinearLayoutManager(getActivity());
    mRecyclerView.setLayoutManager(manager);

    String queryPath = "MatchSummaries/" + String.valueOf(mSettings.Year) + "/" + mSettings.TeamShortName;
    Log.d(TAG, "Query: " + queryPath);
    mMatchSummaryQuery = FirebaseDatabase.getInstance().getReference().child(queryPath).orderByChild("MatchDate");

    mRecyclerViewAdapter = newAdapter();

    // scroll to bottom on new messages
    mRecyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

      @Override
      public void onItemRangeInserted(int positionStart, int itemCount) {

        mRecyclerView.smoothScrollToPosition(mRecyclerViewAdapter.getItemCount());
      }
    });

    mRecyclerView.setAdapter(mRecyclerViewAdapter);
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
  }

  private void updateSubtitle() {

    Log.d(TAG, "++updateSubtitle()");
    mCallback.onPopulated(mRecyclerViewAdapter.getItemCount());
  }

  private void updateUI() {
    Log.d(TAG, "++updateUI()");
    if (mRecyclerViewAdapter != null && mRecyclerViewAdapter.getItemCount() > 0) {
      mErrorMessage.setText("");
    } else {
      mErrorMessage.setText(String.format(getString(R.string.err_no_results_for_team), mSettings.TeamShortName));
    }

    updateSubtitle();
  }

  private RecyclerView.Adapter newAdapter() {

    FirebaseRecyclerOptions<MatchSummary> options = new FirebaseRecyclerOptions.Builder<MatchSummary>()
      .setQuery(mMatchSummaryQuery, MatchSummary.class)
      .setLifecycleOwner(getActivity())
      .build();

    return new FirebaseRecyclerAdapter<MatchSummary, MatchSummaryHolder>(options) {

      @Override
      public MatchSummaryHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new MatchSummaryHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.match_item, parent, false));
      }

      @Override
      protected void onBindViewHolder(@NonNull MatchSummaryHolder holder, int position, @NonNull MatchSummary model) {

        model.MatchId = getRef(position).getKey();
        holder.bind(model);
      }

      @Override
      public void onDataChanged() {

        updateUI();
      }
    };
  }

  private class MatchSummaryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final TextView mTitleTextView;
    private final TextView mMatchDateTextView;
    private final TextView mMatchScoreTextView;
    private final TextView mMatchStatusTextView;

    private MatchSummary mMatchSummary;

    MatchSummaryHolder(View itemView) {
      super(itemView);

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
