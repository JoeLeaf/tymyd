package com.hygzs.tymyd.crypto

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.textfield.TextInputLayout
import com.hygzs.tymyd.R

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CryptoLingShiFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CryptoLingShiFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var encryptionInputLayout: TextInputLayout
    private lateinit var decryptionInputLayout: TextInputLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_crypto_ling_shi, container, false)
        val view = inflater.inflate(R.layout.fragment_crypto_ling_shi, container, false)
        encryptionInputLayout = view.findViewById(R.id.encryptionInputLayout)
        decryptionInputLayout = view.findViewById(R.id.decryptionInputLayout)
        //监听输入框内容变化
        encryptionInputLayout.editText?.addTextChangedListener {
            //如果焦点在加密输入框的情况下
            if (encryptionInputLayout.editText?.hasFocus() == true) {
                var str = it.toString()
                try {
                    var test = str.split("{>[")[0]
                    val test2 = ">[" + str.split("{>[")[1]
                    test += "{"
                    test = test.replace("<a href=LING_YU_SHARE:", "")
                    test = test.replace("{", "}")
                        .replace("y", "{")
                        .replace(" ", "#")
                        .replace("8", "|")
                        .replace("7", "9")
                        .replace("6", "8")
                        .replace("5", "7")
                        .replace("4", "6")
                        .replace("3", "5")
                        .replace("2", "4")
                        .replace("1", "3")
                        .replace("0", "2")
                        .replace("/", "1")
                        .replace("a", "灵力值")
                        .replace("*", ",")
                        .replace(".", "0")
                        .replace("[", "]")
                        .replace("Y", "[")
                        .replace("i", "词条")
                        .replace("o", "左")
                        .replace("n", "右")
                        .replace("_", "种类")
                        .replace("`", "品质")
                    decryptionInputLayout.editText?.setText("<a href=LING_YU_SHARE:$test$test2")
                } catch (e: Exception) {
                    if (str.isNotEmpty()) {
                        ToastUtils.showShort("输入内容错误哦~")
                        decryptionInputLayout.editText?.setText("")
                    }
                }
            }
        }
        decryptionInputLayout.editText?.addTextChangedListener {
            //如果焦点在解密输入框的情况下
            if (decryptionInputLayout.editText?.hasFocus() == true) {
                var str = it.toString()
                try {
                    var test = str.split("}>[")[0]
                    val test2 = ">[" + str.split("}>[")[1]
                    test += "}"
                    test = test.replace("<a href=LING_YU_SHARE:", "")
                    test = test.replace("{", "y")
                        .replace("}", "{")
                        .replace("0", ".")
                        .replace("1", "/")
                        .replace("2", "0")
                        .replace("3", "1")
                        .replace("4", "2")
                        .replace("5", "3")
                        .replace("6", "4")
                        .replace("7", "5")
                        .replace("8", "6")
                        .replace("9", "7")
                        .replace("#", " ")
                        .replace("|", "8")
                        .replace("灵力值", "a")
                        .replace(",", "*")
                        .replace("[", "Y")
                        .replace("]", "[")
                        .replace("词条", "i")
                        .replace("左", "o")
                        .replace("右", "n")
                        .replace("种类", "_")
                        .replace("品质", "`")
                    encryptionInputLayout.editText?.setText("<a href=LING_YU_SHARE:$test$test2")
                } catch (e: Exception) {
                    if (str.isNotEmpty()) {
                        ToastUtils.showShort("输入内容错误哦~")
                        encryptionInputLayout.editText?.setText("")
                    }
                }
            }
        }

        view.findViewById<TextView>(R.id.copyEncryption).setOnClickListener {
            ClipboardUtils.copyText(encryptionInputLayout.editText?.text.toString())
            ToastUtils.showShort("已复制到剪贴板！")
        }
        view.findViewById<TextView>(R.id.copyDecryption).setOnClickListener {
            ClipboardUtils.copyText(decryptionInputLayout.editText?.text.toString())
            ToastUtils.showShort("已复制到剪贴板！")
        }

        //实现点击键盘外隐藏键盘
        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                KeyboardUtils.hideSoftInput(activity)
            }
            false
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CryptoLingShiFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CryptoLingShiFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}