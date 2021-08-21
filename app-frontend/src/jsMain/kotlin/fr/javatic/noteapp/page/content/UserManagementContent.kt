package fr.javatic.noteapp.page.content

import androidx.compose.runtime.*
import fr.javatic.noteapp.AppContext
import fr.javatic.noteapp.AppStyleSheet
import fr.javatic.noteapp.component.ButtonWithIcon
import fr.javatic.noteapp.component.UserForm
import fr.javatic.noteapp.user.User
import fr.javatic.noteapp.user.UserUpdate
import fr.javatic.noteapp.user.rest.DeleteUserEndpoint
import fr.javatic.yafull.uikit.*
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.form
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.*

object UserManagementContent {
    @Composable
    fun render(appContext: AppContext) {
        val allUsers by appContext.api.getUsers()
        var filter by remember { mutableStateOf("") }
        val users by remember(allUsers, filter) {
            derivedStateOf {
                if (filter.isBlank()) allUsers
                else allUsers.filter { it.login.contains(filter, true) || it.fullname.contains(filter, true) }
            }
        }

        var newUser by remember { mutableStateOf(false) }
        var editUser by remember { mutableStateOf<User?>(null) }

        val scope = rememberCoroutineScope()

        Div({ ukContainer.ukWidth2XLarge }) {
            H1 {
                Text("User Management")
                Span({ ukAlignRight }) {
                    A(attrs = {
                        ukIconButton
                        ukTextSecondary
                        ukTooltip("Refresh")
                        ukIcon(UkIcon.refresh)
                        onClick {
                            appContext.api.refreshUsers()
                        }
                    })
                }
            }
            Div({
                ukFlex.ukFlexRow
                classes(AppStyleSheet.width100)
            }) {
                ButtonWithIcon(UkButtonStyle.primary(), UkIcon.plusCircle, "Add") {
                    newUser = true
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
                        Th({ ukTableShrink }) { Text("Login") }
                        Th { Text("Full Name") }
                        Th { Text("Roles") }
                        Th({ classes(AppStyleSheet.css { width(100.px) }) })
                    }
                }
                Tbody {
                    for (user in users) {
                        key(user.id) {
                            Tr({
                                onClick {
                                    editUser = user
                                }
                            }) {
                                Td { Text(user.login) }
                                Td { Text(user.fullname) }
                                Td { Text(user.roles.joinToString()) }
                                Td {
                                    ButtonWithIcon(UkButtonStyle.danger(), UkIcon.trash, null) {
                                        it.stopPropagation()
                                        scope.launch(SupervisorJob()) {
                                            appContext.restClient.request(DeleteUserEndpoint) {
                                                describe {
                                                    this.id(user.id)
                                                    handler(this.responseOk) {
                                                        UIkit.notification("User deleted", NotificationStatus.PRIMARY)
                                                    }
                                                    handler(this.responseNotFound) {
                                                        UIkit.notification("User not found", NotificationStatus.WARNING)
                                                    }
                                                }
                                            }.perform()
                                            appContext.api.refreshUsers()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (editUser != null) {
            ModalEditUser(appContext, editUser, { editUser = null }) {
                editUser = null
                appContext.api.refreshUsers()
            }
        }

        if (newUser) {
            ModalEditUser(appContext, editUser, { newUser = false }) {
                newUser = false
                appContext.api.refreshUsers()
            }
        }
    }

    @Composable
    fun ModalEditUser(appContext: AppContext, user: User?, onCloseRequest: () -> Unit, onUserSaved: () -> Unit) {
        var userUpdate by remember { mutableStateOf<UserUpdate?>(null) }
        val scope = rememberCoroutineScope()

        UkModalOf {
            val formId = "edit_user_form"
            Div({ ukModalDialog }) {
                Div({ ukModalHeader }) {
                    H2({ ukModalTitle }) {
                        if (user == null) {
                            Text("Create User")
                        } else {
                            Text("Edit User ${user.login}")
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
                        UserForm.render(appContext, formId, user) {
                            userUpdate = it
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

                        if (userUpdate == null) {
                            disabled()
                        }

                        ukButton(UkButtonStyle.primary())
                        ukMarginLeft

                        onClick {
                            scope.launch(SupervisorJob()) {
                                UserForm.save(appContext, userUpdate)
                                onUserSaved()
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
