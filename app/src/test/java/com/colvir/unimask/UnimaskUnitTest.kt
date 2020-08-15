package com.colvir.unimask

import android.text.Editable
import android.util.Log
import org.junit.Test

import org.junit.Assert.*
import java.lang.Exception


class SlotsController {

    protected val slots = mutableListOf<Slot>()

    fun addSlot(slot : Slot){
        slots.add(slot)
    }

    fun getContent(editable : Editable, includeHint : Boolean) {
        for (slot in slots){
            slot.getContent(editable, includeHint)
        }

    }
}

abstract class Slot(val slotController : SlotsController, val type : SlotType){
    abstract fun getContent(editable : Editable, includeHint : Boolean)
}

open class MaskSlot(slotController : SlotsController, type : SlotType, val mask : String) : Slot(slotController, type){

    override fun getContent(editable : Editable, includeHint : Boolean) {
        editable.append(mask)
    }

}

class SlotPosition(){
    val hint : Char? = null
    val value : Char? = null

    fun getValue(includeHint : Boolean) : Char {
        return if(value != null){
            value
        } else if(hint != null && includeHint){
            hint
        } else {
            ' '
        }
    }
}

open class PlaceholderSlot(slotController : SlotsController, type : SlotType, val size : Int) : Slot(slotController, type){

    val content = mutableListOf<SlotPosition>()

    protected val sb = StringBuilder(30)

    init {
        for(i in 0 until size){
            content.add(SlotPosition())
        }
    }

    override fun getContent(editable : Editable, includeHint : Boolean) {
        sb.clear()
        for (poz in content){
            val char = poz.getValue(includeHint)
            sb.append(char)
        }
        editable.append(sb.toString())
    }
}


enum class SlotType { MASK, PLACEHOLDER}

class Unimask {

    companion object {

        fun parseClassicMask(mask : String, placeholder : Char = '#') : SlotsController {

            val slotController = SlotsController()
            val sb = StringBuilder(50)
            var currentStatus : SlotType? = null

            fun addSlot(){
                val slot = when(currentStatus){
                    SlotType.MASK -> MaskSlot(slotController, currentStatus!!, sb.toString())
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
        controller.getContent(editable, includeHint = false)

        val content = editable.toString()

        assertEquals(content,"  .  .  ")

        Log.i("","")


    }
}