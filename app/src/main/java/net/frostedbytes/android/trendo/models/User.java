package net.frostedbytes.android.trendo.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

  /**
   * Email address of user.
   */
  public String Email;

  /**
   * Name of user.
   */
  public String UserName;

  /**
   * Constructs a new User object with default values.
   */
  @SuppressWarnings("unused")
  public User() {
    this("", "");

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
  }

  /**
   * Constructor allowing creation of user with properties.
   * @param userName Name of user
   * @param eMail Email address of user
   */
  public User(String userName, String eMail) {

    this.Email = eMail;
    this.UserName = userName;
  }
}
