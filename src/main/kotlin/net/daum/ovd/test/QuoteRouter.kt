package net.daum.ovd.test

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

import org.springframework.http.MediaType.APPLICATION_STREAM_JSON
import org.springframework.http.MediaType.TEXT_EVENT_STREAM
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RequestPredicates.accept

@Configuration
class QuoteRouter {

    @Bean
    fun route(quoteHandler: QuoteHandler): RouterFunction<ServerResponse> {
        return RouterFunctions
                .route<ServerResponse>(GET("/quotes").and(accept(TEXT_EVENT_STREAM)), HandlerFunction<ServerResponse> { quoteHandler.fetchQuotesSSE(it) })
                .andRoute(GET("/quotes").and(accept(APPLICATION_STREAM_JSON)), HandlerFunction<ServerResponse> { quoteHandler.fetchQuotes(it) })
    }
}
