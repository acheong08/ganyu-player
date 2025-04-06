**Mobile Development 2024/25 Portfolio**

# Retrospective

Student ID: `c23041974`

I set out with quite an ambitious goal: to completely replace VLC (with all the basic features) while adding features such as Invidious integration, `yt-dlp` download, and Lua scripting. Despite the limited time, I was able to meet most of my initial goals with the exception of Spotify integration. Whenever something went wrong, I would first try to fix it by referring to documentation. When unable to resolve the issue quickly, I used workarounds that built up technical debt - for example, stuffing data in semantically incorrect fields to work around missing tags.

One thing I would do differently is a higher separation of concerns in regards to business logic and parts that touch the Android API. Jetpack compose is on its way to becoming cross platform but due to direct dependencies on Android APIs, it is currently not possible to port to other platforms. Instead, all platform-specific code should be abstracted into a compatible interface which is then used in the shared business logic.

If I had more time, I would replace Lua scripting with Webassembly to support more languages and improve performance. Other additional features:

- Automatically fetch artist art via Invidious
- Subtitle integration
- Playback speed settings

# References

<https://www.jetbrains.com/compose-multiplatform/>

