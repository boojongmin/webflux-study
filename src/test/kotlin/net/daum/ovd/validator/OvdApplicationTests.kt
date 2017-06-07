package net.daum.ovd.validator

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Repository
import org.springframework.test.annotation.Commit
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.junit4.SpringRunner
import javax.transaction.Transactional

@RunWith(SpringRunner::class)
@SpringBootTest
@Transactional
open class OvdApplicationTests {

	@Autowired var repo: ValidateFormatRepository? = null
	@Autowired var service: ValidateService? = null


	@Test
	@Rollback(false)
	fun contextLoads() {
		repo?.save(ValidateFormat(ValidateType.NORMAL_SLOT, "{a: 1}"))
		println(repo?.count())
	}

	@Test
	fun test01() {
		val save1 = service?.save("NORMAL_SLOT", "{}")
		val save2 = service?.save("NORMAL_SLOT", "")
		val save3 = service?.save("UNKOWN_SLOT", "{}")
		assertThat(save1?.get()).isEqualTo(ResultVo(true, "ok"))
		assertThat(save2?.get()?.condition).isEqualTo(false)
		assertThat(save3?.get()?.condition).isEqualTo(false)
	}



}
