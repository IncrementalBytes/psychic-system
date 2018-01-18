package net.frostedbytes.android.trendo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import net.frostedbytes.android.trendo.models.User;

public class SignInActivity extends BaseActivity implements View.OnClickListener {

  private static final String TAG = "SignInActivity";

  private DatabaseReference mDatabase;
  private FirebaseAuth mAuth;

  private EditText mEmailField;
  private EditText mPasswordField;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_sign_in);
    mDatabase = FirebaseDatabase.getInstance().getReference();
    mAuth = FirebaseAuth.getInstance();

    mEmailField = findViewById(R.id.signin_edit_email);
    mPasswordField = findViewById(R.id.signin_edit_password);
    Button signInButton = findViewById(R.id.signin_button_sign_in);
    Button signUpButton = findViewById(R.id.signin_button_sign_up);

    signInButton.setOnClickListener(this);
    signUpButton.setOnClickListener(this);
  }

  @Override
  public void onStart() {
    super.onStart();

    if (mAuth.getCurrentUser() != null) {
      onAuthSuccess(mAuth.getCurrentUser());
    }
  }

  @Override
  public void onClick(View view) {

    switch (view.getId()) {
      case R.id.signin_button_sign_in:
        signIn();
        break;
      case R.id.signin_button_sign_up:
        signUp();
        break;
    }
  }

  @Override
  public void onPointerCaptureChanged(boolean hasCapture) {

  }

  private void signIn() {

    Log.d(TAG, "++::signIn()");
    if (!validateForm()) {
      return;
    }

    showProgressDialog();
    String email = mEmailField.getText().toString();
    String password = mPasswordField.getText().toString();
    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {

            Log.d(TAG, "Sign-In complete: " + task.isSuccessful());
            hideProgressDialog();

            if (task.isSuccessful()) {
              onAuthSuccess(task.getResult().getUser());
            } else {
              Toast.makeText(SignInActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
            }
          }
        });
  }

  private void signUp() {

    Log.d(TAG, "++signUp()");
    if (!validateForm()) {
      return;
    }

    showProgressDialog();
    String email = mEmailField.getText().toString();
    String password = mPasswordField.getText().toString();
    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {

            Log.d(TAG, "Create user complete: " + task.isSuccessful());
            hideProgressDialog();

            if (task.isSuccessful()) {
              onAuthSuccess(task.getResult().getUser());
            } else {
              Toast.makeText(SignInActivity.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
            }
          }
        });
  }

  private void onAuthSuccess(FirebaseUser user) {

    String username = usernameFromEmail(user.getEmail());
    writeNewUser(user.getUid(), username, user.getEmail());
    startActivity(new Intent(SignInActivity.this, MainActivity.class));
    finish();
  }

  private String usernameFromEmail(String email) {

    return email.contains("@") ? email.split("@")[0] : email;
  }

  private boolean validateForm() {

    boolean result = true;
    if (TextUtils.isEmpty(mEmailField.getText().toString())) {
      mEmailField.setError("Required");
      result = false;
    } else {
      mEmailField.setError(null);
    }

    if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
      mPasswordField.setError("Required");
      result = false;
    } else {
      mPasswordField.setError(null);
    }

    return result;
  }

  private void writeNewUser(String userId, String name, String email) {

    User user = new User(name, email);
    mDatabase.child("users").child(userId).setValue(user);
  }
}
