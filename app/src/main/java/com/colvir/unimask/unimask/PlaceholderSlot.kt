package com.colvir.unimask.unimask

import android.text.Editable

open class PlaceholderSlot(slotController : SlotsController, type : SlotType, size : Int)
    : Slot(slotController, type), Iterable<SlotPosition> {

    var hintColor : Int? = null
    var valueColor : Int? = null

    var includeHint : Boolean? = null

    val positions = mutableListOf<SlotPosition>()

    override val length: Int
        get() = positions.size

    override fun firstEmptyPosition(): Int {
        var poz = 0
        for(position in positions){
            if(position.isEmpty()){
                return poz
            }
            poz++
        }
        return -1
    }

    init {
        for(i in 0 until size){
            positions.add(SlotPosition(this))
        }
    }

    override fun getContent(editable : Editable){
        for (poz in positions){
            poz.getValue(editable)
        }
    }

    override fun insert(c: Char, localPosition : Int, isHint: Boolean)  : SlotResult {

        val res = super.insert(c, localPosition, isHint)
        if(res.status != SlotResultStatuses.UNKNOWN){
            return res
        }

        val pos = positions[localPosition]

        val previousChar = pos.value
        pos.setupValue(c)
        return SlotResult(SlotResultStatuses.ACCEPTED, 1, poppedChar = previousChar )
    }

    class PositionIterator(private val list : List<SlotPosition>) : Iterator<SlotPosition>{
        var idx = 0

        override fun hasNext(): Boolean = idx < list.size

        override fun next(): SlotPosition = list[idx++]
    }

    override fun iterator(): Iterator<SlotPosition> = PositionIterator(positions)

    fun removeValueAt(relativePosition: Int) {
        val poz = positions[relativePosition]
        poz.value = null
    }

}
