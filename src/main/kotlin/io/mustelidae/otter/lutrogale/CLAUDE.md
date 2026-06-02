# Code Conventions

## `*Finder` Class

- Entity 조회 시 Repository를 항상 Finder로 감싸서 호출한다. Controller/Interaction에서 Repository를 직접 사용하지 않는다.
- Repository 반환값이 `Optional`인 경우 Finder에서 반드시 NotFound 검사를 수행한다. 데이터가 없으면 즉시 예외를 던진다.
- `*DSLRepository`(QueryDSL)도 Finder가 감싸서 호출부의 사용성을 단순하게 유지한다.
- Cache가 필요한 조회는 Finder에서 Redis에 접근하여 캐시 데이터를 가져온다.
- 모든 Finder 클래스에 `@Transactional(readOnly = true)`를 선언한다.

```kotlin
@Service
@Transactional(readOnly = true)
class FooFinder(
    private val fooRepository: FooRepository,
    private val fooDSLRepository: FooDSLRepository,
) {
    fun findBy(id: Long): Foo =
        fooRepository.findById(id).orElseThrow { DataNotFindException(DefaultError(ErrorCode.HD00, "Foo not found")) }
}
```

## QueryDSL 활용

- QueryDSL을 사용하는 Repository 클래스는 `*DSLRepository`로 명명한다.
- 단건 조회: `ManyToOne` 연관관계는 모두 Join Fetch하여 한 번의 쿼리로 가져온다.
- 다건 조회: N+1이 발생하지 않도록 한다. `ManyToOne`은 fetch join으로 한 번에 가져오고, 컬렉션은 별도 쿼리로 분리한다.
- 단건 조회에서 하위 컬렉션이 있으면 Fetch Join을 사용하여 컬렉션을 한 번에 가져온다.

```kotlin
@Repository
class FooDSLRepository(private val queryFactory: JPAQueryFactory) {

    // 단건: ManyToOne + 컬렉션 모두 fetch join
    fun findWithRelations(id: Long): Foo? =
        queryFactory
            .selectFrom(foo)
            .join(foo.project, project).fetchJoin()
            .leftJoin(foo.items, item).fetchJoin()   // 하위 컬렉션 fetch join
            .where(foo.id.eq(id))
            .fetchOne()

    // 다건: ManyToOne fetch join, 컬렉션은 별도 쿼리로 N+1 방지
    fun findAllBy(projectId: Long): List<Foo> =
        queryFactory
            .selectFrom(foo)
            .join(foo.project, project).fetchJoin()
            .where(project.id.eq(projectId))
            .fetch()
}
```
