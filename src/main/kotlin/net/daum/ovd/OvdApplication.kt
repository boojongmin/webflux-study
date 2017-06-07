package net.daum.ovd

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication(scanBasePackages = arrayOf("net.daum.ovd.validator"))
@EnableAsync
class OvdApplication

fun main(args: Array<String>) {
    SpringApplication.run(OvdApplication::class.java, *args)
}
