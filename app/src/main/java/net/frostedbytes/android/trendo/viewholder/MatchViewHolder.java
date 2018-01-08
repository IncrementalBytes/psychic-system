package net.frostedbytes.android.trendo.viewholder;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import net.frostedbytes.android.trendo.R;
import net.frostedbytes.android.trendo.models.Match;

public class MatchViewHolder extends RecyclerView.ViewHolder {

  public TextView mTitleTextView;
  public TextView mMatchDateTextView;
  public TextView mMatchStatusTextView;
  public ImageView mDeleteMatchImageView;

  private Match mMatch;

  public MatchViewHolder(View itemView) {
    super(itemView);

    mTitleTextView = itemView.findViewById(R.id.match_item_title);
    mMatchDateTextView = itemView.findViewById(R.id.match_item_date);
    mMatchStatusTextView = itemView.findViewById(R.id.match_item_status);
    mDeleteMatchImageView = itemView.findViewById(R.id.match_item_delete);
//    mMatchImageView.setOnTouchListener(new OnTouchListener() {
//      @Override
//      public boolean onTouch(View view, MotionEvent motionEvent) {
//
//        switch (motionEvent.getAction()) {
//          case MotionEvent.ACTION_DOWN:
//            if (getActivity() != null) {
//              AlertDialog dialog = new AlertDialog.Builder(getActivity())
//                  .setTitle("Delete this match?")
//                  .setPositiveButton(android.R.string.ok,
//                      new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                          if (mMatch != null && mMatch.Id != null) {
//                            //updateUI();
//                          }
//                        }
//                      })
//                  .setNegativeButton(android.R.string.cancel,
//                      new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                        }
//                      })
//                  .create();
//              dialog.show();
//            } else {
//              System.out.println("getActivity() is null.");
//            }
//            break;
//          case MotionEvent.ACTION_UP:
//            view.performClick();
//            return true;
//        }
//
//        return true;
//      }
//    });
  }

  public void bindToMatch(Match match, View.OnClickListener deleteMatchClickListener) {

    mMatch = match;
    if (mMatch.HomeId != null && mMatch.AwayId != null) {
      mTitleTextView.setText(
          String.format(
              "%1s vs %2s",
              mMatch.HomeId,
              mMatch.AwayId));
    } else {
      mTitleTextView.setText("N/A");
    }

    mMatchDateTextView.setText(Match.formatDateForDisplay(mMatch.MatchDate));
    mMatchStatusTextView.setText(mMatch.IsFinal ? "FT" : "In Progress");

    mDeleteMatchImageView.setOnClickListener(deleteMatchClickListener);
  }
}
