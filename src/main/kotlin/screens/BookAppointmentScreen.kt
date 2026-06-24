import react.FC
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.span
import web.cssom.ClassName

val BookAppointmentScreen = FC<AppScreenProps> { props ->
    val data = props.data
    val s = strings(data.lang)

    topBar(s.bookAppointmentTitle)

    div {
        className = ClassName("muted text-sm mb-2")
        +s.chooseAppointmentType
    }

    div {
        className = ClassName("choice-grid mt-2")

        button {
            className = ClassName("choice-card")
            onClick = { props.go(Screen.VehicleTestBooking) }
            svgIcon(ICON_SHIELD_OK, "choice-svg-icon")
            div { className = ClassName("choice-title"); +s.runVehicleTest }
            div { className = ClassName("choice-subtitle"); +s.runVehicleTestSub }
        }

        button {
            className = ClassName("choice-card")
            onClick = { props.go(Screen.BundleInspection) }
            svgIcon(ICON_LIST, "choice-svg-icon")
            div { className = ClassName("choice-title"); +s.buildBundle }
            div { className = ClassName("choice-subtitle"); +s.buildBundleSub }
        }
    }

    val upcoming = data.appointments.filter { it.status == "Upcoming" }
    if (upcoming.isNotEmpty()) {
        span { className = ClassName("label-muted mt-3"); +s.upcomingAppointments }
        div { className = ClassName("mt-2") }
        upcoming.take(3).forEach { appt ->
            sectionCard {
                div {
                    className = ClassName("row")
                    div {
                        className = ClassName("col")
                        span { className = ClassName("bold"); +appt.serviceType }
                        span { className = ClassName("muted text-sm mt-1"); +"${appt.vehicleCategory} · ${appt.date} · ${appt.time}" }
                    }
                    span { className = ClassName("status-badge status-upcoming"); +s.upcoming }
                }
            }
        }
    }

    val bundles = data.bundles
    if (bundles.isNotEmpty()) {
        span { className = ClassName("label-muted mt-3"); +s.savedBundles }
        div { className = ClassName("mt-2") }
        bundles.take(3).forEach { bundle ->
            sectionCard {
                div {
                    className = ClassName("row")
                    div {
                        className = ClassName("col")
                        span { className = ClassName("bold"); +bundle.name }
                        span { className = ClassName("muted text-sm mt-1"); +"${bundle.items.size} ${s.items}" }
                    }
                    button {
                        className = ClassName("btn-sm-primary")
                        onClick = { props.go(Screen.RunBundle(bundle.id)) }
                        +s.run
                    }
                }
            }
        }
    }
}
