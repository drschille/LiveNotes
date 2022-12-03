package no.designsolutions.livenotes.data

import androidx.lifecycle.LiveData

class NotebooksRepository(private val notebooksDao: NotebooksDao) {

    val notebooks: List<Notebook> = notebooksDao.getNotebooks()

    suspend fun addNotebook(notebook: Notebook) {
        notebooksDao.insertNotebook(notebook)
    }

}