/*
 * Copyright 2019 Ryan Ward
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

package net.frostedbytes.android.trendo.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    private static final String TAG = BaseActivity.BASE_TAG + MatchListFragment.class.getSimpleName();

    public interface OnMatchListListener {

        void onMatchListPopulated(int size);

        void onMatchListItemSelected();
    }

    private OnMatchListListener mCallback;

    private RecyclerView mRecyclerView;

    private ArrayList<MatchSummary> mMatchSummaries;
    private String mTeamId;

    public static MatchListFragment newInstance(ArrayList<MatchSummary> matchSummaries, String teamId) {

        LogUtils.debug(TAG, "++newInstance(ArrayList<>)");
        MatchListFragment fragment = new MatchListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(BaseActivity.ARG_MATCH_SUMMARIES, matchSummaries);
        args.putString(BaseActivity.ARG_TEAM_ID, teamId);
        fragment.setArguments(args);
        return fragment;
    }

    /*
        Fragment Override(s)
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        LogUtils.debug(TAG, "++onAttach(Context)");
        try {
            mCallback = (OnMatchListListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                String.format(Locale.ENGLISH, "Missing interface implementations for %s", context.toString()));
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            mMatchSummaries = arguments.getParcelableArrayList(BaseActivity.ARG_MATCH_SUMMARIES);
            mTeamId = arguments.getString(BaseActivity.ARG_TEAM_ID);
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

    /*
        Private Method(s)
     */
    private void updateUI() {

        if (mMatchSummaries != null && mMatchSummaries.size() > 0) {
            LogUtils.debug(TAG, "++updateUI()");
            MatchSummaryAdapter matchAdapter = new MatchSummaryAdapter(mMatchSummaries);
            mRecyclerView.setAdapter(matchAdapter);
            mCallback.onMatchListPopulated(matchAdapter.getItemCount());
        } else {
            mCallback.onMatchListPopulated(0);
        }
    }

    /**
     * Adapter class for MatchSummary objects
     */
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
        public int getItemCount() {
            return mMatchSummaries.size();
        }
    }

    /**
     * Holder class for MatchSummary objects
     */
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
            if (getContext() != null && !mTeamId.equals(BaseActivity.DEFAULT_ID)) {
                if ((mTeamId.equals(mMatchSummary.HomeId) && mMatchSummary.HomeScore > mMatchSummary.AwayScore) ||
                    (mTeamId.equals(mMatchSummary.AwayId) && mMatchSummary.AwayScore > mMatchSummary.HomeScore)) {
                    mMatchScoreTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.favorite));
                } else if ((mTeamId.equals(mMatchSummary.HomeId) && mMatchSummary.HomeScore < mMatchSummary.AwayScore) ||
                    (mTeamId.equals(mMatchSummary.AwayId) && mMatchSummary.AwayScore < mMatchSummary.HomeScore)) {
                    mMatchScoreTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.ahead));
                } else {
                    mMatchScoreTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.behind));
                }
            }

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
                } else if (mMatchSummary.MatchDate.compareTo(currentDate) > 0) {
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
            mCallback.onMatchListItemSelected();
        }
    }
}
