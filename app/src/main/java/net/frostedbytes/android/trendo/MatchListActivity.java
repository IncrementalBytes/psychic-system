package net.frostedbytes.android.trendo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseReference.CompletionListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import net.frostedbytes.android.trendo.models.Match;
import net.frostedbytes.android.trendo.views.TouchableImageView;

public class MatchListActivity extends BaseActivity {

  private static final String TAG = "MatchListActivity";

  // Note: mMatchQuery gets cleaned up by FirebaseRecyclerAdapter
  private static final Query sMatchQuery = FirebaseDatabase.getInstance().getReference().child("matches").orderByChild("MatchDate");

  private RecyclerView mRecyclerView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_match_list);

    mRecyclerView = findViewById(R.id.match_list);
    mRecyclerView.setHasFixedSize(true);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
  }

  @Override
  public void onResume() {
    super.onResume();

    Log.d(TAG, "++onResume()");
    updateUI();
  }

  @Override
  public void onStop() {
    super.onStop();

    Log.d(TAG, "++onStop()");
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {

    Log.d(TAG, "++onActivityResult(int, int, Intent)");
    if (resultCode != RESULT_OK) {
      Log.d(TAG, "Child activity returned cancelled.");
      return;
    }

    if (requestCode == BaseActivity.RESULT_MATCH_CREATE || requestCode == BaseActivity.RESULT_MATCH_SELECT) {
      updateUI();
    } else if (requestCode == BaseActivity.RESULT_SETTINGS) {
      // TODO: if year has changed, re-query the matches for that year
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
      case R.id.action_create_match:
        Intent intent = new Intent(getApplicationContext(), MatchCreationActivity.class);
        startActivityForResult(intent, BaseActivity.RESULT_MATCH_CREATE);
        return true;
      case R.id.action_logout:
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, SignInActivity.class));
        finish();
        return true;
      case R.id.action_settings:
        startActivityForResult(new Intent(this, SettingsActivity.class), BaseActivity.RESULT_SETTINGS);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void updateUI() {

    Log.d(TAG, "++updateUI()");
    showProgressDialog("Getting data...");
    final RecyclerView.Adapter adapter = newAdapter();

    // scroll to bottom on new messages
    adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

      @Override
      public void onItemRangeInserted(int positionStart, int itemCount) {

        mRecyclerView.smoothScrollToPosition(adapter.getItemCount());
      }
    });

    mRecyclerView.setAdapter(adapter);
  }

  private RecyclerView.Adapter newAdapter() {

    FirebaseRecyclerOptions<Match> options = new FirebaseRecyclerOptions.Builder<Match>()
      .setQuery(sMatchQuery, Match.class)
      .setLifecycleOwner(this)
      .build();

    return new FirebaseRecyclerAdapter<Match, MatchHolder>(options) {

      @Override
      public MatchHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new MatchHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.match_item, parent, false));
      }

      @Override
      protected void onBindViewHolder(@NonNull MatchHolder holder, int position, @NonNull Match model) {

        model.Id = getRef(position).getKey();
        holder.bind(model);
      }

      @Override
      public void onDataChanged() {

        Log.d(TAG, "++FirebaseRecyclerAdapter::onDataChanged()");
        hideProgressDialog();
      }
    };
  }

  private class MatchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final TextView mTitleTextView;
    private final TextView mMatchDateTextView;
    private final TextView mMatchStatusTextView;
    private final TouchableImageView mMatchImageView;

    private Match mMatch;

    MatchHolder(View itemView) {
      super(itemView);

      itemView.setOnClickListener(this);
      mTitleTextView = itemView.findViewById(R.id.match_item_title);
      mMatchDateTextView = itemView.findViewById(R.id.match_item_date);
      mMatchStatusTextView = itemView.findViewById(R.id.match_item_status);
      mMatchImageView = itemView.findViewById(R.id.match_item_delete);
    }

    void bind(Match match) {

      mMatch = match;
      mTitleTextView.setText(
        String.format(
          "%1s vs %2s",
          mMatch.HomeTeamShortName,
          mMatch.AwayTeamShortName));

      //mMatchDateTextView.setText(Match.formatDateForDisplay(mMatch.MatchDate));
      mMatchDateTextView.setText(Match.formatDateForDisplay(mMatch.MatchDate));
      mMatchStatusTextView.setText(mMatch.IsFinal ? "FT" : "In Progress");
      mMatchImageView.setOnTouchListener(new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

          switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
              AlertDialog.Builder dialog = new AlertDialog.Builder(MatchListActivity.this);
              dialog.setTitle("Delete this match?")
                .setPositiveButton(android.R.string.ok,
                  new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                      if (mMatch != null && mMatch.Id != null) {
                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                        Query matchQuery = database.child("matches").child(mMatch.Id);
                        matchQuery.getRef().removeValue(new CompletionListener() {

                          @Override
                          public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            Log.d(TAG, "++matchQuery::onComplete(DatabaseError, DatabaseReference)");
                          }
                        });
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

      Log.d(TAG, "++MatchHolder::onClick(View)");
      Intent intent = new Intent(getApplicationContext(), MatchDetailActivity.class);
      intent.putExtra(BaseActivity.ARG_MATCH, mMatch);
      startActivityForResult(intent, BaseActivity.RESULT_MATCH_SELECT);
    }
  }
}
