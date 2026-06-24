import react.FC
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.span
import react.useState
import web.cssom.ClassName

val RunBundleScreen = FC<RunBundleProps> { props ->
    val data   = props.data
    val s      = strings(data.lang)
    val bundle = data.bundles.find { it.id == props.bundleId }

    var results    by useState<Map<Int, Boolean>>(emptyMap())
    var activeZone by useState<Zone?>(null)
    var finished   by useState<BundleRun?>(null)

    topBar(s.runInspection, onBack = props.back)

    if (bundle == null) {
        sectionCard {
            div { className = ClassName("empty-state"); +s.bundleNotFound }
        }
        return@FC
    }

    val result = finished
    if (result != null) {
        val zoneMap = mutableMapOf<Zone, Boolean>()
        result.results.forEach { item ->
            val prev = zoneMap[item.zone]
            zoneMap[item.zone] = if (prev == null) item.passed else (prev && item.passed)
        }

        carDiagram(zoneResults = zoneMap)

        sectionCard {
            div {
                className = ClassName("center")
                span { className = ClassName(if (result.passed) "verdict-text pass" else "verdict-text fail")
                    +(if (result.passed) s.passed.uppercase() else s.failed.uppercase())
                }
            }
            div { className = ClassName("muted text-sm mt-1 center"); +"${bundle.name} — ${result.date}" }
            div { className = ClassName("muted text-sm center")
                +"${result.results.count { it.passed }} ${s.ofItemsPassed} ${result.results.size} ${s.items}"
            }
        }

        div { className = ClassName("mt-2") }
        result.results.forEach { item ->
            sectionCard {
                div {
                    className = ClassName("row")
                    div {
                        className = ClassName("col")
                        span { className = ClassName("bold"); +item.description }
                        span { className = ClassName("muted text-sm"); +item.zone.label(data.lang) }
                    }
                    span {
                        className = ClassName(if (item.passed) "text-green bold" else "text-red bold")
                        +(if (item.passed) s.pass else s.fail)
                    }
                }
            }
        }

        div { className = ClassName("mt-2") }
        primaryButton(s.runAnotherInspection) {
            finished = null
            results = emptyMap()
            activeZone = null
        }
        return@FC
    }

    val liveZoneMap = mutableMapOf<Zone, Boolean>()
    bundle.items.forEachIndexed { i, item ->
        val r = results[i] ?: return@forEachIndexed
        val prev = liveZoneMap[item.zone]
        liveZoneMap[item.zone] = if (prev == null) r else (prev && r)
    }

    carDiagram(
        activeZone  = if (liveZoneMap.isEmpty()) activeZone else null,
        zoneResults = liveZoneMap
    )

    div {
        className = ClassName("center muted text-sm mb-2")
        +s.tapRowHint
    }

    div { className = ClassName("mt-2") }
    bundle.items.forEachIndexed { i, item ->
        val state = results[i]
        div {
            className = ClassName("checklist-row" + if (state != null) " selected" else "")
            onClick = { activeZone = item.zone }
            div {
                className = ClassName("col")
                span { className = ClassName("bold"); +item.description }
                span { className = ClassName("muted text-sm"); +item.zone.label(data.lang) }
            }
            div {
                className = ClassName("row gap")
                resultChip(s.pass, state == true, isPass = true) {
                    results = results + (i to true)
                    activeZone = item.zone
                }
                resultChip(s.fail, state == false, isPass = false) {
                    results = results + (i to false)
                    activeZone = item.zone
                }
            }
        }
    }

    val allAnswered = bundle.items.indices.all { results.containsKey(it) }
    div { className = ClassName("mt-2") }
    primaryButton(s.finishInspection, enabled = allAnswered) {
        val run = BundleRun(
            id = newId(),
            bundleName = bundle.name,
            date = today().display(),
            results = bundle.items.mapIndexed { i, item ->
                BundleItemResult(
                    description = item.description,
                    zone = item.zone,
                    passed = results[i] == true
                )
            }
        )
        props.update { it.copy(bundleRuns = listOf(run) + it.bundleRuns) }
        finished = run
    }
}

private fun react.ChildrenBuilder.resultChip(
    label: String,
    active: Boolean,
    isPass: Boolean,
    onClick: () -> Unit
) {
    button {
        className = ClassName(
            "result-chip" + if (active) (if (isPass) " active-pass" else " active-fail") else ""
        )
        this.onClick = { onClick() }
        +label
    }
}
