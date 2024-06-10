package mk.com.currencyconverter.util

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import mk.com.currencyconverter.db.DataManager
import java.util.Locale

class LanguageUtil {
    companion object {
        fun setLanguage(dataManager: DataManager, context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                dataManager.getLanguage().catch { e ->
                    e.printStackTrace()
                }.collect {
                    context.applyNewLocale(Locale(it.language))
                }
            }
        }

        fun Context.applyNewLocale(locale: Locale): Context {
            val config = this.resources.configuration
            val sysLocale = config.locales.get(0)
            if (sysLocale.language != locale.language) {
                Locale.setDefault(locale)
                config.setLocale(locale)
                resources.updateConfiguration(config, resources.displayMetrics)
            }
            return this
        }
    }
}