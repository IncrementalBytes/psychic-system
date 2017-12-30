package net.frostedbytes.android.trendo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import net.frostedbytes.android.trendo.TrendoDbSchema.ConferenceTable;
import net.frostedbytes.android.trendo.TrendoDbSchema.EventTable;
import net.frostedbytes.android.trendo.TrendoDbSchema.MatchEventTable;
import net.frostedbytes.android.trendo.TrendoDbSchema.MatchTable;
import net.frostedbytes.android.trendo.TrendoDbSchema.MatchTrendTable;
import net.frostedbytes.android.trendo.TrendoDbSchema.PlayerTable;
import net.frostedbytes.android.trendo.TrendoDbSchema.TeamTable;
import net.frostedbytes.android.trendo.TrendoDbSchema.TeamTable.TrendTable;
import net.frostedbytes.android.trendo.TrendoDbSchema.TeamTable.TrendTable.VersionTable;

class TrendoDatabaseHelper extends SQLiteOpenHelper {

  TrendoDatabaseHelper(Context context) {
    super(context, TrendoDbSchema.DATABASE_NAME, null, TrendoDbSchema.VERSION);

  }

  @Override
  public void onCreate(SQLiteDatabase db) {

    // generate empty database tables if not found
    db.execSQL("CREATE TABLE IF NOT EXISTS " + ConferenceTable.NAME + "(" +
        ConferenceTable.Cols.ID + ", " +
        ConferenceTable.Cols.CONFERENCE_NAME + ", " +
        ConferenceTable.Cols.PARENT_ID + ", " +
        ConferenceTable.Cols.DEFUNCT + " INTEGER DEFAULT 0, " +
        ConferenceTable.Cols.CREATE_DATE_UTC + " INTEGER DEFAULT(strftime('%s','now','utc')))"
    );

    db.execSQL("CREATE TABLE IF NOT EXISTS " + TeamTable.NAME + "(" +
        TeamTable.Cols.ID + ", " +
        TeamTable.Cols.FULL_NAME + ", " +
        TeamTable.Cols.SHORT_NAME + ", " +
        TeamTable.Cols.CONFERENCE_ID + ", " +
        TeamTable.Cols.PARENT_ID + ", " +
        TeamTable.Cols.DEFUNCT + " INTEGER DEFAULT 0, " +
        TeamTable.Cols.CREATE_DATE_UTC + " INTEGER DEFAULT(strftime('%s','now','utc')))"
    );

    db.execSQL("CREATE TABLE IF NOT EXISTS " + MatchTable.NAME + "(" +
        MatchTable.Cols.ID + ", " +
        MatchTable.Cols.HOME_ID + ", " +
        MatchTable.Cols.AWAY_ID + ", " +
        MatchTable.Cols.MATCH_DATE + ", " +
        MatchTable.Cols.FINAL + " INTEGER DEFAULT 0, " +
        MatchTable.Cols.CREATE_DATE_UTC + " INTEGER DEFAULT(strftime('%s','now','utc')))"
    );

    db.execSQL("CREATE TABLE IF NOT EXISTS " + PlayerTable.NAME + " (" +
        PlayerTable.Cols.ID + ", " +
        PlayerTable.Cols.FIRST_NAME + ", " +
        PlayerTable.Cols.LAST_NAME + ", " +
        PlayerTable.Cols.TEAM_ID + ", " +
        PlayerTable.Cols.DEFUNCT + " INTEGER DEFAULT 0, " +
        PlayerTable.Cols.CREATE_DATE_UTC + " INTEGER DEFAULT(strftime('%s','now','utc')))"
    );

    db.execSQL("CREATE TABLE IF NOT EXISTS " + EventTable.NAME + " (" +
        EventTable.Cols.ID + ", " +
        EventTable.Cols.EVENT_NAME + ", " +
        EventTable.Cols.DEFUNCT + " INTEGER DEFAULT 0, " +
        EventTable.Cols.CREATE_DATE_UTC + " INTEGER DEFAULT(strftime('%s','now','utc')))"
    );

    db.execSQL("CREATE TABLE IF NOT EXISTS " + MatchEventTable.NAME + " (" +
        MatchEventTable.Cols.ID + ", " +
        MatchEventTable.Cols.MATCH_ID + ", " +
        MatchEventTable.Cols.EVENT_ID + ", " +
        MatchEventTable.Cols.MINUTE_OF_EVENT + " INTEGER DEFAULT 0, " +
        MatchEventTable.Cols.TEAM_ID + ", " +
        MatchEventTable.Cols.PLAYER_ID + ", " +
        MatchEventTable.Cols.CREATE_DATE_UTC + " INTEGER DEFAULT(strftime('%s','now','utc')))"
    );

    db.execSQL("CREATE TABLE IF NOT EXISTS " + TrendTable.NAME + " (" +
        TrendTable.Cols.ID + ", " +
        TrendTable.Cols.TREND_NAME + ", " +
        TrendTable.Cols.DEFUNCT + " INTEGER DEFAULT 0, " +
        TrendTable.Cols.CREATE_DATE_UTC + " INTEGER DEFAULT(strftime('%s','now','utc')))"
    );

    db.execSQL("CREATE TABLE IF NOT EXISTS " + MatchTrendTable.NAME + " (" +
        MatchTrendTable.Cols.ID + ", " +
        MatchTrendTable.Cols.MATCH_ID + ", " +
        MatchTrendTable.Cols.TEAM_ID + ", " +
        MatchTrendTable.Cols.TREND_ID + ", " +
        MatchTrendTable.Cols.TREND_VALUE + ", " +
        MatchTrendTable.Cols.CREATE_DATE_UTC + " INTEGER DEFAULT(strftime('%s','now','utc')))"
    );

    db.execSQL("CREATE TABLE IF NOT EXISTS " + VersionTable.NAME + " (" +
        VersionTable.Cols.ID + ", " +
        VersionTable.Cols.TABLE_ID + ", " +
        VersionTable.Cols.VERSION + ", " +
        VersionTable.Cols.DEFUNCT + " INTEGER DEFAULT 0, " +
        VersionTable.Cols.CREATE_DATE_UTC + " INTEGER DEFAULT(strftime('%s','now','utc')))"
    );
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

  }
}
