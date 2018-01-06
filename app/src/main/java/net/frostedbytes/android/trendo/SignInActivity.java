package net.frostedbytes.android.trendo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
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

public class SignInActivity extends MainActivity implements View.OnClickListener {

  private static final String TAG = "SignInActivity";

  private DatabaseReference mDatabase;
  private FirebaseAuth mAuth;

  private EditText mEmailField;
  private EditText mPasswordField;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_in);

    mDatabase = FirebaseDatabase.getInstance().getReference();
    mAuth = FirebaseAuth.getInstance();

    // Views
    mEmailField = findViewById(R.id.field_email);
    mPasswordField = findViewById(R.id.field_password);
    Button signInButton = findViewById(R.id.button_sign_in);
    Button signUpButton = findViewById(R.id.button_sign_up);

    // Click listeners
    signInButton.setOnClickListener(this);
    signUpButton.setOnClickListener(this);
  }

  @Override
  public void onStart() {
    super.onStart();

    // Check auth on Activity start
    if (mAuth.getCurrentUser() != null) {
      onAuthSuccess(mAuth.getCurrentUser());
    }
  }

  @Override
  public void onClick(View v) {

    int i = v.getId();
    if (i == R.id.button_sign_in) {
      signIn();
    } else if (i == R.id.button_sign_up) {
      signUp();
    }
  }

  private void signIn() {

    System.out.println("++" + TAG + "::signIn()");
    if (!validateForm()) {
      return;
    }

    //showProgressDialog();
    String email = mEmailField.getText().toString();
    String password = mPasswordField.getText().toString();
    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {

            System.out.println("Sign-In complete: " + task.isSuccessful());
            //hideProgressDialog();

            if (task.isSuccessful()) {
              onAuthSuccess(task.getResult().getUser());
            } else {
              Toast.makeText(SignInActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
            }
          }
        });
  }

  private void signUp() {

    System.out.println("++" + TAG + "::signUp()");
    if (!validateForm()) {
      return;
    }

    //showProgressDialog();
    String email = mEmailField.getText().toString();
    String password = mPasswordField.getText().toString();
    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {

            System.out.println("Create user complete: " + task.isSuccessful());
            //hideProgressDialog();

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

    // write new user
    writeNewUser(user.getUid(), username, user.getEmail());

    // go to MainActivity
    startActivity(new Intent(SignInActivity.this, MainActivity.class));
    finish();
  }

  private String usernameFromEmail(String email) {

    if (email.contains("@")) {
      return email.split("@")[0];
    } else {
      return email;
    }
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
