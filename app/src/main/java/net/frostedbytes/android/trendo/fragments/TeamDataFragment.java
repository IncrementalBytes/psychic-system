package net.frostedbytes.android.trendo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Team;
import net.frostedbytes.android.trendo.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static net.frostedbytes.android.trendo.BaseActivity.BASE_TAG;

public class TeamDataFragment extends Fragment {

    private static final String TAG = BASE_TAG + TeamDataFragment.class.getSimpleName();

    public interface OnTeamDataListener {

        void onTeamDataSynchronized(boolean needsRefreshing);
    }

    private OnTeamDataListener mCallback;

    private RecyclerView mRecyclerView;

    private ArrayList<Team> mReviewTeams;
    private ArrayList<Team> mTeams;

    public static TeamDataFragment newInstance(ArrayList<Team> teams) {

        LogUtils.debug(TAG, "++newInstance()");
        TeamDataFragment fragment = new TeamDataFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(BaseActivity.ARG_TEAMS, teams);
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
            mCallback = (OnTeamDataListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                String.format(Locale.ENGLISH, "Missing interface implementations for %s", context.toString()));
        }

        Bundle arguments = getArguments();
        if (arguments == null) {
            LogUtils.error(TAG, "Did not receive details about teams.");
            return;
        }

        try {
            if (arguments.getSerializable(BaseActivity.ARG_TEAMS) != null) {
                mTeams = arguments.getParcelableArrayList(BaseActivity.ARG_TEAMS);
            }
        } catch (ClassCastException cce) {
            LogUtils.debug(TAG, "%s", cce.getMessage());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LogUtils.debug(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
        mReviewTeams = new ArrayList<>();
        for (Team team : mTeams) {
            if (!team.IsLocal || !team.IsRemote) {
                mReviewTeams.add(team);
            }
        }

        View view = inflater.inflate(R.layout.fragment_default, container, false);
        if (mReviewTeams.size() > 0) {
            view = inflater.inflate(R.layout.fragment_data_list, container, false);
            mRecyclerView = view.findViewById(R.id.data_list_view);
            final LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(manager);
            Button synchronizeButton = view.findViewById(R.id.data_button_synchronize);
            synchronizeButton.setEnabled(true);
            synchronizeButton.setOnClickListener(v -> synchronizeTeamData());
            updateUI();
        } else {
            TextView defaultMessage = view.findViewById(R.id.default_text_message);
            defaultMessage.setText(getString(R.string.data_synchronized));
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LogUtils.debug(TAG, "++onDestroy()");
        mReviewTeams = null;
        mTeams = null;
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
    private void synchronizeTeamData() {

        LogUtils.debug(TAG, "++synchronizeTeamData()");
        boolean needsRefreshing = false;
        for (Team team : mReviewTeams) {
            LogUtils.debug(TAG, "Checking %s", team.toString());
            if (team.IsLocal && !team.IsRemote) {
                LogUtils.debug(TAG, "Missing %s from server data", team.toString());
                FirebaseDatabase.getInstance().getReference().child(Team.ROOT).child(team.Id).setValue(
                    team,
                    (databaseError, databaseReference) -> {

                        if (databaseError != null) {
                            if (databaseError.getCode() == 0) {
                                LogUtils.debug(TAG, "Successfully added/edited team.");
                            } else if (databaseError.getCode() < 0) {
                                LogUtils.error(TAG, "Could not create team: %s", databaseError.getMessage());
                            }
                        }
                    });

                needsRefreshing = true;
            } else if (!team.IsLocal && team.IsRemote) {
                LogUtils.warn(TAG, "Missing %s from local data", team.toString());
            }
        }

        mCallback.onTeamDataSynchronized(needsRefreshing);
    }

    private void updateUI() {

        if (mReviewTeams != null && mReviewTeams.size() > 0) {
            LogUtils.debug(TAG, "++updateUI()");
            TeamDataAdapter teamAdapter = new TeamDataAdapter(mReviewTeams);
            mRecyclerView.setAdapter(teamAdapter);
        }
    }

    /**
     * Adapter class for Team objects
     */
    private class TeamDataAdapter extends RecyclerView.Adapter<TeamDataHolder> {

        private final List<Team> mTeams;

        TeamDataAdapter(List<Team> teams) {

            mTeams = teams;
        }

        @NonNull
        @Override
        public TeamDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new TeamDataHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull TeamDataHolder holder, int position) {

            Team team = mTeams.get(position);
            holder.bind(team);
        }

        @Override
        public int getItemCount() {
            return mTeams.size();
        }
    }

    /**
     * Holder class for Team objects
     */
    private class TeamDataHolder extends RecyclerView.ViewHolder {

        private final TextView mTitleTextView;
        private final TextView mDateTextView;
        private final TextView mDetailsTextView;
        private final ImageView mLocalImageView;
        private final ImageView mRemoteImageView;

        private Team mTeam;

        TeamDataHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.data_item, parent, false));

            mTitleTextView = itemView.findViewById(R.id.data_text_title);
            mDateTextView = itemView.findViewById(R.id.data_text_date);
            mDetailsTextView = itemView.findViewById(R.id.data_text_details);
            mLocalImageView = itemView.findViewById(R.id.data_image_local);
            mRemoteImageView = itemView.findViewById(R.id.data_image_remote);
        }

        void bind(Team team) {

            mTeam = team;
            mTitleTextView.setText(mTeam.FullName);
            mDateTextView.setText("");
            mDetailsTextView.setText(
                String.format(
                    Locale.getDefault(),
                    "%s (%d - %s)",
                    mTeam.ShortName,
                    mTeam.Established,
                    mTeam.Defunct == 0 ? "present" : String.valueOf(mTeam.Defunct)));
            if (getContext() != null) {
                if (mTeam.IsLocal) {
                    mLocalImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_success_dark, getContext().getTheme()));
                } else {
                    mLocalImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_failure_dark, getContext().getTheme()));
                }

                if (mTeam.IsRemote) {
                    mRemoteImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_success_dark, getContext().getTheme()));
                } else {
                    mRemoteImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_failure_dark, getContext().getTheme()));
                }
            } else {
                LogUtils.warn(TAG, "Failed to get theme from context.");
            }
        }
    }
}
