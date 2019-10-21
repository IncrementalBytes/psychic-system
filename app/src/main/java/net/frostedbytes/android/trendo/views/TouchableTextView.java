package net.frostedbytes.android.trendo.views;

import android.content.Context;
import android.util.AttributeSet;

public class TouchableTextView extends androidx.appcompat.widget.AppCompatTextView {

  public TouchableTextView(Context context) {
    super(context);
  }

  public TouchableTextView(Context context, AttributeSet attrs) {
    super(context, attrs);

  }

  @Override
  public boolean performClick() {
    super.performClick();

    return true;
  }
}
