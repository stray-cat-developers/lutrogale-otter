package io.mustelidae.otter.lutrogale.web.domain.user.repository

import io.mustelidae.otter.lutrogale.web.domain.user.User
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class UserDSLRepository : QuerydslRepositorySupport(User::class.java)
