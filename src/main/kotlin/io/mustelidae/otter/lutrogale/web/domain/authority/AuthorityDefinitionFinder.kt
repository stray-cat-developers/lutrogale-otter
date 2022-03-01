package io.mustelidae.otter.lutrogale.web.domain.authority

import io.mustelidae.otter.lutrogale.web.commons.exception.ApplicationException
import io.mustelidae.otter.lutrogale.web.commons.exception.HumanErr
import io.mustelidae.otter.lutrogale.web.domain.authority.repository.AuthorityDefinitionRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthorityDefinitionFinder(
    private val authorityDefinitionRepository: AuthorityDefinitionRepository
) {

    fun findByLive(authorityDefinitionId: Long): AuthorityDefinition {
        val authorityDefinition = this.findBy(authorityDefinitionId)
        if (!authorityDefinition.status) throw ApplicationException(HumanErr.IS_EXPIRE)
        return authorityDefinition
    }

    fun findByLive(projectId:Long,authorityDefinitionId: Long): AuthorityDefinition {
        val authorityDefinition = this.findByLive(authorityDefinitionId)
        if (authorityDefinition.project!!.id != projectId)
            throw ApplicationException(HumanErr.INVALID_INCLUDE)
        return authorityDefinition
    }

    fun findBy(defineId: Long): AuthorityDefinition {
        return authorityDefinitionRepository.findByIdOrNull(defineId) ?: throw ApplicationException(HumanErr.IS_EMPTY)
    }

    fun findListBy(projectId: Long): List<AuthorityDefinition> {
        return authorityDefinitionRepository.findByProjectIdAndStatusTrue(projectId) ?: emptyList()
    }

}