package net.whollynugatory.android.trendo.utils;

public class DateUtils {

  /**
   * Returns a user-friendly readable string of the date.
   *
   * @param date - Date; formatted YYYYMMDD
   * @return - User-friendly readable string of the date; formatted MM/DD/YYYY
   */
  public static String formatDateForDisplay(String date) {

    return formatDateForDisplay(date, false);
  }

  /**
   * Returns a user-friendly readable string of the date.
   *
   * @param date - Date; formatted YYYYMMDD
   * @param abbreviated - User-friendly readable string is an abbreviated version, if TRUE.
   * @return - User-friendly readable string of the date; formatted MM/DD/YYYY, or MM/DD (abbreviated)
   */
  public static String formatDateForDisplay(String date, boolean abbreviated) {

    String year = "----";
    String month;
    String day;
    if (date.length() > 4) {
      year = date.substring(0, 4);
      month = date.substring(4, 6);
      day = date.substring(6, 8);
    } else if (date.length() > 3) {
      month = date.substring(0, 2);
      day = date.substring(2, 4);
    } else {
      month = date.substring(0, 1);
      day = date.substring(1, 3);
    }

    if (abbreviated) {
      return String.format("%1s/%2s", month, day);
    }

    return String.format("%1s/%2s/%3s", month, day, year);
  }
}
