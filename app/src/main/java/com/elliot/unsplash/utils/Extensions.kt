package com.elliot.unsplash.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.*

//날짜 포맷
fun Date.toString() : String {
    val format = SimpleDateFormat("HH:mm:ss")
    return format.format(this)
}

// 문자열이 Json 형태인지
fun String?.isJsonObject() : Boolean {
    return this?.startsWith("{") == true && this.endsWith("}")
}

//문자열이 Json 배열인지
fun String?.isJsonArray() : Boolean{
   return this?.startsWith("[") == true && this.endsWith("]")

}

//editText에 대한 익스텐션
fun EditText.onMyTextChanged(completion : (Editable?) -> Unit){
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            completion(editable)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

    })
}