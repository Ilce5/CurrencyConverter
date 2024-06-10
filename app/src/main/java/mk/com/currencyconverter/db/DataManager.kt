package mk.com.currencyconverter.db

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class DataManager(val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "DATASTORE")

        val LANGUAGE = stringPreferencesKey("language")
    }

    suspend fun saveLanguage(languageDb: Language) {
        context.dataStore.edit {
            it[LANGUAGE] = languageDb.language
        }
    }

    fun getLanguage() = context.dataStore.data.map {
        Language(
            language = it[LANGUAGE] ?: "MK"
        )
    }
}