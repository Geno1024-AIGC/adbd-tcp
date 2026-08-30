# adbd-tcp

[![Build](https://github.com/Geno1024-AIGC/adbd-tcp/actions/workflows/build.yml/badge.svg)](https://github.com/Geno1024-AIGC/adbd-tcp/actions/workflows/build.yml)

一个零依赖的 Android 小工具:通过 root(su)设置 `adb` 的 TCP 监听端口,并启动/停止 `adbd` 服务。界面只有两个按钮和一个端口输入框。

[English README](README.md)

## 功能

- 设置端口:`setprop service.adb.tcp.port <port>`,并持久化到 SharedPreferences
- 启动/停止 `adbd`:`setprop ctl.start adbd` / `setprop ctl.stop adbd`
- 实时显示当前 `service.adb.tcp.port` 与 `init.svc.adbd` 状态
- 无任何第三方依赖(无 AndroidX、无 UI 库),纯平台 API + 原生命令

## 要求

- Android 5.0 (API 21) 及以上
- 已 root 的设备(应用通过 `su` 执行命令)
- 首次使用时在超级用户管理器(如 Magisk)中授予 root 权限

## 构建

```sh
# 使用 Gradle Wrapper(推荐)
./gradlew assembleDebug
# 或使用系统 Gradle
gradle assembleDebug
```

APK 输出位置:

```
app/build/outputs/apk/debug/app-debug.apk
```

> 注意:请使用常规 OpenJDK 而非 GraalVM 构建,AGP 的 jlink 变换在 GraalVM 上会失败
> (报错 `Module jdk.internal.vm.ci not found`)。

## 使用

1. 打开应用,输入端口号(默认 `5555`)
2. 点 **Set Port** 设置 `service.adb.tcp.port`,端口会被保存,下次启动自动回填
3. 点 **Start/Stop ADBD** 切换 `adbd` 运行状态
4. 然后在电脑上执行 `adb connect <设备IP>:<端口>`

## 原理

- `setprop service.adb.tcp.port <port>` 设置 adbd 的 TCP 监听端口
- 状态回读:`getprop init.svc.adbd` 返回 `running` / `stopped` / `restarting`
- 切换服务即向 `init` 的控制接口 `ctl.start` / `ctl.stop` 写入属性
  (等价于 shell 的 `start` / `stop` 命令)
- 所有命令通过 `su -c` 在子线程执行,避免阻塞 UI

## 免责声明

本应用会修改系统属性并控制系统服务,请自行承担使用风险。仅用于调试目的。