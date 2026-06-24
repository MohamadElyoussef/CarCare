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

val BundleScreen = FC<AppScreenProps> { props ->
    val data = props.data
    val s = strings(data.lang)

    var bundleName  by useState("")
    var itemDesc    by useState("")
    var itemZone    by useState(Zone.BODY)
    var items       by useState<List<BundleItem>>(emptyList())
    var activeZone  by useState<Zone?>(null)

    topBar(s.buildInspectionBundle, onBack = props.back)

    div {
        className = ClassName("muted text-sm mb-2")
        +s.buildBundleDesc
    }

    carDiagram(activeZone = activeZone)
    div {
        className = ClassName("center muted text-sm mb-2")
        +(activeZone?.let { "${s.highlightingPrefix}${it.label(data.lang)}" } ?: s.selectZoneHint)
    }

    sectionCard {
        div {
            className = ClassName("field")
            label { +s.bundleName }
            input {
                type = InputType.text
                value = bundleName
                placeholder = s.bundleNamePlaceholder
                onChange = { bundleName = (it.target as HTMLInputElement).value }
            }
        }
    }

    sectionCard {
        span { className = ClassName("label-muted mb-2"); +s.addItemLabel }
        div { className = ClassName("mt-2") }
        div {
            className = ClassName("field")
            label { +s.description }
            input {
                type = InputType.text
                value = itemDesc
                placeholder = s.descriptionPlaceholder
                onChange = { itemDesc = (it.target as HTMLInputElement).value }
            }
        }
        div {
            className = ClassName("field")
            label { +s.carZone }
            div {
                className = ClassName("chip-row")
                Zone.entries.forEach { zone ->
                    chip(zone.label(data.lang), active = itemZone == zone) {
                        itemZone = zone
                        activeZone = zone
                    }
                }
            }
        }
        button {
            className = ClassName("btn-secondary")
            disabled = itemDesc.isBlank()
            onClick = {
                if (itemDesc.isNotBlank()) {
                    items = items + BundleItem(description = itemDesc.trim(), zone = itemZone)
                    activeZone = itemZone
                    itemDesc = ""
                }
            }
            +s.addItemBtn
        }
    }

    if (items.isNotEmpty()) {
        span { className = ClassName("label-muted mt-2"); +"${s.addItemLabel.uppercase()} (${items.size})" }
        div { className = ClassName("mt-2") }
        items.forEachIndexed { i, item ->
            div {
                className = ClassName("checklist-row")
                onClick = { activeZone = item.zone }
                div {
                    className = ClassName("col")
                    span { className = ClassName("bold"); +item.description }
                    span { className = ClassName("muted text-sm"); +item.zone.label(data.lang) }
                }
                button {
                    className = ClassName("icon-btn")
                    onClick = {
                        it.stopPropagation()
                        items = items.toMutableList().also { m -> m.removeAt(i) }
                        if (activeZone == item.zone && items.none { it.zone == item.zone }) {
                            activeZone = items.lastOrNull()?.zone
                        }
                    }
                    +"×"
                }
            }
        }
    }

    div { className = ClassName("mt-2") }
    primaryButton(
        s.saveBundle,
        enabled = bundleName.isNotBlank() && items.isNotEmpty()
    ) {
        props.update {
            it.copy(
                bundles = it.bundles + InspectionBundle(
                    id = newId(),
                    name = bundleName.trim(),
                    items = items
                )
            )
        }
        bundleName = ""
        items = emptyList()
        activeZone = null
        itemDesc = ""
    }

    if (data.bundles.isNotEmpty()) {
        span { className = ClassName("label-muted mt-3"); +s.savedBundles }
        div { className = ClassName("mt-2") }
        data.bundles.forEach { bundle ->
            sectionCard {
                div {
                    className = ClassName("row")
                    div {
                        className = ClassName("col")
                        span { className = ClassName("bold"); +bundle.name }
                        span { className = ClassName("muted text-sm mt-1"); +"${bundle.items.size} ${s.items}" }
                    }
                    div {
                        className = ClassName("row gap")
                        button {
                            className = ClassName("btn-sm-primary")
                            onClick = { props.go(Screen.RunBundle(bundle.id)) }
                            +s.run
                        }
                        button {
                            className = ClassName("icon-btn")
                            onClick = {
                                props.update { d ->
                                    d.copy(bundles = d.bundles.filterNot { it.id == bundle.id })
                                }
                            }
                            +"×"
                        }
                    }
                }
            }
        }
    }
}
