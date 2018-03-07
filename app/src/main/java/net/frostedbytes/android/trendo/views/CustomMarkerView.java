package net.frostedbytes.android.trendo.views;

import android.content.Context;
import android.widget.TextView;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import java.util.Locale;
import net.frostedbytes.android.trendo.R;

public class CustomMarkerView extends MarkerView {

  private final TextView mMarkerText;

  public CustomMarkerView(Context context, int layoutResource) {
    super(context, layoutResource);

    mMarkerText = findViewById(R.id.chart_text_marker);
  }

  @Override
  public void refreshContent(Entry entry, Highlight highlight) {

    if (entry instanceof CandleEntry) {
      CandleEntry ce = (CandleEntry) entry;
      mMarkerText.setText(Utils.formatNumber(ce.getHigh(), 0, true));
    } else {
      mMarkerText.setText(String.format(Locale.getDefault(), "Match\r\n%.0f", entry.getX()));
    }

    super.refreshContent(entry, highlight);
  }

  @Override
  public MPPointF getOffset() {

    return new MPPointF(-(getWidth() / 2), -getHeight());
  }
}
