package mk.com.currencyconverter.db

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {
    val allHistory: Flow<List<History>> = historyDao.allHistory()

    @WorkerThread
    suspend fun insertHistory(history: History)
    {
        historyDao.insertHistory(history)
    }
}