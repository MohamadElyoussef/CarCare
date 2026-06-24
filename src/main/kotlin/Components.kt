import react.ChildrenBuilder
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.span
import react.useEffect
import react.useRef
import web.cssom.ClassName
import web.html.HTMLDivElement

fun ChildrenBuilder.sectionCard(extraClass: String = "", block: ChildrenBuilder.() -> Unit) {
    div {
        className = ClassName("section-card $extraClass".trim())
        block()
    }
}

fun ChildrenBuilder.primaryButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    button {
        className = ClassName("btn-primary")
        disabled = !enabled
        this.onClick = { if (enabled) onClick() }
        +text
    }
}

fun ChildrenBuilder.secondaryButton(text: String, onClick: () -> Unit) {
    button {
        className = ClassName("btn-secondary")
        this.onClick = { onClick() }
        +text
    }
}

fun ChildrenBuilder.chip(label: String, active: Boolean, disabled: Boolean = false, onClick: () -> Unit) {
    button {
        className = ClassName("chip" + (if (active) " active" else "") + (if (disabled) " disabled" else ""))
        this.disabled = disabled
        this.onClick = { if (!disabled) onClick() }
        +label
    }
}

fun ChildrenBuilder.topBar(title: String, onBack: (() -> Unit)? = null) {
    div {
        className = ClassName("top-bar")
        if (onBack != null) {
            button {
                className = ClassName("icon-btn")
                onClick = { onBack() }
                +"←"
            }
        }
        h1 { +title }
    }
}

fun ChildrenBuilder.stepIndicator(step: Int, total: Int, label: String) {
    div {
        className = ClassName("step-indicator")
        (1..total).forEach { i ->
            div {
                className = ClassName(when {
                    i < step  -> "step-dot done"
                    i == step -> "step-dot current"
                    else      -> "step-dot"
                })
                +"$i"
            }
            if (i < total) {
                div { className = ClassName("step-line" + if (i < step) " done" else "") }
            }
        }
    }
    div { className = ClassName("step-label"); +label }
}

private const val PATH_HOME     = "M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"
private const val PATH_HISTORY  = "M13 3a9 9 0 0 0-9 9H1l3.89 3.89.07.14L9 12H6c0-3.87 3.13-7 7-7s7 3.13 7 7-3.13 7-7 7c-1.93 0-3.68-.79-4.94-2.06l-1.42 1.42A8.954 8.954 0 0 0 13 21a9 9 0 0 0 0-18zm-1 5v5l4.28 2.54.72-1.21-3.5-2.08V8H12z"
private const val PATH_SETTINGS = "M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58c.18-.14.23-.41.12-.61l-1.92-3.32c-.12-.22-.37-.29-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54c-.04-.24-.24-.41-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58c-.18.14-.23.41-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.57 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.6c-1.98 0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6 3.6 1.62 3.6 3.6-1.62 3.6-3.6 3.6z"

private fun makeSvg(path: String) = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor"><path d="$path"/></svg>"""

private val SVG_HOME     = makeSvg(PATH_HOME)
private val SVG_HISTORY  = makeSvg(PATH_HISTORY)
private val SVG_SETTINGS = makeSvg(PATH_SETTINGS)

private data class NavItem(
    val screen: Screen,
    val svg: String,
    val labelFn: (AppStrings) -> String
)

private val navItems = listOf(
    NavItem(Screen.Home,    SVG_HOME,     { s -> s.navHome }),
    NavItem(Screen.History, SVG_HISTORY,  { s -> s.navHistory }),
    NavItem(Screen.Settings,SVG_SETTINGS, { s -> s.navSettings })
)

external interface NavIconProps : Props {
    var svg: String
    var active: Boolean
}

private val NavIconBadge = FC<NavIconProps> { props ->
    val ref = useRef<HTMLDivElement>(null)
    useEffect(props.svg) {
        ref.current.asDynamic().innerHTML = props.svg
    }
    div {
        this.ref = ref
        className = ClassName("nav-icon-badge" + if (props.active) " active" else "")
    }
}

fun ChildrenBuilder.bottomNav(current: Screen, onSelect: (Screen) -> Unit, lang: String = "en") {
    val s = strings(lang)
    div {
        className = ClassName("bottom-nav")
        navItems.forEach { item ->
            val isActive = current == item.screen
            button {
                className = ClassName(if (isActive) "active" else "")
                onClick = { onSelect(item.screen) }
                NavIconBadge {
                    svg = item.svg
                    active = isActive
                }
                span { +item.labelFn(s) }
            }
        }
    }
}

// ── Public SVG icon renderer ─────────────────────────────────────────────────

