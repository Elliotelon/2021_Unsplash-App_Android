package com.elliot.unsplash.utils

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import java.text.SimpleDateFormat
import java.util.*

//날짜 포맷
fun Date.toSimpleString() : String {
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

//EditText 텍스트 변경을 flow로 받기
@ExperimentalCoroutinesApi
fun EditText.textChangesToFlow(): Flow<CharSequence?>{
    //flow 콜백 받기
    return callbackFlow<CharSequence> {
        val listener = object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Unit
            }

            override fun afterTextChanged(s: Editable?) {
                Unit
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(Constants.TAG, "onTextChanged() / textChangesToFlow() 에 달려있는 와쳐 / text : $text")
                //값 내보내기

                if (text != null) {
                    offer(text)
                }
            }
        }
        //위에서 설정한 리스너 달아주기
        addTextChangedListener(listener)

        //콜백이 사라질때 실행됨
        awaitClose {
            Log.d(Constants.TAG, "textChangesToFlow() awaitClose 실행")
            removeTextChangedListener(listener)

        }

    }.onStart {
        Log.d(Constants.TAG, "textChangesToFlow() / onStart 발동")
        // Rx에서 OnNext와 동일
        // emit으로 이벤트를 전달
        emit(text)

    }
}