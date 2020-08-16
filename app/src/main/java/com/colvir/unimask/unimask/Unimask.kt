package com.colvir.unimask.unimask

import java.lang.Exception

class Unimask {

    companion object {

        var DEFAULT_VALUE_COLOR = 0xDD000000.toInt()
        var DEFAULT_MASK_COLOR =  0x8A000000.toInt()
        var DEFAULT_HINT_COLOR =  0x8A000000.toInt()

        fun parseClassicMask(mask : String?, placeholder : Char = '#') : SlotsController {

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

            if(!mask.isNullOrEmpty()){
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
            }
            return slotController
        }
    }
}