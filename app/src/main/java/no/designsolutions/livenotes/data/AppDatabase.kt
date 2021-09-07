package no.designsolutions.livenotes.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = arrayOf(Notebook::class, Note::class),
    version = 1
)
@TypeConverters(TimestampConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notebooksDao(): NotebooksDao
    abstract fun notesDao(): NotesDao
}