package backend;

public class Media {
    private String mediaID;
    private String title;
    private String genre;
    private String releaseDate;
    private Integer season;
    private Integer episode;

    public Media(String mediaID, String title, String genre, String releaseDate) {
        this.mediaID = mediaID;
        this.title = title;
        this.genre = genre;
        this.releaseDate = releaseDate;
    }

    public Media(String mediaID, String title, String genre, String releaseDate, Integer season, Integer episode) {
        this(mediaID, title, genre, releaseDate);
        this.season = season;
        this.episode = episode;
    }

    public String getMediaID() {
        return mediaID;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public Integer getSeason() {
        return season;
    }

    public Integer getEpisode() {
        return episode;
    }
}
