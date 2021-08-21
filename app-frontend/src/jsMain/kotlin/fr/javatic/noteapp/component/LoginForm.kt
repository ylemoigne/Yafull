package fr.javatic.noteapp.component

import androidx.compose.runtime.*
import fr.javatic.noteapp.AppContext
import fr.javatic.yafull.rest.plugin.jwt.CreateJWTTokenEndpoint
import fr.javatic.yafull.uikit.*
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.autoFocus
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.dom.*

@Suppress("NAME_SHADOWING")
@Composable
fun LoginForm(
    appContext: AppContext,
    login: String = "",
    password: String = "",
    onLoginFailed: (() -> Unit)? = null,
    onLoginSucess: (() -> Unit)? = null,
) {
    var login by mutableStateOf(login)
    var password by mutableStateOf(password)
    var loginFailedMessageState by mutableStateOf(null as String?)

    val scope = rememberCoroutineScope()

    suspend fun performLogin() {
        appContext.restClient.request(CreateJWTTokenEndpoint) {
            describe {
                this.login(login)
                this.password(password)
                handler(this.responseOk) { ctx ->
                    appContext.bearer = this.responseOk.body(ctx)
                    onLoginSucess?.invoke()
                }
                handler(this.responseAuthenticationFailed) { ctx ->
                    val message = this.responseOk.body(ctx)
                    loginFailedMessageState = message
                    onLoginFailed?.invoke()
                }
            }
        }.perform()
    }

    Form(attrs = {
        ukFormHorizontal.ukMarginProp(UkMarginPropValue.ukMarginTop)
        onSubmit {
            it.preventDefault()

        }
    }) {
        Div {
            Div({ ukInline }) {
                Span({ ukFormIcon("user") })
                TextInput(login) {
                    autoFocus()
                    ukInput.ukFormLarge
                    login.ifBlank { ukFormDanger }

                    onInput { login = it.value }
                }
            }
        }

        Div {
            Div({ ukInline }) {
                Span({ ukFormIcon("lock") })
                PasswordInput(password) {
                    ukInput.ukFormLarge
                    password.ifBlank { ukFormDanger }

                    onInput { password = it.value }
                }
            }
        }

        Div {
            Button({
                ukButton(UkButtonStyle.primary())
                if (login.isBlank() || password.isBlank()) {
                    disabled()
                }

                onClick { scope.launch(SupervisorJob()) { performLogin() } }
            }) {
                Text("Login")
            }
        }

        if (loginFailedMessageState != null) {
            P({ ukAlert.ukAlertWarning }) {
                Text("Login Failed : ${loginFailedMessageState}")
            }
        }
    }
}
