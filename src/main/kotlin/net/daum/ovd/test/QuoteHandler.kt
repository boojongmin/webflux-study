package net.daum.ovd.test

import org.springframework.http.MediaType.APPLICATION_STREAM_JSON
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.SynchronousSink
import java.math.BigDecimal
import java.math.MathContext
import java.time.Duration
import java.time.Duration.ofMillis
import java.time.Instant
import java.util.*
import org.springframework.http.MediaType.TEXT_EVENT_STREAM



@Component
open class QuoteHandler(quoteGenerator: QuoteGenerator) {

    private val quoteStream: Flux<Quote>

    init {
        this.quoteStream = quoteGenerator.fetchQuoteStream(ofMillis(200))
    }

    fun fetchQuotesSSE(request: ServerRequest): Mono<ServerResponse> {
        return ok().contentType(TEXT_EVENT_STREAM)
                .body(this.quoteStream, Quote::class.java)
    }

    fun fetchQuotes(request: ServerRequest): Mono<ServerResponse> {
        return ok().contentType(APPLICATION_STREAM_JSON)
                .body(this.quoteStream, Quote::class.java)
    }
}

@Component
class QuoteGenerator {
    private val mathContext = MathContext(2)
    private val random = Random()
    private val prices = ArrayList<Quote>()

    init {
        this.prices.add(Quote("CTXS", 82.26))
        this.prices.add(Quote("DELL", 63.74))
        this.prices.add(Quote("GOjjjOG", 847.24))
        this.prices.add(Quote("MSFT", 65.11))
        this.prices.add(Quote("ORCL", 45.71))
        this.prices.add(Quote("RHT", 84.29))
        this.prices.add(Quote("VMW", 92.21))
    }

    fun fetchQuoteStream(period: Duration): Flux<Quote> {
        return Flux.generate(
                    { 0 },
                    { index:Int, sink: SynchronousSink<Quote> ->
                        val updatedQuote = updateQuote(prices[index])
                        sink.next(updatedQuote)
                        (index + 1) % prices.size
//                        prices.size
                })
                .zipWith(Flux.interval(period))
                .map { it.t1 }
				.map {
				    it.instant = Instant.now()
                    it
                }.share()
				.log()

    }

    private fun updateQuote(quote: Quote): Quote {
        val priceChange = quote.price
                .multiply(BigDecimal(0.05 * this.random.nextDouble()), this.mathContext)
        return Quote(quote.ticker, quote.price.add(priceChange))
    }

}

//data class Quote constructor(val ticker: String,
//                            var price: BigDecimal) {
//    companion object val MATH_CONTEXT = MathContext(2)
//    constructor(ticker: String, price: Double): this(ticker, BigDecimal(0)) {
//        this.price = BigDecimal(price, MATH_CONTEXT)
//    }
//
//}

class Quote private constructor()  {
    companion object val MATH_CONTEXT = MathContext(2)

    var ticker: String = ""
    var price: BigDecimal = BigDecimal(0)
    var instant: Instant = Instant.now()

    constructor(ticker: String, price: BigDecimal): this() {
        this.ticker = ticker
        this.price = price

    }
    constructor(ticker: String, price: Double): this() {
        this.ticker = ticker
        this.price = BigDecimal(price, MATH_CONTEXT)
    }
}

