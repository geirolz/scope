package scope

sealed class ScopeContext private[scope] () {
  type ScopeType <: Scope
}
sealed class TypedScopeContext[S <: Scope] private[scope] () extends ScopeContext() {
  type ScopeType = S
}
object ScopeContext {

  private val genericScopeContext: TypedScopeContext[Scope] = new TypedScopeContext[Scope]

  def of[S <: Scope]: TypedScopeContext[S] = genericScopeContext.asInstanceOf[TypedScopeContext[S]]
}
