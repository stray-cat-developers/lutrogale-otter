package io.mustelidae.otter.lutrogale.web.domain.authority

import io.mustelidae.otter.lutrogale.web.domain.authority.repository.AuthorityDefinitionRepository
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by seooseok on 2016. 10. 4..
 */
@Service
@Transactional
class AuthorityDefinitionInteraction(
    private val authorityDefinitionRepository: AuthorityDefinitionRepository,
) {

    fun createBy(project: Project, name: String): AuthorityDefinition {
        val authorityDefinition: AuthorityDefinition = AuthorityDefinition(name).apply {
            setBy(project)
        }
        return authorityDefinitionRepository.save(authorityDefinition)
    }
}
