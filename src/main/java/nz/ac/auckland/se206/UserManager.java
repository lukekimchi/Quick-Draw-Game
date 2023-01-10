package nz.ac.auckland.se206;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class UserManager {

  private static int id = 0;

  private static HashMap<Integer, User> userMap = new HashMap<Integer, User>();

  private static User currentUser;

  /**
   * Method that adds a user instance to UserMap with current id field as the key. Increase id field
   * by 1 after it is done
   *
   * @param user instance to be added
   */
  public static void addUser(User user) {
    userMap.put(id, user);
    // increase id count for next user
    id += 1;
  }

  /**
   * This method will remove user based on specified username
   *
   * @param username String representing name of user
   * @throws IOException if data cannot be saved
   */
  public static void removeUser(String username) throws IOException {
    // search for user with specified username
    for (Entry<Integer, User> user : userMap.entrySet()) {
      if (user.getValue().getUsername().equals(username)) {
        userMap.remove(user.getKey());
        break;
      }
    }
    // save user data
    App.saveData();
  }

  /**
   * method to return a user instance from map
   *
   * @param id key to access user map
   * @return User instance in user map
   */
  public static User getUser(int id) {
    return userMap.get(id);
  }

  /**
   * method to return a user instance which is the current user
   *
   * @return user instance from current user field
   */
  public static User getCurrentUser() {
    return currentUser;
  }

  /**
   * method to set current user field with a user instance
   *
   * @param username String representing name
   */
  public static void setCurrentUser(String username) {
    // find user with specified username
    for (Entry<Integer, User> user : userMap.entrySet()) {
      if (user.getValue().getUsername().equals(username)) {
        // set currentUser field to this user
        currentUser = user.getValue();
      }
    }
  }

  /**
   * method to return size of user map
   *
   * @return int user map size
   */
  public static int getSize() {
    return userMap.size();
  }

  /**
   * method to return all users
   *
   * @return entrySet of users from user map
   */
  public static Set<Entry<Integer, User>> getAllUsers() {
    return userMap.entrySet();
  }

  /** method to clear user map of all users */
  public static void clear() {
    userMap.clear();
  }
}
