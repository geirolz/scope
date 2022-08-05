package scope.generic

import scope.{ModelMapper, Scope}

import scala.annotation.unused

private[generic] trait ModelMapperBuilderMacrosSyntax {

  extension [S <: Scope](@unused co: ModelMapper.Builder[S]) {
    inline def deriveIdMap[A, B]: ModelMapper[S, A, B] =
      ${ ModelMapperIdMacros.deriveIdMapImpl[S, A, B] }
  }
}
