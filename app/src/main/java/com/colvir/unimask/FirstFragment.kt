package com.colvir.unimask

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.colvir.unimask.unimask.*
import kotlinx.android.synthetic.main.fragment_first.*

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
            Log.i("UTW","")
        }

        //date_text.inputType = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_DATE or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        //val watcher = UnimaskTextWatcher(date_text, "")
        //date_text.addTextChangedListener(watcher)
        //watcher.initialize()

        //date_text.addTextChangedListener(UnimaskTextWatcher(date_text, "##.##.##"))

        val phoneWatcher = UnimaskTextWatcher(phone_text, "+7(###)###-##-##")
        val codeSlot = phoneWatcher.slotController.slots[1] as PlaceholderSlot
        codeSlot.valueColor = Color.RED
        phone_text.addTextChangedListener(phoneWatcher)

    }
}