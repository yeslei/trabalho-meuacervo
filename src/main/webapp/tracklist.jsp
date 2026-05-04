<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:choose>
    <c:when test="${empty tracklist}">
        <div class="track-empty">Faixas indisponiveis no momento.</div>
    </c:when>
    <c:otherwise>
        <ol class="track-items">
            <c:forEach var="track" items="${tracklist}">
                <li><c:out value="${track}"/></li>
            </c:forEach>
        </ol>
    </c:otherwise>
</c:choose>
