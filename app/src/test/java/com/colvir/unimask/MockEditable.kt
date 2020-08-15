package com.colvir.unimask

import android.text.Editable
import android.text.InputFilter

class MockEditable() : Editable {

    private var str: String = ""

    override fun toString(): String {
        return str
    }

    override fun get(index: Int): Char {
        return str[index]
    }

    override fun subSequence(i: Int, i1: Int): CharSequence {
        return str.subSequence(i, i1)
    }

    override val length: Int
        get() = str.length

    override fun replace(
        i: Int,
        i1: Int,
        charSequence: CharSequence,
        i2: Int,
        i3: Int
    ): Editable {
        return this
    }

    override fun replace(i: Int, i1: Int, charSequence: CharSequence): Editable {
        return this
    }

    override fun insert(
        i: Int,
        charSequence: CharSequence,
        i1: Int,
        i2: Int
    ): Editable {
        return this
    }

    override fun insert(i: Int, charSequence: CharSequence): Editable {
        return this
    }

    override fun <T : Any?> getSpans(start: Int, end: Int, type: Class<T>?): Array<T>? {
        return null
    }

    override fun delete(i: Int, i1: Int): Editable {
        return this
    }

    override fun append(charSequence: CharSequence?): Editable {
        str += charSequence
        return this
    }

    override fun append(charSequence: CharSequence?, i: Int, i1: Int): Editable {
        return this
    }

    override fun append(c: Char): Editable {
        return this
    }

    override fun clear() {}
    override fun clearSpans() {}
    override fun setFilters(inputFilters: Array<InputFilter>) {}
    override fun getFilters(): Array<InputFilter>? {
        return null
    }

    override fun getChars(i: Int, i1: Int, chars: CharArray, i2: Int) {}

    override fun setSpan(o: Any, i: Int, i1: Int, i2: Int) {}
    override fun removeSpan(o: Any) {}

    /*
    override fun <T> getSpans(i: Int, i1: Int, aClass: Class<T>): Array<T> {
        return Array(0, )
    }
     */

    override fun getSpanStart(o: Any): Int {
        return 0
    }

    override fun getSpanEnd(o: Any): Int {
        return 0
    }

    override fun getSpanFlags(o: Any): Int {
        return 0
    }

    override fun nextSpanTransition(i: Int, i1: Int, aClass: Class<*>?): Int {
        return 0
    }

}