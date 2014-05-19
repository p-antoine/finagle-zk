package com.twitter.finagle.exp.zookeeper.client

import java.util.logging.Logger
import com.twitter.finagle.ServiceFactory
import com.twitter.util.{Await, Future, Time, Closable}
import com.twitter.finagle.exp.zookeeper._
import com.twitter.finagle.exp.zookeeper.ZookeeperDefinitions._

class Client(val factory: ServiceFactory[Request, Response]) extends Closable {

  private[this] val service = Await.result(factory())

  def close(deadline: Time): Future[Unit] = factory.close(deadline)
  def closeService: Future[Unit] = factory.close()

  // Connection purpose definitions
  def connect: Future[Response] = service(new ConnectRequest)
  def connect(timeOut: Int): Future[Response] = service(new ConnectRequest(0, 0L, timeOut))
  def disconnect: Future[Response] = service(new RequestHeader(1, -11))
  def sendPing: Future[Response] = {
    println("<--ping: ")
    service(new RequestHeader(-2, 11))
  }

  def create(path: String,
    data: Array[Byte],
    acl: Array[ACL],
    createMode: Int,
    xid: Int
    ): Future[Response] = {
    //TODO patch check (chroot)
    /* PathUtils.validatePath(path, createMode)
     val finalPath = PathUtils.prependChroot(path, null)*/
    println("<--create: " + xid)
    val header = RequestHeader(xid, opCode.create)
    val body = CreateRequestBody(path, data, acl, createMode)

    service(new CreateRequest(header, body))
  }

  def delete(path: String, version: Int, xid: Int): Future[Response] = {
    // TODO CHECK STRING
    /*PathUtils.validatePath(path, createMode)
    val finalPath = PathUtils.prependChroot(path, null)*/
    println("<--delete: " + xid)
    val header = RequestHeader(xid, opCode.delete)
    val body = DeleteRequestBody(path, version)

    service(new DeleteRequest(header, body))
  }

  def exists(path: String, watch: Boolean, xid: Int): Future[Response] = {
    // TODO Check path
    /*PathUtils.validatePath(path, createMode)
    val finalPath = PathUtils.prependChroot(path, null)*/
    println("<--exists: " + xid)
    val header = RequestHeader(xid, opCode.exists)
    val body = ExistsRequestBody(path, false) // false because watch's not supported

    service(new ExistsRequest(header, body))
  }

  def getACL(path: String, xid: Int): Future[Response] = {
    // TODO Check path
    /*PathUtils.validatePath(path, createMode)
    val finalPath = PathUtils.prependChroot(path, null)*/
    println("<--getACL: " + xid)
    val header = RequestHeader(xid, opCode.getACL)
    val body = GetACLRequestBody(path)

    service(new GetACLRequest(header, body))
  }

  def getChildren(path: String, watch: Boolean, xid: Int): Future[Response] = {
    // TODO Check path
    /*PathUtils.validatePath(path, createMode)
    val finalPath = PathUtils.prependChroot(path, null)*/
    println("<--getChildren: " + xid)
    val header = RequestHeader(xid, opCode.getChildren)
    val body = GetChildrenRequestBody(path, false) // false because watch's not supported

    service(new GetChildrenRequest(header, body))
  }

  def getChildren2(path: String, watch: Boolean, xid: Int): Future[Response] = {
    // TODO Check path
    /*PathUtils.validatePath(path, createMode)
    val finalPath = PathUtils.prependChroot(path, null)*/
    println("<--getChildren2: " + xid)
    val header = RequestHeader(xid, opCode.getChildren2)
    val body = GetChildren2RequestBody(path, false) // false because watch's not supported

    service(new GetChildren2Request(header, body))
  }

  def getData(path: String, watch: Boolean, xid: Int): Future[Response] = {
    // TODO Check path
    /*PathUtils.validatePath(path, createMode)
    val finalPath = PathUtils.prependChroot(path, null)*/
    println("<--getData: " + xid)
    val header = RequestHeader(xid, opCode.getData)
    val body = GetDataRequestBody(path, false) // false because watch's not supported

    service(new GetDataRequest(header, body))
  }

  // GetMaxChildren is implemented but not available in the java lib
  /*def getMaxChildren(path: String, xid: Int): Future[Response] = {
    /*PathUtils.validatePath(path, createMode)
    val finalPath = PathUtils.prependChroot(path, null)*/
    println("<--getMaxChildren: " + xid)

    val header = RequestHeader(xid, ?)
    val body = GetDataRequestBody(path, false) // false because watch's not supported

    service(new GetDataRequest(header, body))
  }*/

  def setACL(path: String, acl: Array[ACL], version: Int, xid: Int): Future[Response] = {
    // TODO Check path
    /*PathUtils.validatePath(path, createMode)
    val finalPath = PathUtils.prependChroot(path, null)*/
    println("<--setACL: " + xid)
    val header = RequestHeader(xid, opCode.setACL)
    val body = SetACLRequestBody(path, acl, version)

    service(new SetACLRequest(header, body))
  }

  def setData(path: String, data: Array[Byte], version: Int, xid: Int): Future[Response] = {
    // TODO check path
    /*PathUtils.validatePath(path, createMode)
    val finalPath = PathUtils.prependChroot(path, null)*/
    println("<--setData: " + xid)
    val header = RequestHeader(xid, opCode.setData)
    val body = SetDataRequestBody(path, data, version)

    service(new SetDataRequest(header, body))
  }

  def setWatches(relativeZxid: Int,
    dataWatches: Array[String],
    existsWatches: Array[String],
    childWatches: Array[String],
    xid: Int
    ): Future[Response] = {
    println("<--setWatches: " + xid)

    val header = RequestHeader(xid, opCode.setWatches)
    val body = SetWatchesRequestBody(relativeZxid, dataWatches, existsWatches, childWatches)

    service(new SetWatchesRequest(header, body))
  }

  def sync(path: String, xid: Int): Future[Response] = {
    // TODO check path
    /*PathUtils.validatePath(path, createMode)
    val finalPath = PathUtils.prependChroot(path, null)*/
    println("<--sync: " + xid)
    val header = RequestHeader(xid, opCode.sync)
    val body = SyncRequestBody(path)

    service(new SyncRequest(header, body))
  }

  def transaction(opList: Array[OpRequest], xid: Int): Future[Response] = {
    println("<--Transaction: " + xid)

    val header = RequestHeader(xid, opCode.multi)
    val transaction = new Transaction(opList)

    service(new TransactionRequest(header, transaction))
  }
}

object Client {
  private[this] val logger = Logger.getLogger("finagle-zookeeper")

  def apply(factory: ServiceFactory[Request, Response]): Client = {
    new Client(factory)
  }

  def getLogger = logger
}