package net.frostedbytes.android.trendo;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.database.Query;
import java.util.ArrayList;
import java.util.List;
import net.frostedbytes.android.trendo.fragment.MatchFragment;
import net.frostedbytes.android.trendo.models.Match;
import net.frostedbytes.android.trendo.models.Team;

/**
 * Adapted from:
 * https://github.com/mmazzarolo/firebase-recyclerview/tree/master/app/src/main/java/com/example/matteo/firebase_recycleview
 */
//public class MatchAdapter extends FirebaseRecyclerAdapter<MatchAdapter.ViewHolder, Match> {
//
//  private static final String TAG = "MatchAdapter";
//
//  private List<Team> sTeamList;
//
//  public MatchAdapter(Query query, @Nullable ArrayList<Match> items, @Nullable ArrayList<String> keys) {
//    super(query, items, keys);
//
//    Log.d(TAG, "++MatchAdapter(Query, ArrayList<Match>, ArrayList<String>");
//
//    // get team data for matches
//    sTeamList = new ArrayList<>();
//    sTeamList = MatchCenter.get().getTeams();
//  }
//
//  @Override
//  public MatchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//    Log.d(TAG, "++onCreateViewHolder(ViewGroup, int)");
//    View view = LayoutInflater.from(parent.getContext())
//        .inflate(R.layout.match_item, parent, false);
//    return new ViewHolder(view);
//  }
//
//  @Override
//  public void onBindViewHolder(MatchAdapter.ViewHolder holder, int position) {
//
//    Log.d(TAG, "++onBindViewHolder(MatchAdapter.ViewHolder, int)");
//    Match match = getItem(position);
//    holder.matchId = match.Id;
//    holder.mTitleTextView.setText(
//        String.format(
//            "%1s vs %2s",
//            MatchCenter.get().getTeam(match.HomeId).FullName,
//            MatchCenter.get().getTeam(match.AwayId).FullName));
//    holder.mMatchDateTextView.setText(Match.formatDateForDisplay(match.MatchDate));
//    holder.mMatchStatusTextView.setText(match.IsFinal ? "FT" : "In Progress");
//  }
//
//  @Override
//  protected void itemAdded(Match item, String key, int position) {
//
//    Log.d(TAG, "++itemAdded(Match, String, int)");
//  }
//
//  @Override
//  protected void itemChanged(Match oldItem, Match newItem, String key, int position) {
//
//    Log.d(TAG, "++itemChanged(Match, Match, String, int)");
//  }
//
//  @Override
//  protected void itemRemoved(Match item, String key, int position) {
//
//    Log.d(TAG, "++itemRemoved(Match, String, int)");
//  }
//
//  @Override
//  protected void itemMoved(Match item, String key, int oldPosition, int newPosition) {
//
//    Log.d(TAG, "++itemRemoved(Match, String, int, int)");
//  }
//
//  public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//
//    private static final String TAG = "ViewHolder";
//
//    private final TextView mTitleTextView;
//    private final TextView mMatchDateTextView;
//    private final TextView mMatchStatusTextView;
//    private final ImageView mMatchImageView;
//    private String matchId;
//
//    public ViewHolder(View view) {
//      super(view);
//
//      Log.d(TAG, "++ViewHolder(View)");
//      matchId = "";
//      mTitleTextView = itemView.findViewById(R.id.match_item_title);
//      mMatchDateTextView = itemView.findViewById(R.id.match_item_date);
//      mMatchStatusTextView = itemView.findViewById(R.id.match_item_status);
//      mMatchImageView = itemView.findViewById(R.id.match_item_delete);
//    }
//
//    @Override
//    public void onClick(View view) {
//
//      Intent intent = MatchPagerActivity.newIntent(getActivity(), matchId);
//      startActivityForResult(intent, MatchFragment.REQUEST_MATCH);
//    }
//  }
//}
