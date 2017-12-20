package net.frostedbytes.android.trendo;

import java.util.UUID;

public class Match {

  private UUID mId;

  Match(UUID matchId) {

    mId = matchId;
  }

  UUID getId() {
    return mId;
  }
}
