import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import react.FC
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.option
import react.dom.html.ReactHTML.select
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.span
import react.useState
import web.cssom.ClassName
import web.html.InputType

private val VehicleSetupForm = FC<VehicleSetupProps> { props ->
    val init = props.initialInfo
    val isEditing = init != null
    val s = strings(props.lang)
    var carName     by useState(init?.carName     ?: "")
    var carModel    by useState(init?.carModel    ?: "")
    var year        by useState((init?.year ?: 2024).toString())
    var plateNumber by useState(init?.plateNumber ?: "")
    var plateLetter by useState(init?.plateLetter ?: "")
    var emirate     by useState(init?.emirate     ?: "Dubai")

    if (isEditing) {
        topBar(title = s.editVehicle, onBack = props.onCancel)
    } else {
        div {
            className = ClassName("setup-header")
            div { className = ClassName("rta-logo"); +s.rtaCarCare }
            div { className = ClassName("setup-title"); +s.setupTitle }
            div { className = ClassName("setup-subtitle"); +s.setupSubtitle }
        }
    }

    sectionCard {
        div {
            className = ClassName("field")
            label { +s.carName }
            input {
                type = InputType.text
                value = carName
                placeholder = s.carNamePlaceholder
                onChange = { carName = (it.target as HTMLInputElement).value }
            }
        }
        div {
            className = ClassName("field")
            label { +s.carModel }
            input {
                type = InputType.text
                value = carModel
                placeholder = s.carModelPlaceholder
                onChange = { carModel = (it.target as HTMLInputElement).value }
            }
        }
        div {
            className = ClassName("field")
            label { +s.yearLabel }
            input {
                type = InputType.number
                value = year
                placeholder = "2024"
                onChange = { year = (it.target as HTMLInputElement).value }
            }
        }
    }

    sectionCard {
        div {
            className = ClassName("label-muted mb-2")
            +s.licensePlate
        }
        div {
            className = ClassName("field")
            label { +s.plateNumber }
            input {
                type = InputType.text
                value = plateNumber
                placeholder = s.plateNumberPlaceholder
                onChange = { plateNumber = (it.target as HTMLInputElement).value }
            }
        }
        div {
            className = ClassName("field")
            label { +s.plateLetter }
            input {
                type = InputType.text
                value = plateLetter
                placeholder = s.plateLetterPlaceholder
                onChange = { plateLetter = (it.target as HTMLInputElement).value.uppercase().take(2) }
            }
        }
        div {
            className = ClassName("field")
            label { +s.emirate }
            select {
                value = emirate
                onChange = { emirate = (it.target as HTMLSelectElement).value }
                EMIRATES.forEach { em ->
                    option { value = em; +em }
                }
            }
        }
    }

    primaryButton(
        if (init != null) s.saveChanges else s.saveVehicle,
        enabled = carName.isNotBlank() && plateNumber.isNotBlank()
    ) {
        props.onSave(VehicleInfo(
            carName = carName.trim(),
            carModel = carModel.trim(),
            year = year.toIntOrNull() ?: 2024,
            plateNumber = plateNumber.trim(),
            plateLetter = plateLetter.trim().uppercase(),
            emirate = emirate
        ))
    }

    if (props.onCancel != null) {
        div { className = ClassName("mt-2") }
        secondaryButton(s.cancel) { props.onCancel!!() }
    }
}

