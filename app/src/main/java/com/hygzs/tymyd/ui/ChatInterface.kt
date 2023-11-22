package com.hygzs.tymyd.ui

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.transition.Explode
import android.util.Log
import android.util.Pair
import android.view.Gravity
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.hss01248.dialog.StyledDialog
import com.hss01248.dialog.interfaces.MyDialogListener
import com.hygzs.tymyd.BaseActivity
import com.hygzs.tymyd.Data
import com.hygzs.tymyd.R
import com.hygzs.tymyd.util.ReadWriteData
import com.hygzs.tymyd.util.SQLite3Helper

class ChatInterface : BaseActivity() {
    private lateinit var chatInterface: RecyclerView
    private lateinit var chatRecords: ArrayList<Map<String, Any>>
    private lateinit var readWriteData: ReadWriteData
    private lateinit var toolbar: RelativeLayout
    private lateinit var relativeLayout: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        window.run {
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            enterTransition = Explode() //进入动画
            exitTransition = Explode() //退出动画
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_interface)
        init()
    }

    private fun init() {
        toolbar = findViewById(R.id.toolbar)
        relativeLayout = findViewById(R.id.relativeLayout)
        readWriteData = ReadWriteData(this, 11, Data.app)
        chatInterface = findViewById(R.id.chat_interface)
        if (SPUtils.getInstance("notes").getString(Data.TargetFriend)
                .isNotEmpty() && SPUtils.getInstance("notes")
                .getString(Data.TargetFriend) != "null" && SPUtils.getInstance("notes")
                .getString(Data.TargetFriend) != getString(R.string.notes_tip)
        ) {
            findViewById<TextView>(R.id.name).text =
                SPUtils.getInstance("notes").getString(Data.TargetFriend)
        } else {
            findViewById<TextView>(R.id.name).text = Data.TargetFriend
        }

        findViewById<ImageView>(R.id.imageView2).setOnClickListener {
            finishAfterTransition()
        }
        findViewById<ImageView>(R.id.imageView).setOnClickListener {
            ToastUtils.make().setTextColor(Color.WHITE).setBgColor(Color.parseColor("#fd79a8"))
                .setGravity(Gravity.CENTER, 0, 0).setTextSize(18)
                .show("这朵发发一定是有用的！但是现在没有！")
        }
        chatRecords = SQLite3Helper(
            this, PathUtils.getInternalAppFilesPath() + "/C${Data.TargetAccount}.db"
        ).getTableData(Data.TargetFriend)
        Log.e("小叶子 : ", chatRecords.toString())
        //addtype不能直接传入chatRecords，因为chatRecords是List<Map<String, Any>>类型，而addType需要的是List<Any>类型
        //所以需要转换一下

        //[{SrcId=2328362106862204481, DstId=2306125583702177064, SrcName=, DstChatGroupId=0, Content=好友“此别即是永别了”已更名为<color=#6da9e0>初丶秋</color>，快来找我玩吧！, Timestamp=1692543306, Channel=10, Addition=, MsgRid=7269418146342699010, Client_Tag=0}, {SrcId=2306125583702177064, DstId=0, SrcName=, DstChatGroupId=2306125583702177064, Content=2023-09-10 15:11, Timestamp=1694329901, Channel=10, Addition=, MsgRid=0, Client_Tag=0}, {SrcId=2328362106862204481, DstId=2306125583702177064, SrcName=, DstChatGroupId=0, Content=好友“初丶秋”已更名为<color=#6da9e0>手揣兜藏奶糖</color>，快来找我玩吧！, Timestamp=1694329902, Channel=10, Addition=, MsgRid=7277091517733863426, Client_Tag=0}, {SrcId=2328362106862204481, DstId=2306125583702177064, SrcName=, DstChatGroupId=0, Content=好友“手揣兜藏奶糖”已更名为<color=#6da9e0>秋风起满目朱红</color>，快来找我玩吧！, Timestamp=1695491337, Channel=10, Addition=, MsgRid=7282079843075227650, Client_Tag=0}, {SrcId=2328362106862204481, DstId=2306125583702177064, SrcName=, DstChatGroupId=0, Content=好友“秋风起满目朱红”已更名为<color=#6da9e0>久居故梦未曾出</color>，快来找我玩吧！, Timestamp=1697291013, Channel=10, Addition=, MsgRid=7289809392638623746, Client_Tag=0}]
        //如果SrcId和TargetAccount相同就是自己发的消息，否则就是对方发的消息，如果MsgRid为0就是系统消息，否则就是聊天消息
        chatInterface.linear().setup {
            addType<Map<String, Any>> {
                val map = models?.get(it) as Map<String, Any>
                if (map["SrcId"] == Data.TargetAccount && map["MsgRid"].toString() != "0" && !map["Content"].toString()
                        .contains("已更名为<color=#6da9e0>")
                ) {
                    //自己发的消息
                    R.layout.right_chat
                } else if (map["MsgRid"].toString() != "0" && !map["Content"].toString()
                        .contains("已更名为<color=#6da9e0>")
                ) {
                    //对方发的消息
                    R.layout.left_chat
                } else {
                    //时间信息
                    R.layout.time_log
                }
            }

            onBind {
                val map = models?.get(modelPosition) as Map<String, Any>
                when (itemViewType) {
                    R.layout.right_chat -> {
                        //自己发的消息
                        val srcName = map["SrcName"].toString()
                        val content = map["Content"].toString()
                        val timestamp = map["Timestamp"].toString()
                        val name = findView<TextView>(R.id.name)
                        val msg = findView<TextView>(R.id.msg)
                        val textView8 = findView<TextView>(R.id.textView8)
                        val relativeLayout = findView<RelativeLayout>(R.id.relativeLayout)
                        val headSculpture = findView<ImageView>(R.id.head_sculpture)
                        name.text = srcName
                        msg.text = content
                        textView8.text = timestampToTime(timestamp)
                        msg.setOnLongClickListener {
                            StyledDialog.buildIosAlert("小叶子的提示",
                                "是否要删除这个这条消息？",
                                object : MyDialogListener() {
                                    override fun onFirst() {
                                        SQLite3Helper(
                                            this@ChatInterface,
                                            PathUtils.getInternalAppFilesPath() + "/C${Data.TargetAccount}.db"
                                        ).deleteTableData(Data.TargetFriend, timestamp.toLong())
                                        updateDbFile(
                                            PathUtils.getInternalAppFilesPath() + "/C${Data.TargetAccount}.db"
                                        )

                                        chatRecords.remove(map)
                                        ToastUtils.showLong("删除成功~")
                                        notifyItemRemoved(modelPosition)
                                    }

                                    override fun onSecond() {
//                                    notifyItemChanged(position)
                                    }
                                }).setBtnText("确定", "取消").show()
                            true
                        }
                        msg.setOnClickListener {
                            Data.msg = content
                            Data.timestamp = timestamp
                            val intent = Intent(this@ChatInterface, ChatEditing::class.java)
                            startActivity(
                                intent, ActivityOptions.makeSceneTransitionAnimation(
                                    this@ChatInterface, Pair(toolbar, "msg1")
                                ).toBundle()
                            )
                        }
                    }

                    R.layout.left_chat -> {
                        //对方发的消息
                        val srcName = map["SrcName"].toString()
                        val content = map["Content"].toString()
                        val timestamp = map["Timestamp"].toString()
                        val name = findView<TextView>(R.id.name)
                        val msg = findView<TextView>(R.id.msg)
                        val textView8 = findView<TextView>(R.id.textView8)
                        val relativeLayout = findView<RelativeLayout>(R.id.relativeLayout)
                        val headSculpture = findView<ImageView>(R.id.head_sculpture)
                        name.text = srcName
                        msg.text = content
                        textView8.text = timestampToTime(timestamp)
                        msg.setOnLongClickListener {
                            StyledDialog.buildIosAlert("小叶子的提示",
                                "是否要删除这个这条消息？",
                                object : MyDialogListener() {
                                    override fun onFirst() {
                                        SQLite3Helper(
                                            this@ChatInterface,
                                            PathUtils.getInternalAppFilesPath() + "/C${Data.TargetAccount}.db"
                                        ).deleteTableData(Data.TargetFriend, timestamp.toLong())
                                        updateDbFile(
                                            PathUtils.getInternalAppFilesPath() + "/C${Data.TargetAccount}.db"
                                        )

                                        chatRecords.remove(map)
                                        ToastUtils.showLong("删除成功~")
                                        notifyItemRemoved(modelPosition)
                                    }

                                    override fun onSecond() {
//                                    notifyItemChanged(position)
                                    }
                                }).setBtnText("确定", "取消").show()
                            true
                        }
                        msg.setOnClickListener {
                            Data.msg = content
                            Data.timestamp = timestamp
                            val intent = Intent(this@ChatInterface, ChatEditing::class.java)
                            startActivity(
                                intent, ActivityOptions.makeSceneTransitionAnimation(
                                    this@ChatInterface, Pair(toolbar, "msg1")
                                ).toBundle()
                            )
                        }
                    }

                    else -> {
                        //时间信息
                        val timestamp = map["Timestamp"].toString()
                        val content = map["Content"].toString()
                        val timeLog = findView<TextView>(R.id.time_log)
                        val timeLogBg = findView<RelativeLayout>(R.id.time_log_bg)
                        timeLog.text = content
                        timeLogBg.setOnLongClickListener {
                            //弹出删除消息的对话框
                            StyledDialog.buildIosAlert("小叶子的提示",
                                "是否要删除这个这条消息？",
                                object : MyDialogListener() {
                                    override fun onFirst() {
                                        SQLite3Helper(
                                            this@ChatInterface,
                                            PathUtils.getInternalAppFilesPath() + "/C${Data.TargetAccount}.db"
                                        ).deleteTableData(Data.TargetFriend, timestamp.toLong())
                                        updateDbFile(
                                            PathUtils.getInternalAppFilesPath() + "/C${Data.TargetAccount}.db"
                                        )

                                        chatRecords.remove(map)
                                        ToastUtils.showLong("删除成功~")
                                        notifyItemRemoved(modelPosition)
                                    }

                                    override fun onSecond() {
//                                    notifyItemChanged(position)
                                    }
                                }).setBtnText("确定", "取消").show()
                            true
                        }
                        timeLogBg.setOnClickListener {
                            Data.msg = content
                            Data.timestamp = timestamp
                            val intent = Intent(this@ChatInterface, ChatEditing::class.java)
                            startActivity(
                                intent, ActivityOptions.makeSceneTransitionAnimation(
                                    this@ChatInterface, Pair(toolbar, "msg1")
                                ).toBundle()
                            )
                        }
                    }
                }
            }
        }.models = chatRecords
        chatInterface.scrollToPosition(chatRecords.size - 1)
    }

    //把string类型的10位时间戳转换成yyyy-MM-dd HH:mm格式
    private fun timestampToTime(timestamp: String): String {
        val time = timestamp.toLong()
        val date = java.util.Date(time * 1000)
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")
        return sdf.format(date)
    }

    private fun updateDbFile(dbPath: String) {
        val dbBytes = FileIOUtils.readFile2BytesByStream(dbPath)
        val fileName = dbPath.split("/").last().replace("db", "txt")
        readWriteData.write(Data.PathName, fileName, null, dbBytes)
    }

    //界面重新可见时
    override fun onResume() {
        super.onResume()
        updateDbFile(PathUtils.getInternalAppFilesPath() + "/C${Data.TargetAccount}.db")
        init()
    }
}