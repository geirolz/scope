package scope

import cats.{~>, Applicative, Contravariant, FlatMap, Functor, Id}
import cats.arrow.FunctionK
import cats.data.Kleisli

import scala.annotation.{implicitAmbiguous, implicitNotFound, unused}

@implicitNotFound(msg = "Cannot find a mapper for the scope ${S}")
@implicitAmbiguous(msg = "Multiple mapper for the same type ${M2} and same scope ${S}")
class ModelMapperK[F[_], S <: Scope, A, B](private[scope] val mapper: Kleisli[F, A, B]) {

  def apply(a: A)(implicit @unused scopeContext: TypedScopeContext[S]): F[B] = mapper(a)

  def mapScope[S2 <: Scope]: ModelMapperK[F, S2, A, B] =
    this.asInstanceOf[ModelMapperK[F, S2, A, B]]

  def contramap[U](f: U => A): ModelMapperK[F, S, U, B] =
    ModelMapperK.forScope[S](mapper.local(f))

  def map[C](f: B => C)(implicit F: Functor[F]): ModelMapperK[F, S, A, C] =
    ModelMapperK.forScope[S](mapper.map(f))

  def mapK[K[_]](f: F ~> K): ModelMapperK[K, S, A, B] =
    ModelMapperK.forScope[S](mapper.mapK(f))

  def flatMap[C, AA <: A](f: B => ModelMapperK[F, S, AA, C])(implicit
    @unused F: FlatMap[F]
  ): ModelMapperK[F, S, AA, C] =
    ModelMapperK.forScope[S](mapper.flatMap[C, AA](b => f(b).mapper))

  def flatMapF[C](f: B => F[C])(implicit F: FlatMap[F]): ModelMapperK[F, S, A, C] =
    ModelMapperK.forScope[S](mapper.flatMapF(f))

  def squash: ModelMapper[S, A, F[B]] =
    ModelMapper.forScope[S](mapper.run)
}
object ModelMapperK extends ModelMapperKInstances {

  private val builderK: ModelMapperK.BuilderK[Scope] = new ModelMapperK.BuilderK[Scope]

  def forScope[S <: Scope]: ModelMapperK.BuilderK[S] =
    builderK.asInstanceOf[ModelMapperK.BuilderK[S]]

  class BuilderK[S <: Scope] private[ModelMapperK] () {

    def summon[F[_], A, B](implicit m: ModelMapperK[F, S, A, B]): ModelMapperK[F, S, A, B] = m

    def apply[F[_], A, B](k: Kleisli[F, A, B]): ModelMapperK[F, S, A, B] =
      new ModelMapperK[F, S, A, B](k)

    def apply[F[_], A, B](f: A => F[B]): ModelMapperK[F, S, A, B] =
      apply(Kleisli(f))

    def pure[F[_]: Applicative, A, B](b: B): ModelMapperK[F, S, A, B] =
      apply(_ => Applicative[F].pure(b))

    def id[F[_]: Applicative, A]: ModelMapperK[F, S, A, A] =
      apply(Applicative[F].pure(_))

    def lift[F[_]: Applicative, A, B](f: A => B): ModelMapperK[F, S, A, B] =
      apply(f.andThen(Applicative[F].pure(_)))
  }
}

trait ModelMapperKInstances {

  implicit def squashModelMapperK[F[_], S <: Scope, A, B](implicit
    m: ModelMapperK[F, S, A, B]
  ): ModelMapper[S, A, F[B]] = m.squash

  implicit def liftPureModelMapper[F[_]: Applicative, S <: Scope, A, B](implicit
    m: ModelMapper[S, A, B]
  ): ModelMapperK[F, S, A, B] =
    m.mapK[F](new FunctionK[Id, F] {
      override def apply[AA](fa: Id[AA]): F[AA] = Applicative[F].pure(fa)
    })

  implicit def contravariantForModelMapperK[F[_], S <: Scope, AA, BB]
    : Contravariant[ModelMapperK[F, S, *, BB]] = new Contravariant[ModelMapperK[F, S, *, BB]] {
    override def contramap[A, B](fa: ModelMapperK[F, S, A, BB])(
      f: B => A
    ): ModelMapperK[F, S, B, BB] = fa.contramap(f)
  }
}
