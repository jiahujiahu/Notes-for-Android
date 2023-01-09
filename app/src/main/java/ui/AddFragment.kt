package ui.assignments.a4notes.ui

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Note
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ui.assignments.a4notes.R
import ui.assignments.a4notes.viewmodel.NotesViewModel

class AddFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.add_fragment, container, false)
        val myVM: NotesViewModel by activityViewModels { NotesViewModel.Factory }

        view.findViewById<Button>(R.id.createButtonA).setOnClickListener {
            findNavController().navigate(R.id.action_addFragment_to_mainFragment)
            myVM.addNote(view.findViewById<EditText>(R.id.titleA).text.toString(),
                view.findViewById<EditText>(R.id.ContentA).text.toString(),
                view.findViewById<Switch>(R.id.importantSwitchA).isChecked
            )
        }
        return view
    }

}