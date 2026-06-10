package io.mustelidae.otter.lutrogale.web.domain.grant

import io.mustelidae.otter.lutrogale.web.domain.grant.repository.UserAuthorityGrantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserAuthorityGrantFinder(
    private val userAuthorityGrantRepository: UserAuthorityGrantRepository,
) {
    fun findByUserAndDefinition(
        userId: Long,
        authorityDefinitionIds: List<Long>,
    ): List<UserAuthorityGrant> =
        userAuthorityGrantRepository.findAllByUserIdAndAuthorityDefinitionIdInAndStatusTrue(userId, authorityDefinitionIds)
            ?: emptyList()
}
