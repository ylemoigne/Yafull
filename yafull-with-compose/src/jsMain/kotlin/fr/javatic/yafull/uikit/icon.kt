package fr.javatic.yafull.uikit

import external.webpackRequire
import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.Element
import org.w3c.dom.HTMLAnchorElement

fun requireUIKitIcons() = webpackRequire("uikit/dist/js/uikit-icons")

fun <T : Element> AttrsBuilder<T>.ukIcon(name: String, ratio: Int? = null): AttrsBuilder<T> = attr(
    "uk-icon",
    buildList {
        add("icon: $name")
        ratio?.let { add("ratio: $ratio") }
    }.joinToString(";")
)

object UkIcon {
    const val home = "home"
    const val signIn = "sign-in"
    const val signOut = "sign-out"
    const val user = "user"
    const val users = "users"
    const val lock = "lock"
    const val unlock = "unlock"
    const val settings = "settings"
    const val cog = "cog"
    const val nut = "nut"
    const val comment = "comment"
    const val commenting = "commenting"
    const val comments = "comments"
    const val hashtag = "hashtag"
    const val tag = "tag"
    const val cart = "cart"
    const val bag = "bag"
    const val creditCard = "credit-card"
    const val mail = "mail"
    const val receiver = "receiver"
    const val print = "print"
    const val search = "search"
    const val location = "location"

    const val bookmark = "bookmark"

    const val code = "code"
    const val paintBucket = "paint-bucket"
    const val camera = "camera"
    const val videoCamera = "video-camera"
    const val bell = "bell"
    const val microphone = "microphone"
    const val bolt = "bolt"
    const val star = "star"
    const val heart = "heart"
    const val happy = "happy"
    const val lifesaver = "lifesaver"
    const val rss = "rss"
    const val social = "social"
    const val gitBranch = "git-branch"
    const val gitFork = "git-fork"
    const val world = "world"
    const val calendar = "calendar"
    const val clock = "clock"
    const val history = "history"
    const val future = "future"
    const val pencil = "pencil"
    const val trash = "trash"

    const val move = "move"

    const val link = "link"
    const val question = "question"
    const val info = "info"
    const val warning = "warning"
    const val image = "image"
    const val thumbnails = "thumbnails"
    const val table = "table"
    const val list = "list"
    const val menu = "menu"
    const val grid = "grid"
    const val more = "more"
    const val moreVertical = "more-vertical"
    const val plus = "plus"
    const val plusCircle = "plus-circle"
    const val minus = "minus"
    const val minusCircle = "minus-circle"
    const val close = "close"
    const val check = "check"
    const val ban = "ban"
    const val refresh = "refresh"
    const val play = "play"

    const val playCircle = "play-circle"

    const val tv = "tv"
    const val desktop = "desktop"

    const val laptop = "laptop"

    const val tablet = "tablet"

    const val phone = "phone"

    const val tabletLandscape = "tablet-landscape"

    const val phoneLandscape = "phone-landscape"

    const val file = "file"
    const val fileText = "file-text"
    const val filePdf = "file-pdf"
    const val copy = "copy"

    const val fileEdit = "file-edit"

    const val folder = "folder"
    const val album = "album"
    const val push = "push"
    const val pull = "pull"

    const val server = "server"

    const val database = "database"
    const val cloudUpload = "cloud-upload"
    const val cloudDownload = "cloud-download"
    const val download = "download"

    const val upload = "upload"

    const val reply = "reply"
    const val forward = "forward"
    const val expand = "expand"
    const val shrink = "shrink"
    const val arrowUp = "arrow-up"

    const val arrowDown = "arrow-down"

    const val arrowLeft = "arrow-left"
    const val arrowRight = "arrow-right"
    const val chevronUp = "chevron-up"
    const val chevronDown = "chevron-down"
    const val chevronLeft = "chevron-left"

    const val chevronRight = "chevron-right"

    const val chevronDoubleLeft = "chevron-double-left"
    const val chevronDoubleRight = "chevron-double-right"
    const val triangleUp = "triangle-up"
    const val triangleDown = "triangle-down"
    const val triangleLeft = "triangle-left"

    const val triangleRight = "triangle-right"

    const val bold = "bold"

    const val italic = "italic"

    const val strikethrough = "strikethrough"

    const val quoteRight = "quote-right"

    const val _500px = "500px"
    const val behance = "behance"
    const val discord = "discord"
    const val dribbble = "dribbble"
    const val etsy = "etsy"
    const val facebook = "facebook"
    const val flickr = "flickr"
    const val foursquare = "foursquare"
    const val github = "github"
    const val githubAlt = "github-alt"

    const val gitter = "gitter"

    const val google = "google"
    const val instagram = "instagram"
    const val joomla = "joomla"
    const val linkedin = "linkedin"
    const val pagekit = "pagekit"
    const val pinterest = "pinterest"
    const val reddit = "reddit"
    const val soundcloud = "soundcloud"
    const val tiktok = "tiktok"

    const val tripadvisor = "tripadvisor"

    const val tumblr = "tumblr"
    const val twitch = "twitch"
    const val twitter = "twitter"
    const val uikit = "uikit"
    const val vimeo = "vimeo"
    const val whatsapp = "whatsapp"
    const val wordpress = "wordpress"
    const val xing = "xing"
    const val yelp = "yelp"
    const val youtube = "youtube"
}

val <T : HTMLAnchorElement> AttrsBuilder<T>.ukIconLink: AttrsBuilder<T> get() = fluentClasses("uk-icon-link")
val <T : HTMLAnchorElement> AttrsBuilder<T>.ukIconButton: AttrsBuilder<T> get() = fluentClasses("uk-icon-button")
val <T : HTMLAnchorElement> AttrsBuilder<T>.ukIconImage: AttrsBuilder<T> get() = fluentClasses("uk-icon-image")
