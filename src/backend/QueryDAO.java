package backend;
import backend.Media;
import java.sql.*;
import java.util.*;
import java.util.ArrayList;

public class QueryDAO {

    // Search Media by keyword
    public List<Media> searchMedia(String keyword, String filter) {
        List<Media> results = new ArrayList<>();
        if (keyword == null || keyword.isEmpty())
            return results;

        String like = "%" + keyword + "%";
        String sql;

        switch (filter == null ? "All" : filter) {
            case "Actor":
                sql = "SELECT m.media_ID, m.title, m.genre, m.release_date " +
                        "FROM Media m " +
                        "JOIN Acts a ON m.media_ID = a.media_ID " +
                        "JOIN Actor_actress act ON a.ID = act.ID " +
                        "WHERE act.actor_name LIKE ?";
                break;

            case "Director":
                sql = "SELECT m.media_ID, m.title, m.genre, m.release_date " +
                        "FROM Media m " +
                        "JOIN Directs d ON m.media_ID = d.media_ID " +
                        "JOIN Director dir ON d.ID = dir.ID " +
                        "WHERE dir.director_name LIKE ?";
                break;

            case "Genre":
                sql = "SELECT m.media_ID, m.title, m.genre, m.release_date " +
                        "FROM Media m " +
                        "WHERE m.genre LIKE ?";
                break;

            case "Sequel":
                sql = "SELECT DISTINCT m.media_ID, m.title, m.genre, m.release_date " +
                        "FROM Media m " +
                        "JOIN Movie mv ON m.media_ID = mv.media_ID " +
                        "JOIN Sequel s ON mv.media_ID = s.movie1_ID " +
                        "WHERE m.title LIKE ?";
                break;

            default:
                sql = "SELECT m.media_ID, m.title, m.genre, m.release_date " +
                        "FROM Media m WHERE m.title LIKE ?";
                break;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, like);
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
                "SELECT m.media_ID, m.title, m.genre, m.release_date " +
                        "FROM Media m " +
                        "JOIN Watch_History wh ON m.media_id = wh.media_id " +
                        "JOIN Member mem ON wh.member_id = mem.ID " +
                        "WHERE mem.username = ?";

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
        sql = "SELECT mem.ID, mem.member_name " +
                "FROM Member mem " +
                "JOIN Watch_History wh ON mem.ID = wh.member_id " +
                "JOIN Media m ON wh.media_id = m.media_ID " +
                "WHERE m.title = ?";

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

    // maps a ResultSet
    private Media mapMedia(ResultSet rs) throws SQLException {
        String mediaID = rs.getString("media_ID");

        // Try to read season and episode — will be null if movie
        Integer season = null;
        Integer episode = null;
        try {
            season = rs.getInt("season");
            episode = rs.getInt("episode");
        } catch (SQLException e) {
            //ignore if not series
        }

        return new Media(
                mediaID,
                rs.getString("title"),
                rs.getString("genre"),
                rs.getString("release_date"),
                season,
                episode
        );
    }

}
