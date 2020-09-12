package com.colvir.unimask

import android.text.Editable
import org.junit.Test

import org.junit.Assert.*

class UnimaskUnitTest {

    protected fun checkMask(mask : String, expectedResult : String, expectedCursorPosition : Int = -1, action : (controller:SlotsController)->Unit ){
        val editable : Editable = MockEditable()
        val controller = Unimask.parseClassicMask(mask)

        action.invoke(controller)

        controller.getContent(editable)
        val content = editable.toString()
        assertEquals(expectedResult,content)
        if(expectedCursorPosition != -1){
            assertEquals(expectedCursorPosition,controller.currentCursorPosition)
        }
    }


    @Test
    fun parseClassicMask() {
        checkMask("##.##.##","  .  .  ", 0){
        }
    }

    @Test
    fun insertIntoEmptyClassicMaskWithMasksChars() {
        checkMask("##.##.##","12.34.56",8){
            it.insert("12.34.56",0)
        }
    }

    @Test
    fun insertIntoEmptyClassicMask() {
        checkMask("##.##.##","12.34.56", 8){
            it.insert("123456",0)
        }
    }

    @Test
    fun insertExcessiveChar() {
        checkMask("##.##.##","12.34.56", 8){
            it.insert("1234567",0)
        }
    }

    @Test
    fun checkCursorPosition() {
        checkMask("(702)#####","(702)12   ", 7){
            it.insert("1",0)
            it.insert("2",9)
        }
    }

    @Test
    fun insertBetween() {
        checkMask("(702)#####","(702)123  ", 7){
            val sr1 = it.insert("1",0)
            val sr2 =it.insert("3",9)
            val sr3 =it.insert("2", 6)
            val z = 4
        }
    }

    @Test
    fun insertBetweenAndRemoveOne() {
        checkMask("/###/","/123/",3){
            val sr1 = it.insert("1",0)
            val sr2 = it.insert("3",2)
            val sr3 = it.insert("4", 3)
            val sr4 = it.insert("2", 2)
            val z = 4
        }

    }

    @Test
    fun removeOneChar() {
        checkMask("##.##.##","13.45.6 ", 1) {
            it.insert("123456",0)
            it.remove(1,2)
        }
    }

    @Test
    fun removeTwoChars() {
        checkMask("##.##.##","14.56.  ", 1){
            it.insert("123456",0)
            it.remove(1,4)
        }
    }

    @Test
    fun removeAllChars() {
        checkMask("##.##.##","  .  .  ",0){
            it.insert("123456",0)
            it.remove(0,8)
        }
    }

    @Test
    fun removeAtTheEnd() {
        checkMask("##.##.##","12.34.  ",5){
            it.insert("123456",0)
            it.remove(7,8)
            it.remove(6,7)
        }
    }

    @Test
    fun tryingToRemoveMask() {
        checkMask("##.##.##","1 .  .  ",1){
            it.insert("1",0)
            it.insert("2",1)
            it.remove(2,3)
        }
    }

    @Test
    fun insertHint1(){
        checkMask("##.##.##","12.34.56",0){
            it.insert("123456",0, isHint = true)
        }
    }

    @Test
    fun insertHint2(){
        checkMask("##.##.##","12.34.56",0){
            it.insert("12.34.56",0, isHint = true)
        }
    }

}