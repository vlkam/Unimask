package com.colvir.unimask.unimask

import java.lang.reflect.Field

fun<T> Any.getPrivateFieldValue(clazz : Class<out Any>,name : String) : T? {
    val field = clazz.getDeclaredField(name)
    field.isAccessible = true
    @Suppress("UNCHECKED_CAST")
    return field.get(this) as? T
}

fun Any.setPrivateFieldValue(clazz : Class<out Any>,name : String, value : Any?) {
    val field = clazz.getDeclaredField(name)
    field.isAccessible = true
    field.set(this, value)
}

fun Any.getPrivateField(clazz : Class<out Any>, name : String) : Field {
    val field = clazz.getDeclaredField(name)
    field.isAccessible = true
    return field
}