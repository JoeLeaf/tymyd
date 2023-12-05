package com.hygzs.tymyd.crypto

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.textfield.TextInputLayout
import com.hygzs.tymyd.R
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CryptoPropFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CryptoPropFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var webView: WebView
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
        val view = inflater.inflate(R.layout.fragment_crypto_prop, container, false)
        webView = view.findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
//        webView.loadUrl("file:///android_asset/pb/index.html")
        //直接加载会卡顿，所以用post
        webView.post {
            webView.loadUrl("file:///android_asset/pb/index.html")
        }
        webView.webViewClient = object : WebViewClient() {}
        encryptionInputLayout = view.findViewById(R.id.encryptionInputLayout)
        decryptionInputLayout = view.findViewById(R.id.decryptionInputLayout)
        //监听输入框内容变化
        encryptionInputLayout.editText?.addTextChangedListener {
            //如果焦点在加密输入框的情况下
            if (encryptionInputLayout.editText?.hasFocus() == true) {
                var str = it.toString()
                try {
                    val base64Prop = str.split(",")[1].split("/>")[0]
                    webView.post {
                        webView.evaluateJavascript("decodePb(\"$base64Prop\")") { value ->
                            var value2 = value.replace("\\", "")
                            value2 = value2.substring(1, value2.length - 1)
                            decryptionInputLayout.editText?.setText(value2)
                        }
                    }
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
                    val jsonObject = JSONObject(str)
                    val item = jsonObject.getJSONObject("3").getString("3")
                    webView.post {
                        webView.evaluateJavascript("encodePb('$str')") { value ->
                            var value2 = value.substring(1, value.length - 1)
                            value2 = "<item=$item,$value2/>"
                            encryptionInputLayout.editText?.setText(value2)
                        }
                    }
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
         * @return A new instance of fragment CryptoPropFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CryptoPropFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}