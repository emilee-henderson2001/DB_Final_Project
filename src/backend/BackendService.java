package backend;

import java.util.List;
import java.util.Map;

public class BackendService {

    private static final UserDAO userDAO = new UserDAO();
    private static final QueryDAO queryDAO = new QueryDAO();
    private static final PosterService posterService = new PosterService();

    // Authentication
    public static boolean loginMember(String username, String password) {
        return userDAO.validateMember(username, password);
    }

    public static boolean loginAdmin(String username, String password) {
        return userDAO.validateAdmin(username, password);
    }

    public static void enrichPoster(Media media) {
        if (media == null) return;
        try {
            if (media.getPosterUrl() == null || media.getPosterUrl().isBlank()) {
                String poster = posterService.resolvePosterUrl(media.getImdbLink());
                media.setPosterUrl(poster);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Search Movies/Series
    public static List<Media> searchMedia(String keyword, String filter) {
        List<Media> media = queryDAO.searchMedia(keyword, filter);

        // best-effort poster lookup using IMDb links
        for (Media m : media) {
            enrichPoster(m);
        }
        return media;
    }
    public static List<Media> getWatchHistoryByUser(String keyword) {
        return queryDAO.getWatchHistoryByUser(keyword);
    }
    public static List<Member> getWatchHistoryByMedia(String keyword) {
        return queryDAO.getWatchHistoryByMedia(keyword);
    }
    public static List<Media> getTop10PopularMedia(){
        return queryDAO.getTop10PopularMedia();
    }

    // Get Movie Awards.
    public static List<Map<String, Object>> getAwardWinningMovies() {
        return queryDAO.getAwardWinningMovies();
    }

    // See unwatched Movies/Series
    public static List<Map<String, Object>> getUnwatchedSeriesByUser(int memberId) {
        return queryDAO.getUnwatchedSeriesByUser(memberId);
    }

    // Get ID by username
    public static int getMemberIdByUsername(String username) {
        return queryDAO.getMemberIdByUsername(username);
    }

    //add streamed media to watch history
    public static void addMediaToWatchHistory(int memberID, String mediaId) throws Exception{
        queryDAO.addMediaToWatchHistory(memberID,mediaId);
    }
}


