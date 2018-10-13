package net.frostedbytes.android.trendo.fragments;

import static net.frostedbytes.android.trendo.BaseActivity.BASE_TAG;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.utils.DateUtils;
import net.frostedbytes.android.trendo.utils.LogUtils;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.MatchSummary;

public class MatchListFragment extends Fragment {

  private static final String TAG = BASE_TAG + MatchListFragment.class.getSimpleName();

  public interface OnMatchListListener {

    void onPopulated(int size);
    void onSelected();
  }

  private OnMatchListListener mCallback;

  private RecyclerView mRecyclerView;

  private ArrayList<MatchSummary> mMatchSummaries;

  public static MatchListFragment newInstance(ArrayList<MatchSummary> matchSummaries) {

    LogUtils.debug(TAG, "++newInstance(ArrayList<>)");
    MatchListFragment fragment = new MatchListFragment();
    Bundle args = new Bundle();
    args.putParcelableArrayList(BaseActivity.ARG_MATCH_SUMMARIES, matchSummaries);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    LogUtils.debug(TAG, "++onAttach(Context)");
    try {
      mCallback = (OnMatchListListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(
        String.format(Locale.ENGLISH, "%s must implement onPopulated(int) and onSelected(String).", context.toString()));
    }

    Bundle arguments = getArguments();
    if (arguments != null) {
      mMatchSummaries = arguments.getParcelableArrayList(BaseActivity.ARG_MATCH_SUMMARIES);
    } else {
      LogUtils.error(TAG, "Arguments were null.");
    }
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
  public void onDestroy() {
    super.onDestroy();

    LogUtils.debug(TAG, "++onDestroy()");

    mMatchSummaries = null;
  }

  @Override
  public void onResume() {
    super.onResume();

    LogUtils.debug(TAG, "++onResume()");
    updateUI();
  }

  private void updateUI() {

    if (mMatchSummaries != null && mMatchSummaries.size() > 0) {
      LogUtils.debug(TAG, "++updateUI()");
      MatchSummaryAdapter matchAdapter = new MatchSummaryAdapter(mMatchSummaries);
      mRecyclerView.setAdapter(matchAdapter);
      mCallback.onPopulated(matchAdapter.getItemCount());
    } else {
      mCallback.onPopulated(0);
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
          mMatchSummary.HomeFullName,
          mMatchSummary.AwayFullName));
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
        Calendar calendar = Calendar.getInstance();
        String currentDate = String.format(
          Locale.ENGLISH,
          "%04d%02d%02d",
          calendar.get(Calendar.YEAR),
          calendar.get(Calendar.MONTH) + 1, // note: month value is based on 0-11
          calendar.get(Calendar.DAY_OF_MONTH));
        if (mMatchSummary.MatchDate.compareTo(currentDate) == 0) {
          mMatchStatusTextView.setText(R.string.in_progress);
        } else if (mMatchSummary.MatchDate.compareTo(currentDate) > 0){
          mMatchStatusTextView.setText(R.string.upcoming);
        } else {
          mMatchStatusTextView.setText(R.string.delayed);
        }

        mMatchStatusTextView.setTypeface(null, Typeface.ITALIC);
      }
    }

    @Override
    public void onClick(View view) {

      LogUtils.debug(TAG, "++MatchSummaryHolder::onClick(View)");
      mCallback.onSelected();
    }
  }
}
