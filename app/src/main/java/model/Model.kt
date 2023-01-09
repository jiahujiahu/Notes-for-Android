package ui.assignments.a4notes.model

import android.icu.util.GregorianCalendar
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableArrayList
import kotlin.math.sign

class Model {

    /**
     *  A Note. Observe to receive notifications about changes of its properties.
     *  @param id unique id of the note
     *  @param title title of the note
     *  @param content content or body of the note
     *  @param important indicates if the note is important
     *  @param archived indicates if the note is archived
     */
    data class ModelNote(val id: Int, var title: String, var content: String, var important : Boolean = false, var archived : Boolean = false) : BaseObservable() {
        val timestamp = GregorianCalendar.getInstance().time.time
    }

    // Counter for note ids
    private var idCounter = 0

    // Returns a new unique note id
    private fun generateID(): Int {
        return idCounter++
    }

    /**
     * List of all notes. Observe to receive notifications about changes of its content.
     */
    val notes = ObservableArrayList<ModelNote>()

    /**
     * Attempts to add a non-archived note to the model. Observe [notes] to receive notifications about the success of this operation.
     * @param title title of the note
     * @param content content / body of the note
     * @param important indicates if the note is important
     * @see notes
     */
    fun addNote(title: String, content: String, important: Boolean = false) {
        ModelNote(generateID(), title, content, important).apply {
            notes.add(this)
        }
    }

    /**
     * Attempts to remove a note from the model. Observe [notes] to receive notifications about the success of this operation.
     * @param id id of the note to be removed
     * @see notes
     */
    fun removeNote(id: Int) {
        notes.find { it.id == id }?.apply {
            if (important.not())
                notes.remove(this)
        }
    }

    /**
     * Attempts to update the title of a note. Observe the [ModelNote] to receive notifications about the success of this operation.
     * @param id id of the note to be updated
     * @param title new title of the note
     * @see ModelNote
     */
    fun updateNoteTitle(id: Int, title: String) {
        notes.find { it.id == id }.apply {
            this?.title = title
            this?.notifyPropertyChanged(id)
        }
    }

    /**
     * Attempts to update the content / body of a note. Observe the [ModelNote] to receive notifications about the success of this operation.
     * @param id id of the note to be updated
     * @param content new content / body of the note
     * @see ModelNote
     */
    fun updateNoteContent(id: Int, content: String) {
        notes.find { it.id == id }.apply {
            this?.content = content
            this?.notifyPropertyChanged(id)
        }
    }

    /**
     * Attempts to update the archived-state of a note. Observe the [ModelNote] to receive notifications about the success of this operation.
     * @param id id of the note to be updated
     * @param archived new archived-state of the note
     * @see ModelNote
     */
    fun updateNoteArchived(id: Int, archived: Boolean) {
        notes.find { it.id == id }.apply {
            this?.important = if (archived) false else this!!.important
            this?.archived = archived
            this?.notifyPropertyChanged(id)
        }
    }

    /**
     * Attempts to update the important-state of a note. Observe the [ModelNote] to receive notifications about the success of this operation.
     * @param id id of the note to be updated
     * @param important new important-state of the note
     * @see ModelNote
     */
    fun updateNoteImportant(id: Int, important: Boolean) {
        notes.find { it.id == id }.apply {
            this?.important = important
            this?.archived = if (important) false else this!!.archived
            this?.notifyPropertyChanged(id)
        }
    }

    /**
     * Compares two notes. The order is: important notes -> "normal" notes -> archived notes; within each group, newer notes come first.
     * @param idA id of the first note
     * @param idB id of the second note
     * @return -1 if note A comes before B, 1 if B comes before A, and 0 if A and B are considered equal.
     */
    fun compareNotes(idA: Int, idB: Int) : Int {
        val a = notes.find { it.id == idA }
        val b = notes.find { it.id == idB }
        return if (b == null) -1
               else if (a == null) 1
               else {
                   if (a.important and b.important.not()) -1
                   else if (a.important.not() and b.important) 1
                   else if (a.archived.not() and b.archived) -1
                   else if (a.archived and b.archived.not()) 1
                   else sign((b.timestamp - a.timestamp).toDouble()).toInt()
        }
    }

    // Helper function that populates the Model with some notes. The thread sleeps for 10 ms after each addition, so
    //   that all notes have different time stamps. (This makes explaining sorting behaviour more obvious.)
    fun addSomeNotes() {
        notes.add(ModelNote(generateID(), "First Note", "This is the oldest, yet, IMPORTANT (!) note", important = true, archived = false))
        Thread.sleep(10L)
        notes.add(ModelNote(generateID(), "Second Note","This is an archived note", important = false, archived = true))
        Thread.sleep(10L)
        notes.add(ModelNote(generateID(), "Third Note","This is a pretty boring note; not a lot to see here...", important = false, archived = false))
        Thread.sleep(10L)
        notes.add(ModelNote(generateID(), "Fourth Note","This is a note with a reasonably long content / body. Yep, there are quite a few characters in this text; enough so that this note has to be displayed over multiple lines on the Edit-screen.", important = false, archived = false))
        Thread.sleep(10L)
        notes.add(ModelNote(generateID(), "This note (5) has an excitingly long title: W00t, W00t!","But not a lot of content.", important = false, archived = true))
        Thread.sleep(10L)
        notes.add(ModelNote(generateID(), "SIXTH NOTE","THIS. IS. IMPORTANT!!!", important = true, archived = false))
    }
}