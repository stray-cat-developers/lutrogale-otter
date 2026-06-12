package io.mustelidae.otter.lutrogale.api.domain.migration

import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.navigation.repository.MenuNavigationRepository

/**
 * Preview 기능을 위한 메모리 기반 더미 MenuNavigation Repository
 * 실제 데이터베이스에 영향을 주지 않고 메뉴 구조 생성을 시뮬레이션합니다.
 */
class DummyMenuNavigationRepository : MenuNavigationRepository {
    private val storage = mutableListOf<MenuNavigation>()
    private var idCounter = 1L

    override fun <S : MenuNavigation> save(entity: S): S {
        if (entity.id == null) {
            val field = entity.javaClass.getDeclaredField("id")
            field.isAccessible = true
            field.set(entity, idCounter++)
        }
        storage.removeIf { it.id == entity.id }
        storage.add(entity)
        return entity
    }

    override fun <S : MenuNavigation> saveAll(entities: MutableIterable<S>): MutableList<S> = entities.map { save(it) }.toMutableList()

    override fun findById(id: Long): java.util.Optional<MenuNavigation> = java.util.Optional.ofNullable(storage.find { it.id == id })

    override fun existsById(id: Long): Boolean = storage.any { it.id == id }

    override fun findAll(): MutableList<MenuNavigation> = storage.toMutableList()

    override fun findAllById(ids: MutableIterable<Long>): MutableList<MenuNavigation> = storage.filter { it.id in ids }.toMutableList()

    override fun count(): Long = storage.size.toLong()

    override fun deleteById(id: Long) {
        storage.removeIf { it.id == id }
    }

    override fun delete(entity: MenuNavigation) {
        storage.removeIf { it.id == entity.id }
    }

    override fun deleteAllById(ids: MutableIterable<Long>) {
        storage.removeIf { it.id in ids }
    }

    override fun deleteAll(entities: MutableIterable<MenuNavigation>) {
        val idsToDelete = entities.mapNotNull { it.id }
        storage.removeIf { it.id in idsToDelete }
    }

    override fun deleteAll() {
        storage.clear()
    }

    override fun flush() { /* no-op */ }

    override fun <S : MenuNavigation> saveAndFlush(entity: S): S = save(entity)

    override fun <S : MenuNavigation> saveAllAndFlush(entities: MutableIterable<S>): MutableList<S> = saveAll(entities)

    override fun deleteAllInBatch(entities: MutableIterable<MenuNavigation>) = deleteAll(entities)

    override fun deleteAllByIdInBatch(ids: MutableIterable<Long>) = deleteAllById(ids)

    override fun deleteAllInBatch() = deleteAll()

    @Deprecated("Use findById instead")
    override fun getOne(id: Long): MenuNavigation = findById(id).orElseThrow()

    @Deprecated("Use getReferenceById instead")
    override fun getById(id: Long): MenuNavigation = findById(id).orElseThrow()

    override fun getReferenceById(id: Long): MenuNavigation = findById(id).orElseThrow()

    override fun <S : MenuNavigation> findAll(example: org.springframework.data.domain.Example<S>): MutableList<S> = mutableListOf()

    override fun <S : MenuNavigation> findAll(
        example: org.springframework.data.domain.Example<S>,
        sort: org.springframework.data.domain.Sort,
    ): MutableList<S> = mutableListOf()

    override fun <S : MenuNavigation> findAll(
        example: org.springframework.data.domain.Example<S>,
        pageable: org.springframework.data.domain.Pageable,
    ): org.springframework.data.domain.Page<S> =
        org.springframework.data.domain
            .PageImpl(mutableListOf())

    override fun <S : MenuNavigation> count(example: org.springframework.data.domain.Example<S>): Long = 0

    override fun <S : MenuNavigation> exists(example: org.springframework.data.domain.Example<S>): Boolean = false

    override fun <S : MenuNavigation, R : Any> findBy(
        example: org.springframework.data.domain.Example<S>,
        queryFunction: java.util.function.Function<org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery<S>, R>,
    ): R = throw UnsupportedOperationException("findBy is not supported in dummy repository")

    override fun <S : MenuNavigation> findOne(example: org.springframework.data.domain.Example<S>): java.util.Optional<S> =
        java.util.Optional.empty()

    override fun findAll(sort: org.springframework.data.domain.Sort): MutableList<MenuNavigation> = storage.toMutableList()

    override fun findAll(pageable: org.springframework.data.domain.Pageable): org.springframework.data.domain.Page<MenuNavigation> =
        org.springframework.data.domain
            .PageImpl(storage)

    // MenuNavigationRepository 고유 메서드들
    override fun findByProjectIdAndId(
        projectId: Long,
        nodeId: Long,
    ): MenuNavigation? = storage.find { it.project?.id == projectId && it.id == nodeId }

    override fun findByProjectIdAndTreeId(
        projectId: Long,
        treeId: String,
    ): MenuNavigation? =
        storage.find {
            it.project?.id == projectId && it.treeId == treeId
        }

    override fun findByStatusTrueAndProjectId(projectId: Long): List<MenuNavigation> =
        storage.filter {
            it.status && it.project?.id == projectId
        }

    override fun findByStatusTrueAndProjectIdAndIdIn(
        projectId: Long,
        menuNavigationIds: List<Long>,
    ): List<MenuNavigation> =
        storage.filter {
            it.status && it.project?.id == projectId && it.id in menuNavigationIds
        }

    override fun findByIdIn(menuNavigationIds: List<Long>): List<MenuNavigation> = storage.filter { it.id in menuNavigationIds }
}
