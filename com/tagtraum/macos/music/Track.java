package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Id;
import com.tagtraum.japlscript.Inherits;
import com.tagtraum.japlscript.Kind;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.Plural;
import com.tagtraum.japlscript.Reference;
import com.tagtraum.japlscript.Type;
import com.tagtraum.japlscript.execution.Session;
import com.tagtraum.japlscript.language.Picture;
import com.tagtraum.japlscript.language.Tdta;
import com.tagtraum.japlscript.language.TypeClass;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.Map;

@Plural("tracks")
@Code("cTrk")
@Name("track")
@Inherits("item")
public interface Track extends Reference, Item {
   TypeClass CLASS = new TypeClass("track", "«class cTrk»", Application.class, Item.CLASS);

   @Kind("element")
   @Type("artwork")
   default Artwork[] getArtworks() {
      return this.getArtworks(null);
   }

   @Kind("element")
   @Type("artwork")
   Artwork[] getArtworks(String var1);

   @Kind("element")
   @Type("artwork")
   Artwork getArtwork(int var1);

   @Kind("element")
   @Type("artwork")
   Artwork getArtwork(Id var1);

   @Kind("element")
   @Type("artwork")
   default int countArtworks() {
      return this.countArtworks(null);
   }

   @Kind("element")
   @Type("artwork")
   int countArtworks(String var1);

   @Code("pAlb")
   @Kind("property")
   @Name("album")
   @Type("text")
   String getAlbum();

   @Code("pAlb")
   @Kind("property")
   @Name("album")
   @Type("text")
   void setAlbum(String var1);

   @Code("pAlA")
   @Kind("property")
   @Name("album artist")
   @Type("text")
   String getAlbumArtist();

   @Code("pAlA")
   @Kind("property")
   @Name("album artist")
   @Type("text")
   void setAlbumArtist(String var1);

   @Code("pAHt")
   @Kind("property")
   @Name("album disliked")
   @Type("boolean")
   boolean isAlbumDisliked();

   @Code("pAHt")
   @Kind("property")
   @Name("album disliked")
   @Type("boolean")
   void setAlbumDisliked(boolean var1);

   @Code("pALv")
   @Kind("property")
   @Name("album favorited")
   @Type("boolean")
   boolean isAlbumFavorited();

   @Code("pALv")
   @Kind("property")
   @Name("album favorited")
   @Type("boolean")
   void setAlbumFavorited(boolean var1);

   @Code("pAlR")
   @Kind("property")
   @Name("album rating")
   @Type("integer")
   int getAlbumRating();

   @Code("pAlR")
   @Kind("property")
   @Name("album rating")
   @Type("integer")
   void setAlbumRating(int var1);

   @Code("pARk")
   @Kind("property")
   @Name("album rating kind")
   @Type("eRtK")
   Ertk getAlbumRatingKind();

   @Code("pArt")
   @Kind("property")
   @Name("artist")
   @Type("text")
   String getArtist();

   @Code("pArt")
   @Kind("property")
   @Name("artist")
   @Type("text")
   void setArtist(String var1);

   @Code("pBRt")
   @Kind("property")
   @Name("bit rate")
   @Type("integer")
   int getBitRate();

   @Code("pBkt")
   @Kind("property")
   @Name("bookmark")
   @Type("real")
   double getBookmark();

   @Code("pBkt")
   @Kind("property")
   @Name("bookmark")
   @Type("real")
   void setBookmark(double var1);

   @Code("pBkm")
   @Kind("property")
   @Name("bookmarkable")
   @Type("boolean")
   boolean isBookmarkable();

   @Code("pBkm")
   @Kind("property")
   @Name("bookmarkable")
   @Type("boolean")
   void setBookmarkable(boolean var1);

   @Code("pBPM")
   @Kind("property")
   @Name("bpm")
   @Type("integer")
   int getBpm();

   @Code("pBPM")
   @Kind("property")
   @Name("bpm")
   @Type("integer")
   void setBpm(int var1);

   @Code("pCat")
   @Kind("property")
   @Name("category")
   @Type("text")
   String getCategory();

   @Code("pCat")
   @Kind("property")
   @Name("category")
   @Type("text")
   void setCategory(String var1);

   @Code("pClS")
   @Kind("property")
   @Name("cloud status")
   @Type("eClS")
   Ecls getCloudStatus();

