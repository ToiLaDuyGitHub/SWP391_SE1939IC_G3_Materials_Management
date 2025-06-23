<%-- 
    Document   : list-units
    Created on : Jun 21, 2025, 11:56 PM
    Author     : ADMIN
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý Đơn vị Vật tư</title>
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">
        <style>
            .content-card {
                max-width: 1200px;
                margin: 20px auto;
            }
            .search-bar {
                margin-bottom: 20px;
                display: flex;
                gap: 10px;
                align-items: center;
            }
            .search-bar input[type="text"] {
                padding: 8px;
                border: 1px solid #ddd;
                border-radius: 4px;
                width: 300px;
            }
            .search-bar select {
                padding: 8px;
                border: 1px solid #ddd;
                border-radius: 4px;
                width: 200px;
            }
            .add-button {
                padding: 8px 16px;
                background: #28a745;
                color: white;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                font-size: 14px;
            }
            .add-button:hover {
                background: #218838;
            }
            table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 20px;
            }
            th, td {
                border: 1px solid #ddd;
                padding: 12px;
                text-align: center;
            }
            th {
                background: #f4f4f4;
                font-weight: 600;
            }
            tr:nth-child(even) {
                background: #f9f9f9;
            }
            tr:hover {
                background: #f1f1f1;
            }
            .pagination {
                margin-top: 20px;
                display: flex;
                justify-content: center;
                gap: 5px;
            }
            .pagination a {
                padding: 8px 12px;
                border: 1px solid #ddd;
                border-radius: 4px;
                text-decoration: none;
                color: #333;
            }
            .pagination a:hover {
                background: #f4f4f4;
            }
            .pagination a.active {
                background: #28a745;
                color: white;
                border-color: #28a745;
            }
        </style>
    </head>
    <body>
        <div id="dashboard">
            <%@ include file="/sidebar.jsp" %>
            <div class="content" id="contentArea">
                <div class="content-card" id="unitListSection">
                    <h2>Đơn vị Vật tư</h2>
                    <form action="<%= request.getContextPath() %>/list-units" method="get" class="search-bar">
                        <input type="text" name="searchInput" placeholder="Tìm kiếm..." value="${param.searchInput}">
                        <select name="category">
                            <option value="">Tất cả</option>
                            <c:forEach items="${subCategories}" var="cat">
                                <option value="${cat.subcategoryName}" ${param.category == cat.subcategoryName ? 'selected' : ''}>
                                    ${cat.subcategoryName}
                                </option>
                            </c:forEach>
                        </select>
                        <button type="submit" class="add-button">Tìm kiếm</button>
                    </form>
                    <table id="unitTable">
                        <thead><tr><th>Tên</th><th>Đơn vị nhỏ</th><th>Đơn vị lớn</th><th>Tỉ lệ</th></tr></thead>
                        <tbody id="unitTableBody">
                            <c:choose>
                                <c:when test="${empty unitsList}">
                                    <tr><td colspan="4">Không tìm thấy</td></tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${unitsList}" var="unit">
                                        <tr style="display: none;" class="unit-row">
                                            <td>${unit.materialName}</td>
                                            <td>${unit.minUnit}</td>
                                            <td>${unit.maxUnit}</td>
                                            <td>${unit.ratio}</td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                    <div class="pagination" id="pagination"></div>
                </div>
            </div>
        </div>
        <script>
            document.addEventListener("DOMContentLoaded", () => {
                const rows = document.querySelectorAll(".unit-row");
                const pagination = document.getElementById("pagination");
                const perPage = 6;
                let page = 1;
                function showPage(p) {
                    const start = (p - 1) * perPage;
                    rows.forEach((row, i) => row.style.display = (i >= start && i < start + perPage) ? "" : "none");
                }
                function updatePagination() {
                    const total = Math.ceil(rows.length / perPage);
                    pagination.innerHTML = "";
                    for (let i = 1; i <= total; i++) {
                        const a = document.createElement("a");
                        a.href = "#";
                        a.textContent = i;
                        a.className = i === page ? "active" : "";
                        a.onclick = (e) => {
                            e.preventDefault();
                            page = i;
                            showPage(page);
                            updatePagination();
                        };
                        pagination.appendChild(a);
                    }
                }
                if (rows.length) {
                    showPage(page);
                    updatePagination();
                }
            });
        </script>
    </body>
</html>