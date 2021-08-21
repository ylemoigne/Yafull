package fr.javatic.noteapp.page.content

import androidx.compose.runtime.*
import fr.javatic.noteapp.AppContext
import fr.javatic.noteapp.component.UserForm
import fr.javatic.noteapp.user.UserUpdate
import fr.javatic.yafull.uikit.*
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.form
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.dom.*

object ProfileContent {
    @Composable
    fun render(appContext: AppContext) {
        val user by appContext.api.getUser(appContext.currentUserId ?: throw error("Current user must not be null"))
        var userUpdate by remember(appContext.currentUserId) { mutableStateOf<UserUpdate?>(null) }
        val scope = rememberCoroutineScope()

        Div({ ukContainer.ukWidthMedium }) {
            H1 { Text("Edit Profile") }
            val formId = "profile_form"
            println("Before User form render")
            UserForm.render(appContext, formId, user) {
                userUpdate = it
            }
            println("After user form render")
            Button({
                type(ButtonType.Submit)
                form(formId)

                if (userUpdate == null) {
                    disabled()
                }
                ukButton(UkButtonStyle.primary()).ukMargin

                onClick {
                    scope.launch(SupervisorJob()) {
                        UserForm.save(appContext, userUpdate)
                    }
                }
            }) {
                Span({ ukIcon("save") })
                Text("Save")
            }
        }
    }
}
