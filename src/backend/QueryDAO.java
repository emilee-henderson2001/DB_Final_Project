package backend;
import backend.Media;
import java.sql.*;
import java.util.*;
import java.util.ArrayList;

public class QueryDAO {

    // Search Media by keyword
    public List<Media> searchMedia(String keyword, String filter) {
        List<Media> results = new ArrayList<>();

        String effectiveFilter = (filter == null ? "All" : filter);
        boolean isAll = "All".equals(effectiveFilter);
        boolean isSeries = "Series".equals(effectiveFilter);
        boolean isMovie = "Movie".equals(effectiveFilter);
        boolean returnAllMedia = isAll && (keyword == null || keyword.isEmpty());
        boolean returnAllSeries = isSeries && (keyword == null || keyword.isEmpty());
        boolean returnAllMovies = isMovie && (keyword == null || keyword.isEmpty());

        // If not returning all media/series, and no keyword was provided, there is nothing to search
        if (!(returnAllMedia || returnAllSeries || returnAllMovies) && (keyword == null || keyword.isEmpty()))
            return results;

        String like = "%" + (keyword == null ? "" : keyword) + "%";
        String sql;

        if (returnAllMedia) {
            // Show all movies and series
            sql = "SELECT DISTINCT m.media_ID, m.title, m.genre, m.release_date, m.IMBD_link FROM Media m";
        } else if (returnAllSeries) {
            // Show all series only
            sql = "SELECT DISTINCT m.media_ID, m.title, m.genre, m.release_date, m.IMBD_link " +
                    "FROM Series s JOIN Media m ON s.media_ID = m.media_ID";
        } else if (returnAllMovies) {
            // Show all movies only
            sql = "SELECT DISTINCT m.media_ID, m.title, m.genre, m.release_date, m.IMBD_link " +
                    "FROM Movie mv JOIN Media m ON mv.media_ID = m.media_ID";
        } else switch (effectiveFilter) {
            case "Actor":
                sql = "SELECT m.media_ID, m.title, m.genre, m.release_date, m.IMBD_link " +
                        "FROM Media m " +
                        "JOIN Acts a ON m.media_ID = a.media_ID " +
                        "JOIN Actor_actress act ON a.ID = act.ID " +
                        "WHERE act.actor_name LIKE ?";
                break;

            case "Director":
                sql = "SELECT m.media_ID, m.title, m.genre, m.release_date, m.IMBD_link " +
                        "FROM Media m " +
                        "JOIN Directs d ON m.media_ID = d.media_ID " +
                        "JOIN Director dir ON d.ID = dir.ID " +
                        "WHERE dir.director_name LIKE ?";
                break;

            case "Genre":
                sql = "SELECT m.media_ID, m.title, m.genre, m.release_date, m.IMBD_link " +
                        "FROM Media m " +
                        "WHERE m.genre LIKE ?";
                break;

            case "Sequel":
            case "Sequel(s)":
                sql = "SELECT DISTINCT m.media_ID, m.title, m.genre, m.release_date, m.IMBD_link " +
                        "FROM Media m " +
                        "JOIN Movie mv ON m.media_ID = mv.media_ID " +
                        "JOIN Sequel s ON mv.media_ID = s.movie1_ID " +
                        "WHERE m.title LIKE ?";
                break;
            case "Series":
                sql = "SELECT DISTINCT m.media_ID, m.title, m.genre, m.release_date, m.IMBD_link " +
                        "FROM Series s JOIN Media m ON s.media_ID = m.media_ID " +
                        "WHERE m.title LIKE ?";
                break;
            case "Movie":
                sql = "SELECT DISTINCT m.media_ID, m.title, m.genre, m.release_date, m.IMBD_link " +
                        "FROM Movie mv JOIN Media m ON mv.media_ID = m.media_ID " +
                        "WHERE m.title LIKE ?";
                break;
            case "Title":
                sql = "SELECT DISTINCT m.media_ID, m.title, m.genre, m.release_date, m.IMBD_link " +
                        "FROM Media m WHERE m.title LIKE ?";
                break;
            default:
                sql = "SELECT DISTINCT m.media_ID, m.title, m.genre, m.release_date, m.IMBD_link " +
                        "FROM Media m " +
                        "LEFT JOIN Acts a ON m.media_ID = a.media_ID " + //we need left join here so that search includes everything needed
                        "LEFT JOIN Actor_actress act ON a.ID = act.ID " + //because these are linked via another table and not directly in
                        "LEFT JOIN Directs d ON m.media_ID = d.media_ID " + //Media table
                        "LEFT JOIN Director dir ON d.ID = dir.ID " +
                        "WHERE m.title LIKE ? " +
                        "OR m.genre LIKE ? " +
                        "OR act.actor_name LIKE ? " +
                        "OR dir.director_name LIKE ?";
                break;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (!(returnAllMedia || returnAllSeries || returnAllMovies)) {
                ps.setString(1, like);

                // if using 'All' search (with keyword), we have to fill the rest of the parameters since we are using 'OR' in the query
                if (isAll) {
                    ps.setString(2, like);
                    ps.setString(3, like);
                    ps.setString(4, like);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapMedia(rs));
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
    public List<Media> getWatchHistoryByUser(String username) {
        List<Media> results = new ArrayList<>();
        if (username == null || username.isEmpty())
            return results;

        String sql =
                "SELECT m.media_ID, m.title, m.genre, m.release_date, m.IMBD_link, wh.watch_date " +
                        "FROM Media m " +
                        "JOIN Watch_History wh ON m.media_id = wh.media_id " +
                        "JOIN Member mem ON wh.member_id = mem.ID " +
                        "WHERE mem.username = ?" +
                        "ORDER BY wh.watch_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapMedia(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    public List<Member> getWatchHistoryByMedia(String mediaTitle) {
        List<Member> results = new ArrayList<>();
        if (mediaTitle == null || mediaTitle.isEmpty())
            return results;

        String sql;
        sql = "SELECT mem.ID, mem.member_name, wh.watch_date " +
                "FROM Member mem " +
                "JOIN Watch_History wh ON mem.ID = wh.member_id " +
                "JOIN Media m ON wh.media_id = m.media_ID " +
                "WHERE m.title = ?" +
                "ORDER BY wh.watch_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, mediaTitle);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Member member = new Member(rs.getInt("ID"), rs.getString("member_name"));
                    results.add(member);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }
    public List<Media> getTop10PopularMedia() {
        List<Media> results = new ArrayList<>();
        String sql;
        sql = "SELECT m.media_ID, m.title, m.genre, m.release_date, COUNT(wh.media_ID) AS watch_count " +
                "FROM Media m " +
                "JOIN Watch_History wh ON m.media_ID = wh.media_ID " +
                "WHERE MONTH(wh.watch_date) = MONTH(CURRENT_DATE()) " +
                "AND YEAR(wh.watch_date) = YEAR(CURRENT_DATE()) " +
                "GROUP BY m.media_ID, m.title, m.genre, m.release_date " +
                "ORDER BY watch_count DESC ";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                results.add(mapMedia(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // Get Movie Awards.
    public List<Map<String, Object>> getAwardWinningMovies() {
        String sql = """
        SELECT M.title,
               M.genre,
               M.release_date,
               Md.IMBD_link,
               GROUP_CONCAT(A.award_name SEPARATOR ', ') AS awards
        FROM Movie M
        JOIN Media Md ON M.media_ID = Md.media_ID
        JOIN Earned E ON M.media_ID = E.media_ID
        JOIN Award A ON E.award_name = A.award_name
        GROUP BY M.title, M.genre, M.release_date, Md.IMBD_link
        ORDER BY M.title;
    """;

        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("title", rs.getString("title"));
                row.put("genre", rs.getString("genre"));
                row.put("release_date", rs.getString("release_date"));
                row.put("IMBD_link", rs.getString("IMBD_link"));
                row.put("awards", rs.getString("awards"));
                result.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Get Unwatched Series.
    public List<Map<String, Object>> getUnwatchedSeriesByUser(int memberId) {
        String sql = """
        SELECT S.title, S.genre, S.release_date, S.season, S.episode, M.IMBD_link
        FROM Series S
        JOIN Media M ON S.media_ID = M.media_ID
        WHERE S.media_ID NOT IN (
            SELECT media_ID
            FROM Watch_History
            WHERE member_id = ?
        )
        ORDER BY S.title;
    """;

        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("title", rs.getString("title"));
                    row.put("genre", rs.getString("genre"));
                    row.put("release_date", rs.getString("release_date"));
                    row.put("season", rs.getInt("season"));
                    row.put("episode", rs.getInt("episode"));
                    row.put("IMBD_link", rs.getString("IMBD_link"));
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Get ID by username
    public int getMemberIdByUsername(String username) {
        String sql = "SELECT ID FROM Member WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // return -1 if not found
    }
    public void addMediaToWatchHistory(int memberID, String mediaID) throws SQLException{
        String sql = """ 
                INSERT INTO Watch_History(member_id, media_id, watch_date)
                     VALUES (?, ?, NOW());
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){
                 ps.setInt(1, memberID);
                 ps.setString(2, mediaID);
                 ps.execute();
        }
        catch(SQLException e){
                 e.printStackTrace();
        }
    }
    public void addNewMember(int id, String password, String username, String address, String phoneNum, String email){
        String sql = """
                INSERT INTO Member(username, password, address, phoneNum, email)
                VALUES (?, ?, ?, ?, ?);
                """;
            try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);
            ps.setString(2, password);
            ps.setString(3, username);
            ps.setString(4, address);
            ps.setString(5, phoneNum);
            ps.setString(6, email);
            ps.execute();
            }
                catch(SQLException e){
                e.printStackTrace();
            }
    }

    // maps a ResultSet
    private Media mapMedia(ResultSet rs) throws SQLException {
        String mediaID = rs.getString("media_ID");

        // Try to read season and episode — will be null if movie
        Integer season = null;
        Integer episode = null;
        String imdbLink = null;
        Timestamp watchDate = null;
        try {
            season = rs.getInt("season");
            episode = rs.getInt("episode");
        } catch (SQLException e) {
            //ignore if not series
        }
        try {
            imdbLink = rs.getString("IMBD_link");
        } catch (SQLException e) {
            // ignore if not present in query
        }
        try{
            watchDate = rs.getTimestamp("watch_date");
        } catch (SQLException e) {
            //ignore if not present in query
        }
        Media m = new Media(
                mediaID,
                rs.getString("title"),
                rs.getString("genre"),
                rs.getString("release_date"),
                season,
                episode,
                imdbLink
        );
        if(watchDate != null){
            m.setWatchDate(watchDate);
        }
        return m;
    }

}
