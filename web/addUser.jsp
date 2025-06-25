<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Thêm Người Dùng</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">
        <style>
            #dashboard {
                display: flex;
                min-height: 100vh;
            }

            .form-container {
                max-width: 600px;
                margin: 0 auto;
                padding: 30px;
                background: #f9f9f9;
                border-radius: 8px;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            }

            .form-container h2 {
                margin-top: 0;
                margin-bottom: 20px;
                font-size: 24px;
                text-align: center;
                color: #333;
            }

            .form-container h2 i {
                margin-right: 8px;
                color: #007bff;
            }

            .form-row.add-user {
                margin-bottom: 20px;
            }

            .form-row.add-user label {
                display: block;
                margin-bottom: 8px;
                font-size: 16px;
                font-weight: 600;
                color: #333;
            }

            .form-row.add-user input,
            .form-row.add-user select {
                width: 100%;
                padding: 10px;
                font-size: 14px;
                border: 1px solid #ced4da;
                border-radius: 4px;
                box-sizing: border-box;
                transition: border-color 0.3s ease;
            }

            .form-row.add-user input:focus,
            .form-row.add-user select:focus {
                border-color: #007bff;
                outline: none;
                box-shadow: 0 0 5px rgba(0, 123, 255, 0.3);
            }

            button[type="submit"] {
                background-color: #007bff;
                color: white;
                padding: 12px 20px;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                font-size: 16px;
                font-weight: 600;
                width: 100%;
                transition: background-color 0.3s ease;
            }

            button[type="submit"]:hover {
                background-color: #0056b3;
            }

            .alert {
                padding: 12px;
                margin-bottom: 20px;
                border-radius: 4px;
                font-size: 14px;
            }

            .alert-danger {
                background-color: #f8d7da;
                color: #721c24;
                border: 1px solid #f5c6cb;
            }

            .alert-success {
                background-color: #d4edda;
                color: #155724;
                border: 1px solid #c3e6cb;
            }

            .required {
                color: red;
            }

            @media (max-width: 768px) {
                #dashboard {
                    flex-direction: column;
                }

                .sidebar {
                    width: 100%;
                    position: relative;
                    height: auto;
                }

                .content {
                    margin-left: 0;
                    padding: 10px;
                }

                .form-container {
                    padding: 20px;
                    max-width: 100%;
                }
            }
        </style>
    </head>
    <body>
        <div id="dashboard">
            <%@ include file="sidebar.jsp" %>
            <div class="content" id="contentArea">
                <div class="form-container" id="userAdd">
                    <h2><i class="fas fa-user-plus"></i> Thêm Người Dùng Mới</h2>

                    <% String error = (String) request.getAttribute("error"); %>
                    <% if (error != null) { %>
                    <div class="alert alert-danger"><%= error %></div>
                    <% } %>

                    <% String message = (String) request.getAttribute("message"); %>
                    <% if (message != null) { %>
                    <div class="alert alert-success"><%= message %></div>
                    <% } %>

                    <form action="<%= request.getContextPath() %>/add-user" method="post" id="addUserForm">
                        <input type="hidden" name="action" value="add">

                        <div class="form-row add-user">
                            <label for="username">Tên đăng nhập<span class="required">*</span>:</label>
                            <input type="text" id="username" name="username" 
                                   value="<%= request.getParameter("username") != null ? request.getParameter("username") : "" %>" 
                                   required placeholder="Nhập tên đăng nhập">
                        </div>

                        <div class="form-row add-user">
                            <label for="firstName">Họ:</label>
                            <input type="text" id="firstName" name="firstName" 
                                   value="<%= request.getParameter("firstName") != null ? request.getParameter("firstName") : "" %>" 
                                   placeholder="Nhập họ">
                        </div>

                        <div class="form-row add-user">
                            <label for="lastName">Tên:</label>
                            <input type="text" id="lastName" name="lastName" 
                                   value="<%= request.getParameter("lastName") != null ? request.getParameter("lastName") : "" %>" 
                                   placeholder="Nhập tên">
                        </div>

                        <div class="form-row add-user">
                            <label for="phoneNum">Số điện thoại:</label>
                            <input type="text" id="phoneNum" name="phoneNum" 
                                   value="<%= request.getParameter("phoneNum") != null ? request.getParameter("phoneNum") : "" %>" 
                                   placeholder="Nhập số điện thoại">
                        </div>

                        <div class="form-row add-user">
                            <label for="address">Địa chỉ:</label>
                            <input type="text" id="address" name="address" 
                                   value="<%= request.getParameter("address") != null ? request.getParameter("address") : "" %>" 
                                   placeholder="Nhập địa chỉ">
                        </div>

                        <div class="form-row add-user">
                            <label for="roleID">Vai trò<span class="required">*</span>:</label>
                            <select id="roleID" name="roleID" required>
                                <option value="">-- Chọn vai trò --</option>
                                <option value="2" <%= "2".equals(request.getParameter("roleID")) ? "selected" : "" %>>Nhân viên kho</option>
                                <option value="3" <%= "3".equals(request.getParameter("roleID")) ? "selected" : "" %>>Giám đốc công ty</option>
                                <option value="4" <%= "4".equals(request.getParameter("roleID")) ? "selected" : "" %>>Nhân viên công ty</option>
                            </select>
                        </div>

                        <button type="submit">
                            <i class="fas fa-user-plus"></i> Thêm Người Dùng
                        </button>
                    </form>
                </div>
            </div>
        </div>

        <script>
            // Form validation
            document.getElementById('addUserForm').addEventListener('submit', function (e) {
                const username = document.getElementById('username').value.trim();
                const roleID = document.getElementById('roleID').value;

                if (!username) {
                    alert('Vui lòng nhập tên đăng nhập!');
                    e.preventDefault();
                    return;
                }

                if (!roleID) {
                    alert('Vui lòng chọn vai trò!');
                    e.preventDefault();
                    return;
                }

                // Phone validation (if provided)
                const phone = document.getElementById('phoneNum').value.trim();
                if (phone && !/^0[0-9]{9,10}$/.test(phone)) {
                    alert('Số điện thoại không hợp lệ! Phải bắt đầu bằng 0 và có 10-11 chữ số.');
                    e.preventDefault();
                    return;
                }
            });
        </script>
    </body>
</html>