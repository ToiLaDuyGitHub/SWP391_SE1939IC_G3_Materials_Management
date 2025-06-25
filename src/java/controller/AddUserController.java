package controller;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import java.io.IOException;
import util.CreateUserMail;
import util.PasswordUtil;
import util.ResetPassword;
import java.util.regex.Pattern;

@WebServlet(name = "AddUserController", urlPatterns = {"/add-user"})
public class AddUserController extends HttpServlet {

      private static final String SUCCESS_URL = "home.jsp";
    private static final String ERROR_URL = "addUser.jsp";
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        UserDAO userDAO = new UserDAO();

        if ("add".equals(action)) {
            try {
                // Lấy dữ liệu từ form
                String username = req.getParameter("username");
                String firstName = req.getParameter("firstName");
                String lastName = req.getParameter("lastName");
                String phoneNum = req.getParameter("phoneNum");
                String address = req.getParameter("address");
                String roleIDStr = req.getParameter("roleID");

                // Kiểm tra dữ liệu đầu vào
                if (username == null || username.trim().isEmpty()) {
                    req.setAttribute("error", "Tên đăng nhập không được để trống!");
                    req.getRequestDispatcher(ERROR_URL).forward(req, resp);
                    return;
                }

                // Kiểm tra email hợp lệ
                if (!EMAIL_PATTERN.matcher(username.trim()).matches()) {
                    req.setAttribute("error", "Tên đăng nhập phải là email hợp lệ!");
                    req.getRequestDispatcher(ERROR_URL).forward(req, resp);
                    return;
                }

                // Kiểm tra roleID
                if (roleIDStr == null || roleIDStr.trim().isEmpty()) {
                    req.setAttribute("error", "Vui lòng chọn vai trò!");
                    req.getRequestDispatcher(ERROR_URL).forward(req, resp);
                    return;
                }

                int roleID;
                try {
                    roleID = Integer.parseInt(roleIDStr);
                    if (roleID < 1 || roleID > 4) {
                        throw new NumberFormatException("Invalid role ID");
                    }
                } catch (NumberFormatException e) {
                    req.setAttribute("error", "Vai trò không hợp lệ!");
                    req.getRequestDispatcher(ERROR_URL).forward(req, resp);
                    return;
                }
                if (roleID == 3 && userDAO.isDirectorExists()) {
                    req.setAttribute("error", "Đã tồn tại một Giám đốc công ty! Không thể thêm người dùng với vai trò này.");
                    req.getRequestDispatcher(ERROR_URL).forward(req, resp);
                    return;
                }
                // Kiểm tra username đã tồn tại chưa
                if (userDAO.isUsernameExists(username.trim())) {
                    req.setAttribute("error", "Tên đăng nhập đã tồn tại!");
                    req.getRequestDispatcher(ERROR_URL).forward(req, resp);
                    return;
                }

                // Tạo mật khẩu ngẫu nhiên
                String password = ResetPassword.generateRandomString();
                String passwordHash = PasswordUtil.hashPassword(password);
                
                // Tạo đối tượng User
                User newUser = new User(0, username.trim(), passwordHash, 
                    firstName != null ? firstName.trim() : "", 
                    lastName != null ? lastName.trim() : "", 
                    phoneNum != null ? phoneNum.trim() : "", 
                    address != null ? address.trim() : "", 
                    roleID);

                // Thêm người dùng vào cơ sở dữ liệu
                int newUserId = userDAO.insertUser(newUser);
                if (newUserId != -1) {
                    // Gửi email thông tin tài khoản
                    try {
                        CreateUserMail.sendEmail(username.trim(), password);
                        req.setAttribute("message", "Thêm người dùng thành công! Email thông tin đăng nhập đã được gửi.");
                    } catch (Exception emailEx) {
                        System.err.println("Lỗi gửi email: " + emailEx.getMessage());
                        req.setAttribute("message", "Thêm người dùng thành công nhưng không thể gửi email thông báo!");
                    }
                    
                    // Lấy lại danh sách người dùng để hiển thị
                    req.setAttribute("allUser", userDAO.getAllUsersWithRoles());
                    req.setAttribute("manageUser", "YES");
                    req.getRequestDispatcher(SUCCESS_URL).forward(req, resp);
                } else {
                    req.setAttribute("error", "Không thể thêm người dùng! Vui lòng thử lại.");
                    req.getRequestDispatcher(ERROR_URL).forward(req, resp);
                }
                
            } catch (Exception e) {
                System.err.println("Error in AddUserController: " + e.getMessage());
                e.printStackTrace();
                req.setAttribute("error", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
                req.getRequestDispatcher(ERROR_URL).forward(req, resp);
            }
        } else {
            req.setAttribute("error", "Hành động không hợp lệ!");
            req.getRequestDispatcher(ERROR_URL).forward(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Hiển thị form thêm người dùng
        
        req.getRequestDispatcher(ERROR_URL).forward(req, resp);
    }
}