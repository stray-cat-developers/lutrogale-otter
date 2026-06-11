package io.mustelidae.otter.lutrogale

import io.mustelidae.otter.lutrogale.common.Constant
import io.mustelidae.otter.lutrogale.web.domain.admin.Admin
import io.mustelidae.otter.lutrogale.web.domain.admin.AdminRole
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
    private val userGrantController: UserGrantController,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        setupAdminIfNotExist()
        val timestamp = System.currentTimeMillis()
        val projectId = addProject(timestamp)
        setupMenuTree(projectId)
        val authorityDefinitionId = setupDefinition(projectId)
        val userId = addUser(timestamp)
        assignGrant(userId, projectId, authorityDefinitionId)
    }

    private fun assignGrant(
        userId: Long,
        projectId: Long,
        authorityDefinitionId: Long,
    ) {
        userGrantController.assignAuthorityGrant(userId, projectId, listOf(authorityDefinitionId))
    }

    private fun addUser(timestamp: Long): Long =
        userController
            .create(
                UserResources.Request.Create(
                    "test-$timestamp@otter.com",
                    "Lutrogale Otter",
                    false,
                    "Otter World",
                ),
            ).content!!

    private fun setupDefinition(projectId: Long): Long =
        authorityController
            .create(
                projectId,
                AuthorityBundleResources.Request.AuthorityBundle(
                    "Only View Group",
                    listOf(1, 2, 3),
                ),
            ).content!!

    private fun setupMenuTree(projectId: Long) {
        menuTreeController.createBranch(
            projectId,
            MenuTreeResources.Request.Branch(
                "j1_1",
                "1",
                "Get Reviews",
                "/applications/{name}/reviews",
                Constant.NavigationType.MENU,
                RequestMethod.GET,
            ),
        )

        menuTreeController.createBranch(
            projectId,
            MenuTreeResources.Request.Branch(
                "j1_3",
                "j1_1",
                "Personal Review",
                "/{reviewId}",
                Constant.NavigationType.FUNCTION,
                RequestMethod.GET,
            ),
        )

        menuTreeController.createBranch(
            projectId,
            MenuTreeResources.Request.Branch(
                "j1_6",
                "1",
                "Add Review",
                "/applications/{name}/reviews",
                Constant.NavigationType.FUNCTION,
                RequestMethod.POST,
            ),
        )

        menuTreeController.createBranch(
            projectId,
            MenuTreeResources.Request.Branch(
                "j1_9",
                "j1_1",
                "Modify Review",
                "/{reviewId}",
                Constant.NavigationType.FUNCTION,
                RequestMethod.POST,
            ),
        )

        menuTreeController.createBranch(
            projectId,
            MenuTreeResources.Request.Branch(
                "j1_10",
                "j1_1",
                "Delete Review",
                "/{reviewId}",
                Constant.NavigationType.FUNCTION,
                RequestMethod.DELETE,
            ),
        )
    }

    private fun addProject(timestamp: Long): Long =
        projectController
            .create(
                ProjectResources.Request.Create(
                    "Otter Project $timestamp",
                    "This is Sample Project created at $timestamp",
                ),
            ).content!!

    private fun setupAdminIfNotExist() {
        val supervisor = adminRepository.findByIdOrNull(1)

        if (supervisor == null) {
            val admin =
                Admin.of(
                    "admin@osori.com",
                    "admin",
                    "슈퍼 관리자",
                    "오소리의 모든 권한을 가지고 있습니다.",
                    "/static/dist/img/osori.png",
                    AdminRole.SUPER,
                )
            adminRepository.save(admin)
        }
    }
}
