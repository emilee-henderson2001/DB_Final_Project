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
    public List<Media> getWatchHistoryByUser(String userID) {
        List<Media> results = new ArrayList<>();
        if (userID == null || userID.isEmpty())
            return results;

        String sql;
        sql = "SELECT m.media_ID, m.title, m.genre, m.release_date " +
                "FROM Media m " +
                "JOIN Watch_History wh ON m.media_id = wh.media_id " +
                "WHERE wh.member_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(userID));

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



    // maps a ResultSet
    private Media mapMedia(ResultSet rs) throws SQLException {
        return new Media(
                rs.getInt("media_ID"),
                rs.getString("title"),
                rs.getString("genre"),
                rs.getString("release_date")
        );
    }

}
