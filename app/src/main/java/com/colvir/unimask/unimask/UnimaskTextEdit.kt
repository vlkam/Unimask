package com.colvir.unimask.unimask

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class UnimaskTextEdit : AppCompatEditText
{

    var onTextChangedActionForUnifield : ((str: CharSequence?, rawText: String?) -> Unit)? = null

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ){
    }

    val maskWatcher : UnimaskTextWatcher = UnimaskTextWatcher()

    init {
        addTextChangedListener(maskWatcher)
    }

    // Temporary for compatibility
    var hintString : String? = null
    var allowedChars : String? = null
    var deniedChars : String? = null
    var keepHint : Boolean = true
    var charRepresentation : Char = '#'

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)

        onTextChangedActionForUnifield?.invoke(text, maskWatcher.slotController?.content(0))
    }


    var maskRaw : String? = null
        get() = field
        set(value){
            field = value
            if(value != null){
                maskWatcher.slotController = Unimask.parseClassicMask(field)
                maskWatcher.initialize(this)
            }

        }

}