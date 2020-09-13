package com.colvir.unimask

open class StretchingSlot(slotController : SlotsController, type : SlotType, size : Int) : PlaceholderSlot(slotController, type, size) {

    open fun stretchingInsert(c : Char, localPosition : Int, isHint : Boolean) : SlotResult {
        positions.add(localPosition, SlotPosition(this))
        val result = insert(c,localPosition,isHint)
        return result
    }

}