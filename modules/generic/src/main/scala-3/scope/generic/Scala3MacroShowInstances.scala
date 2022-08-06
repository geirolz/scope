package scope.generic

import cats.Show

import scala.quoted.Quotes

trait Scala3MacroShowInstances:
  given (using q: Quotes): Show[q.reflect.Symbol] = Show.fromToString
