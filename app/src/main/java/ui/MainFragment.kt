package ui.assignments.a4notes.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ui.assignments.a4notes.R
import ui.assignments.a4notes.viewmodel.NotesViewModel

class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.main_fragment, container, false)
        val myVM: NotesViewModel by activityViewModels { NotesViewModel.Factory }

        refresh(myVM,view)

        view.findViewById<Switch>(R.id.showArchivedSwitch).setOnCheckedChangeListener {
                _, isChecked ->
            myVM.set(isChecked)
            refresh(myVM,view) //call update function
        }

        view.findViewById<FloatingActionButton>(R.id.plusButton).setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_addFragment)
        }

        return view
    }
    private fun refresh(myVM:NotesViewModel, view: View) {
        myVM.getNotes().observe(viewLifecycleOwner) { list ->
            val linearLayout = view.findViewById<LinearLayout>(R.id.noteList)
            linearLayout.removeAllViews()
            list.forEach { string ->
                // Using the layoutInflator to generate a small "sub-scene-graph" out of string_view.xml.
                //   The layoutInflator returns a View, which is than added to the LinearLayout in activity_main.xml.
                //   For this, the LinearLayout has to have an id. (Usually, ViewGroup are not given an id because it is rarely needed.)
                layoutInflater.inflate(R.layout.note_fragment, null, false).apply {
                    //change note_piece,Title,Content
                    findViewById<TextView>(R.id.titleN).text = "\"${string.value?.title}\""//
                    findViewById<TextView>(R.id.contentN).text = " ${string.value?.content}"//

                    //add color sample: string.value?.important == true then yellow
                    if (string.value?.important == true) {
                        findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.noteFragment).setBackgroundColor(
                            Color.parseColor("#FFD700"))
                    }
                    if (string.value?.archived == true) {
                        findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.noteFragment).setBackgroundColor(
                            Color.parseColor("#FFBDBDBD"))
                    }
                    if (string.value?.important == false && string.value?.archived == false) {
                        findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.noteFragment).setBackgroundColor(
                            Color.parseColor("#FFFFFF"))
                    }

                    linearLayout.addView(this)

                    findViewById<Button>(R.id.aButton).setOnClickListener {//
                        string.value?.let { it1 -> myVM.updateNoteArchived(it1.id, archived = true) }
                    }

                    findViewById<Button>(R.id.dButton).setOnClickListener {//
                        string.value?.let { it1 -> myVM.removeNote(it1.id) }
                    }

                    setOnClickListener {
                        string.value?.let { it1 -> myVM.setNote(it1) }
                        findNavController().navigate(R.id.action_mainFragment_to_editFragment)
                    }

                }
            }
        }
    }

}