package io.mustelidae.otter.lutrogale.web.domain.session

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table

/**
 * Created by seooseok on 2016. 9. 28..
 * Spring session 에서 사용 할 DB 테이블
 * @link org/springframework/session/jdbc/schema-h2.sql
 */

@Entity
@Table(
    name = "SPRING_SESSION",
    indexes = [
        Index(name = "SPRING_SESSION_PK", columnList = "PRIMARY_ID", unique = true),
        Index(name = "SPRING_SESSION_IX1", columnList = "SESSION_ID", unique = true),
        Index(name = "SPRING_SESSION_IX2", columnList = "EXPIRY_TIME"),
        Index(name = "SPRING_SESSION_IX3", columnList = "PRINCIPAL_NAME"),
    ]
)
class SpringSession {
    @Id
    @Column(name = "PRIMARY_ID", length = 36)
    var primaryId: String? = null

    @Column(name = "SESSION_ID", length = 36)
    var sessionId: String? = null

    @Column(name = "CREATION_TIME", nullable = false, length = 19)
    var creationTime: Long = 0

    @Column(name = "LAST_ACCESS_TIME", nullable = false, length = 19)
    var lastAccessTime: Long = 0

    @Column(name = "MAX_INACTIVE_INTERVAL", nullable = false, length = 10)
    var maxInactiveInterval = 0

    @Column(name = "EXPIRY_TIME", nullable = false)
    var expiryTime: Long = 0

    @Column(name = "PRINCIPAL_NAME", length = 100)
    var principalName: String? = null
}
