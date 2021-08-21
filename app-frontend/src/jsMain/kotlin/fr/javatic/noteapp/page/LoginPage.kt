package fr.javatic.noteapp.page

import androidx.compose.runtime.Composable
import fr.javatic.noteapp.AppContext
import fr.javatic.noteapp.AppStyleSheet
import fr.javatic.noteapp.component.LoginForm
import fr.javatic.yafull.router.BrowserNavigation
import fr.javatic.yafull.uikit.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Text

object LoginPage {
    @Composable
    fun render(appContext: AppContext) {
        Div({
            ukSection(UkSectionStyle.primary().preserveColor)
            classes(AppStyleSheet.fullPage)
        }) {
            Div({ ukPositionCenter }) {
                Div({ ukCard(UkCardStyle.default().hover).ukCardBody }) {
                    H3({ ukCardTitle }) {
                        Text("Yafull Notes App")
                    }

                    LoginForm(appContext) {
                        BrowserNavigation.goTo("/")
                    }
                }
            }
        }
    }
}
