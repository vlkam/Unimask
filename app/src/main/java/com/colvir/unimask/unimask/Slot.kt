package com.colvir.unimask.unimask

import android.text.Editable
import android.text.Spannable
import android.text.style.ForegroundColorSpan

class SlotResult(
    val status : Slot.SlotResultStatuses,
    val cursorOffset : Int,
    val poppedChar : Char?
)

enum class SlotType { MASK, PLACEHOLDER}

abstract class Slot(val slotController : SlotsController, val type : SlotType){

    var indexOfSlot : Int = 0

    enum class SlotResultStatuses { UNKNOWN, ACCEPTED, REFUSED, NOT_ENOUGH_SPACE }

    abstract fun getContent(editable : Editable)
    abstract val length : Int
    abstract fun firstEmptyPosition() : Int

    open fun insert(c : Char, localPosition : Int, isHint : Boolean) : SlotResult {
        return SlotResult(status = SlotResultStatuses.UNKNOWN, cursorOffset = 0, poppedChar = null)
    }

}

fun append(c : Char, editable : Editable, color : Int) = append(c.toString(), editable, color)

fun append(string : String, editable : Editable, color : Int){
    val start = editable.length
    editable.append(string)
    editable.setSpan(ForegroundColorSpan(color), start, start + string.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}









