import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import react.FC
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.option
import react.dom.html.ReactHTML.select
import react.dom.html.ReactHTML.span
import react.useState
import web.cssom.ClassName
import web.html.InputType

val VehicleTestBookingScreen = FC<AppScreenProps> { props ->
    val data = props.data
    val s = strings(data.lang)
    val vehicle = data.vehicleInfo

    var step             by useState(1)
    var vehicleCategory  by useState("Passenger Car")
    var serviceType      by useState("Normal Vehicle Test")
    var selectedDate     by useState(today().plusDays(1).isoStr())
    var selectedTime     by useState<String?>(null)
    var confirmed        by useState(false)

    topBar(s.bookVehicleTest, onBack = props.back)

    if (vehicle == null) {
        sectionCard {
            div { className = ClassName("empty-state")
                svgIcon(ICON_CAR, "empty-state-svg")
                span { +s.setupVehicleFirst }
            }
        }
        primaryButton(s.goToHome) { props.go(Screen.Home) }
        return@FC
    }

    if (confirmed) {
        div {
            className = ClassName("success-screen")
            svgIcon(ICON_CHECK_CIRCLE, "success-icon-svg")
            div { className = ClassName("success-title"); +s.bookingConfirmed }
            div {
                className = ClassName("success-subtitle")
                +s.bookingConfirmedSub(serviceType, selectedDate, selectedTime ?: "")
            }
            sectionCard {
                div { className = ClassName("confirm-row")
                    span { className = ClassName("confirm-row-label"); +s.vehicle }
                    span { className = ClassName("confirm-row-value"); +"${vehicle.carName} · ${vehicle.plateNumber}${vehicle.plateLetter}" }
                }
                div { className = ClassName("confirm-row")
                    span { className = ClassName("confirm-row-label"); +s.category }
                    span { className = ClassName("confirm-row-value"); +vehicleCategory }
                }
                div { className = ClassName("confirm-row")
                    span { className = ClassName("confirm-row-label"); +s.service }
                    span { className = ClassName("confirm-row-value"); +serviceType }
                }
                div { className = ClassName("confirm-row")
                    span { className = ClassName("confirm-row-label"); +"${s.date} & ${s.time}" }
                    span { className = ClassName("confirm-row-value"); +"$selectedDate · ${selectedTime ?: ""}" }
                }
            }
            primaryButton(s.backToHome) { props.goRoot(Screen.Home) }
        }
        return@FC
    }

    val stepLabels = listOf(s.stepVehicleType, s.stepServiceType, s.stepDateTime, s.stepConfirm)
    stepIndicator(step = step, total = 4, label = "${s.stepOf(step)} — ${stepLabels[step - 1]}")

    when (step) {

        1 -> {
            span { className = ClassName("label-muted mb-2"); +s.selectVehicleType }
            div { className = ClassName("mt-2") }
            VEHICLE_CATEGORIES.forEach { cat ->
                div {
                    className = ClassName("checklist-row" + if (cat == vehicleCategory) " selected" else "")
                    onClick = { vehicleCategory = cat }
                    span { className = ClassName("bold"); +cat }
                    span {
                        className = ClassName(if (cat == vehicleCategory) "text-primary bold" else "muted")
                        +(if (cat == vehicleCategory) "●" else "○")
                    }
                }
            }
            div { className = ClassName("mt-2") }
            primaryButton(s.next) { step = 2 }
        }

        2 -> {
            span { className = ClassName("label-muted mb-2"); +s.selectServiceType }
            div { className = ClassName("mt-2") }
            RTA_SERVICE_TYPES.forEach { type ->
                div {
                    className = ClassName("checklist-row" + if (type == serviceType) " selected" else "")
                    onClick = { serviceType = type; selectedTime = null }
                    div {
                        className = ClassName("col")
                        span { className = ClassName("bold"); +type }
                        span {
                            className = ClassName("muted text-sm mt-1")
                            +when (type) {
                                "Normal Vehicle Test"  -> s.normalTestDesc
                                "Registration Test"    -> s.registrationTestDesc
                                "Renewal Test"         -> s.renewalTestDesc
                                else -> ""
                            }
                        }
                    }
                    span {
                        className = ClassName(if (type == serviceType) "text-primary bold" else "muted")
                        +(if (type == serviceType) "●" else "○")
                    }
                }
            }
            div { className = ClassName("btn-row") }
            div {
                className = ClassName("btn-row")
                secondaryButton(s.back) { step = 1 }
                primaryButton(s.next) { step = 3 }
            }
        }

        3 -> {
            if (serviceType == "Normal Vehicle Test") {
                span { className = ClassName("label-muted mb-2"); +s.chooseDate }
                div { className = ClassName("mt-2") }
                sectionCard {
                    div {
                        className = ClassName("field")
                        label { +s.date }
                        input {
                            type = InputType.date
                            value = selectedDate
                            onChange = {
                                selectedDate = (it.target as HTMLInputElement).value
                                selectedTime = null
                            }
                        }
                    }
                }
                span { className = ClassName("label-muted mt-2 mb-2"); +s.chooseTime }
                div { className = ClassName("mt-2") }
                div {
                    className = ClassName("chip-row")
                    generateHalfHourSlots().forEach { time ->
                        chip(time, active = selectedTime == time) { selectedTime = time }
                    }
                }
            } else {
                span { className = ClassName("label-muted mb-2"); +s.chooseDate }
                div { className = ClassName("mt-2") }
                div {
                    className = ClassName("calendar-scroll")
                    nextTwoWeeks().forEach { date ->
                        val iso = date.isoStr()
                        button {
                            className = ClassName("calendar-day-btn" + if (iso == selectedDate) " cal-selected" else "")
                            onClick = { selectedDate = iso; selectedTime = null }
                            div { className = ClassName("day-name"); +date.dayName() }
                            div { className = ClassName("day-num"); +date.shortDate() }
                        }
                    }
                }
                span { className = ClassName("label-muted mb-2"); +s.availableSlots }
                div { className = ClassName("mt-2") }
                div {
                    className = ClassName("time-slots-grid")
                    FIXED_SLOT_TIMES.forEach { time ->
                        val isBooked = data.bookedSlots.any { it.date == selectedDate && it.time == time }
                        val isSel    = selectedTime == time
                        button {
                            className = ClassName(
                                "calendar-slot" +
                                when {
                                    isSel    -> " slot-selected"
                                    isBooked -> " slot-booked"
                                    else     -> ""
                                }
                            )
                            disabled = isBooked
                            onClick = { if (!isBooked) selectedTime = time }
                            +time
                        }
                    }
                }
                if (data.bookedSlots.any { it.date == selectedDate }) {
                    div { className = ClassName("muted text-sm mt-1"); +s.greySlots }
                }
            }
            div { className = ClassName("mt-2") }
            div {
                className = ClassName("btn-row")
                secondaryButton(s.back) { step = 2; selectedTime = null }
                primaryButton(s.next, enabled = selectedTime != null) { step = 4 }
            }
        }

        4 -> {
            span { className = ClassName("label-muted mb-2"); +s.confirmBookingTitle }
            div { className = ClassName("mt-2") }

            sectionCard {
                div { className = ClassName("label-muted mb-2"); +s.vehicle.uppercase() }
                div { className = ClassName("bold"); +"${vehicle.carName} · ${vehicle.carModel}" }
                div { className = ClassName("muted text-sm mt-1"); +"${vehicle.plateNumber} ${vehicle.plateLetter} — ${vehicle.emirate}" }
            }

            sectionCard {
                div { className = ClassName("confirm-row")
                    span { className = ClassName("confirm-row-label"); +s.category }
                    span { className = ClassName("confirm-row-value"); +vehicleCategory }
                }
                div { className = ClassName("confirm-row")
                    span { className = ClassName("confirm-row-label"); +s.service }
                    span { className = ClassName("confirm-row-value"); +serviceType }
                }
                div { className = ClassName("confirm-row")
                    span { className = ClassName("confirm-row-label"); +s.date }
                    span { className = ClassName("confirm-row-value"); +selectedDate }
                }
                div { className = ClassName("confirm-row")
                    span { className = ClassName("confirm-row-label"); +s.time }
                    span { className = ClassName("confirm-row-value"); +(selectedTime ?: "—") }
                }
            }

            div {
                className = ClassName("btn-row")
                secondaryButton(s.back) { step = 3 }
                primaryButton(s.confirmBookingBtn) {
                    val sl = selectedTime ?: return@primaryButton
                    val appt = RtaAppointment(
                        id = newId(),
                        vehicleCategory = vehicleCategory,
                        serviceType = serviceType,
                        date = selectedDate,
                        time = sl,
                        status = "Upcoming"
                    )
                    val addSlot = serviceType != "Normal Vehicle Test"
                    props.update {
                        it.copy(
                            appointments = listOf(appt) + it.appointments,
                            bookedSlots = if (addSlot) it.bookedSlots + BookedSlot(selectedDate, sl) else it.bookedSlots
                        )
                    }
                    confirmed = true
                }
            }
        }
    }
}

private fun AppScreenProps.goRoot(screen: Screen) { go(screen) }
