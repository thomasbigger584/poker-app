#!/usr/bin/env bash

cd client/android || exit
#./gradlew clean build || exit

adb devices -l | {
  read -r _ # discard first line
  while IFS= read -r line; do
    device_id=$(echo "$line" | awk '{print $1}' | xargs)
    adb -s "$device_id" install -r app/build/outputs/apk/debug/app-debug.apk
    adb -s "$device_id" shell am start -n com.twb.pokergame/com.twb.pokergame.ui.activity.login.LoginActivity &
  done
}

