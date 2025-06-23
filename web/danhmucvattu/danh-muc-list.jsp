

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Hệ thống Quản lý Xây dựng - Danh sách danh mục vật tư</title>
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">
        <style>
            .active-link {
                font-weight: 600;
                color: #0d6efd !important;
                background-color: #e9ecef;
                border-radius: 4px;
            }
            .main-content {
                margin-left: 250px;
                padding: 20px;
                transition: margin-left 0.3s;
            }

            .sidebar-collapsed + .main-content {
                margin-left: 80px;
            }

            .category-container {
                display: flex;
                flex-direction: column;
                gap: 15px;
            }

            .category-card {
                background: #fff;
                border-radius: 8px;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                overflow: hidden;
            }

            .category-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 15px 20px;
                cursor: pointer;
                background: #f8f9fa;
                border-bottom: 1px solid #eee;
            }

            .category-header:hover {
                background: #e9ecef;
            }

            .category-title {
                font-weight: 600;
                color: #333;
                margin: 0;
            }

            .category-arrow {
                transition: transform 0.3s;
            }

            .category-arrow.rotated {
                transform: rotate(180deg);
            }

            .subcategory-list {
                max-height: 0;
                overflow: hidden;
                transition: max-height 0.3s ease-out;
                background: #fff;
            }

            .subcategory-list.expanded {
                max-height: 500px; /* Điều chỉnh theo số lượng subcategory */
            }

            .subcategory-item {
                padding: 12px 20px;
                border-bottom: 1px solid #f1f1f1;
                display: flex;
                justify-content: space-between;
                align-items: center;
            }

            .subcategory-item:last-child {
                border-bottom: none;
            }

            .subcategory-name {
                color: #555;
            }

            .subcategory-actions {
                display: flex;
                gap: 10px;
            }

            .action-btn {
                background: none;
                border: none;
                cursor: pointer;
                color: #6c757d;
                font-size: 14px;
            }

            .action-btn:hover {
                color: #0d6efd;
            }

            .no-categories {
                text-align: center;
                padding: 20px;
                color: #6c757d;
            }

            .stats-container {
                display: flex;
                gap: 20px;
                margin-bottom: 20px;
            }

            .stat-card {
                background: #fff;
                border-radius: 8px;
                padding: 20px;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                flex: 1;
                text-align: center;
            }

            .stat-number {
                font-size: 2em;
                font-weight: 700;
                color: #0d6efd;
            }

            .stat-label {
                color: #6c757d;
                margin-top: 5px;
            }

            .alert {
                padding: 15px;
                margin-bottom: 20px;
                border: 1px solid transparent;
                border-radius: 4px;
            }

            .alert-success {
                color: #155724;
                background-color: #d4edda;
                border-color: #c3e6cb;
            }

            .alert-error {
                color: #721c24;
                background-color: #f8d7da;
                border-color: #f5c6cb;
            }

            .search-container {
                background: #fff;
                border-radius: 8px;
                padding: 20px;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                margin-bottom: 20px;
            }

            .search-form {
                display: flex;
                gap: 10px;
                align-items: center;
            }

            .search-input {
                flex: 1;
                padding: 10px;
                border: 1px solid #ddd;
                border-radius: 4px;
            }

            .search-btn {
                padding: 10px 20px;
                background: #0d6efd;
                color: white;
                border: none;
                border-radius: 4px;
                cursor: pointer;
            }

            .search-btn:hover {
                background: #0b5ed7;
            }

            .category-actions {
                display: flex;
                gap: 10px;
            }

            .add-btn {
                background: #28a745;
                color: white;
                border: none;
                padding: 8px 12px;
                border-radius: 4px;
                cursor: pointer;
                font-size: 12px;
            }

            .add-btn:hover {
                background: #218838;
            }
        </style>
    </head>
    <body>
        <div id="dashboard">
            <%@ include file="/sidebar.jsp" %> 
            <div class="main-content" id="mainContent">
                <div style="text-align: center; margin-bottom: 20px">
                    <h2 style="color: #0d6efd;">Danh mục vật tư</h2>
                </div>

                <!-- Hiển thị thông báo -->
                <c:if test="${not empty successMessage}">
                    <div class="alert alert-success">
                        ${successMessage}
                    </div>
                </c:if>
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-error">
                        ${errorMessage}
                    </div>
                </c:if>

                <!-- Thống kê -->
                <div class="stats-container">
                    <div class="stat-card">
                        <div class="stat-number">${totalCategories}</div>
                        <div class="stat-label">Tổng danh mục</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number">${totalSubcategories}</div>
                        <div class="stat-label">Tổng danh mục con</div>
                    </div>
                </div>

                <!-- Tìm kiếm -->
                <div class="search-container">
                    <form class="search-form" action="${pageContext.request.contextPath}/manage-category" method="get">
                        <input type="hidden" name="action" value="search">
                        <input type="text" name="searchTerm" value="${searchTerm}" 
                               placeholder="Tìm kiếm danh mục..." class="search-input">
                        <button type="submit" class="search-btn">
                            <i class="fas fa-search"></i> Tìm kiếm
                        </button>
                        <a href="${pageContext.request.contextPath}/manage-category" class="search-btn" style="text-decoration: none;">
                            <i class="fas fa-refresh"></i> Reset
                        </a>
                    </form>
                </div>

                <div class="category-container">
                    <c:choose>
                        <c:when test="${empty categories}">
                            <div class="no-categories">
                                <i class="fas fa-folder-open" style="font-size: 48px; color: #ddd; margin-bottom: 10px;"></i>
                                <p>Chưa có danh mục nào trong hệ thống.</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="category" items="${categories}">
                                <div class="category-card">
                                    <div class="category-header" onclick="toggleCategory(this)">
                                        <h3 class="category-title">${category.categoryName}</h3>
                                        <div class="category-actions">
                                            <button class="action-btn" onclick="viewCategory(${category.categoryID})" 
                                                    title="Xem danh mục">
                                                <i class="fas fa-eye"></i>
                                            </button>
                                            <button class="action-btn" onclick="event.stopPropagation(); editCategory(${category.categoryID}, &quot;${category.categoryName}&quot;)" 
                                                    title="Sửa danh mục">
                                                <i class="fas fa-edit"></i>
                                            </button>
                                            <button class="action-btn" onclick="event.stopPropagation(); deleteCategory(${category.categoryID}, &quot;${category.categoryName}&quot;)" 
                                                    title="Xóa danh mục">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                            <i class="fas fa-chevron-down category-arrow"></i>
                                        </div>
                                    </div>
                                    <div class="subcategory-list">
                                        <!-- Lọc subcategories theo categoryID -->
                                        <c:set var="hasSubcategories" value="false" />
                                        <c:forEach var="subcategoryInfo" items="${subcategoriesWithInfo}">
                                            <c:set var="subcategory" value="${subcategoryInfo[0]}" />
                                            <c:set var="categoryInfo" value="${subcategoryInfo[1]}" />
                                            <c:if test="${subcategory.categoryID == category.categoryID}">
                                                <c:set var="hasSubcategories" value="true" />
                                                <div class="subcategory-item">
                                                    <span class="subcategory-name">${subcategory.subcategoryName}</span>
                                                    <div class="subcategory-actions">
                                                        <button class="action-btn" 
                                                                onclick="editSubcategory(${subcategory.subcategoryID}, ${subcategory.categoryID}, '${subcategory.subcategoryName}')" 
                                                                title="Sửa">
                                                            <i class="fas fa-edit"></i>
                                                        </button>
                                                        <button class="action-btn" 
                                                                onclick="deleteSubcategory(${subcategory.subcategoryID}, '${subcategory.subcategoryName}')" 
                                                                title="Xóa">
                                                            <i class="fas fa-trash"></i>
                                                        </button>
                                                    </div>
                                                </div>
                                            </c:if>
                                        </c:forEach>
                                        <c:if test="${!hasSubcategories}">
                                            <div class="subcategory-item">
                                                <span class="subcategory-name" style="color: #999; font-style: italic;">
                                                    Chưa có danh mục con nào
                                                </span>
                                                <div class="subcategory-actions">
                                                </div>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <script>
             
            // Hàm toggle mở rộng/đóng danh mục
            function toggleCategory(element) {
                const header = element.closest('.category-header');
                const card = header.parentElement;
                const arrow = header.querySelector('.category-arrow');
                const subList = card.querySelector('.subcategory-list');

                arrow.classList.toggle('rotated');
                subList.classList.toggle('expanded');
            }

            // Hàm toggle sidebar
            function toggleSidebar() {
                const sidebar = document.getElementById('sidebar');
                const mainContent = document.getElementById('mainContent');
                sidebar.classList.toggle('collapsed');
                mainContent.classList.toggle('sidebar-collapsed');
            }

            // Hàm toggle dropdown menu
            function toggleDropdown(element) {
                const dropdown = element.closest('.dropdown');
                dropdown.classList.toggle('active');
            }

            // Các hàm xử lý CRUD
            function showAddCategoryModal() {
                const categoryName = prompt("Nhập tên danh mục mới:");
                if (categoryName && categoryName.trim()) {
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = '${pageContext.request.contextPath}/manage-category';
                    
                    const actionInput = document.createElement('input');
                    actionInput.type = 'hidden';
                    actionInput.name = 'action';
                    actionInput.value = 'addCategory';
                    
                    const nameInput = document.createElement('input');
                    nameInput.type = 'hidden';
                    nameInput.name = 'categoryName';
                    nameInput.value = categoryName.trim();
                    
                    form.appendChild(actionInput);
                    form.appendChild(nameInput);
                    document.body.appendChild(form);
                    form.submit();
                }
            }

            function editCategory(categoryId, currentName) {
                const newName = prompt("Sửa tên danh mục:", currentName);
                if (newName && newName.trim() && newName.trim() !== currentName) {
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = '${pageContext.request.contextPath}/manage-category';
                    
                    const actionInput = document.createElement('input');
                    actionInput.type = 'hidden';
                    actionInput.name = 'action';
                    actionInput.value = 'updateCategory';
                    
                    const idInput = document.createElement('input');
                    idInput.type = 'hidden';
                    idInput.name = 'categoryId';
                    idInput.value = categoryId;
                    
                    const nameInput = document.createElement('input');
                    nameInput.type = 'hidden';
                    nameInput.name = 'categoryName';
                    nameInput.value = newName.trim();
                    
                    form.appendChild(actionInput);
                    form.appendChild(idInput);
                    form.appendChild(nameInput);
                    document.body.appendChild(form);
                    form.submit();
                }
            }

            function deleteCategory(categoryId, categoryName) {
                if (confirm(`Bạn có chắc chắn muốn xóa danh mục "${categoryName}"?\nLưu ý: Chỉ có thể xóa danh mục không có danh mục con.`)) {
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = '${pageContext.request.contextPath}/manage-category';
                    
                    const actionInput = document.createElement('input');
                    actionInput.type = 'hidden';
                    actionInput.name = 'action';
                    actionInput.value = 'deleteCategory';
                    
                    const idInput = document.createElement('input');
                    idInput.type = 'hidden';
                    idInput.name = 'categoryId';
                    idInput.value = categoryId;
                    
                    form.appendChild(actionInput);
                    form.appendChild(idInput);
                    document.body.appendChild(form);
                    form.submit();
                }
            }

            function showAddSubcategoryModal(categoryId, categoryName) {
                const subcategoryName = prompt(`Nhập tên danh mục con cho "${categoryName}":`);
                if (subcategoryName && subcategoryName.trim()) {
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = '${pageContext.request.contextPath}/manage-category';
                    
                    const actionInput = document.createElement('input');
                    actionInput.type = 'hidden';
                    actionInput.name = 'action';
                    actionInput.value = 'addSubcategory';
                    
                    const categoryIdInput = document.createElement('input');
                    categoryIdInput.type = 'hidden';
                    categoryIdInput.name = 'categoryId';
                    categoryIdInput.value = categoryId;
                    
                    const nameInput = document.createElement('input');
                    nameInput.type = 'hidden';
                    nameInput.name = 'subcategoryName';
                    nameInput.value = subcategoryName.trim();
                    
                    form.appendChild(actionInput);
                    form.appendChild(categoryIdInput);
                    form.appendChild(nameInput);
                    document.body.appendChild(form);
                    form.submit();
                }
            }

            function editSubcategory(subcategoryId, categoryId, currentName) {
                const newName = prompt("Sửa tên danh mục con:", currentName);
                if (newName && newName.trim() && newName.trim() !== currentName) {
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = '${pageContext.request.contextPath}/manage-category';
                    
                    const actionInput = document.createElement('input');
                    actionInput.type = 'hidden';
                    actionInput.name = 'action';
                    actionInput.value = 'updateSubcategory';
                    
                    const subcategoryIdInput = document.createElement('input');
                    subcategoryIdInput.type = 'hidden';
                    subcategoryIdInput.name = 'subcategoryId';
                    subcategoryIdInput.value = subcategoryId;
                    
                    const categoryIdInput = document.createElement('input');
                    categoryIdInput.type = 'hidden';
                    categoryIdInput.name = 'categoryId';
                    categoryIdInput.value = categoryId;
                    
                    const nameInput = document.createElement('input');
                    nameInput.type = 'hidden';
                    nameInput.name = 'subcategoryName';
                    nameInput.value = newName.trim();
                    
                    form.appendChild(actionInput);
                    form.appendChild(subcategoryIdInput);
                    form.appendChild(categoryIdInput);
                    form.appendChild(nameInput);
                    document.body.appendChild(form);
                    form.submit();
                }
            }

            function deleteSubcategory(subcategoryId, subcategoryName) {
                if (confirm(`Bạn có chắc chắn muốn xóa danh mục con "${subcategoryName}"?`)) {
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = '${pageContext.request.contextPath}/manage-category';
                    
                    const actionInput = document.createElement('input');
                    actionInput.type = 'hidden';
                    actionInput.name = 'action';
                    actionInput.value = 'deleteSubcategory';
                    
                    const idInput = document.createElement('input');
                    idInput.type = 'hidden';
                    idInput.name = 'subcategoryId';
                    idInput.value = subcategoryId;
                    
                    form.appendChild(actionInput);
                    form.appendChild(idInput);
                    document.body.appendChild(form);
                    form.submit();
                }
            }
        </script>
    </body>
</html>

<script>
    function viewCategory(categoryID) {
        // Chuyển hướng đến trang materialList với tham số categoryID
        window.location.href = "${pageContext.request.contextPath}/manage-material?categoryID=" + categoryID;
    }
    
    // ... existing code ...
</script>