package com.colvir.unimask

import android.text.Editable

open class MaskSlot (
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
            slotController.maskColor!!
        } else {
            Unimask.DEFAULT_MASK_COLOR
        }
    }

    override fun getContent(editable : Editable) {
        append(mask, editable, getColorForMask())
    }

    override val length: Int
        get() = mask.length

    override fun firstEmptyPosition(): Int {
        return -1
    }

    override fun insert(c: Char, localPosition : Int, isHint: Boolean)  : SlotResult {

        val res = super.insert(c, localPosition, isHint)
        if(res.status != SlotResultStatuses.UNKNOWN){
            return res
        }

        val charOfMask = mask[localPosition]
        return if(charOfMask == c){
            SlotResult(SlotResultStatuses.ACCEPTED, 1, null)
        } else {
            SlotResult(SlotResultStatuses.REFUSED, 0, null)
        }
    }

}