package com.colvir.unimask

import android.text.Editable
import java.lang.Exception

// Spans https://developer.android.com/reference/kotlin/android/text/style/BulletSpan

open class SlotsController {

    val hintColor : Int? = null
    val valueColor : Int? = null
    val maskColor : Int? = null
    var includeHint : Boolean = true

    var currentCursorPosition : Int = 0

    val slots = mutableListOf<Slot>()

    fun setHint(hint : String?){

    }

    var keepHint : Boolean = true

    fun addSlot(slot : Slot){
        slots.add(slot)

        var idx = 0
        for(slt in slots){
            slt.indexOfSlotForDebugPurpose = idx++
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

    fun findNextPosition(poz: Int) : SlotFindResult? {
        var offset = 0
        for(slot in slots){
            if(poz >= offset && poz < offset + slot.length){
                return SlotFindResult(slot = slot, localPosition = poz - offset)
            }
            offset += slot.length
        }
        return null
    }

    fun findFirstEmptyPosition(from : Int = -1) : Int {
        var offset = 0
        for(slot in slots){
            val poz = slot.firstEmptyPosition()
            if(poz > -1 && poz >= from){
                return offset + poz
            }
            offset += slot.length
        }
        return -1
    }

    class Position (
        val absolutePosition : Int,
        val relativePosition : Int,
        val slot : PlaceholderSlot,
        val position: SlotPosition
    )

    fun positionsList(from : Int, to : Int = -1) : List<Position> {
        var currentPosition = 0
        val list = mutableListOf<Position>()
        loop@ for(slot in slots){
            when(slot){
                is MaskSlot -> {
                    currentPosition += slot.length
                }
                is PlaceholderSlot -> {
                    var localPosition = 0
                    for(position in slot){
                        if(to != -1 && currentPosition > to){
                            break@loop
                        }
                        if(currentPosition >= from){
                            list.add(Position(
                                absolutePosition = currentPosition,
                                relativePosition = localPosition,
                                position = position,
                                slot = slot
                            ))
                        }
                        currentPosition++
                        localPosition++
                    }
                }
            }
        }
        return list
    }

    fun content(from : Int) : String{
        val sb = StringBuilder(50)
        val positions = positionsList(from = from)
        for(pos in positions){
            if(!pos.position.isEmpty()){
                sb.append(pos.position.value)
            }
        }
        return sb.toString()
    }

    open fun remove(fromPosition : Int, toPosition : Int)  {

        var fromPos = fromPosition
        val toPos = toPosition

        // Clear positions
        var positionsForClearing = positionsList( from = fromPos, to = toPos - 1)

        // It seems like nothing to delete because there is a mask
        // Trying to find the first removable position
        if(positionsForClearing.isEmpty()){
            val positionsBefore = positionsList(0, fromPosition)
            if(positionsBefore.isNotEmpty()){
                val position = positionsBefore.last()
                fromPos = position.absolutePosition
                //toPos = position.absolutePosition + 1
                positionsForClearing = positionsList( from = fromPos, to = toPos - 1)
            }
        }

        for(position in positionsForClearing){
            position.slot.removeValueAt(position.relativePosition)
        }

        val lengthOfDelete = toPos - fromPos

        // Move the rest of values to cleared positions
        val positionsForFilling = positionsList(from = fromPos + lengthOfDelete)
        val sb = StringBuilder()
        for(position in positionsForFilling){
            if(!position.position.isEmpty()){
                sb.append(position.position.value)
                position.slot.removeValueAt(position.relativePosition)
            }
        }

        // Insert
        if(sb.isNotEmpty()){
            insert_internal(sb.toString(), poz = fromPos)
        }

        if(positionsForClearing.isNotEmpty()){
            currentCursorPosition = positionsForClearing[0].absolutePosition
        }

        // Need to correct the cursor position it there is a mask before
        val positionsBefore = positionsList(0,currentCursorPosition - 1)
        if(positionsBefore.isNotEmpty()){
            val lastPos = positionsBefore.last()
            if(currentCursorPosition - lastPos.absolutePosition> 1){
                currentCursorPosition = lastPos.absolutePosition + 1
            }
        }

    }

    protected class InsertedChar(
        val char : Char,
        val status : Status
    ){
        enum class Status { NEW, REALLOCATED }
    }

    protected fun insert_internal (string : String, poz : Int, isHint : Boolean = false) : SlotResult {

        var currentPosition = poz

        // Basically it is the current position, but we should take to account the reallocation shouldn't change the cursor position
        var cursorPosition : Int = currentPosition

        val listOfChars = string.toList().map { InsertedChar(char = it, status = InsertedChar.Status.NEW) }.toMutableList()

        fun removeProcessedChar(poppedChar : Char?){
            if(poppedChar != null){
                listOfChars[0] = InsertedChar(char = poppedChar,status = InsertedChar.Status.REALLOCATED)
            } else {
                listOfChars.removeAt(0)
            }

        }

        while(listOfChars.size > 0){

            val char = listOfChars[0]

            val slotFindResult = findNextPosition(currentPosition)
            if(slotFindResult == null){
                // It should be end of mask
                break
            }
            val slot = slotFindResult.slot
            val res = slot.insert(char.char, slotFindResult.localPosition, isHint = isHint)
            when(res.status){
                Slot.SlotResultStatuses.ACCEPTED -> {
                    // All is OK
                    currentPosition += res.cursorOffset
                    if(char.status == InsertedChar.Status.NEW && !isHint){
                        cursorPosition = currentPosition
                    }
                    // Do we have a popped char ? If yes we should try to move this char
                    removeProcessedChar(res.poppedChar)
                }
                Slot.SlotResultStatuses.REFUSED -> {
                    val maskSlot = slot as? MaskSlot
                    if(maskSlot == null){
                        // the slot refuses to accept char, what can we do? Perhaps nothing
                        res.cursorOffset = cursorPosition
                        return res
                    }
                    // it's a mask, lets try to skip that
                    val nextSlotAfterMask = positionsList(currentPosition).firstOrNull()
                    if(nextSlotAfterMask != null){
                        val nextSlot = nextSlotAfterMask.slot
                        val res2 = nextSlot.insert(char.char, 0, isHint = isHint)
                        if(res2.status == Slot.SlotResultStatuses.ACCEPTED){
                            currentPosition = nextSlotAfterMask.absolutePosition + res2.cursorOffset
                            if(char.status == InsertedChar.Status.NEW && !isHint){
                                cursorPosition = currentPosition
                            }
                            removeProcessedChar(res2.poppedChar)
                        } else {
                            // no idea what we may do in that case
                            return SlotResult(status = Slot.SlotResultStatuses.REFUSED, cursorOffset = cursorPosition, poppedChar = null)
                        }
                    } else {
                        res.cursorOffset = cursorPosition
                        return res
                    }
                }
                Slot.SlotResultStatuses.UNKNOWN -> throw Exception("The state of result if unknown")

            }
        }
        return SlotResult(status = Slot.SlotResultStatuses.ACCEPTED, cursorOffset = cursorPosition, poppedChar = null)
    }

    open fun insert(string : String, poz : Int, isHint : Boolean = false) : SlotResult {

        var cursorPosition = poz

        // Check the position
        val firstEmptyPosition = findFirstEmptyPosition()
        if(firstEmptyPosition != -1 && cursorPosition > firstEmptyPosition){
            // There is an empty position before current position
            cursorPosition = firstEmptyPosition
        }

        val res = insert_internal(string, cursorPosition, isHint)

        currentCursorPosition = res.cursorOffset

        // Check if the next slot is a mask ? If yes, moves the cursor
        val firstEmptyPoz = findFirstEmptyPosition(currentCursorPosition)
        if(firstEmptyPoz > currentCursorPosition){
            currentCursorPosition = firstEmptyPoz
        }

        return res
    }

}
