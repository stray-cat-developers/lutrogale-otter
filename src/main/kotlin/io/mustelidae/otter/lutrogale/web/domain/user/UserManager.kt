package io.mustelidae.otter.lutrogale.web.domain.user

import io.mustelidae.otter.lutrogale.web.commons.exception.ApplicationException
import io.mustelidae.otter.lutrogale.web.commons.exception.HumanErr
import io.mustelidae.otter.lutrogale.web.commons.exception.ProcessErr
import io.mustelidae.otter.lutrogale.web.domain.grant.UserAuthorityGrant
import io.mustelidae.otter.lutrogale.web.domain.grant.UserPersonalGrant
import io.mustelidae.otter.lutrogale.web.domain.user.api.UserResource
import io.mustelidae.otter.lutrogale.web.domain.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.ArrayList
import java.util.function.Consumer
import java.util.stream.Collectors

/**
 * Created by seooseok on 2016. 10. 5..
 */
@Service
class UserManager(
    val userRepository: UserRepository
) {

    fun createBy(email: String, name: String): User {
        val user = User(name, email)
        return userRepository.save(user)
    }

    fun createBy(email: String, name: String, status: User.Status): User {
        val user: User = User(name, email).apply {
            this.status = status
        }
        return userRepository.save(user)
    }

    fun createBy(email: String, name: String, department: String?, accessPrivacyInformation: Boolean): User {
        val user = this.createBy(email, name, User.Status.allow)
        user.department = department
        user.isPrivacy = accessPrivacyInformation
        return userRepository.save(user)
    }

    fun findBy(id: Long): User {
        return userRepository.findByIdOrNull(id)
            ?: throw ApplicationException(HumanErr.IS_EMPTY)
    }

    fun findBy(email: String): User {
        return userRepository.findByEmail(email)
    }

    fun findBy(status: User.Status): List<User> {
        return findAll()
            .stream()
            .filter { user: User -> user.status == status }
            .collect(Collectors.toList())
    }

    fun findByLive(): List<User> {
        return findAll().filter { it.status != User.Status.expire }
    }

    fun findByStatusAllow(id: Long): User {
        val user = this.findBy(id)
        require(user.status == User.Status.allow) { "해당 유저는 허가된 유저가 아닙니다" }
        return user
    }

    fun findAllByJoinedProjectUsers(projectId: Long): List<UserResource> {
        val users: List<User> = userRepository.findAllByJoinedProjectUsers(projectId)
        val userResources: MutableList<UserResource> = ArrayList<UserResource>()
        users.forEach(Consumer { user: User -> userResources.add(UserResource.of(user)) })
        return userResources
    }

    fun getUserDetail(id: Long): UserResource {
        val user = this.findBy(id)
        val projects = user.getProjects()
        val userAuthorityGrants: List<UserAuthorityGrant> = user.userAuthorityGrants
        val userPersonalGrants: List<UserPersonalGrant> = user.userPersonalGrants
        return UserResource.ofDetail(user, projects, userAuthorityGrants, userPersonalGrants)
    }

    fun expireBy(userIdGroup: List<Long>) {
        userIdGroup.forEach(
            Consumer { userId: Long ->
                val user = this.findBy(userId)
                user.expire()
            }
        )
    }

    fun modifyBy(userIdGroup: List<Long>, status: User.Status) {
        if (User.Status.expire === status) throw ApplicationException(
            HumanErr.INVALID_ARGS,
            "만료의 경우 별도 만료 API를 이용해주세요."
        )
        userIdGroup.forEach(
            Consumer { userId: Long ->
                val user = this.findBy(userId)
                if (User.Status.expire === user.status) throw ApplicationException(
                    ProcessErr.ALREADY_EXPIRED,
                    arrayOf(user.email)
                )
                user.status = status
            }
        )
    }

    fun modifyBy(userIdGroup: List<Long>, department: String?, isPrivacy: Boolean) {
        userIdGroup.forEach(
            Consumer { userId: Long ->
                val user = this.findBy(userId)
                user.department = department
                user.isPrivacy = isPrivacy
            }
        )
    }

    fun saveBy(user: User) {
        userRepository.save(user)
    }

    fun findAll(): List<User> {
        return userRepository.findAll()
    }
}
