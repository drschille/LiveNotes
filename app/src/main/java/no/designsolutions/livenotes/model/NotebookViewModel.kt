package no.designsolutions.livenotes.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.designsolutions.livenotes.data.AppDatabase
import no.designsolutions.livenotes.data.Notebook
import no.designsolutions.livenotes.data.NotebooksRepository


class NotebookViewModel(application: Application): AndroidViewModel(application) {

    private val notebooks: List<Notebook>
    private val repository: NotebooksRepository
    private var notebooksLiveData: LiveData<List<Notebook>>

    init {
        val notebooksDao = AppDatabase.getDatabase(application).notebooksDao()
        repository = NotebooksRepository(notebooksDao)
        notebooks = repository.notebooks
        notebooksLiveData = MutableLiveData(notebooks)
    }

    fun insertNotebook(notebook: Notebook) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addNotebook(notebook)
        }
    }

    fun getNotebooks() : LiveData<List<Notebook>> {
        return notebooksLiveData
    }

}