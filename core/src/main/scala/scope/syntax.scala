package scope

object syntax extends ModelScopeMapperSyntax

private[scope] sealed trait ModelScopeMapperSyntax {

  implicit class ModelScopeMapperSyntaxOps[A](a: A) {
    def scoped(implicit ctx: ScopeContext): ScopeOpsForAny[A, ctx.ScopeType] =
      new ScopeOpsForAny[A, ctx.ScopeType](a)
  }

  sealed class ScopeOpsForAny[A, S <: Scope] private[scope] (a: A) {
    def as[B](implicit m: ModelMapper[S, A, B], tsc: TypedScopeContext[S]): B = m.apply(a)
  }
}
