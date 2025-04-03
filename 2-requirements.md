**Mobile Development 2024/25 Portfolio**

# Requirements

Student ID: `c23041974`

## Functional requirements

This is an ambitious project and not all functionality might end up completed. The list below is ordered by prioritization of implementation.

### Basic music player

The application should match the music playing functionality of VLC, including but not limited to:

- Full song list with title and artist
- Artists list with name and number of tracks
- A sticky banner with the currently playing song as well as basic controls
- Android media integration for media control on lock screen

### Invidious and yt-dlp integration

- The user should be able to search for remote songs within the app and download
- Downloading a song should automatically insert appropriate metadata such as title, artist, and link. Art is optional
- Invidious subscription feed that shows the latest songs from artists which can be downloaded on click

### Spotify-dl integration

- The user should be able to import spotify playlists by providing a link

### Lua-based plugins for customization

- The application should provide hooks in different areas of the interface and service to allow customization
- Plugins should contain a manifest to inform the application when to make a callback or display custom buttons and text

Example plugin:

**Daily song download**

This plugin should integrate with Invidious to download new songs from subscriptions on a daily basis. To achieve this, the application must provide hooks to schedule background tasks, fetch and download songs, as well as manage playlists.

## Non-functional requirements

### User experience

- The application should be performant and be able handle playlist sizes of up to 10,000 songs which equates to approximately 30 gigabytes.
- Interactions should be intuitive, matching common Android actions such as swiping left to delete, long pressing for extra context, etc.
- Processing and IO should not be done on the main thread. All data should be loaded and processed asynchronously

### Security

- Credentials for third party services should be stored securely and cannot be accessed by other apps.
- Plugins should be sandboxed and should not be able to access any Android APIs such as files or images
- Permissions must be explicitly given for plugins to access features such as internet access

### Other

- The application must be locked in portrait mode
- The font used in the application must be consistent
- The application must have consistent styling and colour theme
- The application shall scale the font by the user’s font size preference
- When the app launches for the first time, all required permissions (e.g. Media access) shall be requested immediately