const val ICON_HISTORY      = PATH_HISTORY
const val ICON_CLIPBOARD    = "M19 3h-4.18C14.4 1.84 13.3 1 12 1c-1.3 0-2.4.84-2.82 2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 0c.55 0 1 .45 1 1s-.45 1-1 1-1-.45-1-1 .45-1 1-1zm2 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"
const val ICON_SHIELD_OK    = "M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm-2 16l-4-4 1.41-1.41L10 14.17l6.59-6.59L18 9l-8 8z"
const val ICON_LIST         = "M3 13h2v-2H3v2zm0 4h2v-2H3v2zm0-8h2V7H3v2zm4 4h14v-2H7v2zm0 4h14v-2H7v2zM7 7v2h14V7H7z"
const val ICON_CAR          = "M18.92 6.01C18.72 5.42 18.16 5 17.5 5h-11c-.66 0-1.21.42-1.42 1.01L3 12v8c0 .55.45 1 1 1h1c.55 0 1-.45 1-1v-1h12v1c0 .55.45 1 1 1h1c.55 0 1-.45 1-1v-8l-2.08-5.99zM6.5 16c-.83 0-1.5-.67-1.5-1.5S5.67 13 6.5 13s1.5.67 1.5 1.5S7.33 16 6.5 16zm11 0c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5zM5 11l1.5-4.5h11L19 11H5z"
const val ICON_PERSON       = "M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"
const val ICON_HELP         = "M11 18h2v-2h-2v2zm1-16C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm0-14c-2.21 0-4 1.79-4 4h2c0-1.1.9-2 2-2s2 .9 2 2c0 2-3 1.75-3 5h2c0-2.25 3-2.5 3-5 0-2.21-1.79-4-4-4z"
const val ICON_CHECK_CIRCLE = "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"

fun ChildrenBuilder.svgIcon(path: String, cls: String) {
    val html = makeSvg(path)
    val dangerousObj: dynamic = js("({})")
    dangerousObj.__html = html
    div {
        className = ClassName(cls)
        asDynamic().dangerouslySetInnerHTML = dangerousObj
    }
}

// ─────────────────────────────────────────────────────────────────────────────

external interface CarDiagramProps : Props {
    var svgHtml: String
}

private val CarDiagramRenderer = FC<CarDiagramProps> { props ->
    val containerRef = useRef<HTMLDivElement>(null)
    useEffect(props.svgHtml) {
        containerRef.current.asDynamic().innerHTML = props.svgHtml
    }
    div {
        ref = containerRef
        className = ClassName("car-diagram-wrapper")
    }
}

fun ChildrenBuilder.carDiagram(
    activeZone: Zone? = null,
    zoneResults: Map<Zone, Boolean> = emptyMap()
) {
    fun zoneClass(base: String, zone: Zone): String {
        val result = zoneResults[zone]
        return when {
            result == true  -> "$base pass"
            result == false -> "$base fail"
            zone == activeZone -> "$base active"
            else -> base
        }
    }

    val hoodCls  = zoneClass("car-zone",  Zone.HOOD)
    val bodyCls  = zoneClass("car-zone",  Zone.BODY)
    val wiperCls = zoneClass("car-wiper", Zone.WIPERS)
    val lightCls = zoneClass("car-light", Zone.LIGHTS)
    val wheelCls = zoneClass("car-wheel", Zone.WHEELS)
    val discCls  = zoneClass("car-disc",  Zone.BRAKES)

    val svgContent = """
<svg viewBox="0 0 440 195" xmlns="http://www.w3.org/2000/svg" class="car-svg">
  <ellipse class="car-shadow" cx="220" cy="172" rx="195" ry="13"/>
  <path class="car-silhouette" d="
    M 20,148
    C 10,148 6,130 10,116
    C 14,92 36,70 76,62
    C 104,46 142,38 182,40
    C 202,41 216,52 224,72
    C 256,66 300,64 332,66
    C 360,67 388,72 404,90
    C 414,100 420,112 420,128
    C 420,140 414,148 404,150
    L 372,150
    C 372,118 344,108 336,108
    C 328,108 300,118 300,150
    L 144,150
    C 144,118 116,108 108,108
    C 100,108 72,118 72,150
    L 20,150 Z"/>
  <path class="car-line" d="M 80,98 C 140,92 260,90 330,94"/>
  <path class="car-glass" d="M 78,64 C 100,50 134,42 176,42 C 196,43 210,53 218,70 C 188,66 144,66 110,72 C 96,75 86,70 78,64 Z"/>
  <path class="car-mirror" d="M 222,65 C 230,61 238,62 240,68 C 234,70 226,70 222,65 Z"/>
  <path class="$hoodCls" d="
    M 224,72
    C 256,66 300,64 332,66
    C 360,67 388,72 404,90
    C 414,100 420,112 420,128
    C 420,140 414,148 404,150
    L 372,150
    C 372,118 344,108 336,108
    C 328,108 300,118 300,150
    L 224,150 Z"/>
  <path class="$bodyCls" d="
    M 20,148
    C 10,148 6,130 10,116
    C 14,92 36,70 76,62
    C 104,46 142,38 182,40
    C 202,41 216,52 224,72
    L 224,150
    L 144,150
    C 144,118 116,108 108,108
    C 100,108 72,118 72,150
    L 20,150 Z"/>
  <path class="$wiperCls" d="M 232,76 L 250,56"/>
  <ellipse class="$lightCls" cx="412" cy="108" rx="8" ry="13"/>
  <rect    class="$lightCls" x="14" y="118" width="10" height="22" rx="4"/>
  <circle class="$wheelCls" cx="108" cy="152" r="30"/>
  <circle class="$discCls"  cx="108" cy="152" r="14"/>
  <circle class="$wheelCls" cx="336" cy="152" r="30"/>
  <circle class="$discCls"  cx="336" cy="152" r="14"/>
</svg>""".trimIndent()

    CarDiagramRenderer { svgHtml = svgContent }
}
