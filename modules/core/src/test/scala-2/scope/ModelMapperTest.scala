package scope

class ModelMapperTest extends munit.FunSuite {

  test("Using ModelMapper with a ScopeContext provided") {

    import scope.syntax.*

    implicit val m: ModelMapper[Scope.Domain, Int, String] =
      ModelMapper.forScope[Scope.Domain](_.toString)

    implicit val scopeCtx: TypedScopeContext[Scope.Domain] =
      ScopeContext.of[Scope.Domain]

    assertEquals(
      obtained = 1.scoped.as[String],
      expected = "1"
    )
  }

  test("Using ModelMapper wit a wrong ScopeContext in scope should not compile") {
    assertNoDiff(
      obtained = compileErrors("""  
          import scope.syntax.*
          import scope.Scope
          import scope.ModelMapper
          
          implicit val m: ModelMapper[Scope.Domain, Int, String] = 
              ModelMapper.forScope[Scope.Domain](_.toString)
              
          implicit val scopeCtx: TypedScopeContext[Scope.Event] =
              ScopeContext.of[Scope.Event]
          
          1.scoped.as[String]                       
     """),
      expected = """
           |error:
           |diverging implicit expansion for type scope.ModelMapper[scopeCtx.ScopeType,Int,String]
           |starting with method liftPureModelMapper in trait ModelMapperKInstances
           |          1.scoped.as[String]                       
           |                     ^
           |""".stripMargin
    )
  }

  test("Using ModelMapper without a ScopeContext in scope should not compile") {
    assertNoDiff(
      obtained = compileErrors("""  
          import scope.syntax.*
          import scope.Scope
          import scope.ModelMapper
          
          implicit val m: ModelMapper[Scope.Domain, Int, String] = ModelMapper.forScope[Scope.Domain](_.toString)
          
          1.scoped.as[String]                       
     """),
      expected = """
           |error: could not find implicit value for parameter ctx: scope.ScopeContext
           |          1.scoped.as[String]                       
           |            ^
           |""".stripMargin
    )
  }

  test("ModelMapperK with Id is assignable to ModelMapper") {
    assertNoDiff(
      obtained = compileErrors("""  
         import cats.Id                               
         import scope.ModelMapperK                    
         import scope.ModelMapper
                             
         val a : ModelMapperK[Id, Scope.Domain, String, String] = ModelMapperK.forScope[Scope.Domain].id                        
         val b : ModelMapper[Scope.Domain, String, String] = a                         
     """),
      expected = ""
    )
  }
}
