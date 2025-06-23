<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý lịch sử nhập/xuất kho</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
            body {
                background-color: #f8f9fa;
            }
            .container {
                max-width: 1400px;
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
            }
            .table td {
                vertical-align: middle;
            }
            .list-unstyled li {
                padding: 0.25rem 0;
                border-bottom: 1px solid #eee;
            }
            .list-unstyled li:last-child {
                border-bottom: none;
            }
            .btn-info {
                background-color: #0dcaf0;
                border-color: #0dcaf0;
                color: white;
                transition: all 0.3s ease;
            }
            .btn-info:hover {
                background-color: #0bb6d9;
                border-color: #0bb6d9;
                color: white;
                transform: translateY(-1px);
            }
            .page-title {
                color: #2c3e50;
                font-weight: 700;
                margin-bottom: 2rem;
                text-transform: uppercase;
                letter-spacing: 1px;
            }
            .table-responsive {
                border-radius: 0.5rem;
                overflow: hidden;
            }
            .material-list {
                max-height: 150px;
                overflow-y: auto;
                padding: 0.5rem;
                background-color: #f8f9fa;
                border-radius: 0.25rem;
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
        </style>
    </head>
    <body>
 
        <div class="container mt-4">
            <h2 class="text-center page-title">Quản lý lịch sử nhập/xuất kho</h2>
            
            <div class="row">
                <!-- Import History Table -->
                <div class="col-md-6">
                    <div class="card">
                        <div class="card-header bg-primary text-white">
                            <h4 class="mb-0"><i class="fas fa-arrow-down me-2"></i>Lịch sử nhập kho</h4>
                        </div>
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table table-hover">
                                    <thead>
                                        <tr>
                                            <th style="width: 5%">STT</th>
                                            <th style="width: 20%">Thời gian nhập</th>
                                            <th style="width: 20%">Người nhập</th>
                                            <th style="width: 40%">Danh sách vật tư</th>
                                            <th style="width: 15%">Thao tác</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${importHistoryList}" var="history" varStatus="loop">
                                            <tr>
                                                <td class="text-center">${loop.index + 1}</td>
                                                <td>${history.date}</td>
                                                <td>${history.userName}</td>
                                                <td>
                                                    <div class="material-list">
                                                        <ul class="list-unstyled mb-0">
                                                            <c:forEach items="${history.materialNames}" var="materialName">
                                                                <li><i class="fas fa-box me-2"></i>${materialName}</li>
                                                            </c:forEach>
                                                        </ul>
                                                    </div>
                                                </td>
                                                <td class="text-center">
                                                    <a href="manage-history?action=viewDetail&type=import&id=${history.historyId}" 
                                                       class="btn btn-info btn-sm">
                                                        <i class="fas fa-eye me-1"></i> Chi tiết
                                                    </a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Export History Table -->
                <div class="col-md-6">
                    <div class="card">
                        <div class="card-header bg-success text-white">
                            <h4 class="mb-0"><i class="fas fa-arrow-up me-2"></i>Lịch sử xuất kho</h4>
                        </div>
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table table-hover">
                                    <thead>
                                        <tr>
                                            <th style="width: 5%">STT</th>
                                            <th style="width: 20%">Thời gian xuất</th>
                                            <th style="width: 20%">Người xuất</th>
                                            <th style="width: 40%">Danh sách vật tư</th>
                                            <th style="width: 15%">Thao tác</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${exportHistoryList}" var="history" varStatus="loop">
                                            <tr>
                                                <td class="text-center">${loop.index + 1}</td>
                                                <td>${history.date}</td>
                                                <td>${history.userName}</td>
                                                <td>
                                                    <div class="material-list">
                                                        <ul class="list-unstyled mb-0">
                                                            <c:forEach items="${history.materialNames}" var="materialName">
                                                                <li><i class="fas fa-box me-2"></i>${materialName}</li>
                                                            </c:forEach>
                                                        </ul>
                                                    </div>
                                                </td>
                                                <td class="text-center">
                                                    <a href="manage-history?action=viewDetail&type=export&id=${history.historyId}" 
                                                       class="btn btn-info btn-sm">
                                                        <i class="fas fa-eye me-1"></i> Chi tiết
                                                    </a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html> 