package io.mustelidae.otter.lutrogale.api.domain.user

import io.mustelidae.otter.lutrogale.web.domain.project.ProjectFinder
import io.mustelidae.otter.lutrogale.web.domain.user.UserInteraction
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserManagementInteraction(
    private val projectFinder: ProjectFinder,
    private val userInteraction: UserInteraction,
) {
    fun expireByEmail(
        apiKey: String,
        email: String,
    ) {
        projectFinder.findByLiveProjectOfApiKey(apiKey)
        userInteraction.expireByEmail(email)
    }

    fun expireByEmails(
        apiKey: String,
        emails: List<String>,
    ) {
        projectFinder.findByLiveProjectOfApiKey(apiKey)
        userInteraction.expireByEmails(emails)
    }
}
