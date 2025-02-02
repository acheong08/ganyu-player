**Mobile Development 2024/25 Portfolio**
# Overview

Student ID: `c23041974`

The proposed application is a combination of spyware and c2 client, both Android mobile apps.

Many open source Android RATs such as AhMyth have long been abandoned with clunky suboptimal interfaces while also being trivial to detect. This project is intended as a successor with the same core functionality (camera, sms, etc) while incorporating new research and improving the user interface.

For example:
- [Syncthing infrastructure as C2 transport](https://github.com/acheong08/syndicate) allows communication without exposing attacker information such as IP address and removing the requirement for a public server.
- [Location surveillence via Apple's CoreLocation infrastructure](https://github.com/acheong08/apple-corelocation-experiments) allows victims to be tracked even after uninstalling the spyware through their routers and hotspots.

This project has good educational value due to the breath of Android APIs that will be used by nature of spyware. Furthermore, I hope to spur protocols such as syncthing to re-evaluate their relay infrastructure to limit unintentional exposure to malicious traffic and for Apple to lock down their location service infrastructure to prevent abuse.

This application will mostly be useful as a base for researching potential malware behavior and detection avenues. However, it could also be useful to law enforcement and governments which could adapt strategies showcased in their own systems.
