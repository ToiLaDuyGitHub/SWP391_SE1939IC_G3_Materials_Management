<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ page errorPage="errorPage.jsp" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Tạo đề nghị sửa chữa vật tư - Hệ thống Quản lý Xây dựng</title>
        <!-- CSS Links -->
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">

        <style>
            /* CSS cải thiện layout và màu sắc */
            .main-content {
                margin-left: 280px; /* Tạo khoảng trống cho sidebar */
                margin-top: 70px; /* Tạo khoảng trống cho header */
                padding: 20px;
                min-height: calc(100vh - 70px);
                transition: margin-left 0.3s ease;
            }

            /* Responsive cho mobile */
            @media (max-width: 768px) {
                .main-content {
                    margin-left: 0;
                    margin-top: 60px;
                }
            }

            /* Cải thiện màu sắc card header để phù hợp với sidebar */
            .card-header {
                background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
                color: white;
                padding: 15px 20px;
                border-radius: 8px 8px 0 0;
                font-weight: 600;
                border: none;
            }

            .card {
                border: none;
                box-shadow: 0 4px 15px rgba(0,0,0,0.1);
                border-radius: 10px;
                margin-bottom: 25px;
            }

            .card-body {
                padding: 25px;
            }

            /* Cải thiện table styling */
            .table {
                border-radius: 8px;
                overflow: hidden;
                box-shadow: 0 2px 8px rgba(0,0,0,0.05);
            }

            .table thead th {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                font-weight: 600;
                border: none;
                padding: 15px 12px;
                font-size: 0.9rem;
            }

            .table tbody td {
                padding: 12px;
                vertical-align: middle;
                border-color: #e9ecef;
            }

            .table-striped tbody tr:nth-of-type(odd) {
                background-color: #f8f9ff;
            }

            .table tbody tr:hover {
                background-color: #e3f2fd;
                transition: background-color 0.2s ease;
            }

            /* Table success styling cho bảng vật tư đã chọn */
            .table-success thead th {
                background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%);
                color: white;
            }

            /* Button styling */
            .btn-add {
                background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%);
                border: none;
                color: white;
                padding: 6px 12px;
                border-radius: 6px;
                font-size: 0.85rem;
                transition: all 0.3s ease;
            }

            .btn-add:hover {
                background: linear-gradient(135deg, #229954 0%, #27ae60 100%);
                transform: translateY(-1px);
                box-shadow: 0 4px 8px rgba(39, 174, 96, 0.3);
                color: white;
            }

            .btn-remove {
                background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
                border: none;
                color: white;
                padding: 6px 12px;
                border-radius: 6px;
                font-size: 0.85rem;
                transition: all 0.3s ease;
            }

            .btn-remove:hover {
                background: linear-gradient(135deg, #c0392b 0%, #a93226 100%);
                transform: translateY(-1px);
                box-shadow: 0 4px 8px rgba(231, 76, 60, 0.3);
                color: white;
            }

            .btn-primary {
                background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
                border: none;
                padding: 10px 20px;
                border-radius: 6px;
                font-weight: 500;
                transition: all 0.3s ease;
            }

            .btn-primary:hover {
                background: linear-gradient(135deg, #2980b9 0%, #21618c 100%);
                transform: translateY(-1px);
                box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
            }

            .btn-secondary {
                background: linear-gradient(135deg, #95a5a6 0%, #7f8c8d 100%);
                border: none;
                padding: 10px 20px;
                border-radius: 6px;
                font-weight: 500;
                transition: all 0.3s ease;
            }

            .btn-secondary:hover {
                background: linear-gradient(135deg, #7f8c8d 0%, #6c7b7d 100%);
                transform: translateY(-1px);
                box-shadow: 0 4px 12px rgba(149, 165, 166, 0.3);
            }

            /* Page header styling */
            .page-header {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                padding: 25px;
                border-radius: 10px;
                margin-bottom: 25px;
                box-shadow: 0 4px 15px rgba(102, 126, 234, 0.2);
            }

            .page-header h2 {
                margin-bottom: 8px;
                font-weight: 600;
            }

            .page-header p {
                opacity: 0.9;
                font-size: 0.95rem;
            }

            /* Form styling */
            .form-control, .form-select {
                border: 2px solid #e9ecef;
                border-radius: 6px;
                padding: 10px 12px;
                transition: all 0.3s ease;
            }

            .form-control:focus, .form-select:focus {
                border-color: #667eea;
                box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.15);
            }

            .form-label {
                font-weight: 500;
                color: #2c3e50;
                margin-bottom: 8px;
            }

            .readonly-field {
                background-color: #f8f9fa;
                border-color: #dee2e6;
            }

            /* Input group styling */
            .input-group .btn {
                border-radius: 0 6px 6px 0;
            }

            .input-group .form-control {
                border-radius: 6px בכדי 0 0 6px;
            }

            /* Quantity input styling */
            .quantity-input {
                max-width: 100px;
                text-align: center;
            }

            /* Action buttons */
            .action-buttons {
                text-align: center;
                margin-top: 30px;
                padding-top: 20px;
                border-top: 2px solid #e9ecef;
            }

            /* Empty message styling */
            .empty-message td {
                text-align: center;
                color: #6c757d;
                font-style: italic;
                padding: 30px 20px;
            }

            /* Notification styling */
            .alert {
                border: none;
                border-radius: 8px;
                padding: 12px 16px;
                font-weight: 500;
            }

            .alert-success {
                background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
                color: #155724;
            }

            .alert-info {
                background: linear-gradient(135deg, #d1ecf1 0%, #bee5eb 100%);
                color: #0c5460;
            }

            .alert-warning {
                background: linear-gradient(135deg, #fff3cd 0%, #ffeaa7 100%);
                color: #856404;
            }

            .alert-danger {
                background: linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%);
                color: #721c24;
            }
        </style>
    </head>
    <body>
        <%
            try {
        %>

        <%@ include file="/sidebar.jsp" %>

        <div class="main-content" id="main-content">
            <div class="page-header">
                <h2><i class="fas fa-tools"></i> TẠO ĐỀ NGHỊ SỬA CHỮA VẬT TƯ</h2>
                <p class="mb-0">Tạo đề nghị sửa chữa vật tư cho các hoạt động xây dựng</p>
            </div>

            <!-- Hiển thị thông báo lỗi/thành công -->
            <%
                String error = request.getParameter("error");
                String success = request.getParameter("success");
                if (error != null) {
            %>
            <div class="alert alert-danger">
                <% if ("noMaterials".equals(error)) { %>
                Vui lòng chọn ít nhất một vật tư để tạo đề nghị sửa chữa.
                <% } else if ("DatabaseError".equals(error)) { %>
                Có lỗi xảy ra với cơ sở dữ liệu. Vui lòng thử lại.
                <% } else if ("InvalidMaterialID".equals(error)) { %>
                ID vật tư không hợp lệ.
                <% } else if ("materialExists".equals(error)) { %>
                Vật tư này đã có trong danh sách đề nghị sửa chữa.
                <% } else { %>
                Có lỗi xảy ra: <%= error %>
                <% } %>
            </div>
            <% } %>

            <% if ("true".equals(success)) { %>
            <div class="alert alert-success">
                Tạo đề nghị sửa chữa mới thành công!
            </div>
            <% } %>

            <!-- Form tìm kiếm vật tư -->
            <div class="card">
                <div class="card-header">
                    <i class="fas fa-search"></i> TÌM KIẾM VẬT TƯ CẦN SỬA CHỮA
                </div>
                <div class="card-body">
                    <form action="<%= request.getContextPath() %>/repair-request" method="get" class="search-form">
                        <input type="hidden" name="service" value="searchByKeywords">
                        <div class="row g-3">
                            <div class="col-md-4">
                                <input type="text" class="form-control" name="keywords" 
                                       placeholder="Nhập tên vật tư, mã vật tư..." 
                                       value="<%= request.getParameter("keywords") != null ? request.getParameter("keywords") : "" %>">
                            </div>
                            <div class="col-md-3">
                                <select class="form-select" name="categoryFilter" id="categoryFilter" onchange="loadSubcategories()">
                                    <option value="">-- Chọn danh mục --</option>
                                    <% 
                                        java.util.List categories = (java.util.List) request.getAttribute("categories");
                                        if (categories != null) {
                                            for (Object category : categories) {
                                                String categoryID = String.valueOf(category.getClass().getMethod("getCategoryID").invoke(category));
                                                String categoryName = (String) category.getClass().getMethod("getCategoryName").invoke(category);
                                                String selected = categoryID.equals(request.getParameter("categoryFilter")) ? "selected" : "";
                                    %>
                                    <option value="<%= categoryID %>" <%= selected %>><%= categoryName %></option>
                                    <% 
                                            }
                                        }
                                    %>
                                </select>
                            </div>
                            <div class="col-md-3">
                                <select class="form-select" name="subcategoryFilter" id="subcategoryFilter">
                                    <option value="">-- Chọn danh mục con --</option>
                                    <% 
                                        java.util.List subcategories = (java.util.List) request.getAttribute("subcategories");
                                        if (subcategories != null) {
                                            for (Object subcategory : subcategories) {
                                                try {
                                                    String subcategoryID = String.valueOf(subcategory.getClass().getMethod("getSubcategoryID").invoke(subcategory));
                                                    String subcategoryName = (String) subcategory.getClass().getMethod("getSubcategoryName").invoke(subcategory));
                                                    String selected = subcategoryID.equals(request.getParameter("subcategoryFilter")) ? "selected" : "";
                                    %>
                                    <option value="<%= subcategoryID %>" <%= selected %>><%= subcategoryName %></option>
                                    <% 
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    %>
                                </select>
                            </div>
                            <div class="col-md-2">
                                <button type="submit" class="btn btn-primary w-100">
                                    <i class="fas fa-search"></i> Tìm kiếm
                                </button>
                            </div>
                        </div>
                    </form>

                    <% if (request.getAttribute("notFoundMaterial") != null) { %>
                    <div class="alert alert-warning mt-3">
                        <%= request.getAttribute("notFoundMaterial") %>
                    </div>
                    <% } %>

                    <!-- Kết quả tìm kiếm -->
                    <div class="table-responsive mt-3">
                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th width="10%">STT</th>
                                    <th width="50%">Tên vật tư</th>
                                    <th width="25%">Nhà cung cấp</th>
                                    <th width="15%">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% 
                                    java.util.List searchResults = (java.util.List) request.getAttribute("searchResults");
                                    if (searchResults == null || searchResults.isEmpty()) { 
                                %>
                                <tr class="empty-message">
                                    <td colspan="4">Vui lòng nhập từ khóa và nhấn "Tìm kiếm" để hiển thị kết quả.</td>
                                </tr>
                                <% 
                                    } else {
                                        int count = 1;
                                        for (Object material : searchResults) {
                                            try {
                                                String materialID = String.valueOf(material.getClass().getMethod("getMaterialID").invoke(material));
                                                String materialName = (String) material.getClass().getMethod("getMaterialName").invoke(material));
                                                String supplierName = (String) material.getClass().getMethod("getSupplierName").invoke(material));
                                %>
                                <tr>
                                    <td class="text-center"><%= count++ %></td>
                                    <td><%= materialName %></td>
                                    <td><%= supplierName %></td>
                                    <td class="text-center">
                                        <form action="<%= request.getContextPath() %>/repair-request" method="post" style="display: inline;">
                                            <input type="hidden" name="service" value="addMaterial">
                                            <input type="hidden" name="materialID" value="<%= materialID %>">
                                            <button type="submit" class="btn btn-add btn-sm">
                                                <i class="fas fa-plus"></i> Chọn sửa chữa
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                                <% 
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <!-- Form tạo đề nghị sửa chữa -->
            <div class="card">
                <div class="card-header">
                    <i class="fas fa-file-alt"></i> CHI TIẾT ĐỀ NGHỊ SỬA CHỮA
                </div>
                <div class="card-body">
                    <form id="createRequestForm" action="<%= request.getContextPath() %>/repair-request" method="post">
                        <input type="hidden" name="service" value="createRequest">

                        <!-- Thông tin đơn hàng -->
                        <div class="row mb-4">
                            <div class="col-md-4">
                                <label class="form-label">Ngày làm đơn:</label>
                                <input type="date" class="form-control readonly-field" name="requestDate" 
                                       value="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()) %>" readonly>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Họ tên người làm đơn:</label>
                                <%
                                    model.User currentUser = (model.User) session.getAttribute("user");
                                    String userName = (currentUser != null) ? currentUser.getFullName() : "Không xác định";
                                %>
                                <input type="text" class="form-control readonly-field" name="requestorName" 
                                       value="<%= userName %>" readonly>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Mã đơn:</label>
                                <% String requestCode = "RR" + String.valueOf(System.currentTimeMillis()).substring(7, 13); %>
                                <input type="text" class="form-control readonly-field" name="requestCode" 
                                       value="<%= requestCode %>" readonly>
                            </div>
                        </div>

                        <!-- Bảng vật tư đã chọn -->
                        <div class="table-responsive">
                            <table class="table table-bordered table-success">
                                <thead>
                                    <tr>
                                        <th width="8%">STT</th>
                                        <th width="35%">Tên vật tư cần sửa chữa</th>
                                        <th width="15%">Số lượng</th>
                                        <th width="12%">Đơn vị</th>
                                        <th width="18%">Nhà cung cấp</th>
                                        <th width="12%">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% 
                                        java.util.List selectedMaterials = (java.util.List) request.getAttribute("selectedMaterials");
                                        if (selectedMaterials == null || selectedMaterials.isEmpty()) { 
                                    %>
                                    <tr class="empty-message">
                                        <td colspan="6">Chưa có vật tư nào được chọn để sửa chữa.</td>
                                    </tr>
                                    <% 
                                        } else {
                                            int count = 1;
                                            for (Object material : selectedMaterials) {
                                                try {
                                                    String materialID = String.valueOf(material.getClass().getMethod("getMaterialID").invoke(material));
                                                    String materialName = (String) material.getClass().getMethod("getMaterialName").invoke(material));
                                                    int quantity = (Integer) material.getClass().getMethod("getQuantity").invoke(material));
                                                    String unit = (String) material.getClass().getMethod("getUnit").invoke(material));
                                                    String supplierName = (String) material.getClass().getMethod("getSupplierName").invoke(material));
                                    %>
                                    <tr>
                                        <td class="text-center"><%= count++ %></td>
                                        <td><%= materialName %></td>
                                        <td>
                                            <input type="number" class="form-control quantity-input" 
                                                   name="quantity_<%= materialID %>" 
                                                   value="<%= quantity %>" min="1" max="999">
                                        </td>
                                        <td class="text-center"><%= unit %></td>
                                        <td><%= supplierName %></td>
                                        <td class="text-center">
                                            <form action="<%= request.getContextPath() %>/repair-request" method="post" style="display: inline;">
                                                <input type="hidden" name="service" value="removeMaterial">
                                                <input type="hidden" name="materialID" value="<%= materialID %>">
                                                <button type="submit" class="btn btn-remove btn-sm">
                                                    <i class="fas fa-trash"></i> Xóa
                                                </button>
                                            </form>
                                        </td>
                                    </tr>
                                    <% 
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    %>
                                </tbody>
                            </table>
                        </div>

                        <!-- Ghi chú -->
                        <div class="row mt-4">
                            <div class="col-12">
                                <label class="form-label">Ghi chú sửa chữa:</label>
                                <textarea class="form-control" name="generalNote" rows="4" 
                                          placeholder="Nhập ghi chú về lý do sửa chữa..."></textarea>
                            </div>
                        </div>

                        <!-- Nút thao tác -->
                        <div class="action-buttons">
                            <a href="<%= request.getContextPath() %>/repair-request?service=resetForm" 
                               class="btn btn-secondary me-3">
                                <i class="fas fa-undo"></i> Reset lại
                            </a>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save"></i> Tạo đề nghị sửa chữa
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <%
            } catch (Exception e) {
                e.printStackTrace();
                out.println("<!-- Error: " + e.getMessage() + " -->");
            }
        %>

        <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
        <script>
                                    function loadSubcategories() {
                                        const categoryID = document.getElementById("categoryFilter").value;
                                        const subcategorySelect = document.getElementById("subcategoryFilter");

                                        // Xóa tất cả options trừ option đầu tiên
                                        while (subcategorySelect.options.length > 1) {
                                            subcategorySelect.remove(1);
                                        }

                                        if (categoryID) {
                                            fetch("<%= request.getContextPath() %>/repair-request?service=getSubcategories&categoryID=" + categoryID)
                                                    .then(response => response.json())
                                                    .then(data => {
                                                        data.forEach(subcategory => {
                                                            const option = document.createElement("option");
                                                            option.value = subcategory.subcategoryID;
                                                            option.textContent = subcategory.subcategoryName;
                                                            subcategorySelect.appendChild(option);
                                                        });
                                                    });
                                        }
                                    }

                                    window.onload = function () {
                                        const categoryID = "<%= request.getParameter("categoryFilter") %>";
                                        if (categoryID) {
                                            loadSubcategories();
                                        }
                                    };
        </script>
    </body>
</html>