   @Code("pCmt")
   @Kind("property")
   @Name("comment")
   @Type("text")
   String getComment();

   @Code("pCmt")
   @Kind("property")
   @Name("comment")
   @Type("text")
   void setComment(String var1);

   @Code("pAnt")
   @Kind("property")
   @Name("compilation")
   @Type("boolean")
   boolean isCompilation();

   @Code("pAnt")
   @Kind("property")
   @Name("compilation")
   @Type("boolean")
   void setCompilation(boolean var1);

   @Code("pCmp")
   @Kind("property")
   @Name("composer")
   @Type("text")
   String getComposer();

   @Code("pCmp")
   @Kind("property")
   @Name("composer")
   @Type("text")
   void setComposer(String var1);

   @Code("pDID")
   @Kind("property")
   @Name("database ID")
   @Type("integer")
   int getDatabaseId();

   @Code("pAdd")
   @Kind("property")
   @Name("date added")
   @Type("date")
   Date getDateAdded();

   @Code("pDes")
   @Kind("property")
   @Name("description")
   @Type("text")
   String getDescription();

   @Code("pDes")
   @Kind("property")
   @Name("description")
   @Type("text")
   void setDescription(String var1);

   @Code("pDsC")
   @Kind("property")
   @Name("disc count")
   @Type("integer")
   int getDiscCount();

   @Code("pDsC")
   @Kind("property")
   @Name("disc count")
   @Type("integer")
   void setDiscCount(int var1);

   @Code("pDsN")
   @Kind("property")
   @Name("disc number")
   @Type("integer")
   int getDiscNumber();

   @Code("pDsN")
   @Kind("property")
   @Name("disc number")
   @Type("integer")
   void setDiscNumber(int var1);

   @Code("pHat")
   @Kind("property")
   @Name("disliked")
   @Type("boolean")
   boolean isDisliked();

   @Code("pHat")
   @Kind("property")
   @Name("disliked")
   @Type("boolean")
   void setDisliked(boolean var1);

   @Code("pDAI")
   @Kind("property")
   @Name("downloader Apple ID")
   @Type("text")
   String getDownloaderAppleId();

   @Code("pDNm")
   @Kind("property")
   @Name("downloader name")
   @Type("text")
   String getDownloaderName();

   @Code("pDur")
   @Kind("property")
   @Name("duration")
   @Type("real")
   double getDuration();

   @Code("enbl")
   @Kind("property")
   @Name("enabled")
   @Type("boolean")
   boolean isEnabled();

   @Code("enbl")
   @Kind("property")
   @Name("enabled")
   @Type("boolean")
   void setEnabled(boolean var1);

   @Code("pEpD")
   @Kind("property")
   @Name("episode ID")
   @Type("text")
   String getEpisodeId();

   @Code("pEpD")
   @Kind("property")
   @Name("episode ID")
   @Type("text")
   void setEpisodeId(String var1);

   @Code("pEpN")
   @Kind("property")
   @Name("episode number")
   @Type("integer")
   int getEpisodeNumber();

   @Code("pEpN")
   @Kind("property")
   @Name("episode number")
   @Type("integer")
   void setEpisodeNumber(int var1);

   @Code("pEQp")
   @Kind("property")
   @Name("EQ")
   @Type("text")
   String getEq();

   @Code("pEQp")
   @Kind("property")
   @Name("EQ")
   @Type("text")
   void setEq(String var1);

   @Code("pStp")
   @Kind("property")
   @Name("finish")
   @Type("real")
   double getFinish();

   @Code("pStp")
   @Kind("property")
   @Name("finish")
   @Type("real")
   void setFinish(double var1);

   @Code("pGpl")
   @Kind("property")
   @Name("gapless")
   @Type("boolean")
   boolean isGapless();

   @Code("pGpl")
   @Kind("property")
   @Name("gapless")
   @Type("boolean")
   void setGapless(boolean var1);

   @Code("pGen")
   @Kind("property")
   @Name("genre")
   @Type("text")
   String getGenre();

   @Code("pGen")
   @Kind("property")
   @Name("genre")
   @Type("text")
   void setGenre(String var1);

   @Code("pGrp")
   @Kind("property")
   @Name("grouping")
   @Type("text")
   String getGrouping();

   @Code("pGrp")
   @Kind("property")
   @Name("grouping")
   @Type("text")
   void setGrouping(String var1);

