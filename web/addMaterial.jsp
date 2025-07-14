<%-- 
    Document   : addMaterial
    Created on : 1 thg 6, 2025, 01:40:09
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Hệ thống Quản lý Xây dựng - Thêm vật tư</title>
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">
        <style>
            .content-card {
                max-width: 1200px;
                margin: 20px auto; 
            }
            .success-message {
                background-color: #4caf50;
                color: #fff;
                padding: 10px;
                border-radius: 6px;
                margin: 10px 0;
                display: flex;
                align-items: center;
                gap: 8px;
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
            .form-group textarea {
                width: 100%;
                padding: 12px;
                border: 1px solid #ddd;
                border-radius: 6px;
                font-size: 14px;
                resize: vertical;
                min-height: 100px;
                transition: border 0.3s, box-shadow 0.3s, transform 0.2s;
            }
            .form-group textarea:focus {
                border-color: #f9a825;
                box-shadow: 0 0 8px rgba(249, 168, 37, 0.3);
                transform: scale(1.01);
                outline: none;
            }
            .invalid-input {
                border-color: #f44336 !important;
                box-shadow: 0 0 8px rgba(244, 67, 54, 0.3) !important;
            }
            .error-text {
                color: #f44336;
                font-size: 12px;
                margin-top: 4px;
                display: none;
            }
        </style>
    </head>
    <body>
        <div id="dashboard">
            <%@ include file="sidebar.jsp" %>
            <div class="content" id="contentArea">
                <!-- Phần Thêm Vật tư -->
                <div class="content-card" id="addMaterialSection">
                    <h2><i class="fas fa-box-open"></i> Thêm mới vật tư</h2>
                    <!-- Thông báo cho thêm vật tư -->
                    <c:if test="${not empty successMessage}">
                        <div class="success-message">
                            <i class="fas fa-check-circle"></i> ${successMessage}
                        </div>
                    </c:if>
                    <c:if test="${not empty error}">
                        <div class="error-message">
                            <i class="fas fa-exclamation-circle"></i> ${error}
                        </div>
                    </c:if>
                    <form id="addMaterialForm" action="add-material" method="post" enctype="multipart/form-data">
                        <div class="form-grid">
                            <!-- Cột bên trái -->
                            <div class="form-column">
                                <div class="form-group">
                                    <label for="materialName"><i class="fas fa-tag"></i> Tên vật tư:</label>
                                    <input type="text" id="materialName" name="MaterialName" placeholder="Nhập tên vật tư" required>
                                    <div id="materialNameError" class="error-text"></div>
                                </div>
                                <div class="form-group">
                                    <label for="image"><i class="fas fa-image"></i> Hình ảnh:</label>
                                    <input type="file" id="image" name="Image" accept="image/*">
                                </div>
                            </div>
                            <!-- Cột bên phải -->
                            <div class="form-column">
                                <div class="form-group">
                                    <label for="subcategory"><i class="fas fa-list"></i> Danh mục:</label>
                                    <select id="subcategory" name="SubcategoryID" required>
                                        <option value="" disabled selected>Chọn danh mục</option>
                                        <c:forEach var="subcat" items="${subcategoryList}">
                                            <option value="${subcat.subcategoryID}">${subcat.subcategoryName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label for="unit"><i class="fas fa-ruler"></i> Đơn vị:</label>
                                    <select id="unit" name="UnitID" required>
                                        <option value="" disabled selected>Chọn đơn vị</option>
                                        <c:forEach var="unit" items="${unitsList}">
                                            <option value="${unit.minUnit}">${unit.minUnit}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label for="detail"><i class="fas fa-info-circle"></i> Chi tiết vật tư:</label>
                                    <textarea id="detail" name="Detail" placeholder="Nhập chi tiết vật tư"></textarea>
                                </div>
                            </div>
                        </div>
                        <div class="form-group form-actions">
                            <button type="submit" class="submit-btn"><i class="fas fa-plus"></i> Thêm vật tư</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <script src="<%= request.getContextPath() %>/js/script.js"></script>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                const profileSection = document.getElementById('addMaterialSection');
                if (profileSection) {
                    profileSection.classList.remove('hidden');
                }

                const materialNameInput = document.getElementById('materialName');
                const materialNameError = document.getElementById('materialNameError');
                const form = document.getElementById('addMaterialForm');

                function validateMaterialName() {
                    const value = materialNameInput.value;
                    const regex = /^[a-zA-Z0-9\s\u00C0-\u1EF9]*$/; // Cho phép chữ cái, số, khoảng trắng và ký tự tiếng Việt
                    const isValidLength = value.length >= 2 && value.length <= 250;
                    const hasNoSpecialChars = regex.test(value);

                    if (!isValidLength) {
                        materialNameInput.classList.add('invalid-input');
                        materialNameError.textContent = 'Tên vật tư phải có 2-250 ký tự.';
                        materialNameError.style.display = 'block';
                        return false;
                    } else if (!hasNoSpecialChars) {
                        materialNameInput.classList.add('invalid-input');
                        materialNameError.textContent = 'Tên vật tư chứa ký tự đặc biệt.';
                        materialNameError.style.display = 'block';
                        return false;
                    } else {
                        materialNameInput.classList.remove('invalid-input');
                        materialNameError.style.display = 'none';
                        return true;
                    }
                }

                form.addEventListener('submit', function (event) {
                    if (!validateMaterialName()) {
                        event.preventDefault();
                    }
                });
            });
        </script>
    </body>
</html>