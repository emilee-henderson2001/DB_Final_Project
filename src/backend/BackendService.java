package backend;

import java.util.List;

public class BackendService {

    private static final UserDAO userDAO = new UserDAO();
    private static final QueryDAO queryDAO = new QueryDAO();

    // Authentication
    public static boolean loginMember(String username, String password) {
        return userDAO.validateMember(username, password);
    }

    public static boolean loginAdmin(String username, String password) {
        return userDAO.validateAdmin(username, password);
    }

    // Search Movies/Series
    public static List<Media> searchMedia(String keyword, String filter) {
        return queryDAO.searchMedia(keyword, filter);
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
}
