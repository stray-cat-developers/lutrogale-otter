package io.mustelidae.otter.lutrogale.web.domain.user

import io.mustelidae.otter.lutrogale.config.DataNotFindException
import io.mustelidae.otter.lutrogale.web.domain.grant.UserAuthorityGrant
import io.mustelidae.otter.lutrogale.web.domain.grant.UserPersonalGrant
import io.mustelidae.otter.lutrogale.web.domain.user.api.UserResources
import io.mustelidae.otter.lutrogale.web.domain.user.repository.UserDSLRepository
import io.mustelidae.otter.lutrogale.web.domain.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors

@Service
@Transactional(readOnly = true)
class UserFinder(
    val userRepository: UserRepository,
    val userDSLRepository: UserDSLRepository,
) {
    fun findBy(id: Long): User =
        userRepository.findByIdOrNull(id)
            ?: throw DataNotFindException("사용자 정보가 없습니다.")

    fun findBy(email: String): User? = userRepository.findByEmail(email)

    fun findByEmails(emails: List<String>): List<User> = userRepository.findByEmailIn(emails)

    fun findBy(status: User.Status): List<User> =
        findAll()
            .stream()
            .filter { user: User -> user.status == status }
            .collect(Collectors.toList())

    fun findByLive(): List<User> = findAll().filter { it.status != User.Status.EXPIRE }

    fun findByStatusAllow(id: Long): User {
        val user = this.findBy(id)
        if (user.status != User.Status.ALLOW) {
            throw IllegalStateException("해당 사용자는 허가된 사용자가 아닙니다")
        }

        return user
    }

    fun findAllByJoinedProjectUsers(projectId: Long): List<User> = userDSLRepository.findAllByJoinedProjectUsers(projectId)

    fun getUserDetail(id: Long): UserResources.Reply.Detail {
        val user = this.findBy(id)
        val projects = user.getProjects()
        val userAuthorityGrants: List<UserAuthorityGrant> = user.userAuthorityGrants
        val userPersonalGrants: List<UserPersonalGrant> = user.userPersonalGrants

        return UserResources.Reply.Detail.from(user, projects, userAuthorityGrants, userPersonalGrants)
    }

    fun findAll(): List<User> = userRepository.findAll()
}
