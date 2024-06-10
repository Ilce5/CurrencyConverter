package mk.com.currencyconverter.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel: ViewModel() {
    var isInitialLanguageAdded = MutableLiveData<Boolean>()
    var initialLanguage = MutableLiveData<String>()

    fun addInitialLanguage(newLanguage: String) {
        isInitialLanguageAdded.value = true
        initialLanguage.value = newLanguage
    }
}