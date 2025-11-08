package backend;
import java.sql.*;

public class UserDAO {

    // Member login check
    public boolean validateMember(String user, String pass) {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM Member WHERE username=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            boolean ok = rs.next();
            conn.close();
            return ok;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean validateAdmin(String user, String pass) {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM Admin WHERE admin_username=? AND admin_password=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            boolean ok = rs.next();
            conn.close();
            return ok;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}