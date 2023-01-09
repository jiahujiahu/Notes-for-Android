package ui.assignments.a4notes.viewmodel

import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import ui.assignments.a4notes.model.Model

class NotesViewModel : ViewModel() {

    /**
     *  The representation of a [Model.ModelNote] in the ViewModel. Only [VMNote]s are exposed to the View.
     */
    data class VMNote(val id: Int, var title: String, var content: String, var important : Boolean = false, var archived : Boolean = false)  {
        constructor(note : Model.ModelNote) : this(note.id, note.title, note.content, note.important, note.archived)
    }

    // model
    private val model = Model()
    private var note = VMNote(-1,"temp title","temp content")

    // list of all currently visible / displayed notes
    private val notes = MutableLiveData<MutableList<MutableLiveData<VMNote>>>(mutableListOf())

    // UI state indicating if archived notes should be displayed
    private val viewArchived = MutableLiveData(false)

    companion object {
        val Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel>
            create(modelClass: Class<T>, extras: CreationExtras): T {
                return NotesViewModel() as T
            }
        }
    }

    init {
        // noteChangeCallback responds to all changes *within* a ModelNote:
        //   onPropertyChanged received notifications from ModelNote if its state has changed, and updates the
        //   corresponding MVNote accordingly. The VMNote is wrapped in MutableLiveData and exposed as LiveData to the
        //   View.
        val noteChangeCallback = object : OnPropertyChangedCallback() {
            override fun onPropertyChanged(modelNote: Observable?, propertyId: Int) {
                notes.value?.find { it.value?.id == propertyId }?.apply {// find MVNote in notes
                    modelNote as Model.ModelNote
                    this.value = VMNote(modelNote.id, modelNote.title, modelNote.content, modelNote.important, modelNote.archived)
                    if (modelNote.archived && viewArchived.value == false) { // if note is archived and archived notes are not showing, remove from notes
                        notes.value = notes.value.apply {
                            this?.removeIf { note -> note.value?.id == value?.id }
                        }
                    } else { // if not, apply changes from ModelNote to MVNote
                        notes.value = notes.value.apply {
                            this?.sortWith { a, b -> model.compareNotes(a?.value!!.id, b?.value!!.id) }
                        }
                    }
                }
            }
        }

        // addOnListChangedCallback responds to all changes of the list of ModelNotes:
        //   onItemRangeInserted is called if a ModelNote is successfully added to the Model
        //   onItemRangeRemoved is called if a ModelNote is successfully removed from the Model
        model.notes.addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableArrayList<Model.ModelNote>>() {
            override fun onChanged(sender: ObservableArrayList<Model.ModelNote>?) {  }
            override fun onItemRangeChanged(sender: ObservableArrayList<Model.ModelNote>?, positionStart: Int, itemCount: Int) {  }
            override fun onItemRangeInserted(sender: ObservableArrayList<Model.ModelNote>?, positionStart: Int, itemCount: Int) {
                val addedNote = sender?.get(positionStart) // get new ModelNote
                addedNote?.addOnPropertyChangedCallback(noteChangeCallback) // add listener to ModelNote
                if (addedNote?.archived?.not() == true) { //
                    notes.value = notes.value.apply {
                        this?.add(MutableLiveData(VMNote(addedNote)))
                        this?.sortWith { p0, p1 -> model.compareNotes(p0?.value!!.id, p1?.value!!.id) }
                    }
                }
            }
            override fun onItemRangeMoved(sender: ObservableArrayList<Model.ModelNote>?, fromPosition: Int, toPosition: Int, itemCount: Int) {  }
            override fun onItemRangeRemoved(sender: ObservableArrayList<Model.ModelNote>?, positionStart: Int, itemCount: Int) {
                notes.value = notes.value!!.apply {
                    removeIf { mvnote -> (sender?.find { modelnote -> modelnote.id == mvnote.value!!.id }) == null }
                }
            }
        })

        model.addSomeNotes()
    }


    /**
     * Returns a read-only version of [notes], which stores read-only observables of [VMNote]. Observe to receive notifications about changes to the list.
     * @see notes
     */
    fun getNotes() : LiveData<MutableList<LiveData<VMNote>>> {
        return notes as LiveData<MutableList<LiveData<VMNote>>>
    }

    fun getNote() : VMNote {
        return note
    }

    fun setNote(newNote : VMNote) {
        note = newNote
    }
    // The following methods are missing:
    // * Functions to get / set the value of viewArchived.
    // * Functions to forward requests from the View to the Model.

    fun get() : Boolean? {
        return viewArchived.value
    }

    fun set(archived: Boolean) {
        viewArchived.value = archived
        notes.value?.clear()
        if(viewArchived.value!!){
            for (n in model.notes){
                var vm = VMNote(n.id,n.title,n.content,n.important,n.archived)
                notes.value?.add(MutableLiveData(vm))
            }
        }else{
            for (n in model.notes){
                if(n.archived == false){
                    var vm = VMNote(n.id,n.title,n.content,n.important,n.archived)
                    notes.value?.add(MutableLiveData(vm))
                }
            }
        }
        notes.value = notes.value.apply {//resort
            this?.sortWith { a, b -> model.compareNotes(a?.value!!.id, b?.value!!.id) }
        }
    }

    fun addNote(title: String, content: String, important: Boolean = false) {
        model.addNote(title, content, important)
    }

    fun removeNote(id: Int) {
        model.removeNote(id)
    }

    fun updateNoteArchived(id: Int, archived: Boolean) {
        model.updateNoteArchived(id, archived)
    }

    fun updateNoteImportant(id: Int, important: Boolean){
        model.updateNoteImportant(id,important)
    }

    fun updateNoteTitle(id: Int, title: String){
        model.updateNoteTitle(id,title)
    }

    fun updateNoteContent(id: Int, content: String){
        model.updateNoteContent(id,content)
    }


}
