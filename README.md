# Activity launch blocked when PIP mode is active

## Description
This project demonstrates a bug that prevents Activities from launching when in PIP mode.  
The system believes the activity is in the background when it's not, resulting in the Activity being blocked until PIP is dismissed.

Steps to reproduce reliably:
1. Open video Activity
2. Press system back to enter PIP mode
3. Background and foreground the app
4. Attempt to open message activity 
5. Activity only opens when PIP is dismissed

https://github.com/user-attachments/assets/fb457de3-6e34-4a6d-809e-d6788f06515d

## ActivityTaskManager logging
When inspecting `ActivityTaskManager` in logcat we can see the following messages. It seems the system thinks the app is in the background when it is not.

```
Background activity launch blocked! goo.gle/android-bal [callingPackage: com.slecornu.pipdemo; callingPackageTargetSdk: 36; callingUid: 10404; callingPid: 16461; appSwitchState: 2; callingUidHasVisibleActivity: true; callingUidHasVisibleNotPinnedActivity: false; callingUidHasNonAppVisibleWindow: false; callingUidProcState: TOP; isCallingUidPersistentSystemProcess: false; allowBalExemptionForSystemProcess: false; intent: Intent { xflg=0x4 cmp=com.slecornu.pipdemo/.MessageActivity }; callerApp: ProcessRecord{63e3dd2 16461:com.slecornu.pipdemo/u0a404}; inVisibleTask: true; balAllowedByPiCreator: BSP.ALLOW_BAL; resultIfPiCreatorAllowsBal: BAL_BLOCK; callerStartMode: MODE_BACKGROUND_ACTIVITY_START_SYSTEM_DEFINED; hasRealCaller: true; isCallForResult: false; isPendingIntent: false; autoOptInReason: notPendingIntent; realCallingPackage: com.slecornu.pipdemo; realCallingPackageTargetSdk: 36; realCallingUid: 10404; realCallingPid: 16461; realCallingUidHasVisibleActivity: true; realCallingUidHasVisibleNotPinnedActivity: false; realCallingUidHasNonAppVisibleWindow: false; realCallingUidProcState: TOP; isRealCallingUidPersistentSystemProcess: false; originatingPendingIntent: null; realCallerApp: ProcessRecord{63e3dd2 16461:com.slecornu.pipdemo/u0a404}; realInVisibleTask: true; balAllowedByPiSender: BSP.ALLOW_BAL; resultIfPiSenderAllowsBal: BAL_BLOCK; realCallerStartMode: MODE_BACKGROUND_ACTIVITY_START_SYSTEM_DEFINED; balRequireOptInByPendingIntentCreator: true; balDontBringExistingBackgroundTaskStackToFg: true]
```
...
```
START u0 {xflg=0x4 cmp=com.slecornu.pipdemo/.MessageActivity} with LAUNCH_MULTIPLE from uid 10404 (sr=38170689) (BAL_BLOCK) result code=0
```


## StrictMode logging
By upgrading my Samsung to Android 16 (Beta) and following [this documentation](https://developer.android.com/guide/components/activities/background-starts#strict-mode), I enabled more debug logging.

Sometimes we can still launch Activities in PIP mode, but only for a given period. This is due to the app being in a "grace period" state.
```
StrictMode policy violation: android.os.strictmode.BackgroundActivityLaunchViolation: Activity start is only allowed by grace period. This may stop working in the future. intent: Intent { xflg=0x4 cmp=com.slecornu.pipdemo/.MessageActivity } (Ask Gemini)
    at android.os.StrictMode.onBackgroundActivityLaunchAborted(StrictMode.java:2481)
    at android.os.StrictMode$BackgroundActivityLaunchCallback.onBackgroundActivityLaunchAborted(StrictMode.java:2228)
    at android.app.IBackgroundActivityLaunchCallback$Stub.onTransact(IBackgroundActivityLaunchCallback.java:92)
    at android.os.Binder.execTransactInternal(Binder.java:1462)
    at android.os.Binder.execTransact(Binder.java:1401)
```

When the Activity is blocked from launching, we get the following message.
```
StrictMode policy violation: android.os.strictmode.BackgroundActivityLaunchViolation: Activity start blocked. The intent would have started ComponentInfo{com.slecornu.pipdemo/com.slecornu.pipdemo.MessageActivity} (Ask Gemini)
    at android.os.StrictMode.onBackgroundActivityLaunchAborted(StrictMode.java:2481)
    at android.os.StrictMode$BackgroundActivityLaunchCallback.onBackgroundActivityLaunchAborted(StrictMode.java:2228)
    at android.app.IBackgroundActivityLaunchCallback$Stub.onTransact(IBackgroundActivityLaunchCallback.java:92)
    at android.os.Binder.execTransactInternal(Binder.java:1462)
    at android.os.Binder.execTransact(Binder.java:1401)
```


## Reproduced on the following environments
- S24 OneUI 8.0 Android 16 (Beta)
- S24 OneUI 7 Android 15
- S22 OneUI 7 Android 15
- Pixel 8 Pro Android 16
- Pixel 9 Pro XL Android 16

I can't reproduce on my Pixel 6.

## Issue tracker
[Android15 > PIP BAL(Background Activity Launch) issue](https://issuetracker.google.com/issues/444980490)

## Relevant documentation
https://developer.android.com/guide/components/activities/background-starts
