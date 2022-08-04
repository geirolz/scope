package scope

class ScopeTest extends munit.FunSuite {

  test("Can create custom scope") {
    assertNoDiff(
      obtained = compileErrors("""
         import scope.Scope
         trait MyCustomScope extends Scope
     """),
      expected = ""
    )
  }
}
