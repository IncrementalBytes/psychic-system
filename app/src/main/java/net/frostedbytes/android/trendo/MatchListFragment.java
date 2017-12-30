package net.frostedbytes.android.trendo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MatchListFragment extends Fragment {

  private static final String TAG = "MatchListFragment";

  private RecyclerView mMatchRecyclerView;
  private MatchAdapter mMatchAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_list, container, false);
    mMatchRecyclerView = view.findViewById(R.id.recycler_view_list);
    mMatchRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    updateUI();

    return view;
  }

  @Override
  public void onResume() {
    super.onResume();

    updateUI();
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

  private void updateUI() {

    System.out.println("++" + TAG + "::updateUI()");
    List<Match> matches = MatchCenter.get(getActivity()).getMatches();
    Collections.sort(matches, new Comparator<Match>() {
      @Override
      public int compare(Match match1, Match match2) {

        if (match1.getMatchDate().getTime() > match2.getMatchDate().getTime()) {
          return -1;
        }

        if (match1.getMatchDate().getTime() < match2.getMatchDate().getTime()) {
          return 1;
        }

        return 0;
      }
    });

    if (mMatchAdapter == null) {
      mMatchAdapter = new MatchAdapter(matches);
      mMatchRecyclerView.setAdapter(mMatchAdapter);
    } else {
      mMatchAdapter.setMatches(matches);
      mMatchAdapter.notifyDataSetChanged();
    }
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

    void setMatches(List<Match> matches) {

      mMatches = matches;
    }
  }

  private class MatchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final TextView mTitleTextView;
    private final TextView mMatchDateTextView;
    private final TextView mMatchStatusTextView;
    private final TouchableImageView mMatchImageView;

    private Match mMatch;

    MatchHolder(LayoutInflater inflater, ViewGroup parent) {
      super(inflater.inflate(R.layout.list_item, parent, false));

      itemView.setOnClickListener(this);
      mTitleTextView = itemView.findViewById(R.id.list_item_title);
      mMatchDateTextView = itemView.findViewById(R.id.list_item_match_date);
      mMatchStatusTextView = itemView.findViewById(R.id.list_item_match_status);
      mMatchImageView = itemView.findViewById(R.id.list_item_delete);
      mMatchImageView.setOnTouchListener(new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

          switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
              if (getActivity() != null) {
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setTitle("Delete this match?")
                    .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {

                          @Override
                          public void onClick(DialogInterface dialog, int which) {

                            if (mMatch != null && mMatch.getId() != null) {
                              // TODO: submit request to web service to remove the match
                              updateUI();
                            }
                          }
                        })
                    .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {

                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                          }
                        })
                    .create();
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

    void bind(Match match) {

      mMatch = match;
      if (mMatch.getHomeId() != null && mMatch.getAwayId() != null) {
        mTitleTextView.setText(
            String.format(
                "%1s vs %2s",
                MatchCenter.get(getActivity()).getTeam(mMatch.getHomeId()).getFullName(),
                MatchCenter.get(getActivity()).getTeam(mMatch.getAwayId()).getFullName())
        );
      } else {
        mTitleTextView.setText("N/A");
      }

      mMatchDateTextView.setText(Match.formatDateForDisplay(mMatch.getMatchDate()));
      mMatchStatusTextView.setText(mMatch.getIsMatchFinal() ? "FT" : "In Progress");
    }

    @Override
    public void onClick(View view) {

      Intent intent = MatchPagerActivity.newIntent(getActivity(), mMatch.getId());
      startActivityForResult(intent, MatchFragment.REQUEST_MATCH);
    }
  }
}
