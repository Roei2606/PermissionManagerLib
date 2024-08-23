package com.example.permissionlibrary;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.permissionlibrary.Interfaces.PermissionResultListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.HashMap;
import java.util.Map;

public class PermissionManager implements PermissionResultListener {

    private static final String PREFS_NAME = "PermissionPrefs";
    private static final String PREFS_DENIED_SUFFIX = "_denied";
    private static final String PREFS_DENIAL_COUNT_SUFFIX = "_denial_count";
    private static final int MAX_DENIALS = 2;

    private Activity activity;
    private Map<Integer, String> permissionMap;
    private ShapeableImageView applyButton;
    private Dialog dialog;
    private SharedPreferences sharedPreferences;

    public PermissionManager(Activity activity) {
        this.activity = activity;
        this.sharedPreferences = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        initializePermissionMap();
        addApplyButtonToActivity();
    }

    private void initializePermissionMap() {
        permissionMap = new HashMap<>();
        permissionMap.put(1, "Camera");
        permissionMap.put(2, "Location");
        permissionMap.put(3, "Storage");
        permissionMap.put(4, "Contacts");
        permissionMap.put(5, "SMS");
        permissionMap.put(6, "Phone");
        permissionMap.put(7, "Audio");
        permissionMap.put(8, "Calendar");
        permissionMap.put(9, "Background Location");
        permissionMap.put(10, "Internet");
    }

    private void addApplyButtonToActivity() {
        FrameLayout rootLayout = activity.findViewById(android.R.id.content);

        applyButton = new ShapeableImageView(activity);
        applyButton.setId(View.generateViewId());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(16, 16, 16, 16); // Adjust margins as needed
        applyButton.setLayoutParams(params);
        applyButton.setImageResource(R.drawable.ic_apply); // Ensure this drawable exists
        applyButton.setContentDescription("Apply");
        applyButton.setOnClickListener(v -> showPermissionDialog());

        rootLayout.addView(applyButton);
    }

    public boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission(String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    public void showPermissionDialog() {
        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_permissions);

        applyButton.setVisibility(View.GONE);

        dialog.setOnDismissListener(dialogInterface -> applyButton.setVisibility(View.VISIBLE));

        setupPermissionButton(dialog, R.id.button_camera_permission, new String[]{Manifest.permission.CAMERA}, 1, R.id.text_camera_permission);
        setupPermissionButton(dialog, R.id.button_location_permission, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2, R.id.text_location_permission);
        setupPermissionButton(dialog, R.id.button_storage_permission, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3, R.id.text_storage_permission);
        setupPermissionButton(dialog, R.id.button_contacts_permission, new String[]{Manifest.permission.READ_CONTACTS}, 4, R.id.text_contacts_permission);
        setupPermissionButton(dialog, R.id.button_sms_permission, new String[]{Manifest.permission.READ_SMS}, 5, R.id.text_sms_permission);
        setupPermissionButton(dialog, R.id.button_phone_permission, new String[]{Manifest.permission.CALL_PHONE}, 6, R.id.text_phone_permission);
        setupPermissionButton(dialog, R.id.button_audio_permission, new String[]{Manifest.permission.RECORD_AUDIO}, 7, R.id.text_audio_permission);
        setupPermissionButton(dialog, R.id.button_calendar_permission, new String[]{Manifest.permission.READ_CALENDAR}, 8, R.id.text_calendar_permission);
        setupPermissionButton(dialog, R.id.button_background_location_permission, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 9, R.id.text_background_location_permission);
        setupPermissionButton(dialog, R.id.button_internet_permission, new String[]{Manifest.permission.INTERNET}, 10, R.id.text_internet_permission);

        dialog.show();
    }

    private void setupPermissionButton(Dialog dialog, int buttonId, final String[] permissions, final int requestCode, int textViewId) {
        ShapeableImageView button = dialog.findViewById(buttonId);
        MaterialTextView textView = dialog.findViewById(textViewId);
        button.setOnClickListener(v -> requestPermission(permissions, requestCode));

        updateTextViewColor(textView, permissions[0]);
    }

    private void updateTextViewColor(MaterialTextView textView, String permission) {
        boolean isDenied = sharedPreferences.getBoolean(permission + PREFS_DENIED_SUFFIX, false);
        if (checkPermission(permission)) {
            textView.setTextColor(Color.GREEN);
        } else if (isDenied) {
            textView.setTextColor(Color.RED);
        } else {
            textView.setTextColor(Color.BLACK);
        }
    }


    public void handlePermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissions.length > 0 && grantResults.length > 0) {
            String permissionName = permissionMap.get(requestCode);
            boolean granted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            Toast.makeText(activity, permissionName + " permission " + (granted ? "granted" : "denied"), Toast.LENGTH_SHORT).show();
            savePermissionState(permissions[0], !granted);
            updatePermissionTextColor(permissionName, granted);
            handleDenialRedirect(permissions[0]);
        }
    }
    private void savePermissionState(String permission, boolean denied) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(permission + PREFS_DENIED_SUFFIX, denied);
        if (denied) {
            int denialCount = sharedPreferences.getInt(permission + PREFS_DENIAL_COUNT_SUFFIX, 0) + 1;
            editor.putInt(permission + PREFS_DENIAL_COUNT_SUFFIX, denialCount);
        }
        editor.apply();
    }

    private void handleDenialRedirect(String permission) {
        int denialCount = sharedPreferences.getInt(permission + PREFS_DENIAL_COUNT_SUFFIX, 0);
        if (denialCount >= MAX_DENIALS) {
            // Redirect to app settings
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
            activity.startActivity(intent);
        }
    }

    private int getTextViewId(String permissionName) {
        switch (permissionName) {
            case "Camera":
                return R.id.text_camera_permission;
            case "Location":
                return R.id.text_location_permission;
            case "Storage":
                return R.id.text_storage_permission;
            case "Contacts":
                return R.id.text_contacts_permission;
            case "SMS":
                return R.id.text_sms_permission;
            case "Phone":
                return R.id.text_phone_permission;
            case "Audio":
                return R.id.text_audio_permission;
            case "Calendar":
                return R.id.text_calendar_permission;
            case "Background Location":
                return R.id.text_background_location_permission;
            case "Internet":
                return R.id.text_internet_permission;
            default:
                return 0;
        }
    }

    private void updatePermissionTextColor(String permissionName, boolean granted) {
        int color;
        int denialCount = sharedPreferences.getInt(permissionName + PREFS_DENIAL_COUNT_SUFFIX, 0);

        if (granted) {
            color = Color.GREEN; // Green if granted
        } else if (denialCount < MAX_DENIALS) {
            color = Color.BLACK; // Black if denied fewer than MAX_DENIALS times
        } else {
            color = Color.RED; // Red if denied MAX_DENIALS times
        }

        int textViewId = getTextViewId(permissionName);

        if (textViewId != 0 && dialog != null) {
            MaterialTextView textView = dialog.findViewById(textViewId);
            if (textView != null) {
                textView.setTextColor(color);
            }
        }
    }

    @Override
    public void onPermissionResult(String permission, boolean granted) {
        updatePermissionTextColor(permission, granted);
    }
}