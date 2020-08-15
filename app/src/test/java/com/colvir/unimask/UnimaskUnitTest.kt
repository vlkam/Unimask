package com.colvir.unimask

import android.text.Editable
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import com.colvir.unimask.unimask.Unimask
import org.junit.Test

import org.junit.Assert.*
import java.lang.Exception



class UnimaskUnitTest {

    @Test
    fun parseClassicMask() {

        val editable : Editable = MockEditable()

        val controller = Unimask.parseClassicMask("##.##.##")
        controller.getContent(editable)

        val content = editable.toString()

        assertEquals(content,"  .  .  ")

    }

    @Test
    fun insertIntoEmptyClassicMaskWithMasksChars() {
        val editable : Editable = MockEditable()
        val controller = Unimask.parseClassicMask("##.##.##")

        controller.insert("12.34.56",0)

        controller.getContent(editable)
        val content = editable.toString()
        assertEquals(content,"12.34.56")

    }

    @Test
    fun insertIntoEmptyClassicMask() {
        val editable : Editable = MockEditable()
        val controller = Unimask.parseClassicMask("##.##.##")

        controller.insert("123456",0)

        controller.getContent(editable)
        val content = editable.toString()
        assertEquals(content,"12.34.56")

    }


}