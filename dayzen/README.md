# DayZen Android – visual 24-hour planner

A native Android implementation inspired by the public DayZen concept: a complete day shown as a radial 24-hour clock, with visual time blocks, quick task creation, editing and local/offline persistence.

## GitHub build

Upload the **contents of this archive directly into the repository root**. Do not place the project inside another folder.

GitHub Actions builds `app/build/outputs/apk/debug/app-debug.apk` and publishes it as the `DayZen-debug-apk` artifact.

The workflow intentionally has no manual `workflow_dispatch` trigger. A push to any branch starts the build.

## Included
- 24-hour radial clock UI
- Current-time hand and live digital time
- Colored time blocks
- Add and edit blocks
- Local offline storage
- Example schedule on first launch
- Modern light UI
- Java + Gradle Android project
