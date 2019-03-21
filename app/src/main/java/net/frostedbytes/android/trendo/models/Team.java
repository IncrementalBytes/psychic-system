package net.frostedbytes.android.trendo.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;
import java.util.Locale;
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.utils.LogUtils;

import static net.frostedbytes.android.trendo.BaseActivity.BASE_TAG;

public class Team implements Parcelable {

    private static final String TAG = BASE_TAG + Team.class.getSimpleName();

    @Exclude
    public static final String ROOT = "Teams";

    public int ConferenceId;

    /**
     * Year team folded; e.g. 0 = still operating
     */
    public int Defunct;

    /**
     * Year team was founded.
     */
    public int Established;

    /**
     * User friendly string identifying team.
     */
    public String FullName;

    @Exclude
    public long GoalDifferential;

    @Exclude
    public long GoalsScored;

    @Exclude
    public String Id;

    @Exclude
    public boolean IsLocal;

    @Exclude
    public boolean IsRemote;

    /**
     * Abbreviation of team.
     */
    public String ShortName;

    @Exclude
    public long TablePosition;

    @Exclude
    public long TotalPoints;

    @Exclude
    public int TotalWins;

    public Team() {

        this.ConferenceId = 0;
        this.Defunct = 0;
        this.Established = 0;
        this.FullName = "";
        this.GoalDifferential = 0;
        this.GoalsScored = 0;
        this.Id = BaseActivity.DEFAULT_ID;
        this.ShortName = "";
        this.TablePosition = 0;
        this.TotalPoints = 0;
        this.TotalWins = 0;
    }

    protected Team(Parcel in) {

        this.ConferenceId = in.readInt();
        this.Defunct = in.readInt();
        this.Established = in.readInt();
        this.GoalDifferential = in.readLong();
        this.GoalsScored = in.readLong();
        this.FullName = in.readString();
        this.Id = in.readString();
        this.ShortName = in.readString();
        this.TablePosition = in.readLong();
        this.TotalPoints = in.readLong();
        this.TotalWins = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Compares this Team with another Team.
     *
     * @param compareTo Team to compare this Team against
     * @return TRUE if this Team equals the other Team, otherwise FALSE
     * @throws ClassCastException if object parameter cannot be cast into Team object
     */
    @Override
    public boolean equals(Object compareTo) throws ClassCastException {

        if (compareTo == null) {
            return false;
        }

        if (this == compareTo) {
            return true;
        }

        //cast to native object is now safe
        if ((compareTo instanceof Team)) {
            try {
                Team compareToTeam = (Team) compareTo;
                if (this.Id.equals(compareToTeam.Id) &&
                    this.ConferenceId == ((Team) compareTo).ConferenceId &&
                    this.FullName.equals(compareToTeam.FullName) &&
                    this.ShortName.equals(compareToTeam.ShortName) &&
                    this.Established == compareToTeam.Established &&
                    this.Defunct == compareToTeam.Defunct) {
                    return true;
                }
            } catch (ClassCastException cce) {
                LogUtils.error(TAG, "Could not cast object to Team class: %s", cce.getMessage());
            }
        }

        return false;
    }

    @NonNull
    @Override
    public String toString() {

        return String.format(
            Locale.ENGLISH,
            "%s (%d-%s)",
            this.FullName,
            this.Established,
            this.Defunct == 0 ? "present" : String.valueOf(this.Defunct));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(this.ConferenceId);
        dest.writeInt(this.Defunct);
        dest.writeInt(this.Established);
        dest.writeString(this.FullName);
        dest.writeLong(this.GoalDifferential);
        dest.writeLong(this.GoalsScored);
        dest.writeString(this.Id);
        dest.writeString(this.ShortName);
        dest.writeLong(this.TablePosition);
        dest.writeLong(this.TotalPoints);
        dest.writeInt(this.TotalWins);
    }

    public static final Creator<Team> CREATOR = new Creator<Team>() {

        @Override
        public Team createFromParcel(Parcel in) {

            return new Team(in);
        }

        @Override
        public Team[] newArray(int size) {

            return new Team[size];
        }
    };
}
