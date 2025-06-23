<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${not empty errorMessage}">
    <div class="search-result-item error">${errorMessage}</div>
</c:if>

<c:if test="${empty materials}">
    <div class="search-result-item">Không tìm thấy vật tư phù hợp</div>
</c:if>

<c:forEach var="material" items="${materials}">
    <div class="search-result-item" onclick="selectMaterial(
        ${material.materialID}, 
        '${material.materialName}', 
        ${material.category.categoryID}, 
        '${material.supplierID.supplierName}', 
        'Cái')">
        ${material.materialName} - ${material.category.categoryName}
    </div>
</c:forEach>