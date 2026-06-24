sealed class Screen {
    data object Home : Screen()
    data object BookAppointment : Screen()
    data object VehicleTestBooking : Screen()
    data object BundleInspection : Screen()
    data class RunBundle(val bundleId: String) : Screen()
    data object History : Screen()
    data object Settings : Screen()
    data object Profile : Screen()
    data object HelpCenter : Screen()
}

fun Screen.bottomTab(): Screen = when (this) {
    Screen.History -> Screen.History
    Screen.Settings, Screen.Profile, Screen.HelpCenter -> Screen.Settings
    else -> Screen.Home
}

external interface AppScreenProps : react.Props {
    var data: AppData
    var update: ((AppData) -> AppData) -> Unit
    var go: (Screen) -> Unit
    var back: () -> Unit
}

external interface RunBundleProps : react.Props {
    var data: AppData
    var update: ((AppData) -> AppData) -> Unit
    var go: (Screen) -> Unit
    var back: () -> Unit
    var bundleId: String
}

external interface VehicleSetupProps : react.Props {
    var initialInfo: VehicleInfo?
    var onSave: (VehicleInfo) -> Unit
    var onCancel: (() -> Unit)?
    var lang: String
}
