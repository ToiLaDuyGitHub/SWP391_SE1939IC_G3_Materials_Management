<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html>

        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Nhập kho vật tư từ đơn đã duyệt</title>
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
            <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
            <style>
                .table-container {
                    margin: 20px;
                    padding: 20px;
                    background-color: white;
                    border-radius: 8px;
                    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                }

                .btn-import {
                    background-color: #28a745;
                    color: white;
                }

                .btn-import:hover {
                    background-color: #218838;
                    color: white;
                }

                .btn-import:disabled {
                    background-color: #6c757d;
                    cursor: not-allowed;
                }
            </style>
        </head>

        <body>
            <div class="container-fluid">
                <div class="table-container">
                    <h2 class="mb-4">Danh sách đơn đã được duyệt</h2>
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Mã đơn</th>
                                <th>Người yêu cầu</th>
                                <th>Ngày yêu cầu</th>
                                <th>Trạng thái</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="request" items="${approvedRequests}">
                                <tr>
                                    <td>${request.purchaseRequestID}</td>
                                    <td>${userDao.getUserById(request.createdByID).fullName}</td>
                                    <td>${request.createdDate}</td>
                                    <td>
                                        <span class="badge bg-success">Đã duyệt</span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${importStatusMap[request.purchaseRequestID]}">
                                                <button class="btn btn-secondary" disabled>
                                                    <i class="fas fa-check"></i> Đã nhập kho
                                                </button>
                                                <a class="btn btn-outline-primary ms-1" title="Xem chi tiết"
                                                    href="${pageContext.request.contextPath}/import-materials?action=viewDetail&purchaseRequestID=${request.purchaseRequestID}">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <button class="btn btn-import"
                                                    onclick="confirmImport(${request.purchaseRequestID})">
                                                    <i class="fas fa-box"></i> Nhập kho
                                                </button>
                                                <a class="btn btn-outline-primary ms-1" title="Xem chi tiết"
                                                    href="${pageContext.request.contextPath}/import-materials?action=viewDetail&purchaseRequestID=${request.purchaseRequestID}">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Import Confirmation Modal -->
            <div class="modal fade" id="importModal" tabindex="-1">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">Xác nhận nhập kho</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <p>Bạn có chắc chắn muốn nhập kho vật tư từ đơn này?</p>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <form id="importForm" action="${pageContext.request.contextPath}/import-materials" method="POST">
                                <input type="hidden" name="action" value="importFromDirector">
                                <input type="hidden" name="requestId" id="requestIdInput">
                                <button type="submit" class="btn btn-primary">Xác nhận</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
            <script>
                let currentRequestId = null;
                const importModal = new bootstrap.Modal(document.getElementById('importModal'));

                function confirmImport(requestId) {
                    currentRequestId = requestId;
                    document.getElementById('requestIdInput').value = requestId;
                    importModal.show();
                }
            </script>
        </body>

        </html>