import kotlinx.browser.window
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val STORAGE_KEY = "carcare_rta_data"

private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

fun loadAppData(): AppData {
    val raw = try {
        window.localStorage.getItem(STORAGE_KEY)
    } catch (e: Throwable) {
        null
    } ?: return AppData()

    return try {
        json.decodeFromString<AppData>(raw)
    } catch (e: Throwable) {
        AppData()
    }
}

fun saveAppData(data: AppData) {
    try {
        window.localStorage.setItem(STORAGE_KEY, json.encodeToString(data))
    } catch (e: Throwable) {
        // Storage unavailable — fail silently
    }
}
