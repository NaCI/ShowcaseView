package naci.showcaseview

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.WorkerThread

class PrefsManager(private val context: Context, private val sequenceID: String) {
    private val prefs: Lazy<SharedPreferences> = lazy {
        // Lazy to prevent IO access to main thread.
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    companion object {
        private const val PREFS_NAME = "showcase_view_sequence_preferences"
    }

    @WorkerThread
    fun isDisplayed(): Boolean {
        return prefs.value.getBoolean(sequenceID, false)
    }

    fun setDisplayed() {
        prefs.value.edit().putBoolean(sequenceID, true).apply()
    }

    fun reset() {
        prefs.value.edit().putBoolean(sequenceID, false).apply()
    }

    fun resetAll() {
        prefs.value.edit().clear().apply()
    }
}