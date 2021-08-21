package fr.javatic.noteapp

import androidx.compose.runtime.*
import fr.javatic.noteapp.note.Note
import fr.javatic.noteapp.note.rest.GetNotesEndpoint
import fr.javatic.noteapp.user.User
import fr.javatic.noteapp.user.rest.GetUserEndpoint
import fr.javatic.noteapp.user.rest.GetUsersEndpoint
import fr.javatic.util.UUIDv4
import fr.javatic.yafull.rest.RestClient

class AppClientApi(private val restClient: RestClient) {
    @Composable
    fun getUser(userId: UUIDv4): State<User?> = produceState<User?>(null, userId) {
        restClient.request(GetUserEndpoint) {
            describe {
                this.id(userId)
                handler(this.reponseUser) {
                    value = this.reponseUser.body(it)
                }
            }
        }.perform()
    }

    private var usersRefreshKey by mutableStateOf(0L)
    fun refreshUsers() {
        usersRefreshKey++
    }

    @Composable
    fun getUsers(refreshKey: Long = 0L): State<List<User>> = produceState(emptyList(), refreshKey) {
        restClient.request(GetUsersEndpoint) {
            describe {
                handler(this.responseUsers) {
                    value = this.responseUsers.body(it)
                }
            }
        }.perform()
    }

    private var notesRefreshKey by mutableStateOf(0L)
    fun refreshNotes() {
        notesRefreshKey++
    }

    @Composable
    fun getNotes(): State<List<Note>> = produceState(emptyList(), notesRefreshKey) {
        restClient.request(GetNotesEndpoint) {
            describe {
                handler(this.responseOk) {
                    value = this.responseOk.body(it)
                }
            }
        }.perform()
    }
}
