# DriveAppFolderViewer
Allows easy visualization of files within an AppFolder in Google Drive

<img src="/art/screenshot-1.png?raw=true" width="200px">

[![](https://jitpack.io/v/Commit451/DriveAppFolderViewer.svg)](https://jitpack.io/#Commit451/DriveAppFolderViewer)

## Dependency
Add this in your root `build.gradle` file (**not** your module `build.gradle` file):

```gradle
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```

Then, add the library to your project `build.gradle`
```gradle
dependencies {
    debugImplementation 'com.github.Commit451:DriveAppFolderViewer:latest.version.here'
    // or, just "implementation" if you want to use this in all your builds
}
```
We recommend restricting access to this UI to just debug builds, since it is powerful and users could end up deleting important files without understanding what they do.

## Usage
This library is intended to be used by developers to get a visualization of the folders they are creating within their private app folders in Google Drive. This is useful at times when you want to see that files and folders are being created properly and get a visualization of the file/folder structure within your app.
To launch the file viewer:
```kotlin
val intent = DriveAppFolderViewer.intent(context)
startActivity(intent)
```
Note: This library makes no attempt to resolve Google API connection issues, so it is best that you assure that a GoogleApiClient is connected before starting this intent.

## Contributing
To contribute to this project and test it with the sample within this repo, you will need to generate your own OAuth 2.0 Client ID following the steps [here](https://developers.google.com/drive/android/get-started)

## Note
- This library is pretty hefty, bringing in Kotlin, Coroutines, okio, and [okyo](https://github.com/Commit451/okyo), as well as the Google Drive v3 Java APIs. Make sure you are aware of this.
- There is a 1,000 page limit on results, therefore only the first 1,000 files/folders will be shown within a folder.

License
--------

    Copyright 2022 Commit 451

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
