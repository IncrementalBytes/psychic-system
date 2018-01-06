package net.frostedbytes.android.trendo;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.database.Query;
import java.util.ArrayList;
import net.frostedbytes.android.trendo.models.Team;

public class TeamAdapter extends FirebaseRecyclerAdapter<TeamAdapter.ViewHolder, Team> {

  private static final String TAG = "TeamAdapter";

  public TeamAdapter(Query query, @Nullable ArrayList<Team> items, @Nullable ArrayList<String> keys) {
    super(query, items, keys);

    Log.d(TAG, "++TeamAdapter(Query, ArrayList<Match>, ArrayList<String>");
  }

  @Override
  public TeamAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    Log.d(TAG, "++onCreateViewHolder(ViewGroup, int)");
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.team_item, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(TeamAdapter.ViewHolder holder, int position) {

    Log.d(TAG, "++onBindViewHolder(MatchAdapter.ViewHolder, int)");
    Team team = getItem(position);
    holder.teamId = team.Id;
    holder.mTitleTextView.setText(team.toString());
  }

  @Override
  protected void itemAdded(Team item, String key, int position) {

    Log.d(TAG, "++itemAdded(Team, String, int)");
  }

  @Override
  protected void itemChanged(Team oldItem, Team newItem, String key, int position) {

    Log.d(TAG, "++itemChanged(Team, Team, String, int)");
  }

  @Override
  protected void itemRemoved(Team item, String key, int position) {

    Log.d(TAG, "++itemRemoved(Team, String, int)");
  }

  @Override
  protected void itemMoved(Team item, String key, int oldPosition, int newPosition) {

    Log.d(TAG, "++itemRemoved(Match, String, int, int)");
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "ViewHolder";

    private final TextView mTitleTextView;
    private String teamId;

    public ViewHolder(View view) {
      super(view);

      Log.d(TAG, "++ViewHolder(View)");
      teamId = "";
      mTitleTextView = itemView.findViewById(R.id.team_item_full_name);
    }
  }
}
