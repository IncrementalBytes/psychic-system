package net.frostedbytes.android.trendo;

import android.content.Context;
import java.util.List;
import net.frostedbytes.android.trendo.models.Match;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class MatchCenterUnitTest {

  @Mock
  private Context mMockContext;

  public void getMatch() throws Exception {

  }

  public void getMatches_isEmpty() throws Exception {

    List<Match> matches = MatchCenter.get(mMockContext.getApplicationContext()).getMatches();
    assertEquals(matches.size(), 0);
  }

  public void getTeam() throws Exception {

  }
}