package io.mustelidae.otter.lutrogale.web.domain.user

import io.mustelidae.otter.lutrogale.web.commons.exception.ApplicationException
import io.mustelidae.otter.lutrogale.web.commons.exception.HumanErr
import io.mustelidae.otter.lutrogale.web.commons.exception.ProcessErr
import io.mustelidae.otter.lutrogale.web.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

/**
 * Created by seooseok on 2016. 10. 5..
 */
@Service
class UserManager(
    val userRepository: UserRepository,
    val userFinder: UserFinder
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

    fun createBy(email: String, name: String, accessPrivacyInformation: Boolean, department: String?): User {
        val user = this.createBy(email, name, User.Status.allow)
        user.department = department
        user.isPrivacy = accessPrivacyInformation
        return userRepository.save(user)
    }

    fun expireBy(userIdGroup: List<Long>) {

        val users = userIdGroup.map {
            userFinder.findBy(it)
        }

        users.map { it.expire() }
        userRepository.saveAll(users)
    }

    fun modifyBy(userIds: List<Long>, status: User.Status) {
        if (User.Status.expire === status) throw ApplicationException(
            HumanErr.INVALID_ARGS,
            "만료의 경우 별도 만료 API를 이용해주세요."
        )

        userIds.forEach {
            val user = userFinder.findBy(it)
            if (User.Status.expire === user.status)
                throw ApplicationException(
                    ProcessErr.ALREADY_EXPIRED,
                    arrayOf(user.email)
                )
            user.status = status

            userRepository.save(user)
        }
    }

    fun modifyBy(userId: Long, name: String, department: String, isPrivacy: Boolean) {
        val user = userFinder.findBy(userId)
        user.apply {
            this.department = department
            this.name = name
            this.isPrivacy = isPrivacy
        }
        userRepository.save(user)
    }
}
