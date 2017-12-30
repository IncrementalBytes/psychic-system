package net.frostedbytes.android.trendo;

import android.content.Context;

class TouchableTextView extends android.support.v7.widget.AppCompatTextView {

  public TouchableTextView(Context context) {
    super(context);
  }

  @Override
  public boolean performClick() {
    super.performClick();

    return true;
  }
}
