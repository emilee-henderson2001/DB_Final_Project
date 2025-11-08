package backend;

import java.util.List;

public class BackendService {

    private static final UserDAO userDAO = new UserDAO();

    // Authentication
    public static boolean loginMember(String username, String password) {
        return userDAO.validateMember(username, password);
    }

    public static boolean loginAdmin(String username, String password) {
        return userDAO.validateAdmin(username, password);
    }
}
