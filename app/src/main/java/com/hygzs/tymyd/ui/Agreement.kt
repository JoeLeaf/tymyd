package com.hygzs.tymyd.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.hss01248.dialog.StyledDialog
import com.hss01248.dialog.interfaces.MyDialogListener
import com.hygzs.tymyd.BaseActivity
import com.hygzs.tymyd.Data
import com.hygzs.tymyd.R
import com.hygzs.tymyd.util.ReadWriteData

class Agreement : BaseActivity() {
    private lateinit var textView3: TextView
    private lateinit var readWriteData: ReadWriteData
    override fun onCreate(savedInstanceState: Bundle?) {
//        window.run {
//            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
//            enterTransition = Explode() //进入动画
//            exitTransition = Explode() //退出动画
//        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agreement)
        val face = Typeface.createFromAsset(assets, "fonts/HongLeiXingShuJianTi-2.otf")
        findViewById<TextView>(R.id.textView4).typeface = face
        textView3 = findViewById(R.id.textView3)
        textView3.typeface = face
        readWriteData = ReadWriteData(this, 11, Data.app)
        textView3.setOnClickListener {
            if (readWriteData.isPermissions) {
                ToastUtils.showLong("欢迎使用")
                Intent(this, ChatRecords::class.java).apply {
                    SPUtils.getInstance("config").put("isAgree", true)
                    startActivity(this)
                    finish()
                }
            } else {
                ToastUtils.showLong("你没有给权限哦~")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    readWriteData.requestPermission()
                }
            }


        }
        //判断是否拥有储存权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) { //请求权限
            StyledDialog.buildIosAlert("授权提示",
                "io！我需要内存读写权限！用来修改记录！",
                object : MyDialogListener() {
                    override fun onFirst() {
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ), 1
                        )
                    }

                    override fun onSecond() {
                        finish()
                    }
                }
            ).setBtnText("给予权限", "退出软件").show()
        }
        if (readWriteData.isPermissions) {
            Log.e("小叶子 : ", "onCreate: 有权限")
        } else {
            Log.e("小叶子 : ", "onCreate: 没有权限")
            StyledDialog.buildIosAlert("授权提示",
                "我要Android/data/天涯BUG刀读取权限才能读取！快给我！\n\n点击给予权限后 请点击底部的\"使用此文件夹\"或\"允许使用DATA\"给予权限",
                object : MyDialogListener() {
                    override fun onFirst() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            readWriteData.requestPermission()
                        }
                    }

                    override fun onSecond() {
                        finish()
                    }
                }
            ).setBtnText("给予权限", "退出软件").show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (permissions[0] == Manifest.permission.READ_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //用户同意使用write
            } else {
                ToastUtils.showLong("请给予储存权限")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        readWriteData.savePermissions(requestCode, resultCode, data) //保存权限
        if (requestCode == 65) {
            if (readWriteData.isAllFilePermission) {
                ToastUtils.showLong("权限申请成功")
            } else {
                ToastUtils.showLong("权限申请失败")
            }
        }
    }

}

