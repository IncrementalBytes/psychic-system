package net.frostedbytes.android.trendo.utils;

import net.frostedbytes.android.trendo.db.entity.TeamEntity;

import java.util.Comparator;

public class SortUtils {

    public static class ByTablePosition implements Comparator<TeamEntity> {

        public int compare(TeamEntity a, TeamEntity b) {

            // TODO: return Long.compare(a.TablePosition, b.TablePosition);
            return 0;
        }
    }

    public static class ByTeamName implements Comparator<TeamEntity> {

        public int compare(TeamEntity a, TeamEntity b) {

            return a.Name.compareTo(b.Name);
        }
    }

    public static class ByTotalPoints implements Comparator<TeamEntity> {

        public int compare(TeamEntity a, TeamEntity b) {

//             if (a.TotalPoints == b.TotalPoints) {
//                if (a.TotalWins == b.TotalWins) {
//                    if (a.GoalDifferential == b.GoalDifferential) {
//                        return Long.compare(a.GoalsScored, b.GoalsScored);
//                    }
//
//                    return Long.compare(a.GoalDifferential, b.GoalDifferential);
//                }
//
//                return Long.compare(a.TotalWins, b.TotalWins);
//            }
//
//            return Long.compare(a.TotalPoints, b.TotalPoints);
            return 0;
        }
    }
}
