import org.w3c.dom.HTMLInputElement
import react.FC
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.span
import react.useState
import web.cssom.ClassName
import web.html.InputType

private fun react.ChildrenBuilder.settingsRow(
    iconPath: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    div {
        className = ClassName("section-card row settings-row")
        this.onClick = { onClick() }
        div {
            className = ClassName("row gap")
            svgIcon(iconPath, "settings-row-icon")
            div {
                className = ClassName("col")
                span { className = ClassName("bold"); +title }
                span { className = ClassName("muted text-sm"); +subtitle }
            }
        }
        span { className = ClassName("muted"); +"›" }
    }
}

val SettingsScreen = FC<AppScreenProps> { props ->
    val data = props.data
    val s = strings(data.lang)
    topBar(s.settingsTitle)

    div {
        className = ClassName("settings-greeting")
        div { className = ClassName("settings-greeting-name"); +data.profile.name.ifBlank { s.driver } }
        div { className = ClassName("settings-greeting-sub muted text-sm"); +s.rtaCarCare }
    }

    settingsRow(ICON_PERSON, s.profileTitle, s.displayName) { props.go(Screen.Profile) }
    settingsRow(ICON_HELP, s.helpCenterTitle, s.faqsAbout) { props.go(Screen.HelpCenter) }
}

val ProfileScreen = FC<AppScreenProps> { props ->
    val data = props.data
    val s = strings(data.lang)
    var name  by useState(data.profile.name)
    var saved by useState(false)

    topBar(s.profileTitle, onBack = props.back)

    sectionCard {
        div {
            className = ClassName("field")
            label { +s.displayName }
            input {
                type = InputType.text
                value = name
                placeholder = s.displayNamePlaceholder
                onChange = { name = (it.target as HTMLInputElement).value; saved = false }
            }
        }
    }

    primaryButton(if (saved) s.savedConfirm else s.saveChanges) {
        props.update { it.copy(profile = it.profile.copy(name = name.ifBlank { s.driver })) }
        saved = true
    }
}

val HelpCenterScreen = FC<AppScreenProps> { props ->
    val s = strings(props.data.lang)
    topBar(s.helpCenterTitle, onBack = props.back)

    val faqs = listOf(
        s.faq1Q to s.faq1A,
        s.faq2Q to s.faq2A,
        s.faq3Q to s.faq3A,
        s.faq4Q to s.faq4A,
        s.faq5Q to s.faq5A,
        s.faq6Q to s.faq6A,
    )

    faqs.forEach { (q, a) ->
        sectionCard("faq-item") {
            div { className = ClassName("bold"); +q }
            div { className = ClassName("muted text-sm mt-1"); +a }
        }
    }
}
