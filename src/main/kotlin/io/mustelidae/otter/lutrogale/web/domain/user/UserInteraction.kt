package io.mustelidae.otter.lutrogale.web.domain.user

import io.mustelidae.otter.lutrogale.common.DefaultError
import io.mustelidae.otter.lutrogale.common.ErrorCode
import io.mustelidae.otter.lutrogale.config.DataNotFindException
import io.mustelidae.otter.lutrogale.config.InvalidArgumentException
import io.mustelidae.otter.lutrogale.config.PolicyException
import io.mustelidae.otter.lutrogale.web.domain.grant.UserGrantInteraction
import io.mustelidae.otter.lutrogale.web.domain.user.api.UserResources
import io.mustelidae.otter.lutrogale.web.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by seooseok on 2016. 10. 5..
 */
@Service
@Transactional
class UserInteraction(
    private val userRepository: UserRepository,
    private val userFinder: UserFinder,
    private val userGrantInteraction: UserGrantInteraction,
) {
    fun createBy(
        email: String,
        name: String,
        status: User.Status,
    ): User {
        val user: User =
            User(name, email).apply {
                this.status = status
            }
        return userRepository.save(user)
    }

    fun createBy(
        email: String,
        name: String,
        accessPrivacyInformation: Boolean,
        department: String?,
    ): User {
        val user = this.createBy(email, name, User.Status.ALLOW)
        user.department = department
        user.isPrivacy = accessPrivacyInformation
        return userRepository.save(user)
    }

    fun expireBy(userIdGroup: List<Long>) {
        val users =
            userIdGroup.map {
                userFinder.findBy(it)
            }

        users.map { it.expire() }
        userRepository.saveAll(users)
    }

    fun expireByEmail(email: String) {
        val user = userFinder.findBy(email) ?: throw DataNotFindException(email, "사용자 정보가 없습니다.")
        user.expire()
        userRepository.save(user)
    }

    fun expireByEmails(emails: List<String>) {
        val users = emails.mapNotNull { userFinder.findBy(it) }
        users.forEach { it.expire() }
        userRepository.saveAll(users)
    }

    fun modifyBy(
        userIds: List<Long>,
        status: User.Status,
    ) {
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

    fun bulkCreateBy(
        emails: List<String>,
        projectId: Long?,
        authorityDefinitionId: Long?,
        initialStatus: User.Status,
    ): List<UserResources.Reply.BatchRegister> {
        if (initialStatus == User.Status.EXPIRE || initialStatus == User.Status.REJECT) {
            throw InvalidArgumentException("대량 등록 초기 상태는 ALLOW 또는 WAIT만 허용됩니다.")
        }
        return emails.map { email ->
            val existing = userFinder.findBy(email)
            if (existing != null) {
                UserResources.Reply.BatchRegister(email, UserResources.Reply.BatchRegister.Outcome.SKIPPED, null)
            } else {
                val user = createBy(email, email.substringBefore("@"), initialStatus)
                if (initialStatus == User.Status.ALLOW && projectId != null && authorityDefinitionId != null) {
                    userGrantInteraction.addByAuthorityGrant(user.id!!, projectId, listOf(authorityDefinitionId))
                }
                UserResources.Reply.BatchRegister(email, UserResources.Reply.BatchRegister.Outcome.SUCCESS, user.id)
            }
        }
    }

    fun modifyBy(
        userId: Long,
        name: String,
        department: String,
        isPrivacy: Boolean,
    ) {
        val user = userFinder.findBy(userId)
        user.apply {
            this.department = department
            this.name = name
            this.isPrivacy = isPrivacy
        }
        userRepository.save(user)
    }
}
