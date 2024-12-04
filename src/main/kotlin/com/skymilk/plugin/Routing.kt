package com.skymilk.plugin

import com.skymilk.route.authRoute
import com.skymilk.route.commentRoute
import com.skymilk.route.followsRoute
import com.skymilk.route.postLikesRoute
import com.skymilk.route.postRoute
import com.skymilk.route.profileRoute
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authRoute()
        followsRoute()
        postRoute()
        profileRoute()
        commentRoute()
        postLikesRoute()

        //리소스 라우터 설정
        staticResources("/", "static")
    }
}
