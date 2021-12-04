package scope

/** Mark the child class as part of specified scope. Avoid hierarchies. Use this just as a
  * convenient method to avoid the implicit val declaration.
  *
  * {{{
  *
  *   case class EntityModel(value: String)
  *   object EntityModel {
  *       implicit val mapper: ModelMapper[Scope.Persistence, EntityModel, DomainModel] =
  *         ModelMapper.scoped[Scope.Persistence]{ entity => ??? }
  *   }
  *
  *
  *   case class DomainModel(value: String)
  *   case class MyRepo() extends InScope[Scope.Persistence]{
  *
  *       def foo = {
  *         val entity: EntityModel = ???
  *         val domain: DomainModel = entity.scoped.as[DomainModel]
  *       }
  *   }
  * }}}
  * @tparam S
  *   Scope type
  */
trait InScope[S <: Scope] {
  implicit protected val scopeCtx: TypedScopeContext[S] = ScopeContext.of[S]
}
