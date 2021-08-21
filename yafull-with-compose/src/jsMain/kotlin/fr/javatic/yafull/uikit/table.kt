package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.HTMLTableCellElement
import org.w3c.dom.HTMLTableElement
import org.w3c.dom.HTMLTableRowElement

val <T : HTMLTableElement> AttrsBuilder<T>.ukTable get() = fluentClasses("uk-table")
val <T : HTMLTableElement> AttrsBuilder<T>.ukTableDivider get() = fluentClasses("uk-table-divider")
val <T : HTMLTableElement> AttrsBuilder<T>.ukTableStriped get() = fluentClasses("uk-table-striped")
val <T : HTMLTableElement> AttrsBuilder<T>.ukTableHover get() = fluentClasses("uk-table-hover")

val <T : HTMLTableElement> AttrsBuilder<T>.ukTableSmall get() = fluentClasses("uk-table-small")
val <T : HTMLTableElement> AttrsBuilder<T>.ukTableJustify get() = fluentClasses("uk-table-justify")

val <T : HTMLTableElement> AttrsBuilder<T>.ukTableMiddle get() = fluentClasses("uk-table-middle")
val <T : HTMLTableRowElement> AttrsBuilder<T>.ukTableMiddle get() = fluentClasses("uk-table-middle")
val <T : HTMLTableCellElement> AttrsBuilder<T>.ukTableMiddle get() = fluentClasses("uk-table-middle")

val <T : HTMLTableCellElement> AttrsBuilder<T>.ukTableShrink get() = fluentClasses("uk-table-shrink")
val <T : HTMLTableCellElement> AttrsBuilder<T>.ukTableExpand get() = fluentClasses("uk-table-expand")

val <T : HTMLTableCellElement> AttrsBuilder<T>.ukTableLink get() = fluentClasses("uk-table-link")

