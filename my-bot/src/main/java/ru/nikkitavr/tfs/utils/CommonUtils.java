package ru.nikkitavr.tfs.utils;

public class CommonUtils {

  public static String mergeToFullName(String firstName, String lastName) {
    StringBuilder fullName = new StringBuilder();
    if (firstName != null) {
      fullName.append(firstName);
      if (lastName != null) {
        fullName.append(" ").append(lastName);
      }
    }
    return fullName.isEmpty() ? null : fullName.toString();
  }
}
