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
