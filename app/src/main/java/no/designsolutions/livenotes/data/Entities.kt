package no.designsolutions.livenotes.data

import androidx.room.*
import java.io.File
import java.util.*

@Entity(tableName = "notebooks")
data class Notebook(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val createdDate: Int,
    val modifiedDate: Int,
    @ColumnInfo(name = "last_played_audio") val lastPlayed: LinkedAudioTimeStamp?,
    val size: Int,
)

@Entity(
    tableName = "notes", primaryKeys = ["notebook_id", "note_id"],
    indices = [Index(value = ["notebook_id", "note_id"])],
)
data class Note(
    @ColumnInfo(name = "notebook_id") val notebookId: Int,
    @ColumnInfo(name = "note_id") val noteId: Int,
    @ColumnInfo(name = "text") val text: String?,
    @ColumnInfo(name = "created_date") val createdDate: Date,
    @ColumnInfo(name = "modified_date") val modifiedDate: Date,
    @ColumnInfo(name = "timestamp") val timestamp: LinkedAudioTimeStamp?,
)

data class LinkedAudioTimeStamp(
    val time: Long?,
    val file: File?,
)

class TimestampConverters {

    @TypeConverter
    fun fromStringToTimestamp(value: String?): LinkedAudioTimeStamp {
        val stringArray = value?.split(" ")
        val time = stringArray!![0].toLong()
        val file = File("media/${stringArray!![1]}")
        return LinkedAudioTimeStamp(time, file)
    }

    @TypeConverter
    fun fromTimestampToString(timestamp: LinkedAudioTimeStamp?): String {
        return """${timestamp?.time.toString()} ${timestamp?.file?.canonicalPath}"""
    }

    @TypeConverter
    fun fromDateToLong(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromLongToDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }

}

@Dao
interface NotebooksDao {
    @Query("SELECT * FROM notebooks")
    fun getNotebooks(): List<Notebook>

    @Insert
    fun insertNotebook(notebook: Notebook)

    @Update
    fun updateNotebook(notebook: Notebook)

    @Delete
    fun deleteNotebook(notebook: Notebook)
}

@Dao
interface NotesDao {
    @Query("SELECT * FROM notes WHERE notebook_id = :notebookId")
    fun loadAllNotesInNotebook(notebookId: Int): Array<Note>

    @Insert
    fun insertNote(note: Note)

    @Update
    fun updateNote(note: Note)

}

