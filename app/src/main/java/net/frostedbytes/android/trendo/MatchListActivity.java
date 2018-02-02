package net.frostedbytes.android.trendo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.Locale;
import net.frostedbytes.android.trendo.models.Match;
import net.frostedbytes.android.trendo.models.MatchSummary;
import net.frostedbytes.android.trendo.models.Settings;

public class MatchListActivity extends BaseActivity {

  private static final String TAG = "MatchListActivity";

  private RecyclerView mRecyclerView;
  private TextView mErrorMessage;

  private RecyclerView.Adapter mAdapter;
  private Settings mSettings;
  private String mUserId;

  private Query mMatchSummaryQuery; /* mMatchQuery gets cleaned up by FirebaseRecyclerAdapter */
  private Query mSettingsQuery;

  private ValueEventListener mSettingsValueListener;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_match_list);

    mRecyclerView = findViewById(R.id.match_list_view);
    mErrorMessage = findViewById(R.id.match_list_text_error_message);

    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    mSettings = new Settings();
    mUserId = getIntent().getStringExtra(BaseActivity.ARG_USER);
    mSettingsQuery = FirebaseDatabase.getInstance().getReference().child("Settings").child(mUserId);
    mSettingsValueListener = new ValueEventListener() {

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {

        mSettings = dataSnapshot.getValue(Settings.class);
        onGatheringSettingsComplete();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

        Log.d(TAG, "++onCancelled(DatabaseError)");
        Log.e(TAG, databaseError.getMessage());
      }
    };
    mSettingsQuery.addValueEventListener(mSettingsValueListener);
  }

  @Override
  public void onResume() {
    super.onResume();

    Log.d(TAG, "++onResume()");
    updateUI();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    Log.d(TAG, "++onDestroy()");
    if (mSettingsQuery != null && mSettingsValueListener != null) {
      mSettingsQuery.removeEventListener(mSettingsValueListener);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {

    Log.d(TAG, "++onActivityResult(int, int, Intent)");
    if (resultCode != RESULT_OK) {
      Log.d(TAG, "Child activity returned cancelled.");
      return;
    }

    switch (requestCode) {
      case BaseActivity.REQUEST_MATCH_SELECT:
        break;
      case BaseActivity.REQUEST_SETTINGS:
        mSettings = new Settings((Settings)data.getSerializableExtra(BaseActivity.ARG_SETTINGS));
        onGatheringSettingsComplete();
        break;
      default:
        Log.w(TAG, "Unknown request code: " + requestCode);
        break;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    Log.d(TAG, "++onCreateOptionsMenu(Menu)");
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    Log.d(TAG, "++onOptionsItemSelected(MenuItem)");
    int i = item.getItemId();
    switch (i) {
      case R.id.action_logout:
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, SignInActivity.class));
        finish();
        return true;
      case R.id.action_settings:
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        settingsIntent.putExtra(BaseActivity.ARG_USER, mUserId);
        startActivityForResult(settingsIntent, BaseActivity.REQUEST_SETTINGS);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void onGatheringSettingsComplete() {

    Log.d(TAG, "++onGatheringSettingsComplete()");
    if (mSettings == null || mSettings.TeamShortName.isEmpty()) {
      Log.d(TAG, "No team settings information found; starting settings activity.");
      Intent settingsIntent = new Intent(this, SettingsActivity.class);
      settingsIntent.putExtra(BaseActivity.ARG_USER, mUserId);
      startActivityForResult(settingsIntent, BaseActivity.REQUEST_SETTINGS);
    } else {
      String queryPath = "MatchSummaries/" + String.valueOf(mSettings.Year) + "/" + mSettings.TeamShortName;
      mMatchSummaryQuery = FirebaseDatabase.getInstance().getReference().child(queryPath).orderByChild("MatchDate");

      mAdapter = newAdapter();

      // scroll to bottom on new messages
      mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {

          mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
        }
      });

      mRecyclerView.setAdapter(mAdapter);
      updateUI();
    }
  }

  private void updateSubtitle() {

    Log.d(TAG, "++updateSubtitle()");
    AppCompatActivity activity = this;
    ActionBar bar = activity.getSupportActionBar();
    if (bar != null) {
      if (mAdapter != null) {
        bar.setSubtitle(getResources().getQuantityString(R.plurals.subtitle, mAdapter.getItemCount(), mSettings.TeamShortName, mAdapter.getItemCount()));
      } else {
        bar.setSubtitle(mSettings.TeamShortName);
      }
    }
  }

  private void updateUI() {
    Log.d(TAG, "++updateUI()");
    if (mAdapter != null) {
      if (mAdapter.getItemCount() > 0) {
        mErrorMessage.setText("");
      } else {
        mErrorMessage.setText(String.format(getString(R.string.err_no_results_for_team), mSettings.TeamShortName));
      }
    }

    updateSubtitle();
  }

  private RecyclerView.Adapter newAdapter() {

    FirebaseRecyclerOptions<MatchSummary> options = new FirebaseRecyclerOptions.Builder<MatchSummary>()
      .setQuery(mMatchSummaryQuery, MatchSummary.class)
      .setLifecycleOwner(this)
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
      Intent intent = new Intent(getApplicationContext(), TrendActivity.class);
      intent.putExtra(BaseActivity.ARG_MATCH_ID, mMatchSummary.MatchId);
      startActivityForResult(intent, BaseActivity.REQUEST_MATCH_SELECT);
    }
  }
}
