package net.frostedbytes.android.trendo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import net.frostedbytes.android.trendo.TrendoDbSchema.MatchTable;
import net.frostedbytes.android.trendo.TrendoDbSchema.TeamTable;

class MatchCenter {

  private static final String TAG = "MatchCenter";

  private static MatchCenter sMatchCenter;

  private final SQLiteDatabase mDatabase;

  static MatchCenter get(Context context) {

    if (sMatchCenter == null) {
      System.out.println("Creating MatchCenter context.");
      sMatchCenter = new MatchCenter(context);
    }

    return sMatchCenter;
  }

  private MatchCenter(Context context) {

    // TODO: add asset import/initialization
    try {
      String dbDataPath = context.getApplicationInfo().dataDir + "/" + TrendoDbSchema.ASSET_DATABASES_PATH;
      File file = new File(dbDataPath + "/" + TrendoDbSchema.DATABASE_NAME);
      if (!file.exists()) {
        System.out.println("Copying database from assets...");
        InputStream is = context.getAssets().open(TrendoDbSchema.DATABASE_NAME);
        File f = new File(dbDataPath + "/");
        if (!f.exists()) {
          if (!f.mkdir()) {
            System.out.println("ERR: Did not create asset directory.");
          }
        }

        String dest = dbDataPath + "/" + TrendoDbSchema.DATABASE_NAME;
        FileOutputStream outs = new FileOutputStream(dest);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
          outs.write(buffer, 0, length);
        }

        outs.flush();
        outs.close();
        is.close();
        System.out.println("WARN: Database copy from assets complete.");
      }
    } catch (IOException ioe) {
      System.out.println("ERR: Failed to extract database from assets: " + ioe.toString());
    } catch (SQLiteException ex) {
      System.out.println("ERR: SQLite failed: " + ex.toString());
    }

    // get table versions from web service and compare to local table versions
    // if no table versions are found or versions are less than web service, pull data locally and construct db
    // NOTE: converting data stream from webservice schema dependent
    mDatabase = new TrendoDatabaseHelper(context).getWritableDatabase();
  }

  Match getMatch(UUID matchId) {

    System.out.println("++" + TAG + "::getMatch(UUID)");
    if (matchId == null) {
      return null;
    }

    try (Cursor cursor = mDatabase.query(
        MatchTable.NAME,
        null,
        MatchTable.Cols.ID + " = ?",
        new String[]{matchId.toString()},
        null,
        null,
        null)) {
      if (cursor.getCount() == 0) {
        return null;
      }

      cursor.moveToFirst();
      String idString = cursor.getString(cursor.getColumnIndex(MatchTable.Cols.ID));
      String homeIdString = cursor.getString(cursor.getColumnIndex(MatchTable.Cols.HOME_ID));
      String awayIdString = cursor.getString(cursor.getColumnIndex(MatchTable.Cols.AWAY_ID));
      long date = cursor.getLong(cursor.getColumnIndex(MatchTable.Cols.MATCH_DATE));
      int isFinal = cursor.getInt(cursor.getColumnIndex(MatchTable.Cols.FINAL));

      // create match object from the data we received from cursor
      Match match = new Match(UUID.fromString(idString));
      if (homeIdString != null) {
        match.setHomeId(UUID.fromString(homeIdString));
      }

      if (awayIdString != null) {
        match.setAwayId(UUID.fromString(awayIdString));
      }

      match.setMatchDate(new Date(date));
      match.setIsMatchFinal(isFinal != 0);
      System.out.println("Found " + match.getId().toString() + " for " + match.toString());
      return match;
    }
  }

  List<Match> getMatches() {

    System.out.println("++" + TAG + "::getMatches()");
    List<Match> matches = new ArrayList<>();
    String sortOrder = MatchTable.Cols.MATCH_DATE + " ASC";
    try (Cursor cursor = mDatabase.query(
        MatchTable.NAME,
        null,
        null,
        null,
        null,
        null,
        sortOrder
    )) {
      if (cursor.getCount() == 0) {
        return null;
      }

      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
        String idString = cursor.getString(cursor.getColumnIndex(MatchTable.Cols.ID));
        String homeIdString = cursor.getString(cursor.getColumnIndex(MatchTable.Cols.HOME_ID));
        String awayIdString = cursor.getString(cursor.getColumnIndex(MatchTable.Cols.AWAY_ID));
        long date = cursor.getLong(cursor.getColumnIndex(MatchTable.Cols.MATCH_DATE));
        int isFinal = cursor.getInt(cursor.getColumnIndex(MatchTable.Cols.FINAL));

        // create match object from the data we received from cursor
        Match match = new Match(UUID.fromString(idString));
        if (homeIdString != null) {
          match.setHomeId(UUID.fromString(homeIdString));
        }

        if (awayIdString != null) {
          match.setAwayId(UUID.fromString(awayIdString));
        }

        match.setMatchDate(new Date(date));
        match.setIsMatchFinal(isFinal != 0);
        matches.add(match);
        cursor.moveToNext();
      }
    }

    return matches;
  }

  Team getTeam(UUID teamId) {

    System.out.println("++" + TAG + "::getTeam(UUID)");
    if (teamId == null) {
      return null;
    }

    return getTeam(TeamTable.Cols.ID + " = ?", new String[]{teamId.toString()});
  }

  private Team getTeam(String query, String[] arguments) {

    System.out.println("++" + TAG + "::getTeam(String, String[])");
    try (Cursor cursor = mDatabase.query(
        TeamTable.NAME,
        null,
        query,
        arguments,
        null,
        null,
        null)) {
      if (cursor.getCount() == 0) {
        return null;
      }

      cursor.moveToFirst();
      String idString = cursor.getString(cursor.getColumnIndex(TeamTable.Cols.ID));
      String fullName = cursor.getString(cursor.getColumnIndex(TeamTable.Cols.FULL_NAME));
      String shortName = cursor.getString(cursor.getColumnIndex(TeamTable.Cols.SHORT_NAME));
      String conferenceId = cursor.getString(cursor.getColumnIndex(TeamTable.Cols.CONFERENCE_ID));
      String parentId = cursor.getString(cursor.getColumnIndex(TeamTable.Cols.PARENT_ID));
      boolean isDefunct = cursor.getInt(cursor.getColumnIndex(TeamTable.Cols.DEFUNCT)) != 0;

      Team team = new Team(UUID.fromString(idString));
      team.setFullName(fullName);
      team.setShortName(shortName);
      if (conferenceId != null && !conferenceId.isEmpty()) {
        team.setConferenceId(UUID.fromString(conferenceId));
      }

      if (parentId != null && !parentId.isEmpty()) {
        team.setParentId(UUID.fromString(parentId));
      }

      team.setIsDefunct(isDefunct);

      return team;
    }
  }
}
