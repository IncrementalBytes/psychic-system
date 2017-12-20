package net.frostedbytes.android.trendo;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class MatchCenter {

  private static final String TAG = "MatchCenter";

  private static MatchCenter sMatchCenter;

  static MatchCenter get(Context context) {

    if (sMatchCenter == null) {
      System.out.println("Creating MatchCenter context.");
      sMatchCenter = new MatchCenter(context);
    }

    return sMatchCenter;
  }

  private MatchCenter(Context context) {

    // TODO: add asset import/initialization
  }

  List<Match> getMatches() {

    System.out.println("++" + TAG + "::getMatches()");
    List<Match> matches = new ArrayList<>();

    // TODO: query local database for information about known matches

    return matches;
  }
}
