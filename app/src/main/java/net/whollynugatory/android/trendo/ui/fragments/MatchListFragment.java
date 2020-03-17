/*
 * Copyright 2020 Ryan Ward
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package net.whollynugatory.android.trendo.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.whollynugatory.android.trendo.R;
import net.whollynugatory.android.trendo.db.viewmodel.TrendoViewModel;
import net.whollynugatory.android.trendo.db.views.MatchSummaryDetails;
import net.whollynugatory.android.trendo.ui.BaseActivity;
import net.whollynugatory.android.trendo.utils.PreferenceUtils;

public class MatchListFragment extends Fragment {

  private static final String TAG = BaseActivity.BASE_TAG + "MatchListFragment";

  public interface OnMatchListListener {

    void onMatchListPopulated(int size);

    void onMatchListItemSelected();
  }

  private OnMatchListListener mCallback;

  private RecyclerView mRecyclerView;

  private TrendoViewModel mTrendViewModel;

  private String mTeamId;

  public static MatchListFragment newInstance() {

    Log.d(TAG, "++newInstance()");
    return new MatchListFragment();
  }

  /*
      Fragment Override(s)
   */
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    Log.d(TAG, "++onActivityCreated(Bundle)");
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    updateUI();
  }

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);

    Log.d(TAG, "++onAttach(Context)");
    try {
      mCallback = (OnMatchListListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(
        String.format(Locale.ENGLISH, "Missing interface implementations for %s", context.toString()));
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    mTeamId = PreferenceUtils.getTeam(getContext());
    mTrendViewModel = new ViewModelProvider(this).get(TrendoViewModel.class);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    final View view = inflater.inflate(R.layout.fragment_match_list, container, false);
    mRecyclerView = view.findViewById(R.id.match_list_view);
    return view;
  }

  @Override
  public void onDetach() {
    super.onDetach();

    Log.d(TAG, "++onDetach()");
    mCallback = null;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    Log.d(TAG, "++onDestroy()");
  }

  @Override
  public void onResume() {
    super.onResume();

    Log.d(TAG, "++onResume()");
  }

  /*
      Private Method(s)
   */
  private void updateUI() {

    MatchSummaryAdapter matchSummaryAdapter = new MatchSummaryAdapter(getContext());
    mRecyclerView.setAdapter(matchSummaryAdapter);
    mTrendViewModel.getAllMatchSummaryDetails(mTeamId, PreferenceUtils.getSeason(getContext())).observe(
      getViewLifecycleOwner(),
      matchSummaryAdapter::setMatchSummaryDetailsList);
  }

  /*
    Adapter class for MatchSummary objects
   */
  private class MatchSummaryAdapter extends RecyclerView.Adapter<MatchSummaryAdapter.MatchSummaryHolder> {

    /*
      Holder class for MatchSummaryDetails
     */
    private class MatchSummaryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

      private final TextView mTitleTextView;
      private final TextView mMatchDateTextView;
      private final TextView mMatchScoreTextView;

      private MatchSummaryDetails mMatchSummaryDetails;

      MatchSummaryHolder(View itemView) {
        super(itemView);

        mTitleTextView = itemView.findViewById(R.id.match_item_title);
        mMatchDateTextView = itemView.findViewById(R.id.match_item_date);
        mMatchScoreTextView = itemView.findViewById(R.id.match_item_score);

        itemView.setOnClickListener(this);
      }

      void bind(MatchSummaryDetails matchSummaryDetails) {

        mMatchSummaryDetails = matchSummaryDetails;

        mTitleTextView.setText(
          String.format(
            Locale.getDefault(),
            "%1s vs %2s",
            mMatchSummaryDetails.HomeName,
            mMatchSummaryDetails.AwayName));
        mMatchDateTextView.setText(
          String.format(
            Locale.US,
            "%d-%02d-%02d",
            mMatchSummaryDetails.Year,
            mMatchSummaryDetails.Month,
            mMatchSummaryDetails.Day));
        mMatchScoreTextView.setText(
          String.format(
            Locale.getDefault(),
            "%1d - %2d",
            mMatchSummaryDetails.HomeScore,
            mMatchSummaryDetails.AwayScore));
        if (getContext() != null && !mTeamId.equals(BaseActivity.DEFAULT_ID)) {
          if ((mTeamId.equals(mMatchSummaryDetails.HomeId) && mMatchSummaryDetails.HomeScore > mMatchSummaryDetails.AwayScore) ||
            (mTeamId.equals(mMatchSummaryDetails.AwayId) && mMatchSummaryDetails.AwayScore > mMatchSummaryDetails.HomeScore)) {
            mMatchScoreTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.favorite));
          } else if ((mTeamId.equals(mMatchSummaryDetails.HomeId) && mMatchSummaryDetails.HomeScore < mMatchSummaryDetails.AwayScore) ||
            (mTeamId.equals(mMatchSummaryDetails.AwayId) && mMatchSummaryDetails.AwayScore < mMatchSummaryDetails.HomeScore)) {
            mMatchScoreTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.ahead));
          } else {
            mMatchScoreTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.behind));
          }
        }
      }

      @Override
      public void onClick(View view) {

        Log.d(TAG, "++MatchSummaryHolder::onClick(View)");
        mCallback.onMatchListItemSelected();
      }
    }

    private final LayoutInflater mInflater;
    private List<MatchSummaryDetails> mMatchSummaryDetailsList;

    MatchSummaryAdapter(Context context) {

      mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MatchSummaryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

      View itemView = mInflater.inflate(R.layout.match_item, parent, false);
      return new MatchSummaryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchSummaryHolder holder, int position) {

      if (mMatchSummaryDetailsList != null) {
        MatchSummaryDetails matchSummaryDetails = mMatchSummaryDetailsList.get(position);
        holder.bind(matchSummaryDetails);
      } else {
        // No books!
      }
    }

    @Override
    public int getItemCount() {

      if (mMatchSummaryDetailsList != null) {
        return mMatchSummaryDetailsList.size();
      } else {
        return 0;
      }
    }

    void setMatchSummaryDetailsList(List<MatchSummaryDetails> matchSummaryDetailsList) {

      Log.d(TAG, "++setMatchSummaryDetailsList(List<MatchSummaryDetails>)");
      mMatchSummaryDetailsList = new ArrayList<>(matchSummaryDetailsList);
      mCallback.onMatchListPopulated(mMatchSummaryDetailsList.size());
      notifyDataSetChanged();
    }
  }
}
