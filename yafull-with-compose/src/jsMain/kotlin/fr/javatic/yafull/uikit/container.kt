package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.Element

val <T : Element> AttrsBuilder<T>.ukContainer get() = fluentClasses("uk-container")
val <T : Element> AttrsBuilder<T>.ukContainerXSmall get() = fluentClasses("uk-container-xsmall")
val <T : Element> AttrsBuilder<T>.ukContainerSmall get() = fluentClasses("uk-container-small")
val <T : Element> AttrsBuilder<T>.ukContainerLarge get() = fluentClasses("uk-container-large")
val <T : Element> AttrsBuilder<T>.ukContainerXLarge get() = fluentClasses("uk-container-xlarge")
val <T : Element> AttrsBuilder<T>.ukContainerExpand get() = fluentClasses("uk-container-expand")
