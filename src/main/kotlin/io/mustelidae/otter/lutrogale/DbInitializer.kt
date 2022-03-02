package io.mustelidae.otter.lutrogale

import io.mustelidae.otter.lutrogale.web.commons.constant.OsoriConstant
import io.mustelidae.otter.lutrogale.web.domain.admin.Admin
import io.mustelidae.otter.lutrogale.web.domain.admin.repository.AdminRepository
import io.mustelidae.otter.lutrogale.web.domain.authority.api.AuthorityBundleResources
import io.mustelidae.otter.lutrogale.web.domain.authority.api.AuthorityController
import io.mustelidae.otter.lutrogale.web.domain.grant.api.UserGrantController
import io.mustelidae.otter.lutrogale.web.domain.navigation.api.MenuTreeController
import io.mustelidae.otter.lutrogale.web.domain.navigation.api.MenuTreeResources
import io.mustelidae.otter.lutrogale.web.domain.project.api.ProjectController
import io.mustelidae.otter.lutrogale.web.domain.project.api.ProjectResources
import io.mustelidae.otter.lutrogale.web.domain.user.api.UserController
import io.mustelidae.otter.lutrogale.web.domain.user.api.UserResources
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMethod

@Profile("default")
@Configuration
@Transactional
class DbInitializer(
    private val adminRepository: AdminRepository,
    private val projectController: ProjectController,
    private val menuTreeController: MenuTreeController,
    private val authorityController: AuthorityController,
    private val userController: UserController,
    private val userGrantController: UserGrantController
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        setupAdminIfNotExist()
        val projectId = addProject()
        setupMenuTree(projectId)
        setupDefinition(projectId)
        val userId = addUser()
        assignGrant(userId, projectId)
    }

    private fun assignGrant(userId: Long, projectId: Long) {
        userGrantController.assignAuthorityGrant(userId, projectId, listOf(1))
        userGrantController.assignPersonalGrant(userId, projectId, listOf(5))
    }

    private fun addUser(): Long {
        return userController.create(UserResources.Request(
            "lutrogale@otter.com",
            "Lutrogale Otter",
            false,
            "Otter World"
        )).content!!
    }

    private fun setupDefinition(projectId: Long){
        authorityController.create(projectId, AuthorityBundleResources.Request.AuthorityBundle(
            "Only View Group",
            listOf(1,2,3)
        ))
    }

    private fun setupMenuTree(projectId:Long){
        menuTreeController.createBranch(projectId, MenuTreeResources.Request.Branch(
            "j1_1",
            "1",
            "Get Reviews",
            "/applications/{name}/reviews",
            OsoriConstant.NavigationType.menu,
            RequestMethod.GET
        ))

        menuTreeController.createBranch(projectId, MenuTreeResources.Request.Branch(
            "j1_3",
            "j1_1",
            "Personal Review",
            "/{reviewId}",
            OsoriConstant.NavigationType.function,
            RequestMethod.GET
        ))

        menuTreeController.createBranch(projectId, MenuTreeResources.Request.Branch(
            "j1_6",
            "1",
            "Add Review",
            "/applications/{name}/reviews",
            OsoriConstant.NavigationType.function,
            RequestMethod.POST
        ))

        menuTreeController.createBranch(projectId, MenuTreeResources.Request.Branch(
            "j1_9",
            "j1_1",
            "Modify Review",
            "/{reviewId}",
            OsoriConstant.NavigationType.function,
            RequestMethod.POST
        ))

        menuTreeController.createBranch(projectId, MenuTreeResources.Request.Branch(
            "j1_10",
            "j1_1",
            "Delete Review",
            "/{reviewId}",
            OsoriConstant.NavigationType.function,
            RequestMethod.DELETE
        ))
    }

    private fun addProject(): Long {
        return projectController.create(ProjectResources.Request(
            "Otter Project", "This is Sample Project"
        )).content!!
    }

    private fun setupAdminIfNotExist() {
        val supervisor = adminRepository.findByIdOrNull(1)

        if (supervisor == null) {
            val admin = Admin(
                "admin@osori.com",
                "슈퍼 관리자",
                "오소리의 모든 권한을 가지고 있습니다.",
                "/static/dist/img/osori.png"
            ).apply {
                setPassword("admin")
                status = true
            }
            adminRepository.save(admin)
        }
    }
}
