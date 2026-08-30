# adbd-tcp

[![Build](https://github.com/Geno1024-AIGC/adbd-tcp/actions/workflows/build.yml/badge.svg)](https://github.com/Geno1024-AIGC/adbd-tcp/actions/workflows/build.yml)

A zero-dependency Android utility that sets the TCP listening port of `adb` and
starts/stops the `adbd` service via root (su). The UI is just two buttons and a
port input field.

[中文文档](README.zh-CN.md)

## Features

- Set port: `setprop service.adb.tcp.port <port>`, persisted in SharedPreferences
- Start/stop `adbd`: `setprop ctl.start adbd` / `setprop ctl.stop adbd`
- Live status of `service.adb.tcp.port` and `init.svc.adbd`
- No third-party dependencies (no AndroidX, no UI libraries), pure platform APIs
  and native commands

## Requirements

- Android 5.0 (API 21)+
- A rooted device (the app executes commands via `su`)
- Grant root on first use in your superuser manager (e.g. Magisk)

## Build

```sh
# With the Gradle wrapper (recommended)
./gradlew assembleDebug
# Or with a system Gradle
gradle assembleDebug
```

APK output:

```
app/build/outputs/apk/debug/app-debug.apk
```

> Note: build with a regular OpenJDK rather than GraalVM. AGP's jlink transform
> fails on GraalVM with `Module jdk.internal.vm.ci not found`.

## Usage

1. Open the app and enter a port (default `5555`)
2. Tap **Set Port** to set `service.adb.tcp.port`; the value is saved and
   restored on next launch
3. Tap **Start/Stop ADBD** to toggle the `adbd` service
4. From your computer: `adb connect <device-ip>:<port>`

## How it works

- `setprop service.adb.tcp.port <port>` sets adbd's TCP listening port
- Status read-back: `getprop init.svc.adbd` returns `running` / `stopped` /
  `restarting`
- Toggling the service writes the `ctl.start` / `ctl.stop` control properties of
  `init` (equivalent to the shell `start` / `stop` commands)
- All commands run through `su -c` on a background thread to keep the UI responsive

## Disclaimer

This app mutates system properties and controls a system service. Use at your own
risk. For debugging purposes only.