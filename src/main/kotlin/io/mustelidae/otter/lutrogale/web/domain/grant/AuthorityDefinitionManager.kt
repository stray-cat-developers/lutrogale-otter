package io.mustelidae.otter.lutrogale.web.domain.grant

import io.mustelidae.smoothcoatedotter.web.commons.exception.ApplicationException
import io.mustelidae.smoothcoatedotter.web.commons.exception.HumanErr
import io.mustelidae.smoothcoatedotter.web.domain.grant.repository.AuthorityDefinitionRepository
import io.mustelidae.smoothcoatedotter.web.domain.project.Project
import org.springframework.data.repository.findByIdOrNull
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

    fun findBy(defineId: Long): AuthorityDefinition {
        return authorityDefinitionRepository.findByIdOrNull(defineId) ?: throw ApplicationException(HumanErr.IS_EMPTY)
    }

    fun findListBy(projectId: Long): List<AuthorityDefinition> {
        return authorityDefinitionRepository.findByProjectIdAndStatusTrue(projectId) ?: emptyList()
    }

    fun findByLive(authorityDefinitionId: Long): AuthorityDefinition {
        val authorityDefinition = this.findBy(authorityDefinitionId)
        if (!authorityDefinition.status) throw ApplicationException(HumanErr.IS_EXPIRE)
        return authorityDefinition
    }

    fun findBy(authorityDefinitionIdGroup: List<Long>): List<AuthorityDefinition> {
        return authorityDefinitionRepository.findByIdIn(authorityDefinitionIdGroup) ?: emptyList()
    }

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
