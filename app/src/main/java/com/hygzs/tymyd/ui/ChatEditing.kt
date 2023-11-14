package com.hygzs.tymyd.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.view.ViewCompat
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ToastUtils
import com.hygzs.tymyd.BaseActivity
import com.hygzs.tymyd.Data
import com.hygzs.tymyd.R
import com.hygzs.tymyd.util.SQLite3Helper

class ChatEditing : BaseActivity() {
    private lateinit var editTextText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_editing)
        ViewCompat.setTransitionName(findViewById<RelativeLayout>(R.id.toolbar), "msg1")
        init()
    }

    private fun init() {
        editTextText = findViewById(R.id.editTextText)
        editTextText.setText(Data.msg)
        findViewById<ImageView>(R.id.imageView2).setOnClickListener {
            finishAfterTransition()
        }
        findViewById<ImageView>(R.id.imageView).setOnClickListener {
            //如果有软键盘就先隐藏
            if(KeyboardUtils.isSoftInputVisible(this)){
                KeyboardUtils.hideSoftInput(this)
            }

            var msg = editTextText.text.toString()
            if (!msg.contains("<") && !msg.contains("/>")) {
                if (msg.last() != '.') {
                    msg += "."
                }
            }
            SQLite3Helper(
                this@ChatEditing,
                PathUtils.getInternalAppFilesPath() + "/C${Data.TargetAccount}.db"
            ).updateTableData(
                Data.TargetFriend,
                Data.timestamp,
                msg
            )
            finishAfterTransition()
        }
    }
}