<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="json" uri="/WEB-INF/tld/json.tld" %>
<%@ page import="java.util.*" %>

<json:object prettyPrint="true">	
	<json:property name="success" value="true" />
	<json:array name="results" var="result" items="${model.results}" >
		<json:object>	
			<json:property name="rcCode" value="${result.rcCode}" />		
			<json:property name="count" value="${result.count}" />
		</json:object>
	</json:array>
</json:object>
