package io.mustelidae.otter.lutrogale.web.domain.admin

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test

internal class AdminTest {
    @Test
    fun `Admin-of 로 생성한 어드민은 즉시 활성 상태이다`() {
        val admin = Admin.of("test@test.com", "password", "테스트", null, null)
        admin.status shouldBe true
    }

    @Test
    fun `Admin-of 에 SUPER 역할을 지정하면 SUPER 어드민이 생성된다`() {
        val admin = Admin.of("super@test.com", "password", "슈퍼", null, null, AdminRole.SUPER)
        admin.role shouldBe AdminRole.SUPER
    }

    @Test
    fun `Admin-of 에 역할을 지정하지 않으면 기본값 REGULAR이다`() {
        val admin = Admin.of("regular@test.com", "password", "레귤러", null, null)
        admin.role shouldBe AdminRole.REGULAR
    }

    @Test
    fun `expire 호출 후 어드민은 비활성 상태이다`() {
        val admin = Admin.of("test@test.com", "password", "테스트", null, null)
        admin.expire()
        admin.status shouldBe false
    }

    @Test
    fun `setPassword 는 비밀번호를 BCrypt 해시로 저장한다`() {
        val admin = Admin.of("test@test.com", "password", "테스트", null, null)
        admin.pw shouldNotBe null
        admin.pw shouldNotBe "password"
        admin.pw!!.startsWith("\$2a\$") shouldBe true
    }

    @Test
    fun `matchesPassword 는 올바른 비밀번호에 true를 반환한다`() {
        val admin = Admin.of("test@test.com", "password", "테스트", null, null)
        admin.matchesPassword("password") shouldBe true
    }

    @Test
    fun `matchesPassword 는 잘못된 비밀번호에 false를 반환한다`() {
        val admin = Admin.of("test@test.com", "password", "테스트", null, null)
        admin.matchesPassword("wrong") shouldBe false
    }
}
