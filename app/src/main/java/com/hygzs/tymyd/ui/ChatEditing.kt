package com.hygzs.tymyd.ui

import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
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
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import kotlin.concurrent.thread
import kotlin.math.log


class ChatEditing : BaseActivity(), ColorPickerDialogListener {
    private lateinit var editTextText: EditText
    private lateinit var imageView4: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_editing)
        ViewCompat.setTransitionName(findViewById<RelativeLayout>(R.id.toolbar), "msg1")
        init()
    }

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


        //imageView4 不影响点击实现随手指拖动
        imageView4.setOnTouchListener { v, event ->
            v.x = event.rawX - v.width / 2
            v.y = event.rawY - v.height / 2
            false
        }
        imageView4.setOnClickListener {
            StyledDialog.buildBottomSheetLv("这都被你发现了！", listOf(
                BottomSheetBean(R.mipmap.duilian, "颜色工具"),
                BottomSheetBean(R.mipmap.duilian, "查看初丶秋库存"),
//                BottomSheetBean(R.mipmap.duilian, "嘿！"),
//                BottomSheetBean(R.mipmap.duilian, "哈！")
            ), "艹！走！忽略ጿ ኈ ቼ ዽ ጿ", object : MyItemDialogListener() {
                override fun onItemClick(text: CharSequence?, position: Int) {
                    when (position) {
                        0 -> {
                            ColorPickerDialog.newBuilder()
                                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                                .setAllowPresets(false)
                                .setDialogId(0).setShowAlphaSlider(false).show(this@ChatEditing)
                        }

                        1 -> {
                            StyledDialog.buildBottomItemDialog(listOf("111","22"),"sss",object :MyItemDialogListener(){
                                override fun onItemClick(text: CharSequence?, position: Int) {
                                    ToastUtils.showShort(text)
                                }
                            }).show()
                        }

                        2 -> {
                            ToastUtils.showShort("嘿！")
                        }

                        3 -> {
                            ToastUtils.showShort("哈！")
                        }
                    }
                }
            }).show()
        }


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

    override fun onDialogDismissed(dialogId: Int) {
        Log.e("ColorPicker", "onDialogDismissed: $dialogId")
    }
}