   @Code("pKnd")
   @Kind("property")
   @Name("kind")
   @Type("text")
   String getKind();

   @Code("pLds")
   @Kind("property")
   @Name("long description")
   @Type("text")
   String getLongDescription();

   @Code("pLds")
   @Kind("property")
   @Name("long description")
   @Type("text")
   void setLongDescription(String var1);

   @Code("pLov")
   @Kind("property")
   @Name("favorited")
   @Type("boolean")
   boolean isFavorited();

   @Code("pLov")
   @Kind("property")
   @Name("favorited")
   @Type("boolean")
   void setFavorited(boolean var1);

   @Code("pLyr")
   @Kind("property")
   @Name("lyrics")
   @Type("text")
   String getLyrics();

   @Code("pLyr")
   @Kind("property")
   @Name("lyrics")
   @Type("text")
   void setLyrics(String var1);

   @Code("pMdK")
   @Kind("property")
   @Name("media kind")
   @Type("eMdK")
   Emdk getMediaKind();

   @Code("pMdK")
   @Kind("property")
   @Name("media kind")
   @Type("eMdK")
   void setMediaKind(Emdk var1);

   @Code("asmo")
   @Kind("property")
   @Name("modification date")
   @Type("date")
   Date getModificationDate();

   @Code("pMNm")
   @Kind("property")
   @Name("movement")
   @Type("text")
   String getMovement();

   @Code("pMNm")
   @Kind("property")
   @Name("movement")
   @Type("text")
   void setMovement(String var1);

   @Code("pMvC")
   @Kind("property")
   @Name("movement count")
   @Type("integer")
   int getMovementCount();

   @Code("pMvC")
   @Kind("property")
   @Name("movement count")
   @Type("integer")
   void setMovementCount(int var1);

   @Code("pMvN")
   @Kind("property")
   @Name("movement number")
   @Type("integer")
   int getMovementNumber();

   @Code("pMvN")
   @Kind("property")
   @Name("movement number")
   @Type("integer")
   void setMovementNumber(int var1);

   @Code("pPlC")
   @Kind("property")
   @Name("played count")
   @Type("integer")
   int getPlayedCount();

   @Code("pPlC")
   @Kind("property")
   @Name("played count")
   @Type("integer")
   void setPlayedCount(int var1);

   @Code("pPlD")
   @Kind("property")
   @Name("played date")
   @Type("date")
   Date getPlayedDate();

   @Code("pPlD")
   @Kind("property")
   @Name("played date")
   @Type("date")
   void setPlayedDate(Date var1);

   @Code("pPAI")
   @Kind("property")
   @Name("purchaser Apple ID")
   @Type("text")
   String getPurchaserAppleId();

   @Code("pPNm")
   @Kind("property")
   @Name("purchaser name")
   @Type("text")
   String getPurchaserName();

   @Code("pRte")
   @Kind("property")
   @Name("rating")
   @Type("integer")
   int getRating();

   @Code("pRte")
   @Kind("property")
   @Name("rating")
   @Type("integer")
   void setRating(int var1);

   @Code("pRtk")
   @Kind("property")
   @Name("rating kind")
   @Type("eRtK")
   Ertk getRatingKind();

   @Code("pRlD")
   @Kind("property")
   @Name("release date")
   @Type("date")
   Date getReleaseDate();

   @Code("pSRt")
   @Kind("property")
   @Name("sample rate")
   @Type("integer")
   int getSampleRate();

   @Code("pSeN")
   @Kind("property")
   @Name("season number")
   @Type("integer")
   int getSeasonNumber();

   @Code("pSeN")
   @Kind("property")
   @Name("season number")
   @Type("integer")
   void setSeasonNumber(int var1);

   @Code("pSfa")
   @Kind("property")
   @Name("shufflable")
   @Type("boolean")
   boolean isShufflable();

   @Code("pSfa")
   @Kind("property")
   @Name("shufflable")
   @Type("boolean")
   void setShufflable(boolean var1);

   @Code("pSkC")
   @Kind("property")
   @Name("skipped count")
   @Type("integer")
   int getSkippedCount();

   @Code("pSkC")
   @Kind("property")
   @Name("skipped count")
   @Type("integer")
   void setSkippedCount(int var1);

   @Code("pSkD")
   @Kind("property")
   @Name("skipped date")
   @Type("date")
   Date getSkippedDate();

