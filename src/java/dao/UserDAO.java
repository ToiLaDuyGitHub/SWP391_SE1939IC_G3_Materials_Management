/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import model.User;
import model.dto.User_Role;
import util.DBUtil;
import util.PasswordUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ToiLaDuyGitHub
 */
public class UserDAO {

    private Argon2 argon2;

    public UserDAO() {
        argon2 = Argon2Factory.create();
    }

    // Lấy thông tin người dùng và vai trò
    public User_Role getUserWithRole(String username) {
        String sql = "SELECT u.UserID, u.FirstName, u.LastName, u.Username, u.PhoneNum, u.Address, r.RoleName, u.RoleID, u.IsActive "
                + "FROM users u "
                + "LEFT JOIN roles r ON u.RoleID = r.RoleID "
                + "WHERE u.Username = ?";

        try (
                Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String firstName = rs.getString("FirstName");
                    String lastName = rs.getString("LastName");
                    String userName = rs.getString("Username");
                    String phoneNum = rs.getString("PhoneNum");
                    String address = rs.getString("Address");
                    String roleName = rs.getString("RoleName");
                    int roleID = rs.getInt("RoleID");
                    int userID = rs.getInt("UserID");
                    boolean isActive = rs.getBoolean("isActive");
                    return new User_Role(userName, firstName, lastName, phoneNum, address, roleName, roleID, userID, isActive);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Kiểm tra thông tin đăng nhập
    public static boolean validateUser(String username, String rawPassword) {
        String sql = "SELECT PasswordHash FROM users WHERE username = ?";

        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("PasswordHash");
                    return PasswordUtil.verifyPassword(storedHash, rawPassword);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void changePassword(String username, String passwordHash) {
        String sql = "update users set PasswordHash = ? where Username = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set các tham số
            stmt.setString(1, passwordHash);
            stmt.setString(2, username);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Cập nhật mật khẩu thất bại, không tìm thấy user: " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUserProfile(String username, String firstName, String lastName, String phone, String address) throws SQLException {
        // Kiểm tra dữ liệu đầu vào
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ không được để trống.");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên không được để trống.");
        }
        if (phone == null || phone.trim().isEmpty() || !isValidPhone(phone)) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (phải có 10-11 chữ số và bắt đầu bằng 0).");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Địa chỉ không được để trống.");
        }

