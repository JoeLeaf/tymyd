package com.hygzs.tymyd.ui

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.view.ViewCompat
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ToastUtils
import com.hss01248.dialog.StyledDialog
import com.hss01248.dialog.bottomsheet.BottomSheetBean
import com.hss01248.dialog.interfaces.MyItemDialogListener
import com.hygzs.tymyd.BaseActivity
import com.hygzs.tymyd.Data
import com.hygzs.tymyd.R
import com.hygzs.tymyd.util.SQLite3Helper
import com.hygzs.tymyd.util.Xyz
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import kotlin.concurrent.thread
import kotlin.math.abs


class ChatEditing : BaseActivity(), ColorPickerDialogListener {
    private lateinit var editTextText: EditText
    private lateinit var imageView4: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_editing)
        ViewCompat.setTransitionName(findViewById<RelativeLayout>(R.id.toolbar), "msg1")
        init()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        imageView4 = findViewById(R.id.imageView4)
        editTextText = findViewById(R.id.editTextText)
        editTextText.setText(Data.msg)
        //做一个抖动动画提示用户这是可以点的imageView4
        thread {
            Thread.sleep(1000)
            val shake: Animation = TranslateAnimation(0f, 10f, 0f, 10f)
            shake.duration = 700
            shake.interpolator = CycleInterpolator(5f)
            imageView4.startAnimation(shake)
        }


        imageView4.setOnTouchListener(object : View.OnTouchListener {
            private var lastX = 0f
            private var lastY = 0f
            private var isClick = true

            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                when (p1?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastX = p1.rawX
                        lastY = p1.rawY
                        isClick = true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        p0?.x = p1.rawX - p0?.width!! / 2
                        p0.y = p1.rawY - p0.height / 2
                        if (abs(p1.rawX - lastX) > 10 || abs(p1.rawY - lastY) > 10) {
                            isClick = false
                        }
                    }

                    MotionEvent.ACTION_UP -> {
                        if (abs(p1.rawX - lastX) < 10 && abs(p1.rawY - lastY) < 10) {
                            isClick = false
                            StyledDialog.buildBottomSheetLv("这都被你发现了！", listOf(
                                BottomSheetBean(R.mipmap.duilian, "颜色工具"),
                                BottomSheetBean(R.mipmap.duilian, "数据加解密")
                            ), "艹！走！忽略ጿ ኈ ቼ ዽ ጿ", object : MyItemDialogListener() {
                                override fun onItemClick(text: CharSequence?, position: Int) {
                                    when (position) {
                                        0 -> {
                                            ColorPickerDialog.newBuilder()
                                                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                                                .setAllowPresets(false)
                                                .setDialogId(0).setShowAlphaSlider(false)
                                                .show(this@ChatEditing)
                                        }

                                        1 -> {
                                            val i = Intent(this@ChatEditing, Crypto::class.java)
                                            startActivity(
                                                i,
                                                ActivityOptions.makeSceneTransitionAnimation(
                                                    this@ChatEditing
                                                )
                                                    .toBundle()
                                            )
                                        }

                                        2 -> {
                                            ToastUtils.showShort("暂无此功能！")
                                        }
                                    }
                                }
                            }).show()
                        }
                    }

                }
                return true
            }
        })

        findViewById<ImageView>(R.id.imageView2).setOnClickListener {
            if (KeyboardUtils.isSoftInputVisible(this)) {
                KeyboardUtils.hideSoftInput(this)
            }
            finishAfterTransition()
        }
        findViewById<ImageView>(R.id.imageView).setOnClickListener {
            //如果有软键盘就先隐藏
            if (KeyboardUtils.isSoftInputVisible(this)) {
                KeyboardUtils.hideSoftInput(this)
            }

            var msg = editTextText.text.toString()
            if (!msg.contains("<") && !msg.contains("/>")) {
                if (msg.last() != '.') {
                    msg += "."
                }
            }
            SQLite3Helper(
                this@ChatEditing, PathUtils.getInternalAppFilesPath() + "/C${Data.TargetAccount}.db"
            ).updateTableData(
                Data.TargetFriend, Data.timestamp, msg
            )
            finishAfterTransition()
            ToastUtils.showShort("修改成功！如果想加载最新聊天数据，请返回到账号列表再进入聊天界面！")
        }
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        //删除前2位A通道
        val colorText = Integer.toHexString(color).substring(2)
        Log.e("小叶子 : ", "$colorText--$color")
        ClipboardUtils.copyText("#$colorText")
        ToastUtils.showShort("已复制颜色值：#$colorText")
    }

    //实现点击键盘外隐藏键盘
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (Xyz.isShouldHideKeyboard(v, ev)) {
                KeyboardUtils.hideSoftInput(this)
            }
        }
        return super.dispatchTouchEvent(ev)
    }
    override fun onDialogDismissed(dialogId: Int) {
        Log.e("ColorPicker", "onDialogDismissed: $dialogId")
    }
}
