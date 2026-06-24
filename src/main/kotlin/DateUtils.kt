import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime

fun today(): LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

fun LocalDate.plusDays(days: Int): LocalDate = LocalDate.fromEpochDays(this.toEpochDays() + days)

fun LocalDate.isoStr(): String =
    "$year-${monthNumber.toString().padStart(2, '0')}-${dayOfMonth.toString().padStart(2, '0')}"

fun LocalDate.display(): String {
    val d = dayOfMonth.toString().padStart(2, '0')
    val m = monthNumber.toString().padStart(2, '0')
    return "$d/$m/$year"
}

fun LocalDate.dayName(): String = when (dayOfWeek) {
    DayOfWeek.MONDAY    -> "Mon"
    DayOfWeek.TUESDAY   -> "Tue"
    DayOfWeek.WEDNESDAY -> "Wed"
    DayOfWeek.THURSDAY  -> "Thu"
    DayOfWeek.FRIDAY    -> "Fri"
    DayOfWeek.SATURDAY  -> "Sat"
    DayOfWeek.SUNDAY    -> "Sun"
    else -> "?"
}

fun LocalDate.shortDate(): String = "${dayOfMonth}/${monthNumber}"

fun nextTwoWeeks(): List<LocalDate> = (1..14).map { today().plusDays(it) }

fun generateHalfHourSlots(): List<String> {
    val slots = mutableListOf<String>()
    for (hour in 8..22) {
        slots.add("${hour.toString().padStart(2,'0')}:00")
        slots.add("${hour.toString().padStart(2,'0')}:30")
    }
    slots.add("23:00")
    return slots
}

fun isOlderThanDays(isoDate: String, days: Int): Boolean {
    val date = runCatching { LocalDate.parse(isoDate) }.getOrNull() ?: return false
    return date.daysUntil(today()) > days
}