        // Câu lệnh SQL để cập nhật thông tin người dùng
        String sql = "UPDATE users SET FirstName = ?, LastName = ?, PhoneNum = ?, Address = ? WHERE Username = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Gán giá trị cho các tham số
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, phone);
            stmt.setString(4, address);
            stmt.setString(5, username);

            // Thực thi câu lệnh cập nhật
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Cập nhật thông tin thất bại, không tìm thấy user: " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Hàm kiểm tra số điện thoại hợp lệ
    private boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }
        // Kiểm tra độ dài: phải từ 10 đến 11 chữ số
        if (phone.length() < 10 || phone.length() > 11) {
            return false;
        }
        // Kiểm tra bắt đầu bằng 0
        if (!phone.startsWith("0")) {
            return false;
        }
        // Kiểm tra tất cả ký tự là số (0-9)
        for (char c : phone.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    //Retrive user and its reset password request status
    public User getUserWithResetRequest(String username) {
        String sql = "SELECT IsResetRequested FROM users WHERE username = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean isResetRequested = rs.getBoolean("IsResetRequested");
                    return new User(username, isResetRequested);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean requestResetPassword(String username) {
        String sql = "UPDATE users SET IsResetRequested = 1 WHERE username = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public List<User> getResetPasswordReqList(int offset, int limit) {
        List<User> userList = new ArrayList<User>();
        String sql = "SELECT u.username, u.firstname, u.lastname, u.isresetrequested FROM users u where u.isresetrequested = 1 LIMIT ? OFFSET ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User u = new User(rs.getString("username"),
                            rs.getString("firstname"),
                            rs.getString("lastname"),
                            rs.getBoolean("isresetrequested"));
                    userList.add(u);
                }
            }
            return userList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Triển khai đếm tổng số yêu cầu
    public int getNoOfResetPasswordRequests() {
        String sql = "SELECT COUNT(*) AS total FROM users WHERE isresetrequested = 1";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; //
        }
    }

    public static User resetPasswordForUser(String username, String passwordHash) {
        String sql = "UPDATE Users SET PasswordHash = ?, isresetrequested = 0 WHERE username = ? AND isresetrequested = 1";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, passwordHash);
            stmt.setString(2, username);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new User(username, false);
    }

    //Main for DAO Testing
    public static void main(String[] args) {
        User u = UserDAO.resetPasswordForUser("toiladuygg@gmail.com", "abc12345");
        System.out.println(u);
    }

    public List<User> getUserByName(String name) {
        List<User> customers = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE RoleID = 1 AND (FirstName LIKE ? OR LastName LIKE ? OR CONCAT(FirstName, ' ', LastName) LIKE ?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, "%" + name + "%");
            stm.setString(2, "%" + name + "%");
            stm.setString(3, "%" + name + "%");
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    User u = new User(
                            rs.getInt("UserID"),
                            rs.getString("Username"),
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            rs.getString("PhoneNum"),
                            rs.getInt("RoleID"),
                            rs.getBoolean("IsActive")
                    );
                    customers.add(u);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return customers;
    }

    public int insertUser(User u) {
        // Kiểm tra tên column chính xác trong database
        String sql = "INSERT INTO users (Username, PasswordHash, FirstName, LastName, PhoneNum, Address, RoleID, IsActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection(); PreparedStatement stm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stm.setString(1, u.getUsername());
            stm.setString(2, u.getPasswordHash()); // Đảm bảo password đã được hash
            stm.setString(3, u.getFirstName());
            stm.setString(4, u.getLastName());
            stm.setString(5, u.getPhoneNum());
            stm.setString(6, u.getAddress());
            stm.setInt(7, u.getRoleID());
            stm.setBoolean(8, true); // IsActive = true

            int affectedRows = stm.executeUpdate();

            if (affectedRows == 0) {
                Logger.getLogger(UserDAO.class.getName()).log(Level.WARNING, "No rows affected when inserting user: " + u.getUsername());
                return -1;
            }

            try (ResultSet rs = stm.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    Logger.getLogger(UserDAO.class.getName()).log(Level.INFO, "Successfully inserted user: " + u.getUsername() + " with ID: " + generatedId);
                    return generatedId;
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Error inserting user: " + u.getUsername(), ex);
            // In ra chi tiết lỗi để debug
            System.err.println("SQL Error Code: " + ex.getErrorCode());
            System.err.println("SQL State: " + ex.getSQLState());
            System.err.println("Error Message: " + ex.getMessage());
        }
        return -1;
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE UserID = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, userId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("UserID"),
                            rs.getString("Username"),
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            rs.getString("PhoneNum"),
                            rs.getInt("RoleID"),
                            rs.getBoolean("IsActive")
                    );
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<User> getFilteredUsers(String keywords, String roleFilter, String statusFilter) {
        List<User> users = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE 1=1");
        List<Object> params = new ArrayList<>();

        // Thêm điều kiện tìm kiếm theo từ khóa
        if (keywords != null && !keywords.trim().isEmpty()) {
            sql.append(" AND (FirstName LIKE ? OR LastName LIKE ? OR CONCAT(FirstName, ' ', LastName) LIKE ?)");
            String keywordPattern = "%" + keywords.trim() + "%";
            params.add(keywordPattern);
            params.add(keywordPattern);
            params.add(keywordPattern);
        }

        // Thêm điều kiện lọc theo vai trò
        if (roleFilter != null && !roleFilter.isEmpty()) {
            try {
                int roleId = Integer.parseInt(roleFilter);
                sql.append(" AND RoleID = ?");
                params.add(roleId);
            } catch (NumberFormatException e) {
                Logger.getLogger(UserDAO.class.getName()).log(Level.WARNING, "Invalid roleFilter: " + roleFilter, e);
            }
        }

        // Thêm điều kiện lọc theo trạng thái
        if (statusFilter != null && !statusFilter.isEmpty()) {
            if (statusFilter.equalsIgnoreCase("true") || statusFilter.equalsIgnoreCase("false")) {
                sql.append(" AND IsActive = ?");
                params.add(Boolean.parseBoolean(statusFilter));
            } else {
                Logger.getLogger(UserDAO.class.getName()).log(Level.WARNING, "Invalid statusFilter: " + statusFilter);
            }
        }

        try (Connection conn = DBUtil.getConnection(); PreparedStatement stm = conn.prepareStatement(sql.toString())) {
            // Gán các tham số cho PreparedStatement
            for (int i = 0; i < params.size(); i++) {
                stm.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    User u = new User(
                            rs.getInt("UserID"),
                            rs.getString("Username"),
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            rs.getString("PhoneNum"),
                            rs.getInt("RoleID"),
                            rs.getBoolean("IsActive")
                    );
                    users.add(u);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Error fetching filtered users", ex);
        }

        return users;
    }

    public List<User_Role> getAllUsersWithRoles() {
        List<User_Role> users = new ArrayList<>();
        String sql = "SELECT u.UserID, u.Username, u.FirstName, u.LastName, u.PhoneNum, u.RoleID, r.RoleName, u.IsActive "
                + "FROM users u JOIN roles r ON u.RoleID = r.RoleID "
                + "WHERE r.RoleName != 'Quản lý kho'";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stm = conn.prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                User_Role ur = new User_Role(
                        rs.getInt("UserID"),
                        rs.getString("Username"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("PhoneNum"),
                        rs.getInt("RoleID"),
                        rs.getString("RoleName"),
                        rs.getBoolean("IsActive")
                );
                users.add(ur);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return users;
    }

    // Lấy danh sách người dùng lọc theo từ khóa, vai trò và trạng thái
    public List<User_Role> getFilteredUsersWithRoles(String keywords, String roleFilter, String statusFilter, int page, int pageSize) {
        List<User_Role> users = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT u.UserID, u.Username, u.FirstName, u.LastName, u.PhoneNum, u.RoleID, r.RoleName, u.IsActive "
                + "FROM users u JOIN roles r ON u.RoleID = r.RoleID WHERE r.RoleName != 'Quản lý kho'");
        List<Object> params = new ArrayList<>();

        if (keywords != null && !keywords.trim().isEmpty()) {
            sql.append(" AND (u.FirstName LIKE ? OR u.LastName LIKE ? OR CONCAT(u.FirstName, ' ', u.LastName) LIKE ?)");
            String keywordPattern = "%" + keywords.trim() + "%";
            params.add(keywordPattern);
            params.add(keywordPattern);
            params.add(keywordPattern);
        }
        if (roleFilter != null && !roleFilter.isEmpty()) {
            try {
                int roleId = Integer.parseInt(roleFilter);
                sql.append(" AND u.RoleID = ?");
                params.add(roleId);
            } catch (NumberFormatException e) {
                Logger.getLogger(UserDAO.class.getName()).log(Level.WARNING, "Invalid roleFilter: " + roleFilter, e);
            }
        }
        if (statusFilter != null && !statusFilter.isEmpty()) {
            if (statusFilter.equalsIgnoreCase("true") || statusFilter.equalsIgnoreCase("false")) {
                sql.append(" AND u.IsActive = ?");
                params.add(Boolean.parseBoolean(statusFilter));
            } else {
                Logger.getLogger(UserDAO.class.getName()).log(Level.WARNING, "Invalid statusFilter: " + statusFilter);
            }
        }
        int offset = (page - 1) * pageSize;
        sql.append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add(offset);

        try (Connection conn = DBUtil.getConnection(); PreparedStatement stm = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stm.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    User_Role ur = new User_Role(
                            rs.getInt("UserID"),
                            rs.getString("Username"),
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            rs.getString("PhoneNum"),
                            rs.getInt("RoleID"),
                            rs.getString("RoleName"),
                            rs.getBoolean("IsActive")
                    );
                    users.add(ur);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return users;
    }

    public void updateUserStatus(int userId, boolean isActive) {
        String sql = "UPDATE users SET IsActive = ? WHERE UserID = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setBoolean(1, isActive);
            stm.setInt(2, userId);
            stm.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public User_Role getUserByIdWithRole(int userId) {
        String sql = "SELECT u.UserID, u.Username, u.FirstName, u.LastName, u.PhoneNum, u.Address, r.RoleName, u.IsActive "
                + "FROM users u JOIN roles r ON u.RoleID = r.RoleID "
                + "WHERE u.UserID = ? AND r.RoleName != 'Quản lý kho'";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, userId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return new User_Role(
                            rs.getInt("UserID"),
                            rs.getString("Username"),
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            rs.getString("PhoneNum"),
                            rs.getString("Address"),
                            rs.getString("RoleName"),
                            rs.getBoolean("IsActive")
                    );
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int getFilteredUsersCount(String keywords, String roleFilter, String statusFilter) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM users u JOIN roles r ON u.RoleID = r.RoleID WHERE r.RoleName != 'Quản lý kho'");
        List<Object> params = new ArrayList<>();

        if (keywords != null && !keywords.trim().isEmpty()) {
            sql.append(" AND (u.FirstName LIKE ? OR u.LastName LIKE ? OR CONCAT(u.FirstName, ' ', u.LastName) LIKE ?)");
            String keywordPattern = "%" + keywords.trim() + "%";
            params.add(keywordPattern);
            params.add(keywordPattern);
            params.add(keywordPattern);
        }
        if (roleFilter != null && !roleFilter.isEmpty()) {
            try {
                int roleId = Integer.parseInt(roleFilter);
                sql.append(" AND u.RoleID = ?");
                params.add(roleId);
            } catch (NumberFormatException e) {
                Logger.getLogger(UserDAO.class.getName()).log(Level.WARNING, "Invalid roleFilter: " + roleFilter, e);
            }
        }
        if (statusFilter != null && !statusFilter.isEmpty()) {
            if (statusFilter.equalsIgnoreCase("true") || statusFilter.equalsIgnoreCase("false")) {
                sql.append(" AND u.IsActive = ?");
                params.add(Boolean.parseBoolean(statusFilter));
            } else {
                Logger.getLogger(UserDAO.class.getName()).log(Level.WARNING, "Invalid statusFilter: " + statusFilter);
            }
        }

        try (Connection conn = DBUtil.getConnection(); PreparedStatement stm = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stm.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Error counting filtered users", ex);
        }
        return 0;
    }

    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE Username = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, username);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Error checking username exists: " + username, ex);
        }
        return false;
    }

    //Edit by Bui Hieu
    public boolean isDirectorExists() {
        String sql = "SELECT COUNT(*) FROM users WHERE RoleID = 3 AND IsActive = true";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stm = conn.prepareStatement(sql)) {
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Error checking director exists", ex);
        }
        return false;
    }
}
