package com.colvir.unimask

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatEditText
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_first.*

class UnimaskTextWatcher(val editText: AppCompatEditText) : TextWatcher {

    override fun afterTextChanged(s: Editable?) {
        Log.i("UTW"," ${s?.toString()} selection ${editText.selectionStart}")
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        Log.i("UTW"," ${s?.toString()} selection ${editText.selectionStart} start $start  count $count  after $after")
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
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
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        edit_text.addTextChangedListener(UnimaskTextWatcher(edit_text))
    }
}