package scope.generic

import cats.Show

object ModelMapperCaseClassIdMessages {

  import cats.syntax.show.*

  def abortDueDifferentTypesFields[TR: Show, FR: Show, DFR: Show](
    aSymbolName: TR,
    bSymbolName: TR,
    aFields: FR,
    bFields: FR,
    diff: DFR
  ): String =
    show"""
       |Type $aSymbolName and $bSymbolName doesn't have the same constructor.
       |Keep in mind that this macro only support the construction using `new`, smart constructors are not supported yet.
       |## Type $aSymbolName fields:
       |$aFields
       |
       |## Type $bSymbolName fields:
       |$bFields
       |
       |------------------------------
       |Differences:
       |$diff
       |""".stripMargin
}
