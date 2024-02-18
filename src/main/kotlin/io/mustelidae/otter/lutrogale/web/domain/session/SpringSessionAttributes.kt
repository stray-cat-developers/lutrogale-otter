package io.mustelidae.otter.lutrogale.web.domain.session

import jakarta.persistence.Column
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.io.Serializable

/**
 * Created by seooseok on 2016. 9. 28..
 * Spring session 에서 사용 할 DB 테이블
 * @link org/springframework/session/jdbc/schema-h2.sql
 */

@Table(
    name = "SPRING_SESSION_ATTRIBUTES",
)
class SpringSessionAttributes : Serializable {
    @Id
    @Column(name = "ATTRIBUTE_NAME", length = 200)
    var attributeName: String? = null

    @Lob
    @Column(name = "ATTRIBUTE_BYTES")
    var attributeBytes: Byte? = null

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(referencedColumnName = "PRIMARY_ID", name = "SESSION_PRIMARY_ID", foreignKey = ForeignKey(name = "SPRING_SESSION_ATTRIBUTES_FK"))
    var springSession: SpringSession? = null
}
