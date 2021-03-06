package co.lischka.musiclist.restapi.models.db

import co.lischka.musiclist.restapi.utils.DatabaseService
import co.lischka.musiclist.restapi.models.TrackEntity

trait TrackEntityTable {

  protected val databaseService: DatabaseService
  import databaseService.driver.api._

  class Tracks(tag: Tag) extends Table[TrackEntity](tag, "track"){
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    def url = column[String]("url")
    def title = column[String]("title")
    def artist = column[String]("artist")
    def description = column[String]("description")

    def * = (id, url, title, artist, description) <> ((TrackEntity.apply _).tupled, TrackEntity.unapply)
  }

  val tracks = TableQuery[Tracks]

}