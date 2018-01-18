package net.frostedbytes.android.trendo.fragments;

import android.content.Context;
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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseReference.CompletionListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Match;
import net.frostedbytes.android.trendo.views.TouchableImageView;

public class MatchListFragment extends Fragment {

  private static final String TAG = "MatchListFragment";

  private RecyclerView mRecyclerView;

  protected static final Query sMatchQuery = FirebaseDatabase.getInstance().getReference().child("matches");

  MatchListListener mCallback;

  public interface MatchListListener {

    void onCreateMatchRequest();
    void onMatchSelected(String matchId);
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    Log.d(TAG, "++onAttach(Context)");
    try {
      mCallback = (MatchListListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException("Calling activity/fragment must implement MatchListListener.");
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle)");
    View view = inflater.inflate(R.layout.fragment_match_list, container, false);
    mRecyclerView = view.findViewById(R.id.match_list);
    mRecyclerView.setHasFixedSize(true);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    attachRecyclerViewAdapter();

    FloatingActionButton fab = view.findViewById(R.id.fab_new_match);
    fab.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {

        Log.d(TAG, "++onClick(View)");
        if (mCallback != null) {
          mCallback.onCreateMatchRequest();
        } else {
          Log.e(TAG, "Callback was null.");
        }
      }
    });

    return view;
  }

  @Override
  public void onStart() {
    super.onStart();

    Log.d(TAG, "++onStart()");
  }

  @Override
  public void onStop() {
    super.onStop();

    Log.d(TAG, "++onStop()");
  }

  private void attachRecyclerViewAdapter() {

    Log.d(TAG, "++attachRecyclerViewAdapter()");
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

  protected RecyclerView.Adapter newAdapter() {

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

        holder.bind(model);
      }

      @Override
      public void onDataChanged() {

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
          mMatch.HomeTeam.FullName,
          mMatch.AwayTeam.FullName));

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
              } else {
                Log.d(TAG, "getActivity() is null.");
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

      Log.d(TAG, "++MatchHolder::onClick(View)");
      if (mCallback != null) {
        mCallback.onMatchSelected(mMatch.Id);
      }
    }
  }
}
