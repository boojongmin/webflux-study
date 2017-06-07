package net.daum.ovd.validator

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.context.annotation.Bean
import org.springframework.data.repository.CrudRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.persistence.*

@Entity
class ValidateFormat(
        var type: ValidateType = ValidateType.NORMAL_SLOT,
        @Column(columnDefinition = "TEXT")
        var json: String = "",
        @Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long = 0L
)

enum class ValidateType {
    NORMAL_SLOT, CHANNEL_SLOT, EXTERNAL_API
}

@Configurable
class TestConfig {
    @Bean
    fun getObjectMapper(): ObjectMapper {
        return ObjectMapper()
    }

//    @Bean
//    fun taskExecutor(): TaskExecutor {
//        val executor = ThreadPoolTaskExecutor()
//        executor.corePoolSize = 30
//        executor.maxPoolSize = 100
//        return executor
//    }

    @Bean
    fun getEs(): ExecutorService? {
        // db connection pool의 갯수랑 같아야하지 않을까?
        return Executors.newFixedThreadPool(30)
    }

}

@Repository
interface ValidateFormatRepository: CrudRepository<ValidateFormat, Long>

@Service
open class ValidateService {
    private val log: Logger = LoggerFactory.getLogger(ValidateService::class.java)
    @Autowired var repo: ValidateFormatRepository? = null
    @Autowired var objMapper: ObjectMapper? = null

    @Async
    fun save(type: String, json: String): CompletableFuture<ResultVo> {
        return CompletableFuture.completedFuture(saveData(type, json))
    }

    fun saveData(type: String, json: String): ResultVo {
        log.error("hello~")
        try {
            objMapper?.readValue(json, Map::class.java)
        } catch(e: Exception) {
            println(e)
            return ResultVo(false, "json 포멧이 유효하지 않습니다")
        }
        try {
            val result = repo?.save(ValidateFormat(ValidateType.valueOf(type), json))
            result?.let {
                return ResultVo(true, "ok")
            }
        } catch (e: IllegalArgumentException) {
            println(e)
            return ResultVo(false, "없는 type 정보를 요청하셨습니다")
        }
        return ResultVo(false, "저장에 실패했습니다")
    }

    fun  read(): CompletableFuture<Iterable<*>>? {
        return CompletableFuture.completedFuture(repo?.findAll())
    }
}

data class ResultVo(val condition: Boolean, val message: String)



