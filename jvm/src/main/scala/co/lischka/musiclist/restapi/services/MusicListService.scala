package co.lischka.musiclist.restapi.services

import co.lischka.musiclist.restapi.models.{MusicListEntity, TrackMusicEntity}
import co.lischka.musiclist.restapi.models.db.{MusicListEntityTable, TrackMusicEntityTable}
import co.lischka.musiclist.restapi.utils.DatabaseService

import scala.concurrent.{ExecutionContext, Future}

class MusicListService(val databaseService: DatabaseService)(implicit executionContext: ExecutionContext) extends MusicListEntityTable with TrackMusicEntityTable {

  import databaseService._
  import databaseService.driver.api._

  def getLists(): Future[Seq[MusicListEntity]] = db.run(musicList.result)

  def getListById(id: Long): Future[Option[MusicListEntity]] = db.run(musicList.filter(_.id === id).result.headOption)

  def deleteList(id: Long): Future[Int] = db.run(musicList.filter(_.id === id).delete)

  def createList(muList: MusicListEntity): Future[MusicListEntity] = db.run(musicList returning musicList += muList)

  def updateList(listUpdate: MusicListEntity): Future[Option[MusicListEntity]] = {
    listUpdate.id match {
      case Some(id) => {
        val result = db.run(musicList.filter(_.id === listUpdate.id).update(listUpdate))
        // Result handling
        result.flatMap(nRowsAffected =>
          if (nRowsAffected <= 0) Future(None) else getListById(listUpdate.id.get)
        )
      }
      case None => Future(None)
    }
  }

  def insertListAtTrack(musicListEntity: MusicListEntity, trackIds: Seq[Long]) = {
    createList(musicListEntity) flatMap { list =>
      linkMusicListWithTracks(list.id.get, trackIds)
    }
  }

  def linkMusicListWithTracks(trackId: Long, musicListId: Seq[Long]) = {
    existAllLists(musicListId) flatMap {
      case true =>
        db.run(trackAtList ++= musicListId.map((id: Long) => TrackMusicEntity(Some(trackId), Some[Long](id))))
      case false => Future(None)
    }
  }

  def existAllLists(lIds: Seq[Long]): Future[Boolean] = {
    val seqOfFutures = lIds map {
      existsList
    }
    Future.sequence(seqOfFutures) map { seq => seq.forall(_ == true) }
  }

  def existsList(lId: Long): Future[Boolean] = {
    db.run {
      musicList.filter(_.id === lId).length.result
    } map {
      _ == 1
    }
  }
}
