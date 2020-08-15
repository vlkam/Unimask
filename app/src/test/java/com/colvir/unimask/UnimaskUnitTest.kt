package com.colvir.unimask

import android.text.Editable
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import org.junit.Test

import org.junit.Assert.*
import java.lang.Exception

open class SlotsController {

    val hintColor : Int? = null
    val valueColor : Int? = null
    val maskColor : Int? = null
    var includeHint : Boolean = false

    protected val slots = mutableListOf<Slot>()

    fun addSlot(slot : Slot){
        slots.add(slot)

        var idx = 0
        for(slt in slots){
            slt.indexOfSlot = idx++
        }
    }

    fun getContent(editable : Editable) {
        for (slot in slots){
            slot.getContent(editable)
        }
    }

    class SlotFindResult(
        val slot : Slot,
        val localPosition: Int
    )

    fun findSlotByPosition(poz: Int) : SlotFindResult {
        var offset = 0
        for(slot in slots){
            if(poz >= offset && poz < offset + slot.length){
                return SlotFindResult(slot = slot, localPosition = poz - offset)
            }
            offset += slot.length
        }
        throw Exception("Slot not found")
    }

    class SlotOffsets(
        val startOffset : Int,
        val endOffset : Int
    )

    fun calculateOffsetForSlot(slot : Slot) : SlotOffsets {
        var offset = 0
        for(slt in slots){
            if(slt == slot){
                return SlotOffsets( startOffset = offset, endOffset =  offset + slt.length)
            }
            offset += slt.length
        }
        throw Exception("Slot not found")
    }


    fun findNextAccessibleSlotAfterMask(maskSlot : MaskSlot) : Slot? {
        val searchFrom = slots.indexOf(maskSlot)
        for(index in searchFrom until slots.size) {
            val slot = slots[index]
            if(slot is MaskSlot){
                if(slot.isTransitionAllowed) {
                    // it allows transmission through the mask, so skip it and find next slot
                    continue
                } else {
                    // it doesn't allow transmission through the mask
                    return null
                }
            } else {
                return slot
            }
        }
        return null
    }

    fun insert(editable: Editable, string : String, poz : Int, level : Int = 1) : SlotResult {

        var currentCursorPosition = poz
        for(char in string){
            val slotFindResult = findSlotByPosition(currentCursorPosition)
            val slot = slotFindResult.slot
            val res = slot.insert(char, slotFindResult.localPosition, editable, isHint = false)
            when(res.status){
                Slot.SlotResultStatuses.ACCEPTED ->{
                    // All is OK
                    currentCursorPosition += res.cursorOffset
                }
                Slot.SlotResultStatuses.NOT_ENOUGH_SPACE -> {
                    TODO()
                }
                Slot.SlotResultStatuses.REFUSED -> {
                    if(level > 1){
                        // it's not first try, so it should be ended
                        return res
                    }
                    val maskSlot = slot as? MaskSlot
                    if(maskSlot == null){
                        // the slot refuses to accept char, what can we do? Perhaps nothing
                        return res
                    }
                    // it's a mask, lets try to skip that
                    val nextSlotAfterMask = findNextAccessibleSlotAfterMask(maskSlot)
                    if(nextSlotAfterMask != null){
                        val offsets = calculateOffsetForSlot(nextSlotAfterMask)
                        val res2 = insert(editable, char.toString(), poz = offsets.startOffset, level = level + 1)
                        if(res2.status == Slot.SlotResultStatuses.ACCEPTED){
                            currentCursorPosition = res2.cursorOffset
                        } else {
                            // I have no idea what may we do here
                            return SlotResult(status = Slot.SlotResultStatuses.REFUSED, cursorOffset = currentCursorPosition)
                        }
                    }

                /*
                    val isItAMask = slot.type == SlotType.MASK
                    if(isItAMask){
                        // Yes, it's a mask
                        if(!slot)

                    }

                 */
                    // let's try to find an another free position
                }
                Slot.SlotResultStatuses.UNKNOWN -> throw Exception("The state of result if unknown")

            }
        }
        return SlotResult(status = Slot.SlotResultStatuses.ACCEPTED, cursorOffset = currentCursorPosition)
    }

}

class SlotResult(
    val status : Slot.SlotResultStatuses,
    var cursorOffset : Int
)

abstract class Slot(val slotController : SlotsController, val type : SlotType){

    var indexOfSlot : Int = 0

    enum class SlotResultStatuses { UNKNOWN, ACCEPTED, REFUSED, NOT_ENOUGH_SPACE }

    abstract fun getContent(editable : Editable)
    abstract val length : Int

    open fun insert(c : Char, localPosition : Int, editable : Editable, isHint : Boolean) : SlotResult {
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

    override fun insert(c: Char, localPosition : Int, editable: Editable, isHint: Boolean)  : SlotResult {

        val res = super.insert(c, localPosition, editable, isHint)
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

    override fun insert(c: Char, localPosition : Int, editable: Editable, isHint: Boolean)  : SlotResult {

        val res = super.insert(c, localPosition, editable, isHint)
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




class UnimaskUnitTest {

    @Test
    fun parseClassicMask() {

        val editable : Editable = MockEditable()

        val controller = Unimask.parseClassicMask("##.##.##")
        controller.getContent(editable)

        val content = editable.toString()

        assertEquals(content,"  .  .  ")

    }

    @Test
    fun insertIntoEmptyClassicMaskWithMasksChars() {
        val editable : Editable = MockEditable()
        val controller = Unimask.parseClassicMask("##.##.##")

        controller.insert(editable,"12.34.56",0)

        controller.getContent(editable)
        val content = editable.toString()
        assertEquals(content,"12.34.56")

    }

    @Test
    fun insertIntoEmptyClassicMask() {
        val editable : Editable = MockEditable()
        val controller = Unimask.parseClassicMask("##.##.##")

        controller.insert(editable,"123456",0)

        controller.getContent(editable)
        val content = editable.toString()
        assertEquals(content,"12.34.56")

    }


}