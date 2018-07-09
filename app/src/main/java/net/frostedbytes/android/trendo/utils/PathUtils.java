package net.frostedbytes.android.trendo.utils;

import java.util.Locale;

public class PathUtils {

  public static String combine(Object... paths) {

    String finalPath = "";
    for (Object path : paths) {
      String format = "%s/%s";
      if (path.getClass() == Integer.class) {
        format = "%s/%d";
      }

      finalPath = String.format(Locale.ENGLISH, format, finalPath, path);
    }

    return finalPath;
  }
}
