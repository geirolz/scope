package scope.generic

import scope.{ModelMapper, Scope}

import scala.annotation.unused

private[generic] trait ModelMapperBuilderMacrosSyntax {

  extension [S <: Scope](@unused co: ModelMapper.Builder[S]) {
    inline def deriveCaseClassIdMap[A, B]: ModelMapper[S, A, B] =
      ${ ModelMapperCaseClassIdMacros.deriveCaseClassIdMap[S, A, B] }
  }
}
