
<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.User" %>
<%@ page import="model.dto.User_Role" %>
<!-- Danh sách người dùng -->
<html>
    <head>
        <meta charset="UTF-8">
        <title>Danh sách Người Dùng</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">

        <style>
            /* Reset mặc định */
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                font-family: 'Segoe UI', Arial, sans-serif;
                background-color: #f5f6fa;
                color: #333;
                line-height: 1.6;
            }

            /* Container chính */
            .content-card {
                max-width: 1200px;
                margin: 20px auto;
                padding: 20px;
                background: #fff;
                border-radius: 10px;
                box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
            }

            /* Tiêu đề */
            .content-card h2 {
                font-size: 22px;
                color: #1a3c6d;
                margin-bottom: 20px;
                display: flex;
                align-items: center;
                gap: 8px;
                font-weight: 600;
            }

            /* Form tìm kiếm và lọc */
            .search-form {
                display: flex;
                align-items: center;
                gap: 15px;
                margin-bottom: 20px;
                flex-wrap: wrap;
            }

            .search-form input[type="text"] {
                padding: 8px 12px;
                border: 1px solid #d1d5db;
                border-radius: 6px;
                font-size: 14px;
                flex: 1;
                min-width: 250px;
                background: #fafafa;
                transition: border-color 0.3s;
            }

            .search-form input[type="text"]:focus {
                border-color: #0056b3;
                outline: none;
            }

            .search-form button {
                padding: 8px 16px;
                background: #0056b3;
                color: #fff;
                border: none;
                border-radius: 6px;
                cursor: pointer;
                display: flex;
                align-items: center;
                gap: 6px;
                font-size: 14px;
                font-weight: 500;
                transition: background 0.3s;
            }

            .search-form button:hover {
                background: #003f87;
            }

            .search-form select {
                padding: 8px 12px;
                border: 1px solid #d1d5db;
                border-radius: 6px;
                font-size: 14px;
                min-width: 180px;
                background: #fafafa;
                cursor: pointer;
                transition: border-color 0.3s;
            }

            .search-form select:focus {
                border-color: #0056b3;
                outline: none;
            }

            /* Thông báo */
            .alert {
                padding: 12px 16px;
                margin-bottom: 20px;
                border-radius: 6px;
                font-size: 14px;
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .alert-warning {
                background: #fefcbf;
                color: #744210;
                border: 1px solid #fbd38d;
            }

            .alert-danger {
                background: #fed7d7;
                color: #9b2c2c;
                border: 1px solid #feb2b2;
            }

            .alert-success {
                background: #c6f6d5;
                color: #276749;
                border: 1px solid #9ae6b4;
            }

            /* Bảng danh sách người dùng */
            table {
                width: 100%;
                border-collapse: collapse;
                font-size: 14px;
                background: #fff;
                border-radius: 0;
                overflow: hidden;
            }

            table thead {
                background: #f39c12; /* Thay đổi từ xanh sang cam */
                color: #fff;
            }

            table th {
                padding: 12px 15px;
                text-align: left;
                font-weight: 600;
            }

            table td {
                padding: 12px 15px;
                border-bottom: 1px solid #e2e8f0;
            }

            table tbody tr {
                transition: background 0.3s;
            }

            table tbody tr:nth-child(even) {
                background: #f8fafc;
            }

            table tbody tr:hover {
                background: #fef9e7; /* Màu nền sáng khi hover, hài hòa với cam */
                cursor: pointer;
            }

            /* Loại bỏ scrollbar ngang */
            table {
                display: table;
                overflow-x: visible;
            }

            /* Responsive */
            @media (max-width: 768px) {
                .search-form {
                    flex-direction: column;
                    align-items: stretch;
                }

                .search-form input[type="text"],
                .search-form select,
                .search-form button {
                    width: 100%;
                    min-width: unset;
                }

                table {
                    font-size: 13px;
                }

                table th, table td {
                    padding: 10px;
                }
            }
            .detail-button {
                padding: 8px 12px;
                background: #0056b3;
                color: #fff;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                font-size: 14px;
                transition: background 0.3s;
            }

            .detail-button:hover {
                background: #003f87;
            }

        </style>
    </head>
    <body>
        <div id="dashboard">
            <%@ include file="sidebar.jsp" %>
            <div class="content" id="contentArea">
                <div class="content-card" id="userList">
                    <h2><i class="fas fa-users"></i> Danh sách người dùng</h2>
                    <!-- Form tìm kiếm và lọc -->
                    <form action="<%= request.getContextPath() %>/manage-user" method="get" class="search-form">
                        <input type="hidden" name="service" value="searchByKeywords">
                        <input type="text" name="keywords" placeholder="Nhập tên người dùng..." value="<%= request.getAttribute("keywords") != null ? request.getAttribute("keywords") : "" %>">
                        <button type="submit"><i class="fas fa-search"></i> Tìm kiếm</button>
                        <select name="roleFilter" onchange="this.form.submit()">
                            <option value="">Tất cả vai trò</option>
                            <option value="1" <%= "1".equals(request.getParameter("roleFilter")) ? "selected" : "" %>>Quản lý kho</option>
                            <option value="2" <%= "2".equals(request.getParameter("roleFilter")) ? "selected" : "" %>>Nhân viên kho</option>
                            <option value="3" <%= "3".equals(request.getParameter("roleFilter")) ? "selected" : "" %>>Giám đốc công ty</option>
                            <option value="4" <%= "4".equals(request.getParameter("roleFilter")) ? "selected" : "" %>>Nhân viên công ty</option>
                        </select>
                        <select name="statusFilter" onchange="this.form.submit()">
                            <option value="">Tất cả trạng thái</option>
                            <option value="true" <%= "true".equals(request.getParameter("statusFilter")) ? "selected" : "" %>>Hoạt động</option>
                            <option value="false" <%= "false".equals(request.getParameter("statusFilter")) ? "selected" : "" %>>Không hoạt động</option>
                        </select>
                    </form>

                    <!-- Thông báo nếu không tìm thấy -->
                    <% if (request.getAttribute("notFoundUser") != null) { %>
                    <div class="alert alert-warning">
                        <%= request.getAttribute("notFoundUser") %>
                    </div>
                    <% } %>

                    <!-- Bảng danh sách người dùng -->
                    <% List<User_Role> allUser = (List<User_Role>) request.getAttribute("allUser");
                            if (allUser != null && !allUser.isEmpty()) { %>
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Username</th>
                                <th>Họ</th>
                                <th>Tên</th>
                                <th>Vai trò</th>
                                <th>Trạng thái</th>
                                <th>Cập nhật trạng thái</th>
                                <th>Chi tiết</th> <!-- Cột mới cho nút Chi tiết -->
                            </tr>
                        </thead>
                        <tbody>
                            <% for (User_Role user : allUser) { %>
                            <tr> <!-- Xóa sự kiện onclick -->
                                <td><%= user.getUserID() %></td>
                                <td><%= user.getUsername() %></td>
                                <td><%= user.getLastName() %></td>
                                <td><%= user.getFirstName() %></td>
                                <td><%= user.getRoleName() %></td>
                                <td><%= user.isIsActive() ? "Hoạt động" : "Không hoạt động" %></td>
                                <td>
                                    <form action="<%= request.getContextPath() %>/manage-user" method="post">
                                        <input type="hidden" name="action" value="updateStatus">
                                        <input type="hidden" name="userId" value="<%= user.getUserID() %>">
                                        <input type="hidden" name="keywords" value="<%= request.getAttribute("keywords") != null ? request.getAttribute("keywords") : "" %>">
                                        <input type="hidden" name="roleFilter" value="<%= request.getAttribute("roleFilter") != null ? request.getAttribute("roleFilter") : "" %>">
                                        <input type="hidden" name="statusFilter" value="<%= request.getAttribute("statusFilter") != null ? request.getAttribute("statusFilter") : "" %>">
                                        <input type="radio" name="isActive" value="true" <%= user.isIsActive() ? "checked" : "" %> onchange="this.form.submit()"> Hoạt động
                                        <input type="radio" name="isActive" value="false" <%= !user.isIsActive() ? "checked" : "" %> onchange="this.form.submit()"> Không hoạt động
                                    </form>
                                </td>
                                <td>
                                    <button onclick="window.location = '<%= request.getContextPath() %>/manage-user?service=userDetail&userId=<%= user.getUserID() %>'" class="detail-button">Chi tiết</button>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                    <% } else { %>
                    <p>Không có người dùng nào.</p>
                    <% } %>

                    <!-- Phân trang -->
                    <% if (request.getAttribute("totalUsers") != null) {
                        int totalUsers = (Integer) request.getAttribute("totalUsers");
                        int currentPage = (Integer) request.getAttribute("currentPage");
                        int pageSize = (Integer) request.getAttribute("pageSize");
                        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);
                        String service = request.getParameter("service");
                        String keywords = request.getParameter("keywords");
                        String roleFilter = request.getParameter("roleFilter");
                        String statusFilter = request.getParameter("statusFilter");
                        String baseUrl = request.getContextPath() + "/manage-user?service=" + (service != null ? service : "listAllUser");
                        if (service != null && service.equals("searchByKeywords")) {
                            if (keywords != null && !keywords.isEmpty()) baseUrl += "&keywords=" + java.net.URLEncoder.encode(keywords, "UTF-8");
                            if (roleFilter != null && !roleFilter.isEmpty()) baseUrl += "&roleFilter=" + roleFilter;
                            if (statusFilter != null && !statusFilter.isEmpty()) baseUrl += "&statusFilter=" + statusFilter;
                        }
                    %>
                    <div class="pagination">
                        <% if (currentPage > 1) { %>
                        <a href="<%= baseUrl + "&page=" + (currentPage - 1) %>">Trước</a>
                        <% } %>
                        <% for (int i = 1; i <= totalPages; i++) { %>
                        <a href="<%= baseUrl + "&page=" + i %>" <%= i == currentPage ? "class='active'" : "" %>><%= i %></a>
                        <% } %>
                        <% if (currentPage < totalPages) { %>
                        <a href="<%= baseUrl + "&page=" + (currentPage + 1) %>">Sau</a>
                        <% } %>
                    </div>
                    <% } %>
                </div>

                <!-- Chi tiết người dùng -->
                <div class="content-card <%= request.getAttribute("detailUser") != null ? "" : "hidden" %>" id="userDetail">
                    <h2><i class="fas fa-user"></i> Chi tiết Người Dùng</h2>
                    <div class="profile-card">
                        <h3>Thông tin chi tiết</h3>
                        <% User_Role detailUser = (User_Role) request.getAttribute("detailUser");
                       if (detailUser != null) { %>
                        <div class="form-row">
                            <label>ID:</label>
                            <span><%= detailUser.getUserID() %></span>
                        </div>
                        <div class="form-row">
                            <label>Tên đăng nhập:</label>
                            <span><%= detailUser.getUsername() %></span>
                        </div>
                        <div class="form-row">
                            <label>Họ và tên:</label>
                            <span><%= detailUser.getFirstName() %> <%= detailUser.getLastName() %></span>
                        </div>
                        <div class="form-row">
                            <label>Vai trò:</label>
                            <span><%= detailUser.getRoleName() %></span>
                        </div>
                        <div class="form-row">
                            <label>Số điện thoại:</label>
                            <span><%= detailUser.getPhoneNum() %></span>
                        </div>
                        <div class="form-row">
                            <label>Địa chỉ:</label>
                            <span><%= detailUser.getAddress() != null ? detailUser.getAddress() : "Chưa có thông tin" %></span>
                        </div>
                        <button onclick="window.location = '<%= request.getContextPath() %>/manage-user?service=listAllUser'">Quay lại</button>
                        <% } else if (request.getAttribute("error") != null) { %>
                        <div class="alert alert-danger">
                            <%= request.getAttribute("error") %>
                        </div>
                        <% } %>
                        <% if (detailUser == null) { %>
                        <p style="color: red;">Không tìm thấy người dùng hoặc có lỗi xảy ra.</p>
                        <% } %>
                    </div>
                </div>
            </div>
        </div>
        <script src="<%= request.getContextPath() %>/js/script.js"></script>
        <script>
                            window.onload = function () {
                                console.log("allUser exists: ", <%= request.getAttribute("allUser") != null %>);
                                console.log("detailUser exists: ", <%= request.getAttribute("detailUser") != null %>);
                                if (<%= request.getAttribute("detailUser") != null %>) {
                                    document.getElementById('userList').style.display = 'none';
                                    document.getElementById('userDetail').style.display = 'block';
                                } else if (<%= request.getAttribute("allUser") != null %>) {
                                    document.getElementById('userList').style.display = 'block';
                                    document.getElementById('userDetail').style.display = 'none';
                                } else {
                                    document.getElementById('userList').style.display = 'block';
                                    document.getElementById('userDetail').style.display = 'none';
                                }
                            }
        </script>
    </body> 
</html>
