**Mobile Development 2024/25 Portfolio**

# API Description

Student ID: `c23041974`

`contentResolver` and `MediaStore` queries are used to retrieve music files its metadata from the filesystem. This is because `READ_EXTERNAL_STORAGE` has been deprecated in favor of more restrictive permissions. However, this limits the metadata we can read as only a limited number of tags are available through this API. Workarounds had to be used such as stuffing the content ID in the genre tag to ensure compatibility.

Key/value `DataStore<Preferences>` is used for storing settings and cached data such as login data for invidious. Instead of storing credentials, only cookies are stored so that you can invalidate the session securely if a device is lost. A Room database is used for storing playlist names and mappings from song IDs. While this is less portable than storing it in the metadata of music files itself, the specific tag used is format-specific and thus not supported by `MediaStore`.

A `MediaSessionService` which wraps around `ExoPlayer` is used to play back audio as well as allowing users to control playback from the lockscreen or external devices (e.g. headphones) via an interactive notification. While the default `MediaPlayer` API is sufficient for local playback of m4a files, `ExoPlayer` supports additional features such as streaming that may be useful in the future. There isn't any added complexity since it's wrapped by `MediaSessionService` and thus there is no reason not to use it.

Finally, Android's NDK API is used to bind to CPython and allow the use of python libraries like `yt-dlp` for downloading music from YouTube and `mutagen` for writing metadata from Kotlin. This makes the matter of media acquisition much easier since there is no equivalent library available for downloading audio from YouTube available in Java. Furthermore, `jaudiotagger` has not been maintained for years and currently has bugs in regards to `m4a`/`mp4` formats leading to crashes in Android.

## References

Android NDK  :   Android developers (no date) Android Developers. Available at: <https://developer.android.com/ndk/> (Accessed: 06 April 2025).

Behavior changes: Apps targeting Android 13 or higher  :   android developers (no date) Android Developers. Available at: <https://developer.android.com/about/versions/13/behavior-changes-13> (Accessed: 06 April 2025).
