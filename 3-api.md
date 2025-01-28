**Mobile Development 2024/25 Portfolio**
# API Description

Student ID: `c23041974`

_Complete the information above and then write your 300-word API description here. You can delete this line.__
The application leverages Android APIs to enable stealth, persistence, and surveillance while prioritizing efficiency and evasion.

1. Background Execution & Stealth

- Service API: A foreground service with START_STICKY ensures uninterrupted operation, masking itself using Notification.Builder to display benign system alerts (e.g., “Updating…”).

- PackageManager: Disables launcher icon visibility via setComponentEnabledSetting to evade user detection.

2. Data Collection

- TelephonyManager: Retrieves IMEI, SIM data, and network state for device fingerprinting.

- ContentResolver: Accesses SMS, contacts, and call logs via content:// URIs (e.g., Telephony.Sms.CONTENT_URI), bypassing direct permission prompts where cached data exists.

- FusedLocationProviderClient: Fetches GPS coordinates with PRIORITY_HIGH_ACCURACY while dynamically adjusting intervals to minimize battery drain.

- WifiManager: Scans nearby BSSIDs for passive geolocation via getScanResults(), aligning with Apple’s CoreLocation-like tracking.

3. Surveillance Features

- Camera2 API: Captures photos/videos silently using CameraDevice.TEMPLATE_RECORD, suppressing shutter sounds via AudioManager.STREAM_SYSTEM volume control.

- MediaRecorder: Monitors microphone input with AudioSource.VOICE_COMMUNICATION for stealthy audio recording.

4. Persistence & Evasion

- JobScheduler: Queues exfiltration tasks during device idle periods to reduce resource footprint.

- FileProvider: Manages temporary files to avoid java.io security restrictions, auto-deleting artifacts post-transmission.

5. Network Communication

- WorkManager: Handles Syncthing-based C2 synchronization, retrying failed transmissions with exponential backoff.

- ConnectivityManager: Monitors network state changes to resume stalled operations instantly.

