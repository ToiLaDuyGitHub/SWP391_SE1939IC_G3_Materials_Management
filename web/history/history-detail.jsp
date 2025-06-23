<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Chi tiết lịch sử ${type == 'import' ? 'nhập' : 'xuất'} kho</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
            body {
                background-color: #f8f9fa;
            }
            .card {
                box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
                border: none;
                margin-bottom: 2rem;
            }
            .card-header {
                border-bottom: none;
                padding: 1.5rem;
            }
            .card-header h4 {
                font-weight: 600;
                margin: 0;
            }
            .table {
                margin-bottom: 0;
            }
            .table th {
                background-color: #f8f9fa;
                font-weight: 600;
                border-top: none;
                width: 30%;
            }
            .material-list {
                max-height: 400px;
                overflow-y: auto;
            }
            .material-list::-webkit-scrollbar {
                width: 6px;
            }
            .material-list::-webkit-scrollbar-track {
                background: #f1f1f1;
            }
            .material-list::-webkit-scrollbar-thumb {
                background: #888;
                border-radius: 3px;
            }
            .material-list::-webkit-scrollbar-thumb:hover {
                background: #555;
            }
            .btn-secondary {
                background-color: #6c757d;
                border-color: #6c757d;
                transition: all 0.3s ease;
            }
            .btn-secondary:hover {
                background-color: #5a6268;
                border-color: #545b62;
                transform: translateY(-1px);
            }
        </style>
    </head>
    <body>
        <div class="container mt-4">
            <div class="card">
                <div class="card-header ${type == 'import' ? 'bg-primary' : 'bg-success'} text-white">
                    <h4 class="mb-0">
                        <i class="fas ${type == 'import' ? 'fa-arrow-down' : 'fa-arrow-up'} me-2"></i>
                        Chi tiết lịch sử ${type == 'import' ? 'nhập' : 'xuất'} kho
                    </h4>
                </div>
                <div class="card-body">
                    <div class="row mb-4">
                        <div class="col-md-6">
                            <div class="card">
                                <div class="card-header bg-light">
                                    <h5 class="mb-0"><i class="fas fa-info-circle me-2"></i>Thông tin chung</h5>
                                </div>
                                <div class="card-body">
                                    <table class="table">
                                        <tr>
                                            <th>Mã lịch sử:</th>
                                            <td>${type == 'import' ? history.importHistoryID : history.exportHistoryID}</td>
                                        </tr>
                                        <tr>
                                            <th>Thời gian:</th>
                                            <td>${type == 'import' ? history.importDate : history.exportDate}</td>
                                        </tr>
                                        <tr>
                                            <th>Người thực hiện:</th>
                                            <td>${user.firstName} ${user.lastName}</td>
                                        </tr>
                                    </table>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="card">
                                <div class="card-header bg-light">
                                    <h5 class="mb-0"><i class="fas fa-file-alt me-2"></i>Thông tin yêu cầu</h5>
                                </div>
                                <div class="card-body">
                                    <table class="table">
                                        <c:if test="${type == 'import'}">
                                            <tr>
                                                <th>Mã yêu cầu mua:</th>
                                                <td>${history.purchaseRequestID}</td>
                                            </tr>
                                            <tr>
                                                <th>Mã yêu cầu trả về:</th>
                                                <td>${history.returnRequestID}</td>
                                            </tr>
                                        </c:if>
                                        <c:if test="${type == 'export'}">
                                            <tr>
                                                <th>Mã yêu cầu xuất:</th>
                                                <td>${history.exportRequestID}</td>
                                            </tr>
                                            <tr>
                                                <th>Mã yêu cầu sửa chữa:</th>
                                                <td>${history.repairRequestID}</td>
                                            </tr>
                                        </c:if>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="card">
                        <div class="card-header bg-light">
                            <h5 class="mb-0"><i class="fas fa-boxes me-2"></i>Danh sách vật tư</h5>
                        </div>
                        <div class="card-body">
                            <div class="table-responsive material-list">
                                <table class="table table-hover">
                                    <thead>
                                        <tr>
                                            <th>STT</th>
                                            <th>Mã vật tư</th>
                                            <th>Tên vật tư</th>
                                            <th>Số lượng</th>
                                            <th>Đơn vị</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${materialsList}" var="material" varStatus="loop">
                                            <tr>
                                                <td class="text-center">${loop.index + 1}</td>
                                                <td>${material.materialId}</td>
                                                <td>${material.materialName}</td>
                                                <td class="text-center">${material.quantity}</td>
                                                <td>${material.unit}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                    
                    <div class="text-center mt-4">
                        <a href="manage-history" class="btn btn-secondary">
                            <i class="fas fa-arrow-left me-2"></i>Quay lại danh sách
                        </a>
                    </div>
                </div>
            </div>
        </div>
        
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html> 