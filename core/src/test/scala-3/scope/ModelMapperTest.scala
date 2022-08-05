package scope

class ModelMapperTest extends munit.FunSuite {

  test("Summon an implicit ModelMapper") {

    given TypedScopeContext[Scope.Domain] =
      ScopeContext.of[Scope.Domain]

    given ModelMapper[Scope.Domain, Int, String] =
      ModelMapper.scoped[Scope.Domain](_.toString)

    assertEquals(
      obtained = ModelMapper.scoped[Scope.Domain].summon[Int, String].apply(1),
      expected = "1"
    )
  }

  test("Creating a ModelMapper from function A => B") {

    given TypedScopeContext[Scope.Domain] =
      ScopeContext.of[Scope.Domain]

    val m: ModelMapper[Scope.Domain, Int, String] =
      ModelMapper.scoped[Scope.Domain](_.toString)

    assertEquals(
      obtained = m(1),
      expected = "1"
    )
  }

  test("Creating a ModelMapper with Pure") {

    given TypedScopeContext[Scope.Domain] =
      ScopeContext.of[Scope.Domain]

    val m: ModelMapper[Scope.Domain, Int, String] =
      ModelMapper.scoped[Scope.Domain].pure("FOO")

    assertEquals(
      obtained = m(1),
      expected = "FOO"
    )
  }

  test("Creating a ModelMapper with Id") {

    given TypedScopeContext[Scope.Domain] =
      ScopeContext.of[Scope.Domain]

    val m: ModelMapper[Scope.Domain, Int, Int] =
      ModelMapper
        .scoped[Scope.Domain]
        .id

    assertEquals(
      obtained = m(1),
      expected = 1
    )
  }

  test("Using ModelMapper with a ScopeContext provided") {

    import scope.syntax.*

    given ModelMapper[Scope.Domain, Int, String] =
      ModelMapper.scoped[Scope.Domain](_.toString)

    given TypedScopeContext[Scope.Domain] =
      ScopeContext.of[Scope.Domain]

    assertEquals(
      obtained = 1.scoped.as[String],
      expected = "1"
    )
  }

  test("Using ModelMapper wit a wrong ScopeContext in scope should not compile") {
    assert(
      compileErrors("""  
          import scope.syntax.*
          import scope.Scope
          import scope.ModelMapper
          
          given ModelMapper[Scope.Domain, Int, String] = 
              ModelMapper.scoped[Scope.Domain](_.toString)
              
          given TypedScopeContext[Scope.Event] =
              ScopeContext.of[Scope.Event]
          
          1.scoped.as[String]                       
     """).nonEmpty
    )
  }

  test("Using ModelMapper without a ScopeContext in scope should not compile") {
    assertNoDiff(
      obtained = compileErrors("""  
          import scope.syntax.*
          import scope.Scope
          import scope.ModelMapper
          
          given ModelMapper[Scope.Domain, Int, String] = ModelMapper.scoped[Scope.Domain](_.toString)
          
          1.scoped.as[String]                       
     """),
      expected = """
           |error: no given instance of type scope.ScopeContext was found for parameter ctx of method scoped in class ModelScopeMapperSyntaxOps
           |          1.scoped.as[String]
           |                 ^
           |""".stripMargin
    )
  }

  test("ModelMapperK with Id is assignable to ModelMapper") {
    assertNoDiff(
      obtained = compileErrors("""  
         import cats.Id                               
         import scope.ModelMapperK                    
         import scope.ModelMapper
         import cats.*
                             
         val a : ModelMapperK[Id, Scope.Domain, String, String] = ModelMapperK.scoped[Scope.Domain].id                        
         val b : ModelMapper[Scope.Domain, String, String] = a                         
     """),
      expected = ""
    )
  }
}
