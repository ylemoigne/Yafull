package fr.javatic.yafull.uikit

import org.w3c.dom.Element
import kotlin.js.Promise

@JsModule("uikit")
@JsNonModule
@JsName("UIKit")
private external object InternalUIkit {
    fun notification(message: String, options: dynamic)

    fun modal(element: Element): Modal

    fun use(require: dynamic)
}

object UIkit {
    fun notification(message: String, status: NotificationStatus? = null, pos: NotificationPosition? = null, timeoutMs: Int? = null, group: String? = null) {
        val options = js("{}")
        status?.let { options.status = it.value }
        pos?.let { options.pos = it.value }
        timeoutMs?.let { options.timeout = it }
        group?.let { options.group = it }
        InternalUIkit.notification(message, options)
    }

    object notification {
        fun closeAll() = InternalUIkit.asDynamic().notification.closeAll()
    }

// DOM Structure manipulation of element rendered by compose by something outside compose doesn't go well
// https://github.com/JetBrains/compose-jb/issues/1085
//    fun modal(element: Element): Modal = InternalUIkit.modal(element)

    fun use(require: dynamic) = InternalUIkit.use(require)
}

external interface Modal {
    fun show()
    fun hide()

    fun alert(message: String): Promise<DialogResult>
    fun confirm(message: String): Promise<DialogResult>
    fun prompt(label: String, input: String): Promise<DialogResult>
    fun dialog(html: String): Modal
}

interface DialogResult {
    val dialog: Element
}

enum class NotificationStatus(val value: String) {
    PRIMARY("primary"),
    SUCCESS("success"),
    WARNING("warning"),
    DANGER("danger"),
}

enum class NotificationPosition(val value: String) {
    TOP_LEFT("top-left"),
    TOP_CENTER("top-center"),
    TOP_RIGHT("top-right"),
    BOTTOM_LEFT("bottom-left"),
    BOTTOM_CENTER("bottom-center"),
    BOTTOM_RIGHT("bottom-right"),
}
