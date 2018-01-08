package net.frostedbytes.android.trendo.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Match;
import net.frostedbytes.android.trendo.viewholder.MatchViewHolder;

public class MatchListFragment extends Fragment {

  private static final String TAG = "MatchListFragment";

  private DatabaseReference mDatabase;
  private FirebaseRecyclerAdapter<Match, MatchViewHolder> mMatchAdapter;

  private RecyclerView mRecycler;
  private LinearLayoutManager mManager;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "++onCreateView(LayoutInflater, ViewGroup, Bundle");
    View view = inflater.inflate(R.layout.fragment_match_list, container, false);
    mDatabase = FirebaseDatabase.getInstance().getReference();

    mRecycler = view.findViewById(R.id.match_list);
    mRecycler.setHasFixedSize(true);

    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    Log.d(TAG, "++onActivityCreated(Bundle");

    // set up layout manager, reverse layout
    mManager = new LinearLayoutManager(getActivity());
    mManager.setReverseLayout(true);
    mManager.setStackFromEnd(true);
    mRecycler.setLayoutManager(mManager);

    // set up FirebaseRecyclerAdapter with the query
    Query matchesQuery = mDatabase.child("matches");
    FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Match>()
        .setQuery(matchesQuery, Match.class)
        .build();

    mMatchAdapter = new FirebaseRecyclerAdapter<Match, MatchViewHolder>(options) {

      @Override
      public MatchViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new MatchViewHolder(inflater.inflate(R.layout.match_item, viewGroup, false));
      }

      @Override
      protected void onBindViewHolder(MatchViewHolder viewHolder, int position, final Match model) {

        final DatabaseReference matchtRef = getRef(position);

        // set click listener for the whole match view
        final String matchKey = matchtRef.getKey();
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

          @Override
          public void onClick(View v) {

            // Create fragment and give it an argument specifying the article it should show
            MatchDetailFragment matchDetailFragment = new MatchDetailFragment();
            Bundle args = new Bundle();
            args.putString(MatchDetailFragment.ARG_MATCH_ID, matchKey);
            matchDetailFragment.setArguments(args);

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, matchDetailFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();

//            mShareMatchId.shareMatchId(matchKey);
//            Intent intent = new Intent(getActivity(), MatchDetailActivity.class);
//            intent.putExtra(MatchDetailActivity.EXTRA_MATCH_KEY, matchKey);
//            startActivity(intent);
          }
        });

        // Bind Post to ViewHolder, setting OnClickListener for the star button
        viewHolder.bindToMatch(model, new View.OnClickListener() {

          @Override
          public void onClick(View deleteMatchItemView) {

            if (getActivity() != null) {
              AlertDialog dialog = new AlertDialog.Builder(getActivity())
                  .setTitle("Delete this match?")
                  .setPositiveButton(android.R.string.ok,
                      new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                          // TODO: remove match from list (and update the UI?)
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
              Log.d(TAG, "getActivity() is null.");
            }
          }
        });
      }
    };

    mRecycler.setAdapter(mMatchAdapter);
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    Log.d(TAG, "++onAttach(Context");
    try {
    } catch (ClassCastException e) {
      throw new ClassCastException("Error in retrieving data. Please try again");
    }
  }

  @Override
  public void onStart() {
    super.onStart();

    Log.d(TAG, "++onStart()");
    if (mMatchAdapter != null) {
      mMatchAdapter.startListening();
    }
  }

  @Override
  public void onStop() {
    super.onStop();

    Log.d(TAG, "++onStop()");
    if (mMatchAdapter != null) {
      mMatchAdapter.stopListening();
    }
  }
}
