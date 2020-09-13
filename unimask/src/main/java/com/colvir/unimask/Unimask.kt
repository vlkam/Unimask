package com.colvir.unimask

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
                    SlotType.MASK -> MaskSlot(slotController, currentStatus!!, sb.toString())
                    SlotType.PLACEHOLDER -> PlaceholderSlot(slotController, currentStatus!!, sb.length)
                    SlotType.STRETCHING -> StretchingSlot(slotController, currentStatus!!, sb.length)
                    else -> throw Exception("")
                }
                slotController.addSlot(slot)
                sb.clear()
            }

            var isStreching = false

            if(!mask.isNullOrEmpty()){
                for(char in mask){
                    if(char == '{'){
                        isStreching = true
                        if(sb.isNotEmpty()){
                            addSlot()
                        }
                        continue
                    } else if (char == '}'){
                        slotController.addSlot(StretchingSlot(slotController, SlotType.STRETCHING, sb.length))
                        isStreching = false
                        currentStatus = null
                        sb.clear()
                        continue
                    } else if(isStreching){
                        sb.append(char)
                        continue
                    }
                    val stateForThisChar = if(char == placeholder){
                        SlotType.PLACEHOLDER
                    } else {
                        SlotType.MASK
                    }
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