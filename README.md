# DisableCamera
開発者向けカメラアプリテスト用アプリ

DisableCamera 端末管理機能でカメラを無効化 - Google Play
https://play.google.com/store/apps/details?id=at.wada811.disablecamera

端末管理機能でカメラを無効化した際に
Camera#open で RuntimeException が発生するか、
その際の処理が適切に行われるかテストするためのアプリです。

参考
Device Administration | Android Developers
http://developer.android.com/guide/topics/admin/device-admin.html

DevicePolicyManager | Android Developers
http://developer.android.com/reference/android/app/admin/DevicePolicyManager.html

DeviceAdminReceiver | Android Developers
http://developer.android.com/reference/android/app/admin/DeviceAdminReceiver.html

DeviceAdminInfo | Android Developers
http://developer.android.com/reference/android/app/admin/DeviceAdminInfo.html#USES_POLICY_DISABLE_CAMERA