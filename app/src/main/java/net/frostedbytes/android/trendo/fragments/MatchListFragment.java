package net.frostedbytes.android.trendo.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import net.frostedbytes.android.trendo.MatchCenter;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Match;
import net.frostedbytes.android.trendo.views.TouchableImageView;

public class MatchListFragment extends Fragment implements MatchCreationFragment.OnMatchCreatedListener {

  private static final String TAG = "MatchListFragment";

  private static final String DIALOG_MATCH = "Create Match dialog";

  OnMatchSelectedListener mCallback;

  public interface OnMatchSelectedListener {

    void onMatchSelected(String matchId);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    try {
      mCallback = (OnMatchSelectedListener) getTargetFragment();
    } catch (ClassCastException e) {
      throw new ClassCastException("Error in retrieving data. Please try again");
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    View view = inflater.inflate(R.layout.fragment_match_list, container, false);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    RecyclerView matchRecyclerView = view.findViewById(R.id.match_list);
    matchRecyclerView.setLayoutManager(linearLayoutManager);

    List<Match> matches = MatchCenter.get().getMatches();
    MatchAdapter matchAdapter = new MatchAdapter(matches);
    matchRecyclerView.setAdapter(matchAdapter);
    matchAdapter.notifyDataSetChanged();

    FloatingActionButton fab = view.findViewById(R.id.fab_new_match);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        MatchCreationFragment dialog = MatchCreationFragment.newInstance();
        dialog.show(getChildFragmentManager(), DIALOG_MATCH);
      }
    });

    return view;
  }

  @Override
  public void onMatchCreated(String matchId) {

    Log.d(TAG, "++onMatchCreated(String)");
    mCallback.onMatchSelected(matchId);
  }

  private class MatchAdapter extends RecyclerView.Adapter<MatchHolder> {

    private List<Match> mMatches;

    MatchAdapter(List<Match> matches) {

      mMatches = matches;
    }

    @Override
    public MatchHolder onCreateViewHolder(ViewGroup parent, int viewType) {

      LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
      return new MatchHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(MatchHolder holder, int position) {

      Match match = mMatches.get(position);
      holder.bind(match);
    }

    @Override
    public int getItemCount() {
      return mMatches.size();
    }
  }

  private class MatchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final TextView mTitleTextView;
    private final TextView mMatchDateTextView;
    private final TextView mMatchStatusTextView;
    private final TouchableImageView mMatchImageView;

    private Match mMatch;

    MatchHolder(LayoutInflater inflater, ViewGroup parent) {
      super(inflater.inflate(R.layout.match_item, parent, false));

      itemView.setOnClickListener(this);
      mTitleTextView = itemView.findViewById(R.id.match_item_title);
      mMatchDateTextView = itemView.findViewById(R.id.match_item_date);
      mMatchStatusTextView = itemView.findViewById(R.id.match_item_status);
      mMatchImageView = itemView.findViewById(R.id.match_item_delete);
    }

    void bind(Match match) {

      mMatch = match;
      if (mMatch.HomeId != null && mMatch.AwayId != null) {
        mTitleTextView.setText(
            String.format(
                "%1s vs %2s",
                MatchCenter.get().getTeam(mMatch.HomeId).FullName,
                MatchCenter.get().getTeam(mMatch.AwayId).FullName)
        );
      } else {
        mTitleTextView.setText("N/A");
      }

      mMatchDateTextView.setText(Match.formatDateForDisplay(mMatch.MatchDate));
      mMatchStatusTextView.setText(mMatch.IsFinal ? "FT" : "In Progress");
      mMatchImageView.setOnTouchListener(new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

          switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
              if (getActivity() != null) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("Delete this match?")
                    .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {

                          @Override
                          public void onClick(DialogInterface dialog, int which) {

                            if (mMatch != null && mMatch.Id != null) {
                              // TODO: submit request to web service to remove the match
                            }
                          }
                        })
                    .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {

                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                          }
                        });
                dialog.create();
                dialog.show();
              } else {
                System.out.println("getActivity() is null.");
              }
              break;
            case MotionEvent.ACTION_UP:
              view.performClick();
              return true;
          }

          return true;
        }
      });
    }

    @Override
    public void onClick(View view) {

      mCallback.onMatchSelected(mMatch.Id);
    }
  }
}
