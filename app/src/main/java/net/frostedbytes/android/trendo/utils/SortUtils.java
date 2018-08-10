package net.frostedbytes.android.trendo.utils;

import java.util.Comparator;
import net.frostedbytes.android.trendo.models.Team;

public class SortUtils {

  public static class ByTeamName implements Comparator<Team>
  {
    public int compare(Team a, Team b) {

      return a.FullName.compareTo(b.FullName);
    }
  }
}
