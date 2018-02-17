package net.frostedbytes.android.trendo.utils;

import android.util.Log;
import net.frostedbytes.android.trendo.BuildConfig;

/**
 * Wrapper class for logging to help remove from non-debug builds.
 */
public class LogUtils {

  public static void debug(final String tag, String message) {

    if (BuildConfig.DEBUG) {
      Log.d(tag, message);
    }
  }

  public static void error(final String tag, String message) {

    if (BuildConfig.DEBUG) {
      Log.e(tag, message);
    }
  }

  public static void warn(final String tag, String message) {

    if (BuildConfig.DEBUG) {
      Log.w(tag, message);
    }
  }
}
