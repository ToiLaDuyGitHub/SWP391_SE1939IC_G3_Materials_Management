package controller;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import model.dto.User_Role;

public class ManageUserController extends HttpServlet {

    private static final String HOME_URL = "Admin_UserManager.jsp";

    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int page = 1;
        String pageStr = req.getParameter("page");
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid page number: " + pageStr);
            }
        }
        int pageSize = 2;

        String service = req.getParameter("service");
        if (service == null || service.trim().isEmpty()) {
            service = "listAllUser";
        }

        UserDAO userDAO = new UserDAO();

        switch (service) {
            case "listAllUser":
                // [SỬA] Gọi phương thức phân trang với bộ lọc rỗng
                String keywords = "";
                String roleFilter = "";
                String statusFilter = "";
                List<User_Role> users = userDAO.getFilteredUsersWithRoles(keywords, roleFilter, statusFilter, page, pageSize);
                int totalUsers = userDAO.getFilteredUsersCount(keywords, roleFilter, statusFilter);
                req.setAttribute("allUser", users);
                req.setAttribute("totalUsers", totalUsers);
                req.setAttribute("currentPage", page);
                req.setAttribute("pageSize", pageSize);
                req.setAttribute("manageUser", "YES");
                break;

            case "searchByKeywords":
                // [SỬA] Gọi phương thức phân trang với bộ lọc
                keywords = req.getParameter("keywords");
                roleFilter = req.getParameter("roleFilter");
                statusFilter = req.getParameter("statusFilter");
                users = userDAO.getFilteredUsersWithRoles(keywords, roleFilter, statusFilter, page, pageSize);
                totalUsers = userDAO.getFilteredUsersCount(keywords, roleFilter, statusFilter);
                req.setAttribute("allUser", users);
                req.setAttribute("totalUsers", totalUsers);
                req.setAttribute("currentPage", page);
                req.setAttribute("pageSize", pageSize);
                req.setAttribute("keywords", keywords);
                req.setAttribute("roleFilter", roleFilter);
                req.setAttribute("statusFilter", statusFilter);
                req.setAttribute("manageUser", "YES");
                break;
            case "userDetail":
                String userIdStr = req.getParameter("userId");
                if (userIdStr != null && !userIdStr.isEmpty()) {
                    try {
                        int userId = Integer.parseInt(userIdStr);
                        User_Role detailUser = userDAO.getUserByIdWithRole(userId);
                        if (detailUser != null) {
                            req.setAttribute("detailUser", detailUser);
                            req.removeAttribute("allUser"); 
                        } else {
                            req.setAttribute("error", "Không tìm thấy người dùng với ID: " + userId);
                        }
                    } catch (NumberFormatException e) {
                        req.setAttribute("error", "ID người dùng không hợp lệ: " + userIdStr);
                    }
                } else {
                    req.setAttribute("error", "Thiếu tham số userId");
                }
                break;

            default:
                req.setAttribute("error", "Yêu cầu không hợp lệ");
                List<User_Role> defaultUsers = userDAO.getAllUsersWithRoles();
                req.setAttribute("allUser", defaultUsers);
                req.setAttribute("manageUser", "YES");
                break;
        }

        req.getRequestDispatcher(HOME_URL).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String service = req.getParameter("service");
        UserDAO userDAO = new UserDAO();

        if ("updateStatus".equals(action)) {
            String userIdStr = req.getParameter("userId");
            String isActiveStr = req.getParameter("isActive");
            String keywords = req.getParameter("keywords");
            String roleFilter = req.getParameter("roleFilter");
            String statusFilter = req.getParameter("statusFilter");
            String pageStr = req.getParameter("page");

            if (userIdStr != null && isActiveStr != null) {
                try {
                    int userId = Integer.parseInt(userIdStr);
                    boolean isActive = Boolean.parseBoolean(isActiveStr);
                    userDAO.updateUserStatus(userId, isActive);

                    // Chuyển hướng về danh sách người dùng với bộ lọc hiện tại
                    String redirectUrl = "manage-user?service=searchByKeywords";
                    if (keywords != null && !keywords.isEmpty()) {
                        redirectUrl += "&keywords=" + URLEncoder.encode(keywords, "UTF-8");
                    }
                    if (roleFilter != null && !roleFilter.isEmpty()) {
                        redirectUrl += "&roleFilter=" + roleFilter;
                    }
                    if (statusFilter != null && !statusFilter.isEmpty()) {
                        redirectUrl += "&statusFilter=" + statusFilter;
                    }

                    resp.sendRedirect(redirectUrl);
                    return;
                } catch (NumberFormatException e) {
                    req.setAttribute("error", "ID người dùng không hợp lệ");
                }
            } else {
                req.setAttribute("error", "Thiếu tham số");
            }
            String redirectUrl = req.getContextPath() + "/manage-user?service=" + (service.equals("searchByKeywords") ? "searchByKeywords" : "listAllUser");
            if (keywords != null && !keywords.isEmpty()) {
                redirectUrl += "&keywords=" + URLEncoder.encode(keywords, "UTF-8");
            }
            if (roleFilter != null && !roleFilter.isEmpty()) {
                redirectUrl += "&roleFilter=" + roleFilter;
            }
            if (statusFilter != null && !statusFilter.isEmpty()) {
                redirectUrl += "&statusFilter=" + statusFilter;
            }
            if (pageStr != null && !pageStr.isEmpty()) {
                redirectUrl += "&page=" + pageStr;
            }
            resp.sendRedirect(redirectUrl);
        }

        // Xử lý các hành động khác (add, edit, v.v.)
        List<User_Role> users = userDAO.getAllUsersWithRoles();
        req.setAttribute("allUser", users);
        req.setAttribute("manageUser", "YES");
        req.getRequestDispatcher(HOME_URL).forward(req, resp);
    }
}