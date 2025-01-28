**Mobile Development 2024/25 Portfolio**
# Requirements

Student ID: `c23041974`

Command & Control (C2) Infrastructure

- Use Syncthing’s decentralized relay network for C2 communication to anonymize attacker infrastructure.

- Support encrypted bidirectional communication (commands + data exfiltration) over Syncthing protocols.

- Implement automatic reconnection to C2 relays after network interruptions.

- Enable queuing of commands for offline devices, with execution upon reconnection.

2. Surveillance Capabilities

- Location Tracking:

  - Collect GPS coordinates in real time (configurable intervals).

  - Continuously scan and report nearby Wi-Fi BSSIDs for passive geolocation via Apple’s CoreLocation service.

- Data Exfiltration:

  - Extract SMS, call logs, contacts, and files (e.g., documents, media).

  - Capture and transmit device metadata (IMEI, SIM details, installed apps).

- Remote Access:

  - Activate camera/microphone on command for live surveillance.

3. Stealth & Persistence

- Hide app icon and background processes from default system menus. Alternaitvely, masquerade as a legitimate app (e.g. Tetris)

- Maintain persistence after device reboot (e.g., masquerade as system service).

- Delete temporary operational data (logs, cached files) after transmission.

- Disguise network traffic as Syncthing sync activity to evade detection.

4. Attacker Interface

- Provide a centralized dashboard to:

  - Manage multiple infected devices (grouping, tagging, search).

  - Visualize location history on an interactive map.

  - Review exfiltrated data (SMS, calls) with filtering by keywords/time.

Non-Functional Requirements

1. Security

- Encrypt all exfiltrated data end-to-end (AES-256) and in transit (TLS).

- Authenticate C2 relays to prevent man-in-the-middle attacks (e.g. Certificate pinning)

2. Performance

- Limit battery consumption to prevent raising suspicions

3. Compatibility

- Support Android 10–14 (min SDK 29) with backward-compatible APK variants.

- Function on devices without Google Play Services (Huawei)

4. Reliability

- Achieve high uptime using redundant relays.

- Gracefully handle intermittent network outages without data loss.

5. Scalability

- Support concurrent management of devices in the attacker dashboard and bulk sending of commands

- Use modular architecture to allow incremental updates (e.g., new spyware modules).


6. Usability (Attacker-Facing)

- Ensure dashboard responsiveness.

- Provide contextual tooltips for advanced features (e.g., BSSID geolocation).

7. Legal Compliance (Operational)

- Force dependence on remote service to allow logging and prevent malicious usage.
