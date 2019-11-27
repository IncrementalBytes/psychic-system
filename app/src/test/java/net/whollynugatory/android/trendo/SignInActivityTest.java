/*
 * Copyright 2019 Ryan Ward
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.whollynugatory.android.trendo;

import android.widget.ImageView;

import com.google.android.gms.common.SignInButton;

import net.whollynugatory.android.trendo.ui.SignInActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class SignInActivityTest {

  @Test
  public void onCreate_shouldInflateLayout() throws Exception {

    final SignInActivity activity = Robolectric.setupActivity(SignInActivity.class);
    ImageView lockImage = activity.findViewById(R.id.sign_in_image_lock);
    SignInButton signInWithGoogleButton = activity.findViewById(R.id.sign_in_button_google);
    assertThat(lockImage).isEqualTo(ImageView.class);
  }
}
