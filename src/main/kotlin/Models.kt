import kotlinx.serialization.Serializable

@Serializable
enum class Zone(val label: String) {
    HOOD("Hood"),
    BODY("Body"),
    WHEELS("Wheels"),
    BRAKES("Brakes"),
    LIGHTS("Lights"),
    WIPERS("Wipers")
}

@Serializable
data class VehicleInfo(
    val carName: String = "",
    val carModel: String = "",
    val year: Int = 2020,
    val plateNumber: String = "",
    val plateLetter: String = "",
    val emirate: String = "Dubai"
)

@Serializable
data class BundleItem(
    val description: String,
    val zone: Zone
)

@Serializable
data class InspectionBundle(
    val id: String,
    val name: String,
    val items: List<BundleItem>
)

@Serializable
data class BundleItemResult(
    val description: String,
    val zone: Zone,
    val passed: Boolean
)

@Serializable
data class BundleRun(
    val id: String,
    val bundleName: String,
    val date: String,
    val results: List<BundleItemResult>
) {
    val passed: Boolean get() = results.isNotEmpty() && results.all { it.passed }
}

@Serializable
data class RtaAppointment(
    val id: String,
    val vehicleCategory: String,
    val serviceType: String,
    val date: String,
    val time: String,
    val status: String = "Upcoming"
)

@Serializable
data class BookedSlot(
    val date: String,
    val time: String
)

@Serializable
data class UserProfile(
    val name: String = "Driver"
)

@Serializable
data class AppData(
    val vehicleInfo: VehicleInfo? = null,
    val profile: UserProfile = UserProfile(),
    val bundles: List<InspectionBundle> = emptyList(),
    val appointments: List<RtaAppointment> = emptyList(),
    val bundleRuns: List<BundleRun> = emptyList(),
    val bookedSlots: List<BookedSlot> = emptyList(),
    val lang: String = "en"
)

fun newId(): String = kotlin.random.Random.nextLong().toString()

val EMIRATES = listOf("Abu Dhabi", "Dubai", "Sharjah", "Ajman", "Umm Al Quwain", "Ras Al Khaimah", "Fujairah")
val VEHICLE_CATEGORIES = listOf("Motorcycle", "Light Vehicle", "Heavy Vehicle", "Light Bus", "Heavy Bus", "Entertainment Motorcycle")
val RTA_SERVICE_TYPES = listOf("Normal Vehicle Test", "Registration Test", "Renewal Test")
val FIXED_SLOT_TIMES = listOf("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00")
