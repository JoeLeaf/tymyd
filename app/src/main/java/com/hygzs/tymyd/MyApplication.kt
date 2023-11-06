package com.hygzs.tymyd

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.hss01248.dialog.ActivityStackManager
import com.hss01248.dialog.StyledDialog


/*
* Created by xyz on 2023/10/30
* 所以你瞅啥？
*/

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        StyledDialog.init(this)
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                ActivityStackManager.getInstance().addActivity(activity)
            }
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {
                ActivityStackManager.getInstance().removeActivity(activity)
            }
        })
    }
}