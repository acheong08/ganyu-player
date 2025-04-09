**Mobile Development 2024/25 Portfolio**

# Overview

Student ID: `c23041974`

Summary: A developer-focused but user-friendly local music player that streamlines discovery, acquisition, and management.

Spotify has ads, require you to pay for downloads, and is missing a ton of smaller artists/vocaloid songs. The alternative thus far has been using a combination `yt-dlp` and `spotDL` to download music from the command line on desktops and using `rsync` to efficiently copy them onto a phone to play on VLC. For music discovery and subscriptions, a Invidious seems to work best. The app aims to streamline this workflow into a single unified interface.

In addition, current music players are currently difficult to extend and automate, leading to long-standing gaps like VLC's decade-old feature request for sorting by date downloaded. I propose a Webassembly plugin system similar to that of Neovim, allowing developers to hook into existing functions to extend functionality without forking the core app. For example, plugins could automatically downloading songs from subscriptions and manage playlists. These plugins should be easily sharable/discoverable to allow non-technical users to benefit from the work of technical ones. Finally, it must be easy to migrate to, supporting imports from local files or Spotify playlists

Overall, it replaces patchwork tools with a all-in-one solution for users prioritizing control and convenience.
