package io.mustelidae.otter.lutrogale.web.domain.project.repository

import io.mustelidae.smoothcoatedotter.web.domain.project.Project
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created by HanJaehyun on 2016. 9. 21..
 */
@Repository
interface ProjectRepository : JpaRepository<Project, Long> {
    fun findByApiKey(apiKey: String): Project?
}
