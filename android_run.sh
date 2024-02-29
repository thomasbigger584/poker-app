#!/usr/bin/env bash

cd client/android || exit
./gradlew build || exit

adb devices -l | {
  read -r _ # discard first line
  while IFS= read -r line; do
    device_id=$(echo "$line" | awk '{print $1}' | xargs)

    adb -s "$device_id" uninstall com.twb.pokerapp
    adb -s "$device_id" install -r app/build/outputs/apk/debug/app-debug.apk

    # running forked as adb shell doesn't allow the next iteration for some reason
    adb -s "$device_id" shell am start \
        -n com.twb.pokerapp/com.twb.pokerapp.ui.activity.login.LoginActivity \
        -a android.intent.action.MAIN \
        -c android.intent.category.LAUNCHER \
        &
  done
}

