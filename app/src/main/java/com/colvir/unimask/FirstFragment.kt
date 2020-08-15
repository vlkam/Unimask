package com.colvir.unimask

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatEditText
import com.colvir.unimask.unimask.SlotsController
import com.colvir.unimask.unimask.Unimask
import kotlinx.android.synthetic.main.fragment_first.*

class UnimaskTextWatcher(val editText: AppCompatEditText) : TextWatcher {

    var slotController : SlotsController

    private var internalChange : Boolean = false
    private var newSubstr : String? = null
    private var insertPosition : Int = 0

    init{

        slotController = Unimask.parseClassicMask("##.##.##")
        val ed = Editable.Factory.getInstance().newEditable("")
        slotController.getContent(ed)
        try {
            internalChange = true
            editText.editableText.clear()
            editText.filters = emptyArray()
            editText.editableText.filters = emptyArray()
            editText.editableText.append(ed)
        } finally {
            internalChange = false
        }
    }

    override fun afterTextChanged(s: Editable?) {
        if(internalChange){
            return
        }

        try{
            internalChange = true
            if(newSubstr != null && s != null){
                slotController.insert(newSubstr!!, insertPosition)

            }
            if(s != null){
                s.clear()
                slotController.getContent(s)
                editText.setSelection(slotController.currentCursorPosition)
            }
        } finally {
            internalChange = false
        }



        Log.i("UTW"," ${s?.toString()} selection ${editText.selectionStart}")
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        if(internalChange){
            return
        }

        newSubstr = null
        insertPosition = start

        Log.i("UTW"," ${s?.toString()} selection ${editText.selectionStart} start $start  count $count  after $after")
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if(internalChange){
            return
        }

        newSubstr = s?.substring(start, start + count)

        Log.i("UTW"," ${s?.toString()}  selection ${editText.selectionStart} start $start  before $before  count $count")
    }

}

class FirstFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            val filters = edit_text.filters
            Log.i("UTW","")
            //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        edit_text.inputType = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_DATE or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS

        val z = edit_text.filters
        //edit_text.filters = emptyArray<InputFilter>()
        //(edit_text as TextView).setPrivateFieldValue(TextView::class.java, "mFilters" ,emptyArray<InputFilter>())
        val x = edit_text.filters

        val watcher = UnimaskTextWatcher(edit_text)
        edit_text.addTextChangedListener(watcher)
        //watcher.initialize()

    }
}