package io.mustelidae.otter.lutrogale.web.domain.authority

import io.mustelidae.otter.lutrogale.common.DefaultError
import io.mustelidae.otter.lutrogale.common.ErrorCode
import io.mustelidae.otter.lutrogale.config.DataNotFindException
import io.mustelidae.otter.lutrogale.config.DataPermissionException
import io.mustelidae.otter.lutrogale.config.PolicyException
import io.mustelidae.otter.lutrogale.web.domain.authority.repository.AuthorityDefinitionRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthorityDefinitionFinder(
    private val authorityDefinitionRepository: AuthorityDefinitionRepository,
) {

    fun findByLive(authorityDefinitionId: Long): AuthorityDefinition {
        val authorityDefinition = this.findBy(authorityDefinitionId)
        if (!authorityDefinition.status) throw throw PolicyException(DefaultError(ErrorCode.PL02, "해당 사용자는 로그인 권한이 만료되었습니다."))
        return authorityDefinition
    }

    fun findByLive(projectId: Long, authorityDefinitionId: Long): AuthorityDefinition {
        val authorityDefinition = this.findByLive(authorityDefinitionId)
        if (authorityDefinition.project!!.id != projectId) {
            throw DataPermissionException("해당프로젝트의 권한이 아닙니다.")
        }
        return authorityDefinition
    }

    fun findBy(defineId: Long): AuthorityDefinition {
        return authorityDefinitionRepository.findByIdOrNull(defineId) ?: throw DataNotFindException("정의된 권한이 없습니다.")
    }

    fun findListBy(projectId: Long): List<AuthorityDefinition> {
        return authorityDefinitionRepository.findByProjectIdAndStatusTrue(projectId) ?: emptyList()
    }

    fun findBy(authorityDefinitionIdGroup: List<Long>): List<AuthorityDefinition> {
        return authorityDefinitionRepository.findByIdIn(authorityDefinitionIdGroup) ?: emptyList()
    }

    fun findByLive(authorityDefinitionIds: List<Long>): List<AuthorityDefinition> {
        val authorityDefinitions = this.findBy(authorityDefinitionIds)

        authorityDefinitions.forEach {
            if (!it.status) {
                throw PolicyException(DefaultError(ErrorCode.PL02, "해당 사용자는 로그인 권한이 만료되었습니다.", causeBy = mapOf("id" to it.id)))
            }
        }

        return authorityDefinitions
    }
}
