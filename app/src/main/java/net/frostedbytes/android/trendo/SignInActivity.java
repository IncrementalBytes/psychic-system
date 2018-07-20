package net.frostedbytes.android.trendo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.View.OnClickListener;

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
import net.frostedbytes.android.trendo.utils.LogUtils;

public class SignInActivity extends BaseActivity implements OnClickListener {

  private static final String TAG = SignInActivity.class.getSimpleName();

  private static final int RC_SIGN_IN = 4701;

  private FirebaseAuth mAuth;
  private GoogleApiClient mGoogleApiClient;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    LogUtils.debug(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_sign_in);

    SignInButton signInWithGoogleButton = findViewById(R.id.sign_in_button_google);
    signInWithGoogleButton.setOnClickListener(this);

    mAuth = FirebaseAuth.getInstance();

    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestIdToken(getString(R.string.default_web_client_id))
      .requestEmail()
      .build();

    mGoogleApiClient = new GoogleApiClient.Builder(this)
      .enableAutoManage(this, connectionResult -> {
        LogUtils.debug(TAG, "++onConnectionFailed(ConnectionResult)");
        LogUtils.debug(
          TAG,
          "%s",
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
  public void onStart() {
    super.onStart();

    LogUtils.debug(TAG, "++onStart()");
    if (mAuth.getCurrentUser() != null) {
      onAuthenticateSuccess(mAuth.getCurrentUser());
    }
  }

  @Override
  public void onClick(View view) {

    LogUtils.debug(TAG, "++onClick()");
    switch (view.getId()) {
      case R.id.sign_in_button_google:
        signInWithGoogle();
        break;
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    LogUtils.debug(TAG, "++onActivityResult(%1d, %2d, Intent)", requestCode, resultCode);
    if (requestCode == RC_SIGN_IN) {
      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      if (result.isSuccess()) {
        GoogleSignInAccount account = result.getSignInAccount();
        if (account != null) {
          firebaseAuthenticateWithGoogle(account);
        } else {
          LogUtils.error(TAG, "Could not get sign-in account.");
          Snackbar.make(findViewById(R.id.activity_sign_in), "Could not get sign-in account.", Snackbar.LENGTH_LONG).show();
        }
      } else {
        LogUtils.error(TAG, "Getting task result failed.", result.getStatus());
        Snackbar.make(findViewById(R.id.activity_sign_in), "Could not sign-in with Google.", Snackbar.LENGTH_SHORT).show();
      }
    }
  }

  private void firebaseAuthenticateWithGoogle(GoogleSignInAccount acct) {

    LogUtils.debug(TAG, "++firebaseAuthWithGoogle(%1s)", acct.getId());
    showProgressDialog(getString(R.string.status_authenticating));
    AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
    mAuth.signInWithCredential(credential)
      .addOnCompleteListener(this, task -> {
        if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
          onAuthenticateSuccess(mAuth.getCurrentUser());
        } else {
          LogUtils.error(
            TAG,
            "Authenticating with Google account failed: %1s",
            task.getException() != null ? task.getException().getMessage() : "");
          Snackbar.make(findViewById(R.id.activity_sign_in), "Authenticating failed.", Snackbar.LENGTH_SHORT).show();
        }

        hideProgressDialog();
      });
  }

  private void signInWithGoogle() {

    LogUtils.debug(TAG, "++signInWithGoogle()");
    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
    startActivityForResult(signInIntent, RC_SIGN_IN);
  }

  private void onAuthenticateSuccess(FirebaseUser user) {

    LogUtils.debug(TAG, "++onAuthenticateSuccess(%1s)", user.getDisplayName());
    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
    intent.putExtra(BaseActivity.ARG_USER_ID, user.getUid());
    intent.putExtra(BaseActivity.ARG_USER_NAME, user.getDisplayName());
    intent.putExtra(BaseActivity.ARG_EMAIL, user.getEmail());
    startActivity(intent);
    finish();
  }
}
