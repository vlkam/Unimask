package com.colvir.unimask

import android.text.Editable
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import com.colvir.unimask.unimask.SlotsController
import com.colvir.unimask.unimask.Unimask
import org.junit.Test

import org.junit.Assert.*
import java.lang.Exception

class UnimaskUnitTest {

    protected fun checkMask(mask : String, expectedResult : String, action : (controller:SlotsController)->Unit ){
        val editable : Editable = MockEditable()
        val controller = Unimask.parseClassicMask(mask)

        action.invoke(controller)

        controller.getContent(editable)
        val content = editable.toString()
        assertEquals(expectedResult,content)
    }


    @Test
    fun parseClassicMask() {
        checkMask("##.##.##","  .  .  "){
        }
    }

    @Test
    fun insertIntoEmptyClassicMaskWithMasksChars() {
        checkMask("##.##.##","12.34.56"){
            it.insert("12.34.56",0)
        }
    }

    @Test
    fun insertIntoEmptyClassicMask() {
        checkMask("##.##.##","12.34.56"){
            it.insert("123456",0)
        }
    }

    @Test
    fun insertExcessiveChar() {
        checkMask("##.##.##","12.34.56"){
            it.insert("1234567",0)
        }
    }

    @Test
    fun checkCursorPosition() {
        checkMask("(702)#####","(702)12   "){
            it.insert("1",0)
            it.insert("2",9)
        }
    }

    @Test
    fun insertBeetween() {
        checkMask("(702)#####","(702)123  "){
            it.insert("1",0)
            it.insert("3",9)
            it.insert("2", 6)
        }
    }

    @Test
    fun insertBeetweenAndRemoveOne() {
        checkMask("/###/","/123/"){
            it.insert("1",0)
            it.insert("3",2)
            it.insert("4", 3)
            it.insert("2", 2)
        }

    }


}