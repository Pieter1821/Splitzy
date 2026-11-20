package app.splitzy.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Choices the user expects Splitzy to remember between sessions. */
interface UserPreferences {
    val currencyCode: Flow<String?>
    suspend fun setCurrencyCode(code: String)
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class DataStoreUserPreferences(context: Context) : UserPreferences {

    private val store = context.applicationContext.dataStore

    override val currencyCode: Flow<String?> = store.data.map { it[CURRENCY] }

    override suspend fun setCurrencyCode(code: String) {
        store.edit { it[CURRENCY] = code }
    }

    private companion object {
        val CURRENCY = stringPreferencesKey("currency_code")
    }
}
