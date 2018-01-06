package net.frostedbytes.android.trendo.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

  public String username;
  public String email;

  @SuppressWarnings("unused")
  public User() {

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    this.username = "";
    this.email = "";
  }

  public User(String username, String email) {

    this.username = username;
    this.email = email;
  }
}
