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

  private EditText mEmailEdit;
  private EditText mPasswordEdit;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "++onCreate(Bundle)");
    setContentView(R.layout.activity_sign_in);
    mDatabase = FirebaseDatabase.getInstance().getReference();
    mAuth = FirebaseAuth.getInstance();

    mEmailEdit = findViewById(R.id.sign_in_edit_email);
    mPasswordEdit = findViewById(R.id.sign_in_edit_password);
    Button signInButton = findViewById(R.id.sign_in_button_sign_in);
    Button signUpButton = findViewById(R.id.sign_in_button_sign_up);

    signInButton.setOnClickListener(this);
    signUpButton.setOnClickListener(this);
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

    Log.d(TAG, "++::signIn()");
    if (!validateForm()) {
      return;
    }

    showProgressDialog("Authenticating...");
    String email = mEmailEdit.getText().toString();
    String password = mPasswordEdit.getText().toString();
    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {

            Log.d(TAG, "Sign-In complete: " + task.isSuccessful());
            hideProgressDialog();

            if (task.isSuccessful()) {
              onAuthenticateSuccess(task.getResult().getUser());
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

    showProgressDialog("Processing...");
    String email = mEmailEdit.getText().toString();
    String password = mPasswordEdit.getText().toString();
    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {

            Log.d(TAG, "Create user complete: " + task.isSuccessful());
            hideProgressDialog();

            if (task.isSuccessful()) {
              onAuthenticateSuccess(task.getResult().getUser());
            } else {
              Toast.makeText(SignInActivity.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
            }
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
    mDatabase.child("Users").child(userId).setValue(user);
  }
}
