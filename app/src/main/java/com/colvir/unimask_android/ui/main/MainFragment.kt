package com.colvir.unimask_android.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.colvir.unimask_android.R
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel

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

        //val phoneWatcher = UnimaskTextWatcher(phone_text, "+7(###)###-##-##")
        //val codeSlot = phoneWatcher.slotController.slots[1] as PlaceholderSlot
        //codeSlot.valueColor = Color.RED
        phone_text.maskRaw = "+7(###)###-##-##"

        //phone_text.addTextChangedListener(phoneWatcher)
    }

}