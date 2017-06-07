package net.daum.ovd.validator

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.RequestPredicates.*
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

//@RestController
//open class TestController(val service: ValidateService) {
//
//    @RequestMapping(value="/api/create", method= arrayOf(RequestMethod.POST))
//    fun get(@RequestBody json: Map<Any, Any>): Mono<ResultVo> {
//        return Mono.fromFuture(service.save("1", "2"))
//
//    }
//
//
//}

@Component
open class ValidateHandler(val service: ValidateService) {
    fun save(request: ServerRequest): Mono<ServerResponse> {
        val type = request.queryParam("type").orElse("")
        val json = request.queryParam("json").orElse("")
        return request.bodyToMono(Map::class.java)
                .flatMap {
                    val type = it["type"] as String
                    val json = it["json"] as String
                    ok().contentType(APPLICATION_JSON)
//                            .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
//                            .header("Access-Control-Allow-Headers", "Content-Type")
                            .body(Mono.fromFuture(service.save(type, json)), ResultVo::class.java)
                }

    }

    fun read(request: ServerRequest): Mono<ServerResponse> {
        return ok().contentType(APPLICATION_JSON)
                .body(Mono.fromFuture(service.read()), Iterable::class.java)
    }
}

@Configuration
open class ValidatorRouter {
    @Bean
    fun route(handler: ValidateHandler): RouterFunction<ServerResponse> {
        return RouterFunctions
                .route<ServerResponse>(POST("/api/create").and(accept(APPLICATION_JSON)), HandlerFunction<ServerResponse> { handler.save(it) })
                .andRoute(GET("/api/read").and(accept(APPLICATION_JSON)), HandlerFunction<ServerResponse> { handler.read(it) })
    }
}

