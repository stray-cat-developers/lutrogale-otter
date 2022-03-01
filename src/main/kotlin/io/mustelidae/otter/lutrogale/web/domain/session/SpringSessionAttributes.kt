package io.mustelidae.otter.lutrogale.web.domain.session

import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ForeignKey
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * Created by seooseok on 2016. 9. 28..
 * Spring session 에서 사용 할 DB 테이블
 * @link org/springframework/session/jdbc/schema-h2.sql
 */

@Entity
@Table(
    name = "SPRING_SESSION_ATTRIBUTES"
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
