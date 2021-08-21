package fr.javatic.noteapp.page.content

import androidx.compose.runtime.*
import fr.javatic.noteapp.AppContext
import fr.javatic.noteapp.AppStyleSheet
import fr.javatic.noteapp.component.ButtonWithIcon
import fr.javatic.noteapp.component.ValidatingTextInput
import fr.javatic.noteapp.note.Note
import fr.javatic.noteapp.note.NoteUpdate
import fr.javatic.noteapp.note.rest.DeleteNoteEndpoint
import fr.javatic.noteapp.note.rest.PutNoteEndpoint
import fr.javatic.util.UUIDv4
import fr.javatic.util.create
import fr.javatic.yafull.uikit.*
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.*

object NotesContent {
    @Composable
    fun render(appContext: AppContext) {
        val allNotes by appContext.api.getNotes()
        var filter by remember { mutableStateOf("") }
        val notes by remember(allNotes, filter) {
            derivedStateOf {
                if (filter.isBlank()) allNotes
                else allNotes.filter {
                    it.title.contains(filter, true) || it.creationDate.toString().contains(filter, true) || it.content.contains(
                        filter,
                        true
                    )
                }
            }
        }

        var newNote by remember { mutableStateOf(false) }
        var editNote by remember { mutableStateOf<Note?>(null) }

        val scope = rememberCoroutineScope()

        Div({ ukContainer.ukWidth2XLarge }) {
            H1 {
                Text("Notes")
                Span({ ukAlignRight }) {
                    A(attrs = {
                        ukIconButton
                        ukTextSecondary
                        ukTooltip("Refresh")
                        ukIcon(UkIcon.refresh)
                        onClick {
                            appContext.api.refreshNotes()
                        }
                    })
                }
            }
            Div({
                ukFlex.ukFlexRow
                classes(AppStyleSheet.width100)
            }) {
                ButtonWithIcon(UkButtonStyle.primary(), UkIcon.plusCircle, "Add") {
                    newNote = true
                }
                Form(attrs = {
                    ukSearch.ukSearchDefault.ukMarginLeft
                    ukFlex1
                }) {
                    A(attrs = { ukSearchIcon })
                    SearchInput(attrs = {
                        ukSearchInput
                        onInput {
                            filter = it.value
                        }
                    })
                }
            }
            Table(
                {
                    ukTable.ukTableStriped.ukTableHover.ukTableMiddle
                })
            {
                Thead {
                    Tr {
                        Th { Text("Title") }
                        Th({ ukTableShrink }) { Text("Created At") }
                        Th({ classes(AppStyleSheet.css { width(100.px) }) })
                    }
                }
                Tbody {
                    for (note in notes) {
                        key(note.id) {
                            Tr({
                                onClick {
                                    editNote = note
                                }
                            }) {
                                Td { Text(note.title) }
                                Td { Text(note.creationDate.toString()) }
                                Td {
                                    ButtonWithIcon(UkButtonStyle.danger(), UkIcon.trash, null) {
                                        it.stopPropagation()
                                        scope.launch(SupervisorJob()) {
                                            appContext.restClient.request(DeleteNoteEndpoint) {
                                                describe {
                                                    this.id(note.id)
                                                    handler(this.responseOk) {
                                                        UIkit.notification("Note deleted", NotificationStatus.PRIMARY)
                                                    }
                                                    handler(this.responseNotFound) {
                                                        UIkit.notification("Note not found", NotificationStatus.WARNING)
                                                    }
                                                }
                                            }.perform()
                                            appContext.api.refreshNotes()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (editNote != null) {
            ModalEditNote(appContext, editNote, { editNote = null }) {
                editNote = null
                appContext.api.refreshNotes()
            }
        }

        if (newNote) {
            ModalEditNote(appContext, editNote, { newNote = false }) {
                newNote = false
                appContext.api.refreshNotes()
            }
        }
    }

    @Composable
    fun ModalEditNote(appContext: AppContext, note: Note?, onCloseRequest: () -> Unit, onNoteSaved: () -> Unit) {
        var noteTitle by remember { mutableStateOf(note?.title ?: "") }
        var noteContent by remember { mutableStateOf(note?.content ?: "") }
        val scope = rememberCoroutineScope()

        UkModalOf {
            val formId = "edit_note_form"
            Div({ ukModalDialog }) {
                Div({ ukModalHeader }) {
                    H2({ ukModalTitle }) {
                        if (note == null) {
                            Text("Create Note")
                        } else {
                            Text("Edit Note ${note.title}")
                        }
                    }
                    A(attrs = {
                        classes(UIKitStylesheet.ukModalCloseDefault)
                        ukClose.ukCloseLarge
                        ukAlignRight
                        onClick { onCloseRequest() }
                    })
                }
                Div({ ukModalBody }) {
                    Div({ ukContainer }) {
                        Form(attrs = {
                            id(formId)
                            ukFormStacked
                            onSubmit {
                                it.preventDefault()
                            }
                        }) {
                            Div {
                                val loginId = "login"
                                Label(loginId, { ukFormLabel }) { Text("Title") }

                                ValidatingTextInput(noteTitle, loginId, { noteTitle.isNotBlank() }) {
                                    noteTitle = it.value
                                }
                            }

                            Div {
                                val contentId = "content"
                                Label(contentId, { ukFormLabel }) { Text("Content") }

                                TextArea({
                                    id(contentId)
                                    ukTextarea
                                    onInput {
                                        noteContent = it.value
                                    }
                                }, noteContent)
                            }
                        }
                    }
                    Div({ ukModalFooter.ukTextRight }) {
                        Button({
                            ukButton(UkButtonStyle.default())
                            ukMarginLeft
                            onClick { onCloseRequest() }
                        }) {
                            Text("Cancel")
                        }
                        Button({
                            type(ButtonType.Submit)
                            form(formId)

                            if (noteTitle.isBlank()) {
                                disabled()
                            }

                            ukButton(UkButtonStyle.primary())
                            ukMarginLeft

                            onClick {
                                println("Save clicked")
                                scope.launch(SupervisorJob()) {
                                    appContext.restClient.request(PutNoteEndpoint) {
                                        describe {
                                            this.note(
                                                NoteUpdate(
                                                    note?.id ?: UUIDv4.create(),
                                                    noteTitle,
                                                    noteContent
                                                )
                                            )
                                            handler(this.created) {
                                                UIkit.notification("Note created", NotificationStatus.SUCCESS)
                                            }
                                            handler(this.updated) {
                                                UIkit.notification("Note updated", NotificationStatus.SUCCESS)
                                            }
                                        }
                                    }.perform()
                                    onNoteSaved()
                                }
                            }
                        }) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}
