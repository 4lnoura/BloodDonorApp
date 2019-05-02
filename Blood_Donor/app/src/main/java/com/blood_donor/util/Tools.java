package com.blood_donor.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;

import com.blood_donor.models.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by moses on 11/1/18.
 */

public class Tools {
    /**
     * Check if the entered email is valid
     *
     * @param email
     * @return
     */
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Get text from text input layout
     *
     * @param textInputLayout
     * @return
     */
    public static String getText(TextInputLayout textInputLayout) {
        return textInputLayout.getEditText().getText().toString();
    }

    public static void setText(TextInputLayout textInputLayout, String text) {
        textInputLayout.getEditText().setText(text);
    }

    /**
     * Saves a user to Android Settings, this will help us retrieve user data for future reference
     *
     * @param context
     * @param user
     */
    public static void saveUserDetails(Context context, User user) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        editor.putInt("age", user.getAge());
        editor.putString("bloodType", user.getBloodType());
        editor.putString("diseases", user.getDiseases());
        editor.putString("email", user.getEmail());
        editor.putString("location", user.getLocation());
        editor.putString("name", user.getName());
        editor.putString("phoneNumber", user.getPhoneNumber());
        editor.putInt("status", user.getStatus());
        editor.putString("photoUrl", user.getPhotoUrl());
        editor.putBoolean("isAdmin", user.isAdmin());

        editor.apply();
    }

    /**
     * Retrieve the user from the Android Preference Settings
     *
     * @param context
     * @return
     */
    public static User getUser(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (!preferences.contains("email")) {
            return null; // if the preferences do not contain the "email", key return null that there is no user
        }
        User user = new User();
        user.setAge(preferences.getInt("age", 0));
        user.setBloodType(preferences.getString("bloodType", ""));
        user.setDiseases(preferences.getString("diseases", ""));
        user.setEmail(preferences.getString("email", ""));
        user.setLocation(preferences.getString("location", ""));
        user.setName(preferences.getString("name", ""));
        user.setPhotoUrl(preferences.getString("phoneNumber", ""));
        user.setStatus(preferences.getInt("status", 0));
        user.setPhotoUrl(preferences.getString("photoUrl", ""));
        user.setAdmin(preferences.getBoolean("isAdmin", false));

        return user;
    }

    /**
     * Clear the user information
     * <p>
     * This will happen when the user logs out
     *
     * @param context
     */
    public static void clearUserInfo(Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        editor.remove("age");
        editor.remove("bloodType");
        editor.remove("diseases");
        editor.remove("email");
        editor.remove("location");
        editor.remove("name");
        editor.remove("phoneNumber");
        editor.remove("state");
        editor.remove("photoUrl");
        editor.remove("isAdmin");

        editor.apply();
    }

}


