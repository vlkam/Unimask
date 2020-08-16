package com.colvir.unimask.unimask

import android.text.Editable

open class SlotPosition(val slot : PlaceholderSlot){

    var hint : Char? = null
    var value : Char? = null

    var hintColor : Int? = null
    var valueColor : Int? = null

    fun isEmpty() : Boolean {
        return value == null
    }


    fun getColorForHint() : Int {
        return if(hintColor != null){
            hintColor!!
        } else if(slot.hintColor != null){
            slot.hintColor!!
        } else if(slot.slotController.hintColor != null){
            slot.slotController.hintColor
        } else {
            Unimask.DEFAULT_HINT_COLOR
        }
    }

    fun getColorForValue() : Int {
        return if(valueColor != null){
            valueColor!!
        } else if(slot.valueColor != null){
            slot.valueColor!!
        } else if(slot.slotController.valueColor != null){
            slot.slotController.valueColor
        } else {
            Unimask.DEFAULT_VALUE_COLOR
        }
    }

    fun getValue(editable : Editable)  {
        val includeHint = slot.includeHint ?: slot.slotController.includeHint
        if(value != null){
            append(value!!, editable, getColorForValue())
        } else if(hint != null && includeHint){
            append(hint!!, editable, getColorForHint())
        } else {
            editable.append(' ')
        }
    }

    fun setupValue(c: Char) {
        value = c
    }
}