[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![License][license-shield]][license-url]


<br />
<p align="center">
  <a href="https://github.com/ylemoigne/Yafull">
    <img src="yafull-logo.svg" alt="Logo" width="80" height="80">
  </a>

<h3 align="center">Yafull Stack</h3>

  <p align="center">
    A Kotlin fullstack lib & template for web applications
<!--
    <br />
    <a href="https://github.com/ylemoigne/Yafull"><strong>Explore the docs »</strong></a>
-->
    <br />
    <br />
    <a href="https://github.com/ylemoigne/Yafull/issues">Report Bug</a>
    ·
    <a href="https://github.com/ylemoigne/Yafull/issues">Request Feature</a>
  </p>
</p>

## About The Project

![example-app-screenshot-1](https://github.com/ylemoigne/Yafull/blob/master/screenshot1.png)
![example-app-screenshot-2](https://github.com/ylemoigne/Yafull/blob/master/screenshot2.png)

*Yafull* is a very experimental project aiming to be a fullstack toolkit for building web application

| It includes the following | State |
| --- | --- |
| A multiplatform(`JS Browser` and `JVM`) way API to describe REST APIs and safely implement and consume them.<br/>(It also automatically generate openapi/swagger definition) | Unstable but functional |
| A JWT Authentication | Stable but incomplete |
| A frontend router to describe & navigate urls and map them to render functions. | It's a miracle that it seems to work |
| A frontend css helpers | Stable but incomplete |
| A backend data layer | Works |
| An example application | It work (and It's a miracle)

Also test coverage is approaching the inexistant level and everything might break or expose strange behaviors. At least, you're warned.

### Built With

This project was started with the only purpose to play with some fun technology.

At the core there is :

* [Kotlin](https://kotlinlang.org/)
* [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
* [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)

On the backend, it showcase the use of

* [Vert.x](https://vertx.io/)
* [Jooq](https://www.jooq.org/)

On the frontend, the work is done trough :

* [Jetbrains Compose for Web](https://compose-web.ui.pages.jetbrains.team/)
* [UIKit](https://getuikit.com/)

Big thanks to them for their amazing works !

### Project's component state

`Vert.x`, `Jooq` and `UIKit` are stable and of production grade quality.

But the rest of the technologies used in this projet are not production ready. You must consider all of this as **highly experimental** (If the about section
was not clear.)

* `Kotlin Multiplatform` is Alpha (using the JS IR compiler which is also Alpha)
* `Compose for Web` is a technology preview

And the project use many experimental api :

* `kotlin.ExperimentalStdlibApi`
* `kotlin.time.ExperimentalTime` (with [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime) which is at `v0.2.1`)
* `kotlinx.serialization.ExperimentalSerializationApi`

And because their is no fun if don't add bleeding edge things, the gradle build use the following `feature preview` :

* `VERSION_CATALOGS`
* `TYPESAFE_PROJECT_ACCESSORS`

The project also contain ugly workaround, during development, the project was hit by :
https://youtrack.jetbrains.com/issue/KT-48214
https://github.com/JetBrains/compose-jb/issues/738
https://github.com/JetBrains/compose-jb/issues/1052

Also some improvment will be possible when this KEEP will land in Kotlin :
https://github.com/Kotlin/KEEP/blob/context-receivers/proposals/context-receivers.md

### Future

The frontend router needs a complete rewrite or at least a serious overhaul.

I did this project during my vacation to play with some fun and amazing tech. I don't know if I'll have the will and time to pursue it. (Of course if it grab
some interest, it can motivate me)

**Feel free to start playing and tinkering with it, and open issue to start discussion about how it feels and your opinions.**

## Running the example App

### Prerequisites

You'll need **either**

- Docker daemon running
- Running instance of PostgreSQL

## Getting Started

```
git clone https://github.com/ylemoigne/Yafull.git
cd Yafull
gradlew :app-backend:run :app-frontend:jsBrowserDevelopmentRun --continue
```

NOTE: A view of rest API is available at `http://localhost:2550/api/_doc/`

## License

Distributed under a modified version of the Hippocratic license. See `LICENSE.md` for more information.

## Contact

Yann Le Moigne - [@LeMoigneY](https://twitter.com/lemoigney) - ylemoigne-yafull@javatic.fr

## Acknowledgements

* [Swagger](https://swagger.io/)
* [Kotest](https://kotest.io/)
* [Testcontainers ](https://www.testcontainers.org/)

[contributors-shield]: https://img.shields.io/github/contributors/ylemoigne/Yafull.svg?style=for-the-badge

[contributors-url]: https://github.com//ylemoigne/Yafull/graphs/contributors

[forks-shield]: https://img.shields.io/github/forks/ylemoigne/Yafull.svg?style=for-the-badge

[forks-url]: https://github.com//ylemoigne/Yafull/network/members

[stars-shield]: https://img.shields.io/github/stars/ylemoigne/Yafull.svg?style=for-the-badge

[stars-url]: https://github.com//ylemoigne/Yafull/stargazers

[issues-shield]: https://img.shields.io/github/issues/ylemoigne/Yafull.svg?style=for-the-badge

[issues-url]: https://github.com/ylemoigne/Yafull/issues

[license-shield]: https://img.shields.io/badge/license-Hippocratic%20%2B%20Patent%20Clause-blue?style=for-the-badge

[license-url]: https://github.com/ylemoigne/Yafull/blob/master/LICENSE.md

[product-screenshot]: images/screenshot.png