   @Code("pSkD")
   @Kind("property")
   @Name("skipped date")
   @Type("date")
   void setSkippedDate(Date var1);

   @Code("pShw")
   @Kind("property")
   @Name("show")
   @Type("text")
   String getShow();

   @Code("pShw")
   @Kind("property")
   @Name("show")
   @Type("text")
   void setShow(String var1);

   @Code("pSAl")
   @Kind("property")
   @Name("sort album")
   @Type("text")
   String getSortAlbum();

   @Code("pSAl")
   @Kind("property")
   @Name("sort album")
   @Type("text")
   void setSortAlbum(String var1);

   @Code("pSAr")
   @Kind("property")
   @Name("sort artist")
   @Type("text")
   String getSortArtist();

   @Code("pSAr")
   @Kind("property")
   @Name("sort artist")
   @Type("text")
   void setSortArtist(String var1);

   @Code("pSAA")
   @Kind("property")
   @Name("sort album artist")
   @Type("text")
   String getSortAlbumArtist();

   @Code("pSAA")
   @Kind("property")
   @Name("sort album artist")
   @Type("text")
   void setSortAlbumArtist(String var1);

   @Code("pSNm")
   @Kind("property")
   @Name("sort name")
   @Type("text")
   String getSortName();

   @Code("pSNm")
   @Kind("property")
   @Name("sort name")
   @Type("text")
   void setSortName(String var1);

   @Code("pSCm")
   @Kind("property")
   @Name("sort composer")
   @Type("text")
   String getSortComposer();

   @Code("pSCm")
   @Kind("property")
   @Name("sort composer")
   @Type("text")
   void setSortComposer(String var1);

   @Code("pSSN")
   @Kind("property")
   @Name("sort show")
   @Type("text")
   String getSortShow();

   @Code("pSSN")
   @Kind("property")
   @Name("sort show")
   @Type("text")
   void setSortShow(String var1);

   @Code("pSiz")
   @Kind("property")
   @Name("size")
   @Type("double integer")
   long getSize();

   @Code("pStr")
   @Kind("property")
   @Name("start")
   @Type("real")
   double getStart();

   @Code("pStr")
   @Kind("property")
   @Name("start")
   @Type("real")
   void setStart(double var1);

   @Code("pTim")
   @Kind("property")
   @Name("time")
   @Type("text")
   String getTime();

   @Code("pTrC")
   @Kind("property")
   @Name("track count")
   @Type("integer")
   int getTrackCount();

   @Code("pTrC")
   @Kind("property")
   @Name("track count")
   @Type("integer")
   void setTrackCount(int var1);

   @Code("pTrN")
   @Kind("property")
   @Name("track number")
   @Type("integer")
   int getTrackNumber();

   @Code("pTrN")
   @Kind("property")
   @Name("track number")
   @Type("integer")
   void setTrackNumber(int var1);

   @Code("pUnp")
   @Kind("property")
   @Name("unplayed")
   @Type("boolean")
   boolean isUnplayed();

   @Code("pUnp")
   @Kind("property")
   @Name("unplayed")
   @Type("boolean")
   void setUnplayed(boolean var1);

   @Code("pAdj")
   @Kind("property")
   @Name("volume adjustment")
   @Type("integer")
   int getVolumeAdjustment();

   @Code("pAdj")
   @Kind("property")
   @Name("volume adjustment")
   @Type("integer")
   void setVolumeAdjustment(int var1);

   @Code("pWrk")
   @Kind("property")
   @Name("work")
   @Type("text")
   String getWork();

   @Code("pWrk")
   @Kind("property")
   @Name("work")
   @Type("text")
   void setWork(String var1);

   @Code("pYr ")
   @Kind("property")
   @Name("year")
   @Type("integer")
   int getYear();

   @Code("pYr ")
   @Kind("property")
   @Name("year")
   @Type("integer")
   void setYear(int var1);

   @Override
   Map<String, Object> getProperties();

   default Artwork addArtwork(Path artworkFile) throws IOException {
      if (Session.getSession() != null) {
         throw new IllegalStateException("Cannot add artwork from within an active session. Call session.commit() first.");
      }

      int addIndex = this.countArtworks();
      Artwork newArtwork = this.getArtwork(addIndex);
      Tdta tdta = new Tdta(artworkFile, newArtwork.getApplicationReference());
      newArtwork.setData(tdta.cast(Picture.class));
      return newArtwork;
   }
}
