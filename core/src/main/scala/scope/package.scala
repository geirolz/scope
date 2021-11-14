import cats.Id

package object scope {
  type ModelMapper[S <: Scope, A, B] = ModelMapperK[Id, S, A, B]
}
