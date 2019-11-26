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
package net.frostedbytes.android.trendo.ui;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import net.frostedbytes.android.trendo.R;

public class SignInActivity extends BaseActivity implements OnClickListener {

  private static final String TAG = BASE_TAG + SignInActivity.class.getSimpleName();

  private static final int RC_SIGN_IN = 4701;

  private ProgressBar mProgressBar;

  private FirebaseAuth mAuth;
  private GoogleApiClient mGoogleApiClient;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_sign_in);

    SignInButton signInWithGoogleButton = findViewById(R.id.sign_in_button_google);
    signInWithGoogleButton.setOnClickListener(this);

    mProgressBar = findViewById(R.id.sign_in_progress);
    mProgressBar.setVisibility(View.INVISIBLE);

    mAuth = FirebaseAuth.getInstance();

    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestIdToken(getString(R.string.default_web_client_id))
      .requestEmail()
      .build();

    mGoogleApiClient = new GoogleApiClient.Builder(this)
      .enableAutoManage(this, connectionResult -> {
        Log.d(TAG, "++onConnectionFailed(ConnectionResult)");
        Log.d(
          TAG,
          connectionResult.getErrorMessage() != null ? connectionResult.getErrorMessage() : "Connection result was null.");
        Snackbar.make(
          findViewById(R.id.activity_sign_in),
          connectionResult.getErrorMessage() != null ? connectionResult.getErrorMessage() : "Connection result was null.",
          Snackbar.LENGTH_LONG).show();
      })
      .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
      .build();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    Log.d(TAG, "++onDestroy()");
  }

  @Override
  public void onStart() {
    super.onStart();

    Log.d(TAG, "++onStart()");
    if (mAuth.getCurrentUser() != null) {
      onAuthenticateSuccess(mAuth.getCurrentUser());
    }
  }

  @Override
  public void onClick(View view) {

    Log.d(TAG, "++onClick()");
    if (view.getId() == R.id.sign_in_button_google) {
      signInWithGoogle();
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    Log.d(TAG, "++onActivityResult(int, int, Intent)");
    if (requestCode == RC_SIGN_IN) {
      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      if (result.isSuccess()) {
        GoogleSignInAccount account = result.getSignInAccount();
        if (account != null) {
          firebaseAuthenticateWithGoogle(account);
        } else {
          Log.e(TAG, "Could not get sign-in account.");
          Snackbar.make(findViewById(R.id.activity_sign_in), "Could not get sign-in account.", Snackbar.LENGTH_LONG).show();
        }
      } else {
        Log.e(TAG, "Getting task result failed " + result.getStatus());
        Snackbar.make(findViewById(R.id.activity_sign_in), "Could not sign-in with Google.", Snackbar.LENGTH_SHORT).show();
      }
    }
  }

  private void firebaseAuthenticateWithGoogle(GoogleSignInAccount acct) {

    Log.d(TAG, "++firebaseAuthWithGoogle(GoogleSignInAccount)");
    mProgressBar.setVisibility(View.VISIBLE);
    mProgressBar.setIndeterminate(true);
    AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
    mAuth.signInWithCredential(credential)
      .addOnCompleteListener(this, task -> {
        if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
          onAuthenticateSuccess(mAuth.getCurrentUser());
        } else {
          Log.e(
            TAG,
            String.format(
              "Authenticating with Google account failed: %1s",
              task.getException() != null ? task.getException().getMessage() : ""));
          Snackbar.make(findViewById(R.id.activity_sign_in), "Authenticating failed.", Snackbar.LENGTH_SHORT).show();
        }

        mProgressBar.setIndeterminate(false);
      });
  }

  private void signInWithGoogle() {

    Log.d(TAG, "++signInWithGoogle()");
    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
    startActivityForResult(signInIntent, RC_SIGN_IN);
  }

  private void onAuthenticateSuccess(FirebaseUser user) {

    Log.d(TAG, "++onAuthenticateSuccess(FirebaseUser)");
    Intent intent = new Intent(SignInActivity.this, DataActivity.class);
    intent.putExtra(BaseActivity.ARG_USER_ID, user.getUid());
    startActivity(intent);
    finish();
  }
}
