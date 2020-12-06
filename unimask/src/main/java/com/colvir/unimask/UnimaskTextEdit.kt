package com.colvir.unimask

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatEditText

class UnimaskTextEdit : AppCompatEditText {

    var hintColor : Int = NO_COLOR
        get() = field
        set(value){
            if(value != NO_COLOR){
                maskWatcher.slotController?.hintColor = value
            }
            field = value
        }
    var valueColor : Int = NO_COLOR
        get() = field
        set(value){
            if(value != NO_COLOR) {
                maskWatcher.slotController?.valueColor = value
            }
            field = value
        }

    var maskColor = NO_COLOR
        get() = field
        set(value){
            if(value != NO_COLOR) {
                maskWatcher.slotController?.maskColor = value
            }
            field = value
        }

    // Temporary for compatibility
    var hintString : String? = null
        get() = field
        set(value){
            field = value
        }

    var allowedChars : String? = null
        get() = field
        set(value){
            field = value
        }

    var deniedChars : String? = null
        get() = field
        set(value){
            field = value
        }

    var keepHint : Boolean = true
        get() = field
        set(value){
            field = value
        }

    var charRepresentation : Char = '#'
        get() = field
        set(value){
            field = value
        }

    var maskRaw : String? = null
        get() = field
        set(value){
            field = value
            if(value != null){
                maskWatcher.slotController = Unimask.parseClassicMask(field)
                maskWatcher.initialize(this)
                initilizeMask()
            }

        }

    val maskWatcher : UnimaskTextWatcher = UnimaskTextWatcher()

    val onFocusListenters = mutableListOf<OnFocusChangeListener>()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(attrs)
    }

    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr ){
        initialize(attrs)
    }

    companion object {

        @ColorInt
        const val NO_COLOR = 0x00FF00FF.toInt()
    }

    protected fun initilizeMask(){
        maskWatcher.slotController?.also {
            this.hintColor = this.hintColor
            this.valueColor = this.valueColor
            this.maskColor = this.maskColor
        }
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)

        if(focused){
            val poz =  maskWatcher.slotController?.findFirstEmptyPosition() ?: -1
            //Log.i(TAG,"on focus position $poz")
            if(poz > -1){
                setSelection(poz)
                android.os.Handler().postDelayed({
                    setSelection(poz)
                },20)
            }
        }

        onFocusListenters.forEach{ it.onFocusChange(this, focused) }

    }

    protected fun initialize(attrs: AttributeSet?){
        if(attrs == null){
            return
        }
        val ta = context.obtainStyledAttributes(attrs, R.styleable.UnimaskTextEdit )
        try{
            if(ta.hasValue(R.styleable.UnimaskTextEdit_unimask_mask)){
                maskRaw = ta.getString(R.styleable.UnimaskTextEdit_unimask_mask)
            }
            if(ta.hasValue(R.styleable.UnimaskTextEdit_unimask_hint)){
                hintString = ta.getString(R.styleable.UnimaskTextEdit_unimask_hint)
            }
            if(ta.hasValue(R.styleable.UnimaskTextEdit_unimask_allowedChars)){
                allowedChars = ta.getString(R.styleable.UnimaskTextEdit_unimask_allowedChars)
            }
            if(ta.hasValue(R.styleable.UnimaskTextEdit_unimask_deniedChars)){
                deniedChars = ta.getString(R.styleable.UnimaskTextEdit_unimask_deniedChars)
            }
            if(ta.hasValue(R.styleable.UnimaskTextEdit_unimask_keepHint)){
                keepHint = ta.getBoolean(R.styleable.UnimaskTextEdit_unimask_keepHint, true)
            }
            if(ta.hasValue(R.styleable.UnimaskTextEdit_unimask_charRepresentation)){
                val str = ta.getString(R.styleable.UnimaskTextEdit_unimask_charRepresentation)
                if(!str.isNullOrEmpty() && str.isNotEmpty()){
                    charRepresentation = str[0]
                }
            }
            if(ta.hasValue(R.styleable.UnimaskTextEdit_unimask_hintColor)){
                 hintColor = ta.getColor(R.styleable.UnimaskTextEdit_unimask_hintColor, NO_COLOR)
            }
            if(ta.hasValue(R.styleable.UnimaskTextEdit_unimask_valueColor)){
                valueColor = ta.getColor(R.styleable.UnimaskTextEdit_unimask_valueColor, NO_COLOR)
            }
            if(ta.hasValue(R.styleable.UnimaskTextEdit_unimask_maskColor)){
                maskColor = ta.getColor(R.styleable.UnimaskTextEdit_unimask_maskColor, NO_COLOR)
            }

        } finally {
            ta.recycle()
        }

    }

    init {
        addTextChangedListener(maskWatcher)
    }

    override fun setTextColor(color: Int) {
        super.setTextColor(color)
        valueColor = color
    }

}