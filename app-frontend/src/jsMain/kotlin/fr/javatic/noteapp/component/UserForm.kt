package fr.javatic.noteapp.component

import androidx.compose.runtime.*
import fr.javatic.kotlinSdkExtensions.toHumanString
import fr.javatic.noteapp.AppContext
import fr.javatic.noteapp.AppStyleSheet
import fr.javatic.noteapp.Role
import fr.javatic.noteapp.user.User
import fr.javatic.noteapp.user.UserUpdate
import fr.javatic.noteapp.user.rest.EstimatePasswordStrengthEndpoint
import fr.javatic.noteapp.user.rest.LoginIsAvailableEndpoint
import fr.javatic.noteapp.user.rest.PutUserEndpoint
import fr.javatic.util.UUIDv4
import fr.javatic.util.create
import fr.javatic.yafull.compose.observableMutableStateOf
import fr.javatic.yafull.uikit.*
import fr.javatic.yafull.utils.password.PasswordStrengthEstimation
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import kotlin.time.Duration

object UserForm {
    @Composable
    fun render(appContext: AppContext, formId: String, user: User?, onUserChange: (UserUpdate?) -> Unit) {
        var formData by remember(user) {
            observableMutableStateOf(UserFormData(appContext, user)) { newState ->
                onUserChange(newState.asUserUpdate())
            }
        }

        val scope = rememberCoroutineScope()
        fun updateState(block: suspend () -> UserFormData) {
            scope.launch(SupervisorJob()) {
                formData = block()
            }
        }

        LaunchedEffect(formData) {
            onUserChange.invoke(formData.asUserUpdate())
        }

        Form(attrs = {
            id(formId)
            ukFormStacked
            onSubmit {
                it.preventDefault()
            }
        }) {
            Fieldset({ ukFieldset }) {
                Legend({ ukLegend }) { Text("Identity") }

                Div {
                    val loginId = "login"
                    Label(loginId, { ukFormLabel }) { Text("Login") }

                    with(formData) {
                        ValidatingTextInput(login, loginId, { login.isNotBlank() && loginIsAvailable }) {
                            updateState { setLogin(it.value) }
                        }
                    }
                }

                Div {
                    val fullnameId = "fullName"
                    Label(fullnameId, { ukFormLabel }) { Text("Full Name") }

                    with(formData) {
                        ValidatingTextInput(fullname, fullnameId, { fullname.isNotBlank() }) {
                            updateState { setFullname(it.value) }
                        }
                    }
                }
            }
            Fieldset({ ukFieldset.ukMargin.ukMarginProp(UkMarginPropValue.ukMarginLargeRight) }) {
                Legend({ ukLegend }) { Text("Roles") }
                Label {
                    with(formData) {
                        CheckboxInput(roles.contains(Role.ADMIN)) {
                            ukCheckbox
                            ukMarginSmallRight
                            onInput {
                                updateState { if (it.value) addRole(Role.ADMIN) else removeRole(Role.ADMIN) }
                            }
                        }
                        Text("Admin")
                    }
                }
            }

            Fieldset({ ukFieldset }) {
                Legend({ ukLegend }) { Text("Change Password") }

                val newPasswordId = "newPassword"
                Label(newPasswordId, { ukFormLabel }) { Text("New Password") }

                with(formData) {
                    ValidatingPasswordInput(
                        password,
                        newPasswordId,
                        { if (password.isBlank()) null else passwordStrengthEstimation is PasswordStrengthEstimation.Valid },
                        formData.passwordStrengthEstimation?.let { { renderPasswordEstimation(it) } }
                    ) {
                        updateState { setPassword(it.value) }
                    }
                }


                val confirmNewPasswordId = "confirmNewPassword"
                Label(confirmNewPasswordId, { ukFormLabel }) { Text("Confirm") }

                with(formData) {
                    ValidatingPasswordInput(
                        passwordConfirm,
                        confirmNewPasswordId,
                        { if (passwordConfirm.isBlank()) null else (password == passwordConfirm) }
                    ) {
                        updateState { setPasswordConfirm(it.value) }
                    }
                }
            }
        }
    }

    @Composable
    private fun renderPasswordEstimation(estimation: PasswordStrengthEstimation) {
        val isValid by remember(estimation) {
            derivedStateOf {
                estimation is PasswordStrengthEstimation.Valid
            }
        }

        Div({
            ukWidthLarge
            ukMarginSmallTop.ukMarginLeft.ukMarginSmallBottom
        }) {
            Div({
                classes(AppStyleSheet.css {
                    display(DisplayStyle.Grid)
                    gridTemplateColumns("auto 100fr")
                    gap(10.px)
                })
            }) {
                Div({
                    classes(AppStyleSheet.css {
                        justifySelf("end")
                    })
                }) {
                    Text("Strength : ")
                }
                Div {
                    Span({
                        if (isValid) ukTextSuccess else ukTextDanger
                    }) {
                        Text(estimation.scoreExplain)
                    }
                }

                Div({
                    classes(AppStyleSheet.css {
                        justifySelf("end")
                    })
                }) {
                    Text("Time to guess : ")
                }
                Div {
                    Span({
                        if (isValid) ukTextSuccess else ukTextDanger
                    }) {
                        Text(Duration.seconds(estimation.crackTimeInSeconds).toHumanString())
                    }
                }
            }

            if (estimation is PasswordStrengthEstimation.Invalid) {
                Div({ ukMarginTop }) {
                    if (estimation.warning.isNotEmpty()) {
                        Span({ ukTextWarning }) { Text(estimation.warning) }
                    }
                    if (estimation.suggestions.isNotEmpty()) {
                        Ul({ ukTextPrimary }) {
                            for (suggestion in estimation.suggestions) {
                                Li { Text(suggestion) }
                            }
                        }
                    }
                }
            }
        }
    }

    suspend fun save(appContext: AppContext, userUpdate: UserUpdate?) {
        if (userUpdate == null) return
        appContext.restClient.request(PutUserEndpoint) {
            describe {
                this.user(userUpdate)
                handler(this.created) {
                    UIkit.notification("User created", NotificationStatus.SUCCESS)
                }
                handler(this.updated) {
                    UIkit.notification("User updated", NotificationStatus.SUCCESS)
                }
            }
        }.perform()
    }

    data class UserFormData(
        private val appContext: AppContext,
        val originalUser: User?,
        val login: String = originalUser?.login ?: "",
        val loginIsAvailable: Boolean = true,
        val fullname: String = originalUser?.fullname ?: "",
        val roles: Set<String> = originalUser?.roles ?: emptySet(),
        val password: String = "",
        val passwordStrengthEstimation: PasswordStrengthEstimation? = null,
        val passwordConfirm: String = "",
    ) {
        suspend fun setFullname(fullname: String): UserFormData = copy(fullname = fullname)

        suspend fun addRole(role: String): UserFormData = copy(roles = roles + role)

        suspend fun removeRole(role: String): UserFormData = copy(roles = roles - role)

        suspend fun setPasswordConfirm(passwordConfirm: String): UserFormData = copy(passwordConfirm = passwordConfirm)

        suspend fun setLogin(login: String): UserFormData {
            if (login == originalUser?.login) return this.copy(login = login, loginIsAvailable = true)

            var loginIsAvailable = loginIsAvailable
            appContext.restClient.request(LoginIsAvailableEndpoint) {
                describe {
                    this.login(login)
                    handler(this.responseIsAvailable) { ctx ->
                        loginIsAvailable = this.responseIsAvailable.body(ctx)
                    }
                }
            }.perform()
            return this.copy(login = login, loginIsAvailable = loginIsAvailable)
        }

        suspend fun setPassword(password: String): UserFormData {
            println("Set password $password")

            if (password.isBlank()) return copy(password = password, passwordStrengthEstimation = null)

            var passwordEstimation = passwordStrengthEstimation
            appContext.restClient.request(EstimatePasswordStrengthEndpoint) {
                describe {
                    this.password(password)
                    handler(this.responsePasswordStrengthEstimation) { ctx ->
                        println("Receive value")
                        passwordEstimation = this.responsePasswordStrengthEstimation.body(ctx)
                    }
                }
            }.perform()

            val cp = copy(password = password, passwordStrengthEstimation = passwordEstimation)
            println("Return copy with $password : $cp")
            return cp
        }

        val isValid: Boolean
            get() {
                val isLoginValid = login.isNotBlank() && loginIsAvailable
                val isFullnameValid = fullname.isNotBlank()
                val isPasswordValid = (password.isBlank() && originalUser != null) || passwordStrengthEstimation is PasswordStrengthEstimation.Valid
                val isPasswordConfirmValid = passwordConfirm == password

                return isLoginValid && isFullnameValid && isPasswordValid && isPasswordConfirmValid
            }

        fun asUserUpdate(): UserUpdate? = if (!isValid) null else UserUpdate(
            originalUser?.id ?: UUIDv4.create(),
            login,
            fullname,
            roles,
            password.ifBlank { null }
        )
    }
}
