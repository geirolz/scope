package scope.generic

import scope.{ModelMapper, Scope}

import scala.annotation.unused

private[generic] trait ModelMapperBuilderMacrosSyntax {

  implicit class ModelMapperBuilderGenericOps[S <: Scope](@unused co: ModelMapper.Builder[S]) {
    def deriveIdMap[A, B]: ModelMapper[S, A, B] =
      macro ModelMapperIdMacros.deriveIdMapImpl[S, A, B]
  }
}
