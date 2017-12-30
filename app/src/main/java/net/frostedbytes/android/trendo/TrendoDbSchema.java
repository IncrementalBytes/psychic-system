package net.frostedbytes.android.trendo;

import java.util.UUID;

class TrendoDbSchema {

  static final int VERSION = 1;
  static final String DATABASE_NAME = "trendoBase.db";
  static final String ASSET_DATABASES_PATH = "databases";
  static final UUID Id = UUID.fromString("b39d0479-d8f6-4f7b-8f18-29951ba06120");

  static final class ConferenceTable {

    static final String NAME = "Conferences";
    static final UUID Id = UUID.fromString("b345ae68-6c7a-4f6f-be8a-847c95bee401");

    static final class Cols {

      static final String ID = "Id";
      static final String CONFERENCE_NAME = "ConferenceName";
      static final String PARENT_ID = "ParentId";
      static final String DEFUNCT = "IsDefunct";
      static final String CREATE_DATE_UTC = "CreateDateUTC";
    }
  }

  static final class EventTable {

    static final String NAME = "Events";
    static final UUID Id = UUID.fromString("a2a62411-4c82-4013-b812-01f367cbf7ac");

    static final class Cols {

      static final String ID = "Id";
      static final String EVENT_NAME = "EventName";
      static final String DEFUNCT = "IsDefunct";
      static final String CREATE_DATE_UTC = "CreateDateUTC";
    }
  }

  static final class MatchTable {

    static final String NAME = "Matches";
    static final UUID Id = UUID.fromString("8f513af7-8e2e-40c5-b6d9-7e5c18e60eef");

    static final class Cols {

      static final String ID = "Id";
      static final String HOME_ID = "HomeId";
      static final String AWAY_ID = "AwayId";
      static final String MATCH_DATE = "MatchDate";
      static final String FINAL = "IsFinal";
      static final String CREATE_DATE_UTC = "CreateDateUTC";
    }
  }

  static final class MatchEventTable {

    static final String NAME = "MatchEvents";
    static final UUID Id = UUID.fromString("be38db3f-035b-4e49-a3b8-e9368c583ad7");

    static final class Cols {

      static final String ID = "Id";
      static final String EVENT_ID = "EventId";
      static final String MATCH_ID = "MatchId";
      static final String PLAYER_ID = "PlayerId";
      static final String TEAM_ID = "TeamId";
      static final String MINUTE_OF_EVENT = "MinuteOfEvent";
      static final String CREATE_DATE_UTC = "CreateDateUTC";
    }
  }

  static final class MatchTrendTable {

    static final String NAME = "MatchTrends";
    static final UUID Id = UUID.fromString("5270b686-6f29-40a9-8e21-7cf0f705c998");

    static final class Cols {

      static final String ID = "Id";
      static final String MATCH_ID = "MatchId";
      static final String TEAM_ID = "TeamId";
      static final String TREND_ID = "TrendId";
      static final String TREND_VALUE = "TrendValue";
      static final String CREATE_DATE_UTC = "CreateDateUTC";
    }
  }

  static final class PlayerTable {

    static final String NAME = "Players";
    static final UUID Id = UUID.fromString("db3551df-d055-47ac-94e2-338edc9a4dbd");

    static final class Cols {

      static final String ID = "Id";
      static final String FIRST_NAME = "FirstName";
      static final String LAST_NAME = "LastName";
      static final String TEAM_ID = "TeamId";
      static final String DEFUNCT = "IsDefunct";
      static final String CREATE_DATE_UTC = "CreateDateUTC";
    }
  }

  static final class TeamTable {

    static final String NAME = "Teams";
    static final UUID Id = UUID.fromString("e5d21ec2-696e-4781-aee7-7733e22c2500");

    static final class Cols {

      static final String ID = "Id";
      static final String FULL_NAME = "FullName";
      static final String SHORT_NAME = "ShortName";
      static final String CONFERENCE_ID = "ConferenceId";
      static final String PARENT_ID = "ParentId";
      static final String DEFUNCT = "IsDefunct";
      static final String CREATE_DATE_UTC = "CreateDateUTC";
    }

    static final class TrendTable {

      static final String NAME = "Trends";
      static final UUID Id = UUID.fromString("382485c6-5dfd-4eae-b543-c88c281f7845");

      static final class Cols {

        static final String ID = "Id";
        static final String TREND_NAME = "TrendName";
        static final String DEFUNCT = "IsDefunct";
        static final String CREATE_DATE_UTC = "CreateDateUTC";
      }

      static final class VersionTable {

        static final String NAME = "Versions";
        static final UUID Id = UUID.fromString("67a7728e-5d4f-4287-9f0a-1336d3ac812c");

        static final class Cols {

          static final String ID = "Id";
          static final String TABLE_ID = "TableId";
          static final String VERSION = "Version";
          static final String DEFUNCT = "IsDefunct";
          static final String CREATE_DATE_UTC = "CreateDateUTC";
        }
      }
    }
  }
}