private val HomeDashboard = FC<AppScreenProps> { props ->
    var editMode by useState(false)

    val data    = props.data
    val s       = strings(data.lang)
    val vehicle = data.vehicleInfo

    if (vehicle == null) return@FC

    if (editMode) {
        VehicleSetupForm {
            key = "edit"
            initialInfo = vehicle
            lang = data.lang
            onSave = { info ->
                props.update { it.copy(vehicleInfo = info) }
                editMode = false
            }
            onCancel = { editMode = false }
        }
    } else {
        // ── Dashboard ─────────────────────────────────────────────────────

        div {
            className = ClassName("home-header")
            div {
                className = ClassName("col")
                div { className = ClassName("welcome-greeting"); +s.welcomeBack }
                div { className = ClassName("welcome-name"); +data.profile.name.ifBlank { s.driver } }
            }
            div {
                className = ClassName("home-header-right")
                button {
                    className = ClassName("lang-toggle-btn")
                    onClick = {
                        props.update { it.copy(lang = if (it.lang == "ar") "en" else "ar") }
                    }
                    +s.langToggle
                }
                div { className = ClassName("rta-logo"); +s.rta }
            }
        }

        // Vehicle card
        div {
            className = ClassName("vehicle-card")
            div {
                className = ClassName("vehicle-card-header")
                div {
                    className = ClassName("col")
                    div { className = ClassName("vehicle-card-name"); +vehicle.carName }
                    div { className = ClassName("vehicle-card-model"); +"${vehicle.carModel} · ${vehicle.year}" }
                }
                button {
                    className = ClassName("vehicle-card-edit")
                    onClick = { editMode = true }
                    +s.edit
                }
            }
            div {
                className = ClassName("vehicle-plate")
                span { className = ClassName("vehicle-plate-number"); +vehicle.plateNumber }
                span { className = ClassName("vehicle-plate-letter"); +vehicle.plateLetter }
            }
            div { className = ClassName("vehicle-card-emirate"); +vehicle.emirate }
        }

        // Stats
        val upcomingCount = data.appointments.count { it.status == "Upcoming" }
        div {
            className = ClassName("stat-grid")
            sectionCard {
                span { className = ClassName("label-muted"); +s.total }
                div { className = ClassName("text-xl mt-1"); +"${data.appointments.size}" }
                span { className = ClassName("text-sm muted"); +s.appointments }
            }
            sectionCard {
                span { className = ClassName("label-muted"); +s.upcoming.uppercase() }
                div { className = ClassName("text-xl mt-1 text-primary"); +"$upcomingCount" }
                span { className = ClassName("text-sm muted"); +s.scheduled }
            }
        }

        primaryButton(s.bookAppointmentBtn) { props.go(Screen.BookAppointment) }

        val recentAppts = data.appointments.take(2)
        val recentRuns  = data.bundleRuns.take(1)

        span { className = ClassName("label-muted mt-3"); +s.recentActivity }
        div { className = ClassName("mt-2") }

        if (recentAppts.isEmpty() && recentRuns.isEmpty()) {
            sectionCard {
                div {
                    className = ClassName("empty-state")
                    svgIcon(ICON_CLIPBOARD, "empty-state-svg")
                    span { +s.noActivity }
                }
            }
        } else {
            recentAppts.forEach { appt ->
                sectionCard {
                    div {
                        className = ClassName("row")
                        div {
                            className = ClassName("col")
                            div {
                                className = ClassName("row gap")
                                span { className = ClassName("badge badge-test"); +s.vehicleTest }
                                span { className = ClassName("bold text-sm"); +appt.serviceType }
                            }
                            span { className = ClassName("muted text-sm mt-1"); +"${appt.date} · ${appt.time}" }
                        }
                        span { className = ClassName("status-badge status-${appt.status.lowercase()}"); +appt.status }
                    }
                }
            }
            recentRuns.forEach { run ->
                sectionCard {
                    div {
                        className = ClassName("row")
                        div {
                            className = ClassName("col")
                            div {
                                className = ClassName("row gap")
                                span { className = ClassName("badge badge-bundle"); +s.bundle }
                                span { className = ClassName("bold text-sm"); +run.bundleName }
                            }
                            span { className = ClassName("muted text-sm mt-1"); +run.date }
                        }
                        val st = if (run.passed) "passed" else "failed"
                        span { className = ClassName("status-badge status-$st"); +(if (run.passed) s.passed else s.failed) }
                    }
                }
            }
        }
    }
}

val HomeScreen = FC<AppScreenProps> { props ->
    val vehicleInfo = props.data.vehicleInfo
    val s = strings(props.data.lang)

    if (vehicleInfo == null) {
        VehicleSetupForm {
            key = "new"
            initialInfo = null
            lang = props.data.lang
            onSave = { info -> props.update { it.copy(vehicleInfo = info) } }
            onCancel = null
        }
    } else {
        HomeDashboard {
            data = props.data
            update = props.update
            go = props.go
            back = props.back
        }
    }
}
