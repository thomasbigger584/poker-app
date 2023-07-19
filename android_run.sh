#!/usr/bin/env bash

cd client/android || exit
./gradlew clean build || exit

adb devices -l | {
  read -r _ # discard first line
  while IFS= read -r line; do
    device_id=$(echo "$line" | awk '{print $1}')

    if [ -n "$device_id" ]; then
      device_name=$(echo "$line" | awk '{print $5}')

      echo "Installing to $device_name..."
      adb -s $device_id install -r app/build/outputs/apk/debug/app-debug.apk

      echo "Running app on $device_name..."
      adb -s $device_id shell monkey -p com.twb.pokergame 1
    fi
  done

  echo "done"
}
