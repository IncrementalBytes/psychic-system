package net.frostedbytes.android.trendo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import net.frostedbytes.android.trendo.utils.LogUtils;
import net.frostedbytes.android.trendo.views.TouchableTextView;

public class SignInActivity extends BaseActivity implements View.OnClickListener {

  private static final String TAG = "SignInActivity";

  private static final int RC_SIGN_IN = 4701;

  private FirebaseAuth mAuth;
  private GoogleSignInClient mGoogleSignInClient;

  private EditText mEmailEdit;
  private EditText mPasswordEdit;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    LogUtils.debug(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_sign_in);

    mEmailEdit = findViewById(R.id.sign_in_edit_email);
    TouchableTextView forgotPasswordText = findViewById(R.id.sign_in_text_forgot_password);
    forgotPasswordText.setOnTouchListener((view, motionEvent) -> {
      switch (motionEvent.getAction()) {
        case MotionEvent.ACTION_DOWN:
          String emailAddress = mEmailEdit.getText().toString();
          if (!emailAddress.isEmpty() && emailAddress.contains("@")) {
            mAuth.sendPasswordResetEmail(emailAddress)
              .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                  Snackbar.make(findViewById(R.id.activity_sign_in), "Reset email sent.", Snackbar.LENGTH_SHORT).show();
                }
              });
          } else {
            mEmailEdit.setError("Required");
            return false;
          }

          return true;
        case MotionEvent.ACTION_UP:
          view.performClick();
          return true;
      }

      return false;
    });

    mPasswordEdit = findViewById(R.id.sign_in_edit_password);
    Button signInButton = findViewById(R.id.sign_in_button_sign_in);
    Button signUpButton = findViewById(R.id.sign_in_button_sign_up);
    SignInButton signInWithGoogleButton = findViewById(R.id.sign_in_button_google);

    signInButton.setOnClickListener(this);
    signUpButton.setOnClickListener(this);
    signInWithGoogleButton.setOnClickListener(this);

    mAuth = FirebaseAuth.getInstance();

    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestIdToken(getString(R.string.default_web_client_id))
      .requestEmail()
      .build();
    mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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
      case R.id.sign_in_button_sign_in:
        signInWithEmail();
        break;
      case R.id.sign_in_button_sign_up:
        signUpWithEmail();
        break;
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
      Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
      try {
        GoogleSignInAccount account = task.getResult(ApiException.class);
        firebaseAuthenticateWithGoogle(account);
      } catch (ApiException e) {
        LogUtils.warn(TAG, "Getting task result failed: %1s", e.getMessage());
        Snackbar.make(findViewById(R.id.activity_sign_in), "Google sign in failed.", Snackbar.LENGTH_SHORT).show();
      }
    }
  }

  private void firebaseAuthenticateWithGoogle(GoogleSignInAccount acct) {

    LogUtils.debug(TAG, "++firebaseAuthWithGoogle(%1s)", acct.getId());
    showProgressDialog(getString(R.string.status_authenticating));
    AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
    mAuth.signInWithCredential(credential)
      .addOnCompleteListener(this, task -> {
        if (task.isSuccessful()) {
          onAuthenticateSuccess(mAuth.getCurrentUser());
        } else {
          LogUtils.warn(
            TAG,
            "Authenticating with Google account failed: %1s",
            task.getException() != null ? task.getException().getMessage() : "");
          Snackbar.make(findViewById(R.id.activity_sign_in), "Authenticating failed.", Snackbar.LENGTH_SHORT).show();
        }

        hideProgressDialog();
      });
  }

  private void signInWithEmail() {

    LogUtils.debug(TAG, "++::signInWithEmail()");
    if (!validateForm()) {
      return;
    }

    showProgressDialog(getString(R.string.status_authenticating));
    String email = mEmailEdit.getText().toString();
    String password = mPasswordEdit.getText().toString();
    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
      LogUtils.debug(TAG, "Sign-In complete: " + task.isSuccessful());
      if (task.isSuccessful()) {
        onAuthenticateSuccess(task.getResult().getUser());
      } else {
        LogUtils.warn(
          TAG,
          "Sign-In with email failed: %1s",
          task.getException() != null ? task.getException().getMessage() : "");
        Snackbar.make(findViewById(R.id.activity_sign_in), "Email Sign-in failed.", Toast.LENGTH_SHORT).show();
      }

      hideProgressDialog();
    });
  }

  private void signInWithGoogle() {

    LogUtils.debug(TAG, "++signInWithGoogle()");
    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
    startActivityForResult(signInIntent, RC_SIGN_IN);
  }

  private void signUpWithEmail() {

    LogUtils.debug(TAG, "++signUp()");
    if (!validateForm()) {
      return;
    }

    showProgressDialog(getString(R.string.status_processing));
    String email = mEmailEdit.getText().toString();
    String password = mPasswordEdit.getText().toString();
    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
      LogUtils.debug(TAG, "Create user complete: " + task.isSuccessful());
      if (task.isSuccessful()) {
        onAuthenticateSuccess(task.getResult().getUser());
      } else {
        LogUtils.warn(
          TAG,
          "Sign up with email failed: %1s",
          task.getException() != null ? task.getException().getMessage() : "");
        Snackbar.make(findViewById(R.id.activity_sign_in), "Sign Up Failed", Toast.LENGTH_SHORT).show();
      }

      hideProgressDialog();
    });
  }

  private void onAuthenticateSuccess(FirebaseUser user) {

    LogUtils.debug(TAG, "++onAuthenticateSuccess(%1s)", user.getDisplayName());
    Intent intent = new Intent(SignInActivity.this, MatchListActivity.class);
    intent.putExtra(BaseActivity.ARG_USER, user.getUid());
    startActivity(intent);
    finish();
  }

  private boolean validateForm() {

    boolean result = true;
    if (TextUtils.isEmpty(mEmailEdit.getText().toString())) {
      mEmailEdit.setError("Required");
      result = false;
    } else if (!mEmailEdit.getText().toString().contains("@")) {
      mEmailEdit.setError("Valid Email");
      result = false;
    } else {
      mEmailEdit.setError(null);
    }

    if (TextUtils.isEmpty(mPasswordEdit.getText().toString())) {
      mPasswordEdit.setError("Required");
      result = false;
    } else {
      mPasswordEdit.setError(null);
    }

    return result;
  }
}
