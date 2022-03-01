package io.mustelidae.otter.lutrogale.web.domain.home

import io.mustelidae.otter.lutrogale.web.domain.user.User
import io.mustelidae.otter.lutrogale.web.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

/**
 * Created by seooseok on 2016. 10. 5..
 */
@Service
class DashBoard(
    private val userRepository: UserRepository
) {
    fun countByLiveTotalUsers(): Int {
        val statusGroup: MutableList<User.Status> = ArrayList()
        statusGroup.add(User.Status.allow)
        statusGroup.add(User.Status.reject)
        statusGroup.add(User.Status.wait)
        return userRepository.countAllByStatusIn(statusGroup)
    }

    fun countByLiveWaitUsers(): Int {
        val statusGroup: MutableList<User.Status> = ArrayList()
        statusGroup.add(User.Status.wait)
        return userRepository.countAllByStatusIn(statusGroup)
    }

    fun countByLiveRejectUsers(): Int {
        val statusGroup: MutableList<User.Status> = ArrayList()
        statusGroup.add(User.Status.reject)
        return userRepository.countAllByStatusIn(statusGroup)
    }

    fun countByLiveAllowUsers(): Int {
        val statusGroup: MutableList<User.Status> = ArrayList()
        statusGroup.add(User.Status.allow)
        return userRepository.countAllByStatusIn(statusGroup)
    }
}
