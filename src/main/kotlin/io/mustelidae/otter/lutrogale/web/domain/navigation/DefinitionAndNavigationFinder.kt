package io.mustelidae.otter.lutrogale.web.domain.navigation

import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.AuthorityNavigationUnitRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class DefinitionAndNavigationFinder(
    private val authorityNavigationUnitRepository: AuthorityNavigationUnitRepository,
) {

    fun findMappings(authorityDefinitionId: Long, menuNavigationIds: List<Long>): List<AuthorityNavigationUnit> {
        return authorityNavigationUnitRepository.findByStatusTrueAndAuthorityDefinitionIdAndMenuNavigationIdIn(authorityDefinitionId, menuNavigationIds) ?: emptyList()
    }
}
