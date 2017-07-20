package co.lischka.musiclist.restapi.services

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.util.ByteString
import co.lischka.musiclist.restapi.models.TrackEntity
import co.lischka.musiclist.restapi.models.db.TrackEntityTable
import co.lischka.musiclist.restapi.parsers.YoutubeParser

import scala.concurrent.{ExecutionContext, Future}

class SearchService() (
  implicit executionContext: ExecutionContext,
  system: akka.actor.ActorSystem,
  materializer: ActorMaterializer
)extends TrackEntityTable{

  def search(title: String): Future[List[TrackEntity]] = {
    val youtubeUri = s"https://www.googleapis.com/youtube/v3/search/?q=${title}&maxResults=25&part=snippet&key=" +
    "AIzaSyBtUanlaGBqScDggFrqUkMVa63sYORidZg"

    val f = for {
      response <- Http().singleRequest(HttpRequest(uri = youtubeUri))
      bytes <- response.entity.dataBytes.runFold(ByteString(""))(_ ++ _)
    } yield YoutubeParser.parseTracks(bytes.utf8String)
    f
  }
val t = cretb()


}
