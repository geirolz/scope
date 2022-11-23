import cats.Id

package object scope {
  type ModelMapper[S <: Scope, A, B] = ModelMapperK[Id, S, A, B]

  type ==>[A, B] = PartiallyAppliedModelMapperK[A, B]
  type In[P <: Exist[PartiallyAppliedModelMapperK[?, ?]], S <: Scope] =
    ModelMapper[S, P#_A, P#_B]
  type Exist[P <: PartiallyAppliedModelMapperK[?, ?]] = P
}
