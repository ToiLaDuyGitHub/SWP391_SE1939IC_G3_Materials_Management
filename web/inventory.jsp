<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Tồn kho vật tư</title>
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">
        <style>
            .content-card {
                max-width: 1550px;
            }
            .inventory-container {
                padding: 20px;
                background: #f9f9f9;
                border-radius: 10px;
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                margin: 20px;
            }
            .search-container {
                display: flex;
                justify-content: center;
                align-items: center;
                margin-bottom: 20px;
                background: #f9f9f9;
                padding: 15px;
                border-radius: 10px;
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                max-width: 1200px;
                margin-left: auto;
                margin-right: auto;
            }
            .search-container form {
                display: flex;
                align-items: center;
                width: 100%;
                gap: 10px;
            }
            .search-container input[type="text"] {
                padding: 12px 15px;
                border: 2px solid #e0e0e0;
                border-radius: 25px;
                font-size: 14px;
                font-family: 'Poppins', sans-serif;
                outline: none;
                transition: border-color 0.3s, box-shadow 0.3s;
                flex: 1;
                background: #fff;
            }
            .search-container input[type="text"]:focus {
                border-color: #4a90e2;
                box-shadow: 0 0 8px rgba(74, 144, 226, 0.3);
            }
            .search-container button {
                padding: 12px 25px;
                background: linear-gradient(90deg, #4a90e2, #50e3c2);
                color: #fff;
                border: none;
                border-radius: 25px;
                cursor: pointer;
                font-size: 16px;
                font-family: 'Poppins', sans-serif;
                display: flex;
                align-items: center;
                gap: 8px;
                transition: background 0.3s, transform 0.2s;
            }
            .search-container button:hover {
                background: linear-gradient(90deg, #50e3c2, #4a90e2);
                transform: translateY(-2px);
            }
            .search-container button i {
                font-size: 16px;
            }
            .material-table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 20px;
            }
            .material-table th, .material-table td {
                padding: 12px;
                text-align: left;
                border-bottom: 1px solid #ddd;
            }
            .material-table th {
                background-color: #4CAF50;
                color: white;
            }
            .material-table tr:hover {
                background-color: #f1f1f1;
            }
            .material-table td {
                font-size: 14px;
            }
            .edit-button {
                background-color: #4CAF50;
                color: white;
                border: none;
                padding: 3px 8px;
                text-align: center;
                text-decoration: none;
                display: inline-block;
                font-size: 12px;
                margin: 2px;
                cursor: pointer;
                border-radius: 3px;
                width: 70px;
                height: 28px;
                line-height: 22px;
            }
            .edit-button:hover {
                background-color: #45a049;
            }
            .pagination {
                display: flex;
                justify-content: center;
                margin-top: 20px;
            }
            .pagination a {
                color: #4a90e2;
                padding: 8px 16px;
                text-decoration: none;
                border: 1px solid #ddd;
                margin: 0 4px;
                border-radius: 3px;
            }
            .pagination a.active {
                background-color: #4a90e2;
                color: white;
                border: 1px solid #4a90e2;
            }
            .pagination a:hover:not(.active) {
                background-color: #ddd;
            }
            .error-message {
                background-color: #f44336;
                color: #fff;
                padding: 10px;
                border-radius: 6px;
                margin: 10px 0;
                display: flex;
                align-items: center;
                gap: 8px;
            }
        </style>
    </head>
    <body>

        <%@ include file="sidebar.jsp" %>
        <div class="content" id="contentArea">
            <div>
                <!-- Form tìm kiếm -->
                <div class="search-container">
                    <form action="${pageContext.request.contextPath}/search-inventory" method="get">
                        <input type="text" name="keyword" placeholder="Nhập từ khóa (ví dụ: búa)" value="${param.keyword}">
                        <button type="submit"><i class="fas fa-search"></i> Tìm kiếm</button>
                    </form>
                </div>

                <c:if test="${not empty sessionScope.successMessage}">
                    <div class="success-message">
                        <i class="fas fa-check-circle"></i> ${sessionScope.successMessage}
                    </div>
                    <c:remove var="successMessage" scope="session"/>
                </c:if>

                <!-- Thông báo lỗi -->
                <c:if test="${not empty errorMessage}">
                    <div class="error-message">
                        <i class="fas fa-exclamation-circle"></i> ${errorMessage}
                    </div>
                </c:if>

                <div class="inventory-container">
                    <h2><i class="fas fa-boxes"></i> Tồn kho vật tư</h2>
                    <table class="material-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tên vật tư</th>
                                <th>Số lượng tổng</th>
                                <th>Dùng được</th>
                                <th>Hỏng</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:set var="page" value="${param.page != null ? param.page : 1}" />
                            <c:set var="itemsPerPage" value="10" />
                            <c:set var="start" value="${(page - 1) * itemsPerPage}" />
                            <c:set var="end" value="${start + itemsPerPage - 1}" />
                            <c:set var="totalItems" value="${materialList.size()}" />
                            <c:set var="totalPages" value="${(totalItems + itemsPerPage - 1) / itemsPerPage}" />

                            <c:forEach var="material" items="${materialList}" begin="${start}" end="${end}">
                                <tr>
                                    <td>${material.materialID}</td>
                                    <td>${material.materialName}</td>
                                    <td>${material.quantity.totalQuantity}</td>
                                    <td>${material.quantity.usableQuantity}</td>
                                    <td>${material.quantity.brokenQuantity}</td>
                                    <td>
                                        <button class="edit-button" onclick="showInventoryDetail(${material.materialID}, '${material.materialName}', ${material.quantity.totalQuantity}, ${material.quantity.usableQuantity}, ${material.quantity.brokenQuantity})">Thay đổi</button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <!-- Pagination Controls -->
                    <div class="pagination">
                        <c:forEach var="i" begin="1" end="${totalPages}">
                            <a href="${pageContext.request.contextPath}/search-inventory?keyword=${param.keyword}&page=${i}" 
                               class="${i == page ? 'active' : ''}">${i}</a>
                        </c:forEach>
                    </div>
                </div>
            </div>

            <!-- Modal chi tiết tồn kho -->
            <div id="editModalOverlay" class="modal-overlay" onclick="closeEditModal()"></div>
            <div id="editModal" class="modal">
                <span class="close" onclick="closeEditModal()">×</span>
                <h3>Chi tiết tồn kho</h3>
                <form id="editInventoryForm" action="${pageContext.request.contextPath}/update-inventory" method="post">
                    <input type="hidden" name="materialID" id="materialID">
                    <div class="form-group">
                        <label for="materialName">Tên vật tư:</label>
                        <input type="text" id="materialName" name="materialName" readonly>
                    </div>
                    <div class="form-group">
                        <label for="totalQuantity">Số lượng tổng:</label>
                        <input type="number" id="totalQuantity" name="totalQuantity" readonly>
                    </div>
                    <div class="form-group">
                        <label for="usableQuantity">Dùng được:</label>
                        <input type="number" id="usableQuantity" name="usableQuantity">
                    </div>
                    <div class="form-group">
                        <label for="brokenQuantity">Hỏng:</label>
                        <input type="number" id="brokenQuantity" name="brokenQuantity">
                    </div>
                    <div class="form-actions">
                        <button type="submit" class="submit-btn"><i class="fas fa-save"></i> Lưu</button>
                        <button type="button" class="cancel-btn" onclick="closeEditModal()"><i class="fas fa-times"></i> Hủy</button>
                    </div>
                </form>
            </div>
        </div>

        <script src="<%= request.getContextPath() %>/js/script.js"></script>
    </body>
</html>