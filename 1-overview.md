**Mobile Development 2024/25 Portfolio**

# Overview

Student ID: `c23041974`

Summary: A developer-focused but user-friendly local music player that streamlines discovery, acquisition, and management.

Spotify has ads, require you to pay for downloads, and is missing a ton of smaller artists/vocaloid songs. The alternative thus far has been using a combination `yt-dlp` and `spotDL` to download music from the command line on desktops and using `rsync` to efficiently copy them onto a phone to play on VLC. For music discovery and subscriptions, a Invidious seems to work best. The app aims to streamline this workflow into a single unified interface.

In addition, current music players are currently difficult to extend and automate, leading to long-standing gaps like VLC's decade-old feature request for sorting by date downloaded. I propose a Lua plugin system similar to that of Neovim, allowing developers to hook into existing functions to extend functionality without forking the core app. For example, plugins could automatically downloading songs from subscriptions and manage playlists. These plugins should be easily sharable/discoverable to allow non-technical users to benefit from the work of technical ones.

Finally, it must be easy to migrate to, supporting imports from local files or Spotify playlists

Overall, it replaces patchwork tools with a all-in-one solution for users prioritizing control and convenience.

<!--
Proposed application: A scriptable, developer-focused music player automating personalized music management.

The app streamlines fragmented workflows (e.g., manual YouTube/CLI downloads, rsync transfers to players like VLC) by integrating spotify-dl/yt-dlp for playlist imports, Invidious for artist subscriptions/auto-downloads, and a Luau scripting engine for plugins. Users write/share scripts (e.g., custom sorting, "Spotify Wrapped"-style reports) via a repository, bypassing reliance on stagnant platforms—addressing long-standing gaps like VLC’s decade-old sorting feature requests.

Why build this? Developers and power users lack tools that balance automation with deep customization. Existing players either restrict workflows (e.g., no CLI integration) or lack extensibility, forcing users to tolerate missing features or maintain brittle DIY setups. This app solves both: scripting enables tailored functionality (e.g., auto-organize by release date, sync with niche cloud services) without forking the core app, while backend integrations unify music discovery, download, and playback.

Value: It replaces patchwork tools with a programmable, all-in-one solution for users prioritizing control over their music libraries. The plugin ecosystem fosters community-driven innovation, ensuring adaptability to evolving needs—a necessity for audiophiles, hobbyists, and developers seeking efficiency without sacrificing personalization. -->
