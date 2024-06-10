package mk.com.currencyconverter.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: HistoryRepository): ViewModel() {
    val history: LiveData<List<History>> = repository.allHistory.asLiveData()

    fun addTaskItem(history: History) = viewModelScope.launch {
        repository.insertHistory(history)
    }
}

class HistoryModelFactory(private val repository: HistoryRepository) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java))
            return HistoryModelFactory(repository) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}