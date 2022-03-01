package io.mustelidae.otter.lutrogale.web.domain.authority

import io.mustelidae.otter.lutrogale.web.commons.exception.ApplicationException
import io.mustelidae.otter.lutrogale.web.commons.exception.HumanErr
import io.mustelidae.otter.lutrogale.web.domain.authority.repository.AuthorityDefinitionRepository
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.function.Consumer

/**
 * Created by seooseok on 2016. 10. 4..
 */
@Service
@Transactional
class AuthorityDefinitionManager(
    private val authorityDefinitionRepository: AuthorityDefinitionRepository,
) {

    fun createBy(project: Project, name: String): AuthorityDefinition {
        val authorityDefinition: AuthorityDefinition = AuthorityDefinition(name).apply {
            setBy(project)
        }
        return authorityDefinitionRepository.save<AuthorityDefinition>(authorityDefinition)
    }

    @Deprecated("사용하지 말자")
    fun findBy(authorityDefinitionIdGroup: List<Long>): List<AuthorityDefinition> {
        return authorityDefinitionRepository.findByIdIn(authorityDefinitionIdGroup) ?: emptyList()
    }

    @Deprecated("사용하지 말자")
    fun findByLive(authorityDefinitionIds: List<Long>): List<AuthorityDefinition> {
        val authorityDefinitions = this.findBy(authorityDefinitionIds)
        authorityDefinitions.forEach(
            Consumer { authorityDefinition: AuthorityDefinition ->
                if (!authorityDefinition.status) throw ApplicationException(
                    HumanErr.IS_EXPIRE,
                    arrayOf(authorityDefinition.id)
                )
            }
        )
        return authorityDefinitions
    }
}
