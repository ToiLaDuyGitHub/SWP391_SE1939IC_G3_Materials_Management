/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.User;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 *
 * @author ToiLaDuyGitHub 
 * Cực kì lưu ý: 
 * - Chỉ chạy hàm này khi cần thêm dữ liệu của user mà không chạy web 
 * - Không được sử dụng username đã có trong db để chạy class này, vì class chưa được kiểm tra validate 
 * - Đây chỉ là class test, nên sau khi deploy lên pro sẽ xoá
 */
public class CreateNewUser {

    public static void main(String[] args) {
        //Dữ liệu test, trước khi chạy main hãy kiểm tra có bản ghi nào của gmail này không, nếu có thì phải xoá trước
        String username = "admin";
        String password = "admin";
        String passwordHash = PasswordUtil.hashPassword(password);
        User u = new User(username, passwordHash, "Hieu", "Nguyen Trung", 1, "0346832536", "Nhân nghĩa, Lý Nhân, Hà Nội", LocalDateTime.now(), true, false);
        saveUser(u);
    }

    // Hàm lưu user vào database
    public static boolean saveUser(User u) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            // 1. Kết nối database
            conn = DBUtil.getConnection();

            // 2. Kiểm tra username/email đã tồn tại chưa
            if (isUserExists(conn, u.getUsername())) {
                System.out.println("Username hoặc email đã tồn tại!");
                return false;
            }

            // 3. Chuẩn bị câu lệnh SQL
            String sql = "INSERT INTO users (Username, PasswordHash, FirstName, LastName, RoleID, PhoneNum, Address, RegistrationDate, IsActive)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, u.getUsername());
            stmt.setString(2, u.getPasswordHash());
            stmt.setString(3, u.getFirstName());
            stmt.setString(4, u.getLastName());
            stmt.setInt(5, u.getRoleID());
            stmt.setString(6, u.getPhoneNum());
            stmt.setString(7, u.getAddress());
            stmt.setTimestamp(8, Timestamp.valueOf(u.getRegistrationDate()));
            stmt.setBoolean(9, u.isIsActive());

            // 5. Thực thi và kiểm tra kết quả
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Tạo tài khoản test thành công");
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // 6. Đóng kết nối
            DBUtil.closeConnection(conn);
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Hàm kiểm tra user/email trùng
    private static boolean isUserExists(Connection conn, String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Trả về true nếu tồn tại
            }
        }
    }
}
