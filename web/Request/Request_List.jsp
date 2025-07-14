<%-- 
    Document   : Request_List
    Created on : Jun 12, 2025, 13:35:46
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Danh sách yêu cầu của tôi</title>
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
        <style>
            .material-table {
                width: 100%;
                min-width: 1400px;
                margin-top: 0;
            }
            .material-table table {
                width: 100%;
                border-collapse: collapse;
            }
            .material-table th, .material-table td {
                padding: 15px 20px;
                text-align: center;
                border: 1px solid #ddd;
            }
            .material-table th {
                background: #f9a825;
                color: #4a90e2;
                font-weight: 600;
                position: sticky;
                top: 0;
                z-index: 1;
            }
            .material-table td a {
                color: #4a90e2;
                text-decoration: none;
            }
            .material-table td a:hover {
                text-decoration: underline;
            }
            .modal {
                display: none;
                position: fixed;
                top: 50%;
                left: 50%;
                transform: translate(-50%, -50%);
                background: #fff;
                padding: 20px;
                border-radius: 8px;
                box-shadow: 0 4px 8px rgba(0,0,0,0.2);
                z-index: 1000;
                width: 600px;
                max-height: 80vh;
                overflow-y: auto;
            }
            .modal.show {
                display: block;
            }
            .modal-overlay {
                display: none;
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0,0,0,0.5);
                z-index: 999;
            }
            .modal-overlay.show {
                display: block;
            }
            .modal .close {
                position: absolute;
                top: 10px;
                right: 10px;
                font-size: 20px;
                cursor: pointer;
            }
            .modal .form-row {
                margin-bottom: 15px;
            }
            .modal .form-row.inline {
                display: flex;
                gap: 20px;
            }
            .modal label {
                display: block;
                margin-bottom: 5px;
                font-weight: 500;
            }
            .modal input {
                width: 100%;
                padding: 8px;
                border: 1px solid #ddd;
                border-radius: 4px;
                box-sizing: border-box;
                background: #f9f9f9;
            }
            .modal .material-table {
                margin-top: 15px;
                min-width: 100%;
            }
            .modal .material-table th, .modal .material-table td {
                padding: 10px;
            }
            .material-table td button {
                padding: 8px 12px;
                background: #4a90e2;
                color: #fff;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                font-size: 14px;
                margin: 0 5px;
            }
            .material-table td button:hover {
                background: #357abd;
            }
            .modal .button-group {
                display: flex;
                justify-content: flex-end;
                gap: 10px;
                margin-top: 20px;
            }
            .modal .button-group button {
                padding: 8px 12px;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                font-size: 14px;
            }
            .modal .button-group button.edit-btn {
                background: #4a90e2;
                color: #fff;
            }
            .modal .button-group button.edit-btn:hover {
                background: #357abd;
            }
            .modal .button-group button.delete-btn {
                background: #dc3545;
                color: #fff;
            }
            .modal .button-group button.delete-btn:hover {
                background: #c82333;
            }
            .modal .button-group button.close-btn {
                background: #6c757d;
                color: #fff;
            }
            .modal .button-group button.close-btn:hover {
                background: #5a6268;
            }
            .notification {
                margin-bottom: 20px;
                padding: 10px;
                border-radius: 4px;
                font-size: 14px;
                text-align: center;
            }
            .notification.success {
                background: #d4edda;
                color: #155724;
                border: 1px solid #c3e6cb;
            }
            .notification.error {
                background: #f8d7da;
                color: #721c24;
                border: 1px solid #f5c6cb;
            }
            .search-form {
                margin-bottom: 20px;
                display: flex;
                align-items: center;
                gap: 15px;
                background: #f9f9f9;
                padding: 15px;
                border-radius: 8px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                flex-wrap: wrap;
            }
            .search-form .input-group {
                position: relative;
                flex: 1;
                min-width: 200px;
            }
            .search-form .input-group i {
                position: absolute;
                top: 50%;
                left: 12px;
                transform: translateY(-50%);
                color: #6c757d;
            }
            .search-form input[type="text"],
            .search-form input[type="date"] {
                width: 100%;
                padding: 10px 10px 10px 35px;
                border: 1px solid #ddd;
                border-radius: 6px;
                font-size: 14px;
                background: #fff;
                transition: border-color 0.3s, box-shadow 0.3s;
            }
            .search-form input:focus {
                outline: none;
                border-color: #4a90e2;
                box-shadow: 0 0 5px rgba(74, 144, 226, 0.3);
            }
            .search-form button {
                padding: 10px 20px;
                background: #4a90e2;
                color: #fff;
                border: none;
                border-radius: 6px;
                cursor: pointer;
                font-size: 14px;
                display: flex;
                align-items: center;
                gap: 5px;
                transition: background 0.3s;
            }
            .search-form button:hover {
                background: #357abd;
            }
            /* Pagination Styles */
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
            @media (max-width: 1200px) {
                .material-table {
                    min-width: 1000px;
                }
            }
            @media (max-width: 768px) {
                .material-table {
                    min-width: 800px;
                }
                .material-table th, .material-table td {
                    padding: 10px 15px;
                }
                .search-form {
                    flex-direction: column;
                    align-items: stretch;
                }
                .search-form .input-group {
                    min-width: 100%;
                }
            }
        </style>
    </head>
    <body>
        <%@ include file="/sidebar.jsp" %>
        <div class="content">
            <div class="" id="requestListSection">
                <h2><i class="fas fa-list"></i> Danh sách yêu cầu của tôi</h2>
                <!-- Hiển thị thông báo từ session -->
                <c:if test="${not empty sessionScope.message}">
                    <div class="notification ${sessionScope.messageType eq 'error' ? 'error' : 'success'}">${fn:escapeXml(sessionScope.message)}</div>
                    <% session.removeAttribute("message"); session.removeAttribute("messageType"); %>
                </c:if>
                <!-- Search Form -->
                <form action="${pageContext.request.contextPath}/search-request" method="get" class="search-form">
                    <div class="input-group">
                        <i class="fas fa-search"></i>
                        <input type="text" name="requestCode" placeholder="Nhập mã đơn (VD: EP01)" value="${param.requestCode}">
                    </div>
                    <div class="input-group">
                        <i class="fas fa-calendar-alt"></i>
                        <input type="date" name="startDate" value="${param.startDate}">
                    </div>
                    <div class="input-group">
                        <i class="fas fa-calendar-alt"></i>
                        <input type="date" name="endDate" value="${param.endDate}">
                    </div>
                    <button type="submit"><i class="fas fa-search"></i> Tìm kiếm</button>
                </form>

                <div class="material-table">
                    <table id="requestTable">
                        <thead>
                            <tr>
                                <th>Mã đơn</th>
                                <th>Loại đơn</th>
                                <th>Ngày tạo</th>
                                <th>Trạng thái</th>
                                <th>Người tạo</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody id="requestTableBody">
                            <c:set var="page" value="${param.page != null ? param.page : 1}" />
                            <c:set var="itemsPerPage" value="6" />
                            <c:set var="start" value="${(page - 1) * itemsPerPage}" />
                            <c:set var="end" value="${start + itemsPerPage - 1}" />
                            <c:set var="totalItems" value="${processedRequests.size()}" />
                            <c:set var="totalPages" value="${(totalItems + itemsPerPage - 1) / itemsPerPage}" />
                            <c:choose>
                                <c:when test="${not empty processedRequests}">
                                    <c:forEach var="request" items="${processedRequests}" begin="${start}" end="${end}">
                                        <tr>
                                            <td>${fn:escapeXml(request.requestCode)}</td>
                                            <td>${fn:escapeXml(request.requestType)}</td>
                                            <td>${fn:escapeXml(request.createdDate)}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${request.status == 1}">Đã duyệt</c:when>
                                                    <c:when test="${request.status == 0}">Chưa duyệt</c:when>
                                                    <c:otherwise>Không xác định</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>${fn:escapeXml(request.createdByName)}</td>
                                            <td>
                                                <button onclick="showRequestStaff(
                                                                '${fn:escapeXml(request.requestCode)}',
                                                                '${fn:escapeXml(request.requestType)}',
                                                                '${fn:escapeXml(request.createdDate)}',
                                                                '${fn:escapeXml(request.createdByName)}',
                                                                '${fn:escapeXml(request.approvedByName != null ? request.approvedByName : 'Chưa có người duyệt')}',
                                                        ${request.status},
                                                        ${request.requestId},
                                                        [<c:forEach var="material" items="${request.materials}" varStatus="status">{materialId:${material.materialId}, materialName:'${fn:escapeXml(material.materialName)}', quantity:${material.quantity}}${status.last ? '' : ','}</c:forEach>]
                                                                )">Xem chi tiết</button>
                                                </td>
                                            </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="6">Không có yêu cầu nào để hiển thị.</td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
                <!-- Pagination Controls -->
                <div class="pagination">
                    <c:forEach var="i" begin="1" end="${totalPages}">
                        <a href="${pageContext.request.contextPath}/search-request?requestCode=${param.requestCode}&startDate=${param.startDate}&endDate=${param.endDate}&page=${i}" class="${i == page ? 'active' : ''}">${i}</a>
                    </c:forEach>
                </div>
            </div>
        </div>
        <!-- Modal for Request Details -->
        <div id="editModal" class="modal">
            <span class="close" onclick="closeEditModal()">×</span>
            <h3>Chi tiết yêu cầu</h3>
            <div class="form-row inline">
                <div>
                    <label for="requestCode">Mã đơn</label>
                    <input type="text" id="requestCode" readonly>
                </div>
                <div>
                    <label for="requestType">Loại đơn</label>
                    <input type="text" id="requestType" readonly>
                </div>
                <div>
                    <label for="statusText">Trạng thái</label>
                    <input type="text" id="statusText" readonly>
                </div>
            </div>
            <div class="form-row inline">
                <div>
                    <label for="approvedByName">Người duyệt</label>
                    <input type="text" id="approvedByName" readonly>
                </div>
                <div>
                    <label for="createdDate">Ngày tạo</label>
                    <input type="text" id="createdDate" readonly>
                </div>
            </div>
            <div class="form-row">
                <label>Vật tư trong đơn</label>
                <div class="material-table">
                    <table>
                        <thead>
                            <tr>
                                <th>Tên vật tư</th>
                                <th>Số lượng</th>
                            </tr>
                        </thead>
                        <tbody id="materialTableBody">
                            <tr><td colspan="2">Không có vật tư</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="button-group">
                <form id="editForm" action="${pageContext.request.contextPath}/edit-request" method="GET">
                    <input type="hidden" name="requestId" id="editRequestId">
                    <input type="hidden" name="requestType" id="editRequestType">
                    <button type="submit" class="edit-btn">Sửa</button>
                </form>
                <form id="deleteForm" action="${pageContext.request.contextPath}/delete-request" method="POST">
                    <input type="hidden" name="requestId" id="deleteRequestId">
                    <input type="hidden" name="requestType" id="deleteRequestType">
                    <button type="submit" class="delete-btn">Xóa</button>
                </form>
                <button class="close-btn" onclick="closeEditModal()">Đóng</button>
            </div>
        </div>
        <div id="editModalOverlay" class="modal-overlay"></div>
        <script src="${pageContext.request.contextPath}/js/script.js"></script>

    </body>
</html>