package com.colvir.unimask.unimask

import android.text.Editable
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import java.lang.Exception

class SlotResult(
    val status : Slot.SlotResultStatuses,
    var cursorOffset : Int
)

abstract class Slot(val slotController : SlotsController, val type : SlotType){

    var indexOfSlot : Int = 0

    enum class SlotResultStatuses { UNKNOWN, ACCEPTED, REFUSED, NOT_ENOUGH_SPACE }

    abstract fun getContent(editable : Editable)
    abstract val length : Int

    open fun insert(c : Char, localPosition : Int, isHint : Boolean) : SlotResult {
        return SlotResult(status = SlotResultStatuses.UNKNOWN, cursorOffset = 0)
    }

}

open class MaskSlot(
    slotController : SlotsController,
    type : SlotType,
    val mask : String,
    val isTransitionAllowed : Boolean
) : Slot(slotController, type){

    val maskColor : Int? = null

    fun getColorForMask() : Int {
        return if(maskColor != null){
            maskColor
        } else if(slotController.maskColor != null){
            slotController.maskColor
        } else {
            Unimask.DEFAULT_MASK_COLOR
        }
    }

    override fun getContent(editable : Editable) {
        append(mask, editable, getColorForMask())
    }

    override val length: Int
        get() = mask.length

    override fun insert(c: Char, localPosition : Int, isHint: Boolean)  : SlotResult {

        val res = super.insert(c, localPosition, isHint)
        if(res.status != SlotResultStatuses.UNKNOWN){
            return res
        }

        val charOfMask = mask[localPosition]
        return if(charOfMask == c){
            SlotResult(SlotResultStatuses.ACCEPTED, 1)
        } else {
            SlotResult(SlotResultStatuses.REFUSED, 0)
        }
    }

}

fun append(c : Char, editable : Editable, color : Int) = append(c.toString(), editable, color)

fun append(string : String, editable : Editable, color : Int){
    val start = editable.length
    editable.append(string)
    editable.setSpan(ForegroundColorSpan(color),start, start + string.length - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

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

open class PlaceholderSlot(slotController : SlotsController, type : SlotType, val size : Int) : Slot(slotController, type){

    var hintColor : Int? = null
    var valueColor : Int? = null

    var includeHint : Boolean? = null

    val content = mutableListOf<SlotPosition>()

    override val length: Int
        get() = content.size

    init {
        for(i in 0 until size){
            content.add(SlotPosition(this))
        }
    }

    override fun getContent(editable : Editable){
        for (poz in content){
            poz.getValue(editable)
        }
    }


    fun doesItHaveOneModeEmptyPosition(fromPosition : Int) : Boolean{
        for(index in fromPosition until content.size){
            val pos = content[index]
            if(pos.isEmpty()){
                return true
            }
        }
        return false
    }

    override fun insert(c: Char, localPosition : Int, isHint: Boolean)  : SlotResult {

        val res = super.insert(c, localPosition, isHint)
        if(res.status != SlotResultStatuses.UNKNOWN){
            return res
        }

        val pos = content[localPosition]

        // An easy case, position is empty
        if(pos.isEmpty()){
            pos.setupValue(c)
            return SlotResult(SlotResultStatuses.ACCEPTED, 1)
        }


/*
        val charOfMask = mask[localPosition]
        if(charOfMask == c){
            result.status = SlotResultStatuses.ACCEPTED
            result.cursorOffset++
        } else {
            result.status = SlotResultStatuses.REFUSED
        }

 */
        return SlotResult(SlotResultStatuses.UNKNOWN, 1)
    }


}


enum class SlotType { MASK, PLACEHOLDER}

class Unimask {

    companion object {

        var DEFAULT_VALUE_COLOR = 0xDD000000.toInt()
        var DEFAULT_MASK_COLOR =  0x8A000000.toInt()
        var DEFAULT_HINT_COLOR =  0x8A000000.toInt()

        fun parseClassicMask(mask : String, placeholder : Char = '#') : SlotsController {

            val slotController = SlotsController()
            val sb = StringBuilder(50)
            var currentStatus : SlotType? = null

            fun addSlot(){
                val slot = when(currentStatus){
                    SlotType.MASK -> MaskSlot(slotController, currentStatus!!, sb.toString(), isTransitionAllowed = true)
                    SlotType.PLACEHOLDER -> PlaceholderSlot(slotController, currentStatus!!, sb.length)
                    else -> throw Exception("")
                }
                slotController.addSlot(slot)
                sb.clear()
            }

            for(char in mask){
                val stateForThisChar = if(char == placeholder) SlotType.PLACEHOLDER else SlotType.MASK
                if(currentStatus != stateForThisChar && sb.isNotEmpty()){
                    addSlot()
                }
                currentStatus = stateForThisChar
                sb.append(char)
            }
            if(sb.isNotEmpty()){
                addSlot()
            }
            return slotController
        }
    }
}


