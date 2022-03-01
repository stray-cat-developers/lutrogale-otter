package io.mustelidae.otter.lutrogale.web.domain.grant

import io.mustelidae.otter.lutrogale.web.domain.grant.repository.UserPersonalGrantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserPersonalGrantFinder(
    private val userPersonalGrantRepository: UserPersonalGrantRepository
) {
    fun findByUserAndMenu(userId: Long, menuNavigationIds: List<Long>): List<UserPersonalGrant> {
        return userPersonalGrantRepository.findAllByUserIdAndMenuNavigationIdInAndStatusTrue(userId, menuNavigationIds)
    }
}
