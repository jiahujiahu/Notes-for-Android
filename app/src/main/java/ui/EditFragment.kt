package ui.assignments.a4notes.ui

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Note
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ui.assignments.a4notes.R
import ui.assignments.a4notes.viewmodel.NotesViewModel

class EditFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.edit_fragment, container, false)
        val myVM: NotesViewModel by activityViewModels { NotesViewModel.Factory }
        view.findViewById<EditText>(R.id.titleE).text = Editable.Factory.getInstance().newEditable(myVM.getNote().title)
        view.findViewById<EditText>(R.id.ContentE).text = Editable.Factory.getInstance().newEditable(myVM.getNote().content)
        view.findViewById<Switch>(R.id.importantSwitchE).isChecked = myVM.getNote().important
        view.findViewById<Switch>(R.id.archivedSwitchE).isChecked = myVM.getNote().archived
        //save changes
        view.findViewById<EditText>(R.id.titleE).addTextChangedListener(
            afterTextChanged = {
                myVM.updateNoteTitle(myVM.getNote().id,
                    view.findViewById<EditText>(R.id.titleE).text.toString())
            }
        )

        view.findViewById<EditText>(R.id.ContentE).addTextChangedListener(
            afterTextChanged = {
                myVM.updateNoteContent(myVM.getNote().id,
                    view.findViewById<EditText>(R.id.ContentE).text.toString())
            }
        )

        view.findViewById<Switch>(R.id.importantSwitchE).setOnCheckedChangeListener { compoundButton, b ->
            myVM.updateNoteImportant(myVM.getNote().id,b)
            //when important on archived off
            if(b == true){
                view.findViewById<Switch>(R.id.archivedSwitchE).isChecked = !b
                myVM.updateNoteArchived(myVM.getNote().id,false)
            }
        }


        view.findViewById<Switch>(R.id.archivedSwitchE).setOnCheckedChangeListener { compoundButton, b ->
            myVM.updateNoteArchived(myVM.getNote().id,b)
            //when archived on important off
            if(b == true){
                view.findViewById<Switch>(R.id.importantSwitchE).isChecked = !b
                myVM.updateNoteImportant(myVM.getNote().id,false)
            }
        }

        return view
    }

}