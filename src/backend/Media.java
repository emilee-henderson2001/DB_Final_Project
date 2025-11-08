package backend;

public class Media {
    private int mediaID;
    private String title;
    private String genre;
    private String releaseDate;

    public Media(int mediaID, String title, String genre, String releaseDate) {
        this.mediaID = mediaID;
        this.title = title;
        this.genre = genre;
        this.releaseDate = releaseDate;
    }

    public int getMediaID() {
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

    // Debugging and display
    @Override
    public String toString() {
        return title + " (" + genre + ", " + releaseDate + ")";
    }
}
