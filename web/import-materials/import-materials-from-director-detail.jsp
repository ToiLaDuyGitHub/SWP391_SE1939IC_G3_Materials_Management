<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Nhập kho vật tư từ đơn đã duyệt</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <style>
            .container {
                margin: 20px;
                padding: 20px;
                background-color: white;
                border-radius: 8px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            }
            .material-row {
                margin-bottom: 15px;
                padding: 10px;
                border: 1px solid #dee2e6;
                border-radius: 4px;
            }
            .btn-submit {
                background-color: #28a745;
                color: white;
            }
            .btn-submit:hover {
                background-color: #218838;
                color: white;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h2 class="mb-4">Nhập kho vật tư từ đơn #${requestId}</h2>
            
            <form action="${pageContext.request.contextPath}/import-materials" method="POST">
                <input type="hidden" name="action" value="importFromDirector">
                <input type="hidden" name="requestId" value="${requestId}">
                <input type="hidden" name="userId" value="${sessionScope.userRole.userID}">
                
                <div class="materials-list">
                    <c:forEach var="item" items="${requestMaterials}">
                        <div class="material-row">
                            <div class="row">
                                <div class="col-md-4">
                                    <strong>${item.materialName}</strong>
                                </div>
                                <div class="col-md-3">
                                    <label>Số lượng:</label>
                                    <input type="number" class="form-control" 
                                           value="${item.quantity}" readonly>
                                </div>
                                <div class="col-md-3">
                                    <label>Đơn vị:</label>
                                    <input type="text" class="form-control" 
                                           value="${item.unit}" readonly>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
                
                <div class="mt-4">
                    
                    <a href="${pageContext.request.contextPath}/import-materials?action=showImportFromDirector" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i> Quay lại
                    </a>
                </div>
            </form>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            let currentRequestId = null;
            const importModal = new bootstrap.Modal(document.getElementById('importModal'));

            function confirmImport(requestId) {
                currentRequestId = requestId;
                importModal.show();
            }

            
        </script>
    </body>
</html> 