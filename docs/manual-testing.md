# Manual sharing regression checklist

Run this checklist on a physical Android device before merging a change that affects
sharing, storage access, networking, or server lifecycle.

## Setup

- Install the debug APK on an Android device.
- Connect that device and a second device (or computer) to the same Wi-Fi network.
- Prepare a test folder that contains at least one small file and one large file.

## Checklist

1. Launch MyFi and confirm the server status indicates that sharing is online.
2. Tap **Select folder** and choose the prepared folder through Android's system folder picker.
3. Confirm the selected folder's files are available from the browser page.
4. Scan the QR code with the second device, or enter the displayed `http://<ip>:8080` URL manually.
5. Confirm the browser page loads and its search and sort controls work.
6. Download a small file and verify that its name and contents match the original.
7. Download a large file and verify that transfer progress and speed are shown, then that the UI returns to the normal online state.
8. Tap the server power button to stop sharing and confirm the browser can no longer reach the URL.
9. Tap the server power button again and confirm the browser can reach the same URL and download a file.
10. Close the app and confirm the local server is no longer reachable.

## Expected result

Every step completes without a crash, and only the folder explicitly selected by the
user is exposed through the browser page.
