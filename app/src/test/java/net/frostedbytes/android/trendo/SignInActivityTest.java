package net.frostedbytes.android.trendo;

import android.widget.ImageView;

import com.google.android.gms.common.SignInButton;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SignInActivityTest {

  @Test
  public void onCreate_shouldInflateLayout() throws Exception {

    final SignInActivity activity = Robolectric.setupActivity(SignInActivity.class);
    ImageView lockImage = activity.findViewById(R.id.sign_in_image_lock);
    SignInButton signInWithGoogleButton = activity.findViewById(R.id.sign_in_button_google);
    assertThat(lockImage).isEqualTo(ImageView.class);
  }
}
