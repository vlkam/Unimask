package com.colvir.unimask.unimask

import android.text.Editable
import java.lang.Exception

open class SlotsController {

    val hintColor : Int? = null
    val valueColor : Int? = null
    val maskColor : Int? = null
    var includeHint : Boolean = false

    var currentCursorPosition : Int = 0

    val slots = mutableListOf<Slot>()

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

    fun findSlotByPosition(poz: Int) : SlotFindResult? {
        var offset = 0
        for(slot in slots){
            if(poz >= offset && poz < offset + slot.length){
                return SlotFindResult(slot = slot, localPosition = poz - offset)
            }
            offset += slot.length
        }
        return null
    }

    fun findFirstEmptyPosition() : Int {
        var offset = 0
        for(slot in slots){
            when(slot){
                is MaskSlot -> { }
                is PlaceholderSlot -> {
                    for(position in slot.positions){
                        if(position.isEmpty()){
                            return offset + slot.positions.indexOf((position))
                        }
                    }
                }
            }
            offset += slot.length
        }
        return -1
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

    protected fun insert_internal (string : String, poz : Int, level : Int = 1) : SlotResult {
        var currentPosition = poz

        for(char in string){
            val slotFindResult = findSlotByPosition(currentPosition)
            if(slotFindResult == null){
                // It should be end of mask
                break
            }
            val slot = slotFindResult.slot
            val res = slot.insert(char, slotFindResult.localPosition, isHint = false)
            when(res.status){
                Slot.SlotResultStatuses.ACCEPTED -> {
                    // All is OK
                    currentPosition += res.cursorOffset
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
                        val res2 = insert_internal(char.toString(), poz = offsets.startOffset, level = level + 1)
                        if(res2.status == Slot.SlotResultStatuses.ACCEPTED){
                            currentPosition = res2.cursorOffset
                        } else {
                            // I have no idea what may we do here
                            return SlotResult(status = Slot.SlotResultStatuses.REFUSED, cursorOffset = currentPosition)
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
        return SlotResult(status = Slot.SlotResultStatuses.ACCEPTED, cursorOffset = currentPosition)
    }

    fun insert(string : String, poz : Int) : SlotResult {

        var cursorPosition = poz

        // Check the position
        var firstEmptyPosition = findFirstEmptyPosition()
        if(firstEmptyPosition != -1 && cursorPosition > firstEmptyPosition){
            // There is an empty position before current position
            cursorPosition = firstEmptyPosition
        } else {
            val res = findSlotByPosition(cursorPosition)
            if(res != null && res.slot is MaskSlot){
                // this is a mask, move for first empty position
                cursorPosition = firstEmptyPosition
            }
        }

        val res = insert_internal(string, cursorPosition)
        currentCursorPosition = res.cursorOffset
        return res
    }

}
