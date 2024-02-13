package io.mustelidae.otter.lutrogale.web.domain.user

import io.mustelidae.otter.lutrogale.common.DefaultError
import io.mustelidae.otter.lutrogale.common.ErrorCode
import io.mustelidae.otter.lutrogale.config.InvalidArgumentException
import io.mustelidae.otter.lutrogale.config.PolicyException
import io.mustelidae.otter.lutrogale.web.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

/**
 * Created by seooseok on 2016. 10. 5..
 */
@Service
class UserInteraction(
    val userRepository: UserRepository,
    val userFinder: UserFinder,
) {

    fun createBy(email: String, name: String, status: User.Status): User {
        val user: User = User(name, email).apply {
            this.status = status
        }
        return userRepository.save(user)
    }

    fun createBy(email: String, name: String, accessPrivacyInformation: Boolean, department: String?): User {
        val user = this.createBy(email, name, User.Status.ALLOW)
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
        if (User.Status.EXPIRE === status) {
            throw InvalidArgumentException("만료의 경우 별도 만료 API를 이용해주세요.")
        }

        userIds.forEach {
            val user = userFinder.findBy(it)
            if (User.Status.EXPIRE === user.status) {
                throw PolicyException(DefaultError(ErrorCode.PL02))
            }
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
