# PermissionManager Library

PermissionManager is an Android library designed to simplify the process of managing runtime permissions in Android applications. This library provides a user-friendly interface for requesting permissions and displaying their status in a dialog.

## Features

- Request multiple runtime permissions.
- Display the current status of each permission.
- Persist denied permissions state even after the dialog is closed.
- Easily customizable and extendable.

## Installation

### Gradle

Add the following dependency to your project's `build.gradle` file:

```gradle
dependencies {
    implementation 'com.example:permissionlibrary:1.0.0'
}
```
## Usage

### Step 1: Initialize and Configure PermissionManager in Your Activity
In your Activity, create an instance of PermissionManager:
```java
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.permissionlibrary.PermissionManager;

public class MainActivity extends AppCompatActivity {

    private PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionManager = new PermissionManager(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.handlePermissionsResult(requestCode, permissions, grantResults);
    }
}
```
### Step 2: Configure Permissions in AndroidManifest
Ensure that you have declared all the necessary permissions in your AndroidManifest.xml:
```java
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.permissionmanagerproject">

    <application
        ...>
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_CONTACTS" />
    <uses-permission android:name="android.permission.READ_SMS" />
    <uses-permission android:name="android.permission.CALL_PHONE" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.READ_CALENDAR" />
    <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
    <uses-permission android:name="android.permission.INTERNET" />
</manifest>
```
