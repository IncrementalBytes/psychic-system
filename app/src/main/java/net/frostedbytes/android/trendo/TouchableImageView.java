package net.frostedbytes.android.trendo;

import android.content.Context;

class TouchableImageView extends android.support.v7.widget.AppCompatImageView {

  public TouchableImageView(Context context) {
    super(context);
  }

  @Override
  public boolean performClick() {
    super.performClick();

    return true;
  }
}
