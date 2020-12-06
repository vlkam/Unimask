package com.colvir.unimask

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityEvent
import androidx.appcompat.widget.AppCompatEditText

open class UnimaskTextWatcher() : TextWatcher {

    var slotController : SlotsController? = null

    private var internalChange : Boolean = false
    private var newSubstr : String? = null
    private var insertPosition : Int = 0

    // Delete
    protected var isDelete : Boolean = false
    protected var deleteFrom : Int = 0
    protected var deleteTo : Int = 0

    protected var editText : AppCompatEditText? = null

    var onTextChangedAction : ((str: CharSequence?, rawText: String?) -> Unit)? = null

    companion object {
        const val TAG = "UTW"
    }

    fun initialize(editText : AppCompatEditText){

        this.editText = editText

        if(slotController != null){
            val ed = Editable.Factory.getInstance().newEditable("")
            slotController!!.getContent(ed)
            try {
                internalChange = true
                editText.editableText.clear()
                editText.filters = emptyArray()
                editText.editableText.filters = emptyArray()
                editText.editableText.append(ed)
                val poz = slotController!!.findFirstEmptyPosition()
                if(poz > -1){
                    editText.setSelection(poz)
                }

                // It doesn't allow set the cursor on a mask
                editText.setAccessibilityDelegate(object : View.AccessibilityDelegate() {

                    fun checkPosition(eventType: Int){
                        if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
                            val delta = editText.selectionEnd - editText.selectionStart
                            if (delta == 0) {
                                val res = slotController?.findNextPosition(editText.selectionStart)
                                if (res?.slot?.type == SlotType.MASK) {
                                    val firstEmptyPosition = slotController?.findFirstEmptyPosition() ?: -1
                                    if (firstEmptyPosition != -1) {
                                        try {
                                            internalChange = true
                                            // sometimes it the mask was changed from code here occurs an exception
                                            if(editText.text?.length ?: 0 > firstEmptyPosition){
                                                editText.setSelection(firstEmptyPosition)
                                            }
                                        } finally {
                                            internalChange = false
                                        }
                                    }
                                }
                            }
                            Log.i("", "")
                        }
                    }

                    override fun sendAccessibilityEventUnchecked(host: View?, event: AccessibilityEvent?) {
                        super.sendAccessibilityEventUnchecked(host, event)
                        if(internalChange){
                            return
                        }

                        if (event != null){
                            checkPosition(event.eventType)
                        }
                    }

                    override fun sendAccessibilityEvent(host: View?, eventType: Int) {
                        super.sendAccessibilityEvent(host, eventType)
                        if(internalChange){
                            return
                        }
                        checkPosition(eventType)
                    }
                })

            } finally {
                internalChange = false
            }
            editText.setOnFocusChangeListener { v, hasFocus ->
                //Log.i(TAG,"on focus")
                if(hasFocus){
                    val poz = slotController?.findFirstEmptyPosition() ?: -1
                    //Log.i(TAG,"on focus position $poz")
                    if(poz > -1){
                        editText.setSelection(poz)
                        android.os.Handler().postDelayed({
                            editText.setSelection(poz)
                        },20)
                    }
                }
            }

        }

    }

    override fun afterTextChanged(s: Editable?) {
        if(internalChange){
            return
        }

        s?.filters = emptyArray<InputFilter>()

        if(slotController != null){
            try{
                internalChange = true
                if(isDelete){
                    slotController?.remove(deleteFrom,deleteTo)
                } else {
                    if(newSubstr != null && s != null){
                        slotController?.insert(newSubstr!!, insertPosition)
                    }
                }

                if(s != null){
                    s.clear()
                    if(slotController != null){
                        slotController!!.getContent(s)
                        editText?.setSelection(slotController!!.currentCursorPosition)
                    }
                }
            } finally {
                internalChange = false
            }
        }

        onTextChangedAction?.invoke(s?.toString(), slotController?.content(0))

        Log.i("UTW"," ${s?.toString()} selection ${editText?.selectionStart}")
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        if(internalChange){
            return
        }

        if(slotController != null){
            newSubstr = null
            insertPosition = start
            isDelete = after == 0
            if(isDelete){
                deleteFrom = start
                deleteTo = start + count
            }
        }

        Log.i("UTW"," ${s?.toString()} selection ${editText?.selectionStart} start $start  count $count  after $after")
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if(internalChange){
            return
        }

        if(slotController != null){
            newSubstr = s?.substring(start, start + count)
        }

        Log.i("UTW"," ${s?.toString()}  selection ${editText?.selectionStart} start $start  before $before  count $count")
    }

}