package com.colvir.unimask.unimask

import android.text.Editable

open class PlaceholderSlot(slotController : SlotsController, type : SlotType, val size : Int) : Slot(slotController, type){

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


    /*
    fun doesItHaveOneModeEmptyPosition(fromPosition : Int) : Boolean{
        for(index in fromPosition until positions.size){
            val pos = positions[index]
            if(pos.isEmpty()){
                return true
            }
        }
        return false
    }
     */

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


}
