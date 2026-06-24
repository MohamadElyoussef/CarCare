import kotlinx.browser.document
import react.create
import react.dom.client.createRoot
import web.dom.Element

fun main() {
    val container = document.getElementById("root")?.unsafeCast<Element>() ?: error("Couldn't find #root in index.html")
    createRoot(container).render(App.create())
}
