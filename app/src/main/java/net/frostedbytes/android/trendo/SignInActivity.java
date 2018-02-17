package net.frostedbytes.android.trendo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import net.frostedbytes.android.trendo.models.User;
import net.frostedbytes.android.trendo.utils.LogUtils;
import net.frostedbytes.android.trendo.views.TouchableTextView;

public class SignInActivity extends BaseActivity implements View.OnClickListener {

  private static final String TAG = "SignInActivity";

  private DatabaseReference mDatabase;
  private FirebaseAuth mAuth;

  private EditText mEmailEdit;
  private EditText mPasswordEdit;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    LogUtils.debug(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_sign_in);
    mDatabase = FirebaseDatabase.getInstance().getReference();
    mAuth = FirebaseAuth.getInstance();

    mEmailEdit = findViewById(R.id.sign_in_edit_email);
    TouchableTextView forgotPasswordText = findViewById(R.id.sign_in_text_forgot_password);
    forgotPasswordText.setOnTouchListener((view, motionEvent) -> {

      switch (motionEvent.getAction()) {
        case MotionEvent.ACTION_DOWN:
          String emailAddress = mEmailEdit.getText().toString();
          if (!emailAddress.isEmpty() && emailAddress.contains("@")) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.sendPasswordResetEmail(emailAddress)
              .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                  Toast.makeText(SignInActivity.this, "Reset email sent.", Toast.LENGTH_SHORT).show();
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

    signInButton.setOnClickListener(this);
    signUpButton.setOnClickListener(this);
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
        signIn();
        break;
      case R.id.sign_in_button_sign_up:
        signUp();
        break;
    }
  }

  private void signIn() {

    LogUtils.debug(TAG, "++::signIn()");
    if (!validateForm()) {
      return;
    }

    showProgressDialog("Authenticating...");
    String email = mEmailEdit.getText().toString();
    String password = mPasswordEdit.getText().toString();
    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {

      LogUtils.debug(TAG, "Sign-In complete: " + task.isSuccessful());
      hideProgressDialog();

      if (task.isSuccessful()) {
        onAuthenticateSuccess(task.getResult().getUser());
      } else {
        Toast.makeText(SignInActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
      }
    });
  }

  private void signUp() {

    LogUtils.debug(TAG, "++signUp()");
    if (!validateForm()) {
      return;
    }

    showProgressDialog("Processing...");
    String email = mEmailEdit.getText().toString();
    String password = mPasswordEdit.getText().toString();
    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {

      LogUtils.debug(TAG, "Create user complete: " + task.isSuccessful());
      hideProgressDialog();

      if (task.isSuccessful()) {
        onAuthenticateSuccess(task.getResult().getUser());
      } else {
        Toast.makeText(SignInActivity.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
      }
    });
  }

  private void onAuthenticateSuccess(FirebaseUser user) {

    String username = usernameFromEmail(user.getEmail());
    writeNewUser(user.getUid(), username, user.getEmail());

    Intent intent = new Intent(SignInActivity.this, MatchListActivity.class);
    intent.putExtra(BaseActivity.ARG_USER, user.getUid());
    startActivity(intent);
    finish();
  }

  private String usernameFromEmail(String email) {

    return email.contains("@") ? email.split("@")[0] : email;
  }

  private boolean validateForm() {

    boolean result = true;
    if (TextUtils.isEmpty(mEmailEdit.getText().toString())) {
      mEmailEdit.setError("Required");
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

  private void writeNewUser(String userId, String name, String email) {

    User user = new User(name, email);
    mDatabase.child(User.ROOT).child(userId).setValue(user);
  }
}
