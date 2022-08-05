package scope

class ScopeContextTest extends munit.FunSuite {

  test("Multiple ScopeContext for same Scope must be equals") {
    assertEquals(ScopeContext.of[Scope.Domain], ScopeContext.of[Scope.Domain])
  }

  test("Cannot create a ScopeContext for the wrong Scope type") {
    assertNoDiff(
      obtained = compileErrors("""
         given TypedScopeContext[Scope.Domain] = ScopeContext.of[Scope.Endpoint]
     """),
      expected = """
         |error:
         |Found:    scope.TypedScopeContext[scope.Scope.Endpoint]
         |Required: scope.TypedScopeContext[scope.Scope.Domain]
         |
         |The following import might make progress towards fixing the problem:
         |
         |  import munit.Clue.generate
         |
         |         given TypedScopeContext[Scope.Domain] = ScopeContext.of[Scope.Endpoint]
         |                                                               ^
         |""".stripMargin
    )
  }

  test("Cannot have multiple ScopeContext in the same scope") {
    assertNoDiff(
      obtained = compileErrors("""
         given TypedScopeContext[Scope.Domain] = ScopeContext.of[Scope.Domain]
         given TypedScopeContext[Scope.Endpoint] = ScopeContext.of[Scope.Endpoint]

         summon[ScopeContext]
     """),
      expected = """
           |error: ambiguous given instances: both given instance given_TypedScopeContext_Endpoint and given instance given_TypedScopeContext_Domain match type scope.ScopeContext of parameter x of method summon in object Predef
           |         summon[ScopeContext]
           |                            ^
           |""".stripMargin
    )
  }
}
