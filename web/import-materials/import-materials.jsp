<%-- Document : import-materials Created on : Jun 10, 2025, 10:00:00 AM Author : Admin --%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Hệ thống Quản lý Xây dựng - Nhập kho vật tư</title>
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap"
              rel="stylesheet">
        <link rel="stylesheet"
              href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
        <style>
            /* Reset và thiết lập cơ bản */
            * {
                box-sizing: border-box;
            }

            body {
                margin: 0;
                padding: 0;
                font-family: 'Poppins', sans-serif;
                background-color: #f5f5f5;
            }

            /* Container chính */
            .content-section {
                width: 100%;
                margin-left: 20px;
                padding: 30px;
                background-color: #fff;
                border-radius: 10px;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            }

            /* Layout container */
            .import-container {
                display: flex;
                gap: 40px;
                margin-bottom: 30px;
                width: 100%;
                min-height: 500px;
            }

            /* Panels */
            .left-panel,
            .right-panel {
                flex: 1;
                padding: 30px;
                background-color: #f9f9f9;
                border-radius: 10px;
                box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
                min-height: 500px;
            }

            .left-panel {
                border-left: 5px solid #4a90e2;
            }

            .right-panel {
                border-left: 5px solid #4caf50;
            }

            /* Form groups */
            .form-group {
                margin-bottom: 25px;
            }

            .form-group label {
                display: block;
                margin-bottom: 10px;
                font-weight: 600;
                font-size: 16px;
                color: #333;
            }

            .form-group input,
            .form-group select {
                width: 100%;
                padding: 15px;
                border: 2px solid #ddd;
                border-radius: 8px;
                font-size: 16px;
                transition: all 0.3s ease;
            }

            .form-group input:focus,
            .form-group select:focus {
                border-color: #4a90e2;
                outline: none;
                box-shadow: 0 0 10px rgba(74, 144, 226, 0.2);
            }

            .form-group input:disabled,
            .form-group select:disabled {
                background-color: #f0f0f0;
                cursor: not-allowed;
            }

            .form-group textarea {
                width: 100%;
                padding: 15px;
                border: 2px solid #ddd;
                border-radius: 8px;
                font-size: 16px;
                resize: vertical;
                min-height: 120px;
                transition: all 0.3s ease;
            }

            .form-group textarea:focus {
                border-color: #f9a825;
                box-shadow: 0 0 10px rgba(249, 168, 37, 0.3);
                outline: none;
            }

            /* Buttons */
            .submit-btn {
                background-color: #4a90e2;
                color: white;
                border: none;
                padding: 15px 25px;
                border-radius: 8px;
                cursor: pointer;
                font-size: 16px;
                font-weight: 600;
                transition: all 0.3s ease;
                width: 100%;
            }

            .submit-btn:hover {
                background-color: #357abd;
                transform: translateY(-2px);
                box-shadow: 0 4px 12px rgba(74, 144, 226, 0.3);
            }

            .submit-btn:disabled {
                background-color: #cccccc;
                cursor: not-allowed;
                transform: none;
                box-shadow: none;
            }

            /* Search container */
            .search-container {
                margin-bottom: 25px;
                display: flex;
                gap: 15px;
            }

            .search-container input {
                flex: 1;
                padding: 15px;
                border: 2px solid #ddd;
                border-radius: 8px;
                font-size: 16px;
            }

            .search-container button {
                padding: 15px 25px;
                background-color: #4a90e2;
                color: white;
                border: none;
                border-radius: 8px;
                cursor: pointer;
                font-size: 16px;
                font-weight: 600;
                transition: all 0.3s ease;
            }

            .search-container button:hover {
                background-color: #357abd;
            }

            /* Search results */
            .search-results {
                max-height: 400px;
                overflow-y: auto;
                border: 2px solid #ddd;
                border-radius: 8px;
                background-color: #fff;
            }

            .search-result-item {
                padding: 18px;
                border-bottom: 1px solid #eee;
                cursor: pointer;
                font-size: 16px;
                transition: background-color 0.2s ease;
            }

            .search-result-item:hover {
                background-color: #f0f8ff;
            }

            .search-result-item:last-child {
                border-bottom: none;
            }

            /* Selected materials section */
            .selected-materials-section {
                margin-top: 40px;
                padding: 30px;
                background-color: #f9f9f9;
                border-radius: 10px;
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                border-left: 5px solid #f9a825;
            }

            /* Tables */
            .selected-materials {
                width: 100%;
                border-collapse: collapse;
                margin-top: 20px;
                font-size: 16px;
                background-color: #fff;
                border-radius: 8px;
                overflow: hidden;
                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            }

            .selected-materials th,
            .selected-materials td {
                border: 1px solid #ddd;
                padding: 15px;
                text-align: left;
            }

            .selected-materials th {
                background-color: #4a90e2;
                color: white;
                font-weight: 600;
                font-size: 16px;
            }

            .selected-materials tr:nth-child(even) {
                background-color: #f9f9f9;
            }

            .selected-materials tr:hover {
                background-color: #f0f8ff;
            }

            /* Action buttons */
            .remove-btn {
                padding: 10px 18px;
                background-color: #f44336;
                color: white;
                border: none;
                border-radius: 6px;
                cursor: pointer;
                font-size: 14px;
                font-weight: 600;
                transition: all 0.3s ease;
            }

            .remove-btn:hover {
                background-color: #d32f2f;
                transform: translateY(-1px);
            }

            /* Success/Error messages */
            .success-message {
                background-color: #4caf50;
                color: #fff;
                padding: 15px;
                border-radius: 8px;
                margin: 15px 0;
                display: flex;
                align-items: center;
                gap: 10px;
                font-size: 16px;
            }

            .error-message {
                background-color: #f44336;
                color: #fff;
                padding: 15px;
                border-radius: 8px;
                margin: 15px 0;
                display: flex;
                align-items: center;
                gap: 10px;
                font-size: 16px;
            }

            /* Headings */
            h2 {
                font-size: 32px;
                margin-bottom: 30px;
                color: #333;
                text-align: center;
            }

            h3 {
                font-size: 24px;
                margin-bottom: 25px;
                color: #444;
            }

            /* Modal styles */
            .modal,
            .confirm-modal {
                display: none;
                position: fixed;
                z-index: 1000;
                left: 0;
                top: 0;
                width: 100%;
                height: 100%;
                overflow: auto;
                background-color: rgba(0, 0, 0, 0.5);
            }

            .modal-content,
            .confirm-modal-content {
                background-color: #fefefe;
                margin: 5% auto;
                padding: 30px;
                border-radius: 10px;
                width: 85%;
                max-width: 900px;
                box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
            }

            .confirm-modal-content {
                width: 50%;
                max-width: 500px;
                margin: 15% auto;
                text-align: center;
            }

            .confirm-buttons {
                margin-top: 25px;
                display: flex;
                justify-content: center;
                gap: 20px;
            }

            .confirm-btn,
            .cancel-btn {
                padding: 15px 30px;
                border: none;
                border-radius: 8px;
                cursor: pointer;
                font-size: 16px;
                font-weight: 600;
                transition: all 0.3s ease;
            }

            .confirm-btn {
                background-color: #4caf50;
                color: white;
            }

            .cancel-btn {
                background-color: #f44336;
                color: white;
            }

            .confirm-btn:hover {
                background-color: #45a049;
            }

            .cancel-btn:hover {
                background-color: #d32f2f;
            }

            /* Responsive design */
            @media (max-width: 1200px) {
                .content-section {
                    width: 98%;
                    padding: 25px;
                }

                .import-container {
                    gap: 30px;
                }
            }

            @media (max-width: 992px) {
                .import-container {
                    flex-direction: column;
                    gap: 25px;
                }

                .left-panel,
                .right-panel {
                    width: 100%;
                    min-height: 400px;
                }

                .search-container {
                    flex-direction: column;
                    gap: 10px;
                }

                .search-container input,
                .search-container button {
                    width: 100%;
                }
            }

            @media (max-width: 768px) {
                .content-section {
                    margin: 10px;
                    padding: 20px;
                }

                .selected-materials {
                    font-size: 14px;
                }

                .selected-materials th,
                .selected-materials td {
                    padding: 10px;
                }

                h2 {
                    font-size: 28px;
                }

                h3 {
                    font-size: 20px;
                }
            }
        </style>

    </head>

    <body>
        <div id="dashboard">
            <%@ include file="../sidebar.jsp" %>
            <div class="content" id="contentArea" >
                <!-- Import Material Section -->
                <div class="content-section" id="importMaterialSection" style="width:100%">
                    <h2><i class="fas fa-box-open"></i> Nhập kho vật tư</h2>
                    <!-- Thông báo cho nhập kho vật tư -->
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

                    <form id="importMaterialForm" action="${pageContext.request.contextPath}/import-materials" method="post">
                        <input type="hidden" name="action" value="import">
                        <div class="import-container">
                            <!-- Phần bên trái: Thông tin vật tư -->
                            <div class="left-panel">
                                <h3><i class="fas fa-info-circle"></i> Thông tin vật tư</h3>
                                <div class="form-group">
                                    <label for="materialName">Tên vật tư:</label>
                                    <input type="text" id="materialName" name="materialName">
                                    <input type="hidden" id="materialId" name="materialId">
                                </div>
                                <div class="form-group">
                                    <label for="category">Danh mục:</label>
                                    <select id="category" name="categoryID">
                                        <option value="">-- Chọn danh mục --</option>
                                        <c:forEach var="category" items="${categories}">
                                            <option value="${category.categoryID}">${category.categoryName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label for="supplier">Nhà cung cấp:</label>
                                    <input type="text" id="supplier" name="supplier" disabled>
                                </div>
                                <div class="form-group">
                                    <label for="newQuantity">Số lượng mới:</label>
                                    <input type="number" id="newQuantity" name="newQuantity" min="0" value="0" required>
                                </div>
                                <div class="form-group">
                                    <label for="brokenQuantity">Số lượng hỏng:</label>
                                    <input type="number" id="brokenQuantity" name="brokenQuantity" min="0" value="0" required>
                                </div>
                                <div class="form-group">
                                    <button type="button" id="addToListBtn" class="submit-btn" onclick="addToImportList()"><i
                                            class="fas fa-plus"></i> Thêm vào danh sách</button>
                                </div>
                            </div>

                            <!-- Phần bên phải: Tìm kiếm vật tư -->
                            <div class="right-panel">
                                <h3><i class="fas fa-search"></i> Tìm kiếm vật tư</h3>
                                <div class="search-container">
                                    <input type="text" id="searchInput" placeholder="Nhập tên vật tư...">
                                    <button type="button" onclick="searchMaterials()">Tìm kiếm</button>
                                </div>
                                <div class="search-results" id="searchResults">
                                    <!-- Kết quả tìm kiếm sẽ được thêm vào đây bằng AJAX -->
                                </div>
                            </div>
                        </div>

                        <!-- Bảng hiển thị vật tư đã chọn -->
                        <div class="selected-materials-section">
                            <h3><i class="fas fa-list"></i> Danh sách vật tư nhập kho</h3>
                            <table class="selected-materials" id="selectedMaterialsTable">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Tên vật tư</th>
                                        <th>Danh mục</th>
                                        <th>Nhà cung cấp</th>
                                        <th>Số lượng mới</th>
                                        <th>Số lượng hỏng</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody id="selectedMaterialsList">
                                    <!-- Dữ liệu vật tư đã chọn sẽ được thêm vào đây bằng JavaScript -->
                                </tbody>
                            </table>
                        </div>

                        <div class="form-group form-actions" style="margin-top: 20px;">
                            <button type="button" class="submit-btn" onclick="confirmImport()"><i class="fas fa-plus"></i>
                                Nhập kho</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Modal xác nhận nhập kho -->
        <div id="confirmModal" class="confirm-modal">
            <div class="confirm-modal-content">
                <h3>Xác nhận nhập kho</h3>
                <p>Bạn chắc chắn muốn nhập kho các vật tư đã chọn?</p>
                <div class="confirm-buttons">
                    <button class="confirm-btn" onclick="submitImport()">Xác nhận</button>
                    <button class="cancel-btn" onclick="closeConfirmModal()">Hủy</button>
                </div>
            </div>
        </div>

        <script>
            // Mảng lưu trữ vật tư đã chọn
            let selectedMaterials = [];

            // Tự động hiển thị 
            document.addEventListener('DOMContentLoaded', function () {
                const importSection = document.getElementById('importMaterialSection');
                if (importSection) {
                    importSection.classList.remove('hidden');
                }

                // Thêm event listener cho input search
                document.getElementById('searchInput').addEventListener('input', debounce(searchMaterials, 300));

                // Thêm event listener cho category change
                document.getElementById('category').addEventListener('change', validateMaterialAndCategory);

                // Thêm event listener cho material name change
                document.getElementById('materialName').addEventListener('input', validateMaterialAndCategory);
            });

            // Debounce function để tránh gọi API quá nhiều
            function debounce(func, wait) {
                let timeout;
                return function executedFunction(...args) {
                    const later = () => {
                        clearTimeout(timeout);
                        func(...args);
                    };
                    clearTimeout(timeout);
                    timeout = setTimeout(later, wait);
                };
            }

            // Tìm kiếm vật tư bằng AJAX cho panel phải
            function searchMaterials() {
                const searchTerm = document.getElementById('searchInput').value.trim();
                const searchResults = document.getElementById('searchResults');
                if (!searchTerm) {
                    searchResults.innerHTML = '';
                    return;
                }

                fetch('import-materials?action=search&keyword=' + searchTerm)
                    .then(response => response.json())
                    .then(materials => {
                        searchResults.innerHTML = '';
                        if (materials.length === 0) {
                            searchResults.innerHTML = '<div class="search-result-item">Không tìm thấy vật tư</div>';
                        } else {
                            materials.forEach(material => {
                                const div = document.createElement('div');
                                div.className = 'search-result-item';
                                div.textContent = material.name;
                                div.setAttribute('data-material', JSON.stringify({
                                    id: material.id,
                                    name: material.name,
                                    categoryId: material.categoryId,
                                    supplier: material.supplier
                                }));
                                div.addEventListener('click', function() {
                                    const data = JSON.parse(this.getAttribute('data-material'));
                                    selectMaterial(data.id, data.name, data.categoryId, data.supplier);
                                });
                                searchResults.appendChild(div);
                            });
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        searchResults.innerHTML = '<div class="search-result-item">Lỗi khi tìm kiếm</div>';
                    });
            }

            // Validate material name and category
            function validateMaterialAndCategory() {
                const materialName = document.getElementById('materialName').value;
                const categoryId = document.getElementById('category').value;
                
                if (!materialName || !categoryId) {
                    disableMaterialFields();
                    return;
                }

                fetch('import-materials?action=validate&materialName=' + materialName + '&categoryID=' + categoryId)
                    .then(response => response.json())
                    .then(data => {
                        if (data.isValid) {
                            enableMaterialFields();
                            document.getElementById('supplier').value = data.material.supplier;
                        } else {
                            disableMaterialFields();
                            alert(data.errorMessage || 'Tên vật tư hoặc danh mục không tồn tại!');
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        disableMaterialFields();
                    });
            }

            function disableMaterialFields() {
                document.getElementById('supplier').value = '';
                document.getElementById('supplier').disabled = true;
            }

            function enableMaterialFields() {
                document.getElementById('supplier').disabled = false;
            }

            // Chọn vật tư từ kết quả tìm kiếm
            function selectMaterial(id, name, categoryId, supplier) {
                document.getElementById('materialId').value = id;
                document.getElementById('materialName').value = name;
                document.getElementById('category').value = categoryId;
                document.getElementById('supplier').value = supplier;

                // Focus vào trường số lượng mới
                document.getElementById('newQuantity').focus();
            }

            // Thêm vật tư vào danh sách nhập kho
            function addToImportList() {
                const materialId = document.getElementById('materialId').value;
                const materialName = document.getElementById('materialName').value;
                const categorySelect = document.getElementById('category');
                const categoryId = categorySelect.value;
                const categoryName = categorySelect.options[categorySelect.selectedIndex].text;
                const supplier = document.getElementById('supplier').value;
                const newQuantity = parseInt(document.getElementById('newQuantity').value) || 0;
                const brokenQuantity = parseInt(document.getElementById('brokenQuantity').value) || 0;

                // Kiểm tra dữ liệu
                if (!materialId || !materialName || !categoryId || !supplier) {
                    alert('Vui lòng điền đầy đủ thông tin vật tư!');
                    return;
                }

                if (newQuantity < 0 || brokenQuantity < 0) {
                    alert('Số lượng không được âm!');
                    return;
                }

                if (newQuantity === 0 && brokenQuantity === 0) {
                    alert('Vui lòng nhập số lượng hợp lệ!');
                    return;
                }

                // Kiểm tra xem vật tư đã được chọn chưa
                const existingIndex = selectedMaterials.findIndex(item => item.id === materialId);
                if (existingIndex !== -1) {
                    alert('Vật tư này đã được thêm vào danh sách!');
                    return;
                }

                // Thêm vật tư vào mảng đã chọn
                selectedMaterials.push({
                    id: materialId,
                    name: materialName,
                    categoryId: categoryId,
                    category: categoryName,
                    supplier: supplier,
                    newQuantity: newQuantity,
                    brokenQuantity: brokenQuantity
                });

                // Cập nhật bảng vật tư đã chọn
                updateSelectedMaterialsTable();
                // Reset form
                resetMaterialForm();
            }

            function resetMaterialForm() {
                document.getElementById('materialId').value = '';
                document.getElementById('materialName').value = '';
                document.getElementById('category').selectedIndex = 0;
                document.getElementById('supplier').value = '';
                document.getElementById('newQuantity').value = '0';
                document.getElementById('brokenQuantity').value = '0';
                document.getElementById('searchInput').value = '';
                document.getElementById('searchInput').focus();
                disableMaterialFields();
            }

            // Cập nhật bảng vật tư đã chọn
            function updateSelectedMaterialsTable() {
                const tableBody = document.getElementById('selectedMaterialsList');
                tableBody.innerHTML = '';

                selectedMaterials.forEach(function(material, index) {
                    var row = document.createElement('tr');
                    var html = '';
                    html += '<td>' + material.id + '</td>';
                    html += '<td>' + material.name + '</td>';
                    html += '<td>' + material.category + '</td>';
                    html += '<td>' + material.supplier + '</td>';
                    html += '<td>' +
                        '<input type="number" name="newQuantity_' + material.id + '" value="' + material.newQuantity + '" min="0" onchange="updateMaterialQuantity(' + index + ', \'newQuantity\', this.value)" required>' +
                        '<input type="hidden" name="materialId_' + index + '" value="' + material.id + '">';
                    html += '</td>';
                    html += '<td>' +
                        '<input type="number" name="brokenQuantity_' + material.id + '" value="' + material.brokenQuantity + '" min="0" onchange="updateMaterialQuantity(' + index + ', \'brokenQuantity\', this.value)" required>' +
                        '</td>';
                    html += '<td>' +
                        '<button type="button" class="remove-btn" onclick="removeMaterial(' + index + ')"><i class="fas fa-trash"></i> Xóa</button>' +
                        '</td>';
                    row.innerHTML = html;
                    tableBody.appendChild(row);
                });

                // Thêm input ẩn để lưu số lượng vật tư đã chọn
                const form = document.getElementById('importMaterialForm');
                const countInput = document.getElementById('materialCount');
                if (countInput) {
                    countInput.value = selectedMaterials.length;
                } else {
                    const input = document.createElement('input');
                    input.type = 'hidden';
                    input.id = 'materialCount';
                    input.name = 'materialCount';
                    input.value = selectedMaterials.length;
                    form.appendChild(input);
                }
            }

            // Cập nhật số lượng vật tư
            function updateMaterialQuantity(index, field, value) {
                selectedMaterials[index][field] = parseInt(value) || 0;
            }

            // Xóa vật tư khỏi danh sách đã chọn
            function removeMaterial(index) {
                selectedMaterials.splice(index, 1);
                updateSelectedMaterialsTable();
            }

            // Mở modal xác nhận nhập kho
            function confirmImport() {
                if (selectedMaterials.length === 0) {
                    alert('Vui lòng chọn ít nhất một vật tư để nhập kho!');
                    return;
                }

                // Kiểm tra số lượng
                let valid = true;
                selectedMaterials.forEach(material => {
                    if (material.newQuantity <= 0 && material.brokenQuantity <= 0) {
                        valid = false;
                    }
                });

                if (!valid) {
                    alert('Vui lòng nhập số lượng hợp lệ cho tất cả vật tư!');
                    return;
                }

                document.getElementById('confirmModal').style.display = 'block';
            }

            // Đóng modal xác nhận
            function closeConfirmModal() {
                document.getElementById('confirmModal').style.display = 'none';
            }

            // Gửi form nhập kho
            function submitImport() {
                document.getElementById('importMaterialForm').submit();
            }
        </script>
    </body>

</html>
