package net.frostedbytes.android.trendo.utils;

import android.util.Log;
import java.util.Locale;
import net.frostedbytes.android.trendo.BuildConfig;

/**
 * Wrapper class for logging to help remove from non-debug builds.
 */
public class LogUtils {

  public static void debug(final String tag, String messageFormat, Object... args) {

    if (BuildConfig.DEBUG) {
      Log.d(tag, String.format(Locale.getDefault(), messageFormat, args));
    }
  }

  public static void error(final String tag, String messageFormat, Object... args) {

    if (BuildConfig.DEBUG) {
      Log.e(tag, String.format(Locale.getDefault(), messageFormat, args));
    }
  }

  public static void warn(final String tag, String messageFormat, Object... args) {

    if (BuildConfig.DEBUG) {
      Log.w(tag, String.format(Locale.getDefault(), messageFormat, args));
    }
  }
}
