<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${isValid}">
    <result>
        <status>valid</status>
        <materialId>${material.materialID}</materialId>
        <supplierName>${material.supplierID.supplierName}</supplierName>
        <unit>${unit}</unit>
    </result>
</c:if>

<c:if test="${not isValid}">
    <result>
        <status>invalid</status>
        <message>${errorMessage}</message>
    </result>
</c:if>