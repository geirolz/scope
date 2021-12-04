package scope

trait Scope
object Scope {

  // domain
  trait Domain extends Scope

  // config
  trait Configuration extends Scope

  // persistence
  trait Persistence extends Scope
  trait PersistenceRead extends Scope
  trait PersistenceWrite extends Scope

  // endpoint
  trait Endpoint extends Scope
  trait PrivateEndpoint extends Scope
  trait PublicEndpoint extends Scope

  // HTTP
  trait HTTPRequest extends Scope
  trait HTTPResponse extends Scope

  // RPC
  trait RPCRequest extends Scope
  trait RPCResponse extends Scope

  // events
  trait Event extends Scope
  trait CQRSCommand extends Scope
  trait CQRSQuery extends Scope
}
