**Mobile Development 2024/25 Portfolio**

# Requirements

Student ID: `c23041974`

## Functional requirements

### Basic music player

The application should match the music playing functionality of VLC, including but not limited to:

- Full song list with title and artist
- Artists list with name, optional art, and number of tracks
- Album list with name and artist
- A sticky banner with the currently playing song as well as basic controls
- Android media integration for media control on lock screen
- Full text search for artists, song titles, and albums

### Invidious and yt-dlp integration

- The user should be able to search for remote songs within the app and download them in different formats
- Downloading a song should automatically insert appropriate metadata such as title, artist, and link. Art is optional
- Due to recent YouTube VPN blocks, a method must be provided for the user to log in, such as providing cookies.txt or using WebView to prompt for login.

### Spotify-dl integration

- The user should be able to import spotify playlists by providing a link
- Integration with the Spotify API is explicitly **not** a goal

### Lua-based plugins for customization

- The application should provide hooks in different areas of the interface and service to allow customization
- Plugins should contain configuration to inform the application when to make a callback or display custom buttons and text

Example plugins:

**Sort by ELO**

For such a plugin, it should register 2 callbacks:

- An upvote/downvote button that shows in a song's extra actions menu
- A sorting implementation that shows up alongside other options like alphabetical order

When the sorting option is selected, the callback should be called with the full list of songs which returns an array of song ids.

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
