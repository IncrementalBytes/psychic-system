package net.frostedbytes.android.trendo.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;
import java.util.HashMap;
import java.util.Map;
import net.frostedbytes.android.trendo.BaseActivity;
import net.frostedbytes.android.trendo.utils.LogUtils;

import static net.frostedbytes.android.trendo.BaseActivity.BASE_TAG;

public class MatchSummary implements Parcelable {

    private static final String TAG = BASE_TAG + MatchSummary.class.getSimpleName();

    @Exclude
    public static final String ROOT = "MatchSummaries";

    @Exclude
    public String AwayFullName;

    /**
     * Unique identifier for away team.
     */
    public String AwayId;

    /**
     * Goals scored by the away team.
     */
    public long AwayScore;

    @Exclude
    public String HomeFullName;

    /**
     * Unique identifier for home team.
     */
    public String HomeId;

    /**
     * Goals scored by the home team.
     */
    public long HomeScore;

    /**
     * Value indicating whether or not this match is final.
     */
    public boolean IsFinal;

    @Exclude
    public boolean IsLocal;

    @Exclude
    public boolean IsRemote;

    /**
     * Unique identifier for Match object; used as key for json.
     */
    @Exclude
    public String Id;

    /**
     * Number of ticks representing the year, month, and date of this match.
     */
    public String MatchDate;

    /**
     * Constructs a new MatchSummary object with default values.
     */
    public MatchSummary() {

        this.AwayFullName = "";
        this.AwayId = BaseActivity.DEFAULT_ID;
        this.AwayScore = 0;
        this.HomeFullName = "";
        this.HomeId = BaseActivity.DEFAULT_ID;
        this.HomeScore = 0;
        this.IsFinal = false;
        this.IsLocal = false;
        this.IsRemote = false;
        this.Id = BaseActivity.DEFAULT_ID;
        this.MatchDate = BaseActivity.DEFAULT_DATE;
    }

    /**
     * Creates a mapped object based on values of this match summary object
     *
     * @return A mapped object of match summary
     */
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("AwayId", this.AwayId);
        result.put("AwayScore", this.AwayScore);
        result.put("HomeId", this.HomeId);
        result.put("HomeScore", this.HomeScore);
        result.put("IsFinal", this.IsFinal);
        result.put("MatchDate", this.MatchDate);
        return result;
    }

    protected MatchSummary(Parcel in) {

        this.AwayFullName = in.readString();
        this.AwayId = in.readString();
        this.AwayScore = in.readLong();
        this.HomeFullName = in.readString();
        this.HomeId = in.readString();
        this.HomeScore = in.readLong();
        this.IsFinal = in.readInt() != 0;
        this.Id = in.readString();
        this.MatchDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Compares this MatchSummary with another MatchSummary.
     *
     * @param compareTo MatchSummary to compare this MatchSummary against
     * @return TRUE if this MatchSummary equals the other MatchSummary, otherwise FALSE
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
        if ((compareTo instanceof MatchSummary)) {
            try {
                MatchSummary compareToMatchSummary = (MatchSummary) compareTo;
                if (this.HomeId.equals(compareToMatchSummary.HomeId) &&
                    this.AwayId.equals(compareToMatchSummary.AwayId) &&
                    this.MatchDate.equals(compareToMatchSummary.MatchDate)) {
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

        return String.format("%s vs. %s on %s", this.HomeId, this.AwayId, this.MatchDate);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.AwayFullName);
        dest.writeString(this.AwayId);
        dest.writeLong(this.AwayScore);
        dest.writeString(this.AwayFullName);
        dest.writeString(this.HomeId);
        dest.writeLong(this.HomeScore);
        dest.writeInt(this.IsFinal ? 1 : 0);
        dest.writeString(this.Id);
        dest.writeString(this.MatchDate);
    }

    public static final Creator<MatchSummary> CREATOR = new Creator<MatchSummary>() {

        @Override
        public MatchSummary createFromParcel(Parcel in) {

            return new MatchSummary(in);
        }

        @Override
        public MatchSummary[] newArray(int size) {

            return new MatchSummary[size];
        }
    };
}
