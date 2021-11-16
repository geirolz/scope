package scope

class ModelMapperKTest extends munit.FunSuite {

  test("Using ModelMapperK with a wrong ScopeContext in scope should not compile") {
    assertNoDiff(
      obtained = compileErrors("""  
          import scope.syntax.*
          import scope.Scope
          import scope.ModelMapper
          import scala.util.*
          
          implicit val m: ModelMapperK[Try, Scope.Domain, Int, String] = 
              ModelMapperK.scoped[Scope.Domain](i => Success(i.toString))
              
          implicit val scopeCtx: TypedScopeContext[Scope.Event] =
              ScopeContext.of[Scope.Event]
          
          1.scoped.as[Try[String]]                       
     """),
      expected = """
            |error:
            |diverging implicit expansion for type scope.ModelMapper[scopeCtx.ScopeType,Int,scala.util.Try[String]]
            |starting with method liftPureModelMapper in trait ModelMapperKInstances
            |          1.scoped.as[Try[String]]                       
            |                     ^
            |""".stripMargin
    )
  }

  test("Using ModelMapperK without a ScopeContext in scope should not compile") {
    assertNoDiff(
      obtained = compileErrors("""  
          import scope.syntax.*
          import scope.Scope
          import scope.ModelMapper
          import scala.util.*
          
          implicit val m: ModelMapperK[Try, Scope.Domain, Int, String] = 
                ModelMapperK.scoped[Scope.Domain](i => Success(i.toString))
          
          1.scoped.as[String]                       
     """),
      expected = """
           |error: could not find implicit value for parameter ctx: scope.ScopeContext
           |          1.scoped.as[String]                       
           |            ^
           |""".stripMargin
    )
  }

  test("ModelMapper is assignable to ModelMapperK with Id") {
    assertNoDiff(
      obtained = compileErrors("""  
         import cats.Id                               
         import scope.ModelMapperK                    
         import scope.ModelMapper
                             
         val a : ModelMapper[Scope.Domain, String, String] = ModelMapper.scoped[Scope.Domain].id[String]                       
         val b : ModelMapperK[Id, Scope.Domain, String, String] = a                         
     """),
      expected = ""
    )
  }
}
