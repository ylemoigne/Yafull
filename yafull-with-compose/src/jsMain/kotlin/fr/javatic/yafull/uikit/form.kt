package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.*

val <T : HTMLInputElement> AttrsBuilder<T>.ukInput get() = fluentClasses("uk-input")
val <T : HTMLSelectElement> AttrsBuilder<T>.ukSelect get() = fluentClasses("uk-select")
val <T : HTMLTextAreaElement> AttrsBuilder<T>.ukTextarea get() = fluentClasses("uk-textarea")
val <T : HTMLInputElement> AttrsBuilder<T>.ukRadio get() = fluentClasses("uk-radio")
val <T : HTMLInputElement> AttrsBuilder<T>.ukCheckbox get() = fluentClasses("uk-checkbox")
val <T : HTMLInputElement> AttrsBuilder<T>.ukRange get() = fluentClasses("uk-range")

val <T : HTMLFieldSetElement> AttrsBuilder<T>.ukFieldset get() = fluentClasses("uk-fieldset")
val <T : HTMLLegendElement> AttrsBuilder<T>.ukLegend get() = fluentClasses("uk-legend")

val <T : HTMLInputElement> AttrsBuilder<T>.ukFormDanger get() = fluentClasses("uk-form-danger")
val <T : HTMLInputElement> AttrsBuilder<T>.ukFormSuccess get() = fluentClasses("uk-form-success")
val <T : HTMLSelectElement> AttrsBuilder<T>.ukFormDanger get() = fluentClasses("uk-form-danger")
val <T : HTMLSelectElement> AttrsBuilder<T>.ukFormSuccess get() = fluentClasses("uk-form-success")
val <T : HTMLTextAreaElement> AttrsBuilder<T>.ukFormDanger get() = fluentClasses("uk-form-danger")
val <T : HTMLTextAreaElement> AttrsBuilder<T>.ukFormSuccess get() = fluentClasses("uk-form-success")

val <T : HTMLInputElement> AttrsBuilder<T>.ukFormLarge get() = fluentClasses("uk-form-large")
val <T : HTMLInputElement> AttrsBuilder<T>.ukFormSmall get() = fluentClasses("uk-form-small")
val <T : HTMLSelectElement> AttrsBuilder<T>.ukFormLarge get() = fluentClasses("uk-form-large")
val <T : HTMLSelectElement> AttrsBuilder<T>.ukFormSmall get() = fluentClasses("uk-form-small")
val <T : HTMLTextAreaElement> AttrsBuilder<T>.ukFormLarge get() = fluentClasses("uk-form-large")
val <T : HTMLTextAreaElement> AttrsBuilder<T>.ukFormSmall get() = fluentClasses("uk-form-small")

val <T : HTMLInputElement> AttrsBuilder<T>.ukFormWidthLarge get() = fluentClasses("uk-form-width-large")
val <T : HTMLInputElement> AttrsBuilder<T>.ukFormWidthMedium get() = fluentClasses("uk-form-width-medium")
val <T : HTMLInputElement> AttrsBuilder<T>.ukFormWidthSmall get() = fluentClasses("uk-form-width-small")
val <T : HTMLInputElement> AttrsBuilder<T>.ukFormWidthXSmall get() = fluentClasses("uk-form-width-xsmall")
val <T : HTMLSelectElement> AttrsBuilder<T>.ukFormWidthLarge get() = fluentClasses("uk-form-width-large")
val <T : HTMLSelectElement> AttrsBuilder<T>.ukFormWidthMedium get() = fluentClasses("uk-form-width-medium")
val <T : HTMLSelectElement> AttrsBuilder<T>.ukFormWidthSmall get() = fluentClasses("uk-form-width-small")
val <T : HTMLSelectElement> AttrsBuilder<T>.ukFormWidthXSmall get() = fluentClasses("uk-form-width-xsmall")
val <T : HTMLTextAreaElement> AttrsBuilder<T>.ukFormWidthLarge get() = fluentClasses("uk-form-width-large")
val <T : HTMLTextAreaElement> AttrsBuilder<T>.ukFormWidthMedium get() = fluentClasses("uk-form-width-medium")
val <T : HTMLTextAreaElement> AttrsBuilder<T>.ukFormWidthSmall get() = fluentClasses("uk-form-width-small")
val <T : HTMLTextAreaElement> AttrsBuilder<T>.ukFormWidthXSmall get() = fluentClasses("uk-form-width-xsmall")

val <T : HTMLInputElement> AttrsBuilder<T>.ukFormBlank get() = fluentClasses("uk-form-blank")
val <T : HTMLSelectElement> AttrsBuilder<T>.ukFormBlank get() = fluentClasses("uk-form-blank")
val <T : HTMLTextAreaElement> AttrsBuilder<T>.ukFormBlank get() = fluentClasses("uk-form-blank")

val <T : HTMLFormElement> AttrsBuilder<T>.ukFormStacked get() = fluentClasses("uk-form-stacked")
val <T : HTMLFormElement> AttrsBuilder<T>.ukFormHorizontal get() = fluentClasses("uk-form-horizontal")

val <T : Element> AttrsBuilder<T>.ukFormLabel get() = fluentClasses("uk-form-label")
val <T : Element> AttrsBuilder<T>.ukFormControls get() = fluentClasses("uk-form-controls")

fun <T : Element> AttrsBuilder<T>.ukFormIcon(name: String, flip: Boolean = false) {
    fluentClasses("uk-form-icon")
    if (flip) {
        fluentClasses("uk-form-icon-flip")
    }
    ukIcon(name)
}

fun <T : Element> AttrsBuilder<T>.ukFormCustom(
    /**
     * Use a button, text or a link as a select form. Just add the target: SELECTOR option to the uk-form-custom attribute to select where the option value should be displayed. target: true will select the adjacent element in the markup.
     *
     * https://getuikit.com/docs/form#component-option
     */
    target: String? = null
) = attr("uk-form-custom", target?.let { "target: $target" } ?: "")
