**Mobile Development 2024/25 Portfolio**

# Retrospective

Student ID: `c23041974`

I set out with quite an ambitious goal: to completely replace VLC (with all the basic features) while adding features such as Invidious integration, `yt-dlp` download, and WASM plugins. Despite the limited time, I was able to meet most of my initial goals with the exception of Spotify integration. Whenever something went wrong, I would first try to fix it by referring to documentation. When unable to resolve the issue quickly, I used workarounds that built up technical debt - for example, stuffing data in semantically incorrect fields to work around missing tags. Suboptimal but works with time constraints.

One thing I would do differently is a higher separation of concerns in regards to business logic and parts that touch the Android API. Jetpack compose is on its way to becoming cross platform but due to direct dependencies on Android APIs, it is currently not possible to port to other platforms. Instead, all platform-specific code should be abstracted into a compatible interface which is then used in the shared business logic.

If I had more time, some features I would add:

- Automatically fetch artist art via Invidious
- Subtitle integration
- Playback speed settings
- A plugin registry like npm

# References

Bianchi, A. (2024) Compose multiplatform: Paving the way for the future of cross-platform development, Medium. Available at: <https://medium.com/@xandebianchi/compose-multiplatform-paving-the-way-for-the-future-of-cross-platform-development-d5665e837f86> (Accessed: 06 April 2025).
