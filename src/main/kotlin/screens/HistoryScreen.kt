import react.FC
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.span
import react.useState
import web.cssom.ClassName

private data class HistoryItem(
    val sortKey: String,
    val date: String,
    val type: String,
    val title: String,
    val subtitle: String,
    val status: String,
    val detail: String = "",
    val appointmentId: String? = null
)

val HistoryScreen = FC<AppScreenProps> { props ->
    val data    = props.data
    val s       = strings(data.lang)
    val vehicle = data.vehicleInfo
    val plate   = if (vehicle != null) "${vehicle.plateNumber}${vehicle.plateLetter}" else "—"

    var expandedIndex by useState<Int?>(null)
    var cancelledId   by useState<String?>(null)

    val items = buildList {
        data.appointments.forEach { appt ->
            add(HistoryItem(
                sortKey       = appt.date,
                date          = appt.date,
                type          = "test",
                title         = appt.serviceType,
                subtitle      = "${appt.vehicleCategory} · $plate",
                status        = appt.status,
                detail        = "${s.date}: ${appt.date}   ${s.time}: ${appt.time}   ${s.category}: ${appt.vehicleCategory}",
                appointmentId = appt.id
            ))
        }
        data.bundleRuns.forEach { run ->
            val st = if (run.passed) "passed" else "failed"
            add(HistoryItem(
                sortKey  = run.date,
                date     = run.date,
                type     = "bundle",
                title    = run.bundleName,
                subtitle = "${s.bundleInspection} · $plate",
                status   = st,
                detail   = "${run.results.count { it.passed }} ${s.ofItemsPassed} ${run.results.size} ${s.items} · ${run.date}"
            ))
        }
    }.sortedByDescending { it.sortKey }

    topBar(s.historyTitle)

    if (items.isEmpty()) {
        sectionCard {
            div {
                className = ClassName("empty-state")
                svgIcon(ICON_HISTORY, "empty-state-svg-lg")
                span { +s.noHistory }
            }
        }
        return@FC
    }

    items.forEachIndexed { index, item ->
        val isExpanded = expandedIndex == index
        sectionCard {
            div {
                className = ClassName("history-main-row")
                onClick = { expandedIndex = if (isExpanded) null else index }

                div {
                    className = ClassName("col")
                    div {
                        className = ClassName("row gap mb-1")
                        span {
                            className = ClassName(
                                "badge " + if (item.type == "test") "badge-test" else "badge-bundle"
                            )
                            +(if (item.type == "test") s.vehicleTest else s.bundle)
                        }
                    }
                    span { className = ClassName("bold"); +item.title }
                    span { className = ClassName("muted text-sm mt-1"); +item.subtitle }
                }

                div {
                    className = ClassName("col history-right")
                    val displayStatus = when (item.status.lowercase()) {
                        "upcoming" -> s.upcoming
                        "passed"   -> s.passed
                        "failed"   -> s.failed
                        else       -> item.status.replaceFirstChar { it.uppercase() }
                    }
                    span {
                        className = ClassName("status-badge status-${item.status.lowercase()}")
                        +displayStatus
                    }
                    span { className = ClassName("muted text-sm mt-1"); +item.date }
                    span { className = ClassName("expand-hint muted-light"); +(if (isExpanded) "▲" else "▼") }
                }
            }

            if (isExpanded) {
                div { className = ClassName("history-detail-divider") }
                div {
                    className = ClassName("history-detail")
                    span { className = ClassName("label-muted"); +s.details }
                    div { className = ClassName("text-sm mt-1 mb-2"); +item.detail }

                    if (item.type == "test" && item.status.lowercase() == "upcoming" && item.appointmentId != null) {
                        div {
                            className = ClassName("history-actions")

                            button {
                                className = ClassName("history-btn-cancel")
                                onClick = { e ->
                                    e.stopPropagation()
                                    val apptId = item.appointmentId
                                    val appt = data.appointments.find { it.id == apptId }
                                    props.update { d ->
                                        d.copy(
                                            appointments = d.appointments.filterNot { it.id == apptId },
                                            bookedSlots  = if (appt != null && appt.serviceType != "Normal Vehicle Test")
                                                d.bookedSlots.filterNot { it.date == appt.date && it.time == appt.time }
                                            else d.bookedSlots
                                        )
                                    }
                                    cancelledId   = apptId
                                    expandedIndex = null
                                }
                                +s.cancelAppointment
                            }

                            button {
                                className = ClassName("history-btn-reschedule")
                                onClick = { e ->
                                    e.stopPropagation()
                                    val apptId = item.appointmentId
                                    val appt = data.appointments.find { it.id == apptId }
                                    props.update { d ->
                                        d.copy(
                                            appointments = d.appointments.filterNot { it.id == apptId },
                                            bookedSlots  = if (appt != null && appt.serviceType != "Normal Vehicle Test")
                                                d.bookedSlots.filterNot { it.date == appt.date && it.time == appt.time }
                                            else d.bookedSlots
                                        )
                                    }
                                    props.go(Screen.VehicleTestBooking)
                                }
                                +s.reschedule
                            }
                        }
                    }

                    if (item.type == "bundle") {
                        div { className = ClassName("muted text-sm"); +s.inspectionCompleted }
                    }
                }
            }
        }
    }
}
