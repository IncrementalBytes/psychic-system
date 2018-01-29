package net.frostedbytes.android.trendo.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

  public String UserName;
  public String Email;

  @SuppressWarnings("unused")
  public User() {

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    this.UserName = "";
    this.Email = "";
  }

  public User(String username, String email) {

    this.UserName = username;
    this.Email = email;
  }
}
