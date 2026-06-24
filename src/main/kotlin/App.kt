import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.useEffect
import react.useState
import web.cssom.ClassName
import web.dom.document

val App = FC<Props> {
    var data by useState(loadAppData())
    var stack by useState<List<Screen>>(listOf(Screen.Home))
    val current = stack.last()

    val update: ((AppData) -> AppData) -> Unit = { transform ->
        val next = transform(data)
        saveAppData(next)
        data = next
    }
    val go: (Screen) -> Unit = { screen -> stack = stack + screen }
    val back: () -> Unit = { if (stack.size > 1) stack = stack.dropLast(1) }
    val goRoot: (Screen) -> Unit = { screen -> stack = listOf(screen) }

    useEffect(data.lang) {
        document.documentElement?.setAttribute("dir", if (data.lang == "ar") "rtl" else "ltr")
        document.documentElement?.setAttribute("lang", data.lang)
    }

    fun AppScreenProps.wire() {
        this.data = data
        this.update = update
        this.go = go
        this.back = back
    }

    div {
        className = ClassName("app-shell")
        div {
            className = ClassName("app-body")
            div {
                className = ClassName("screen-wrapper")
                key = current.toString()
                when (current) {
                    Screen.Home             -> HomeScreen { wire() }
                    Screen.BookAppointment  -> BookAppointmentScreen { wire() }
                    Screen.VehicleTestBooking -> VehicleTestBookingScreen { wire() }
                    Screen.BundleInspection -> BundleScreen { wire() }
                    is Screen.RunBundle     -> RunBundleScreen {
                        this.data = data
                        this.update = update
                        this.go = go
                        this.back = back
                        this.bundleId = current.bundleId
                    }
                    Screen.History          -> HistoryScreen { wire() }
                    Screen.Settings         -> SettingsScreen { wire() }
                    Screen.Profile          -> ProfileScreen { wire() }
                    Screen.HelpCenter       -> HelpCenterScreen { wire() }
                }
            }
        }
        bottomNav(current = current.bottomTab(), onSelect = goRoot, lang = data.lang)
    }
}
