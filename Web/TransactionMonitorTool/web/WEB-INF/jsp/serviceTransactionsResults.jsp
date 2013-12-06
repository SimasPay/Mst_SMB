<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="json" uri="/WEB-INF/tld/json.tld" %>
<%@ page import="java.util.*" %>

<json:object prettyPrint="true">	
	<json:property name="success" value="true" />
	<json:array name="results" var="result" items="${model.results}" >
		<json:object>
			<json:property name="serviceName" value="${result.service.serviceName}" />
			<json:property name="serviceID" value="${result.service.ID}" />
			<json:property name="count" value="${result.count}" />
			<json:property name="successful" value="${result.successful}" />
			<json:property name="pending" value="${result.pending}" />
			<json:property name="failed" value="${result.failed}" />	
			<json:property name="processing" value="${result.processing}" />
			<json:property name="reversals" value="${result.reversals}" />
			<json:property name="intermediate" value="${result.intermediate}" />
		</json:object>
	</json:array>	
</json:object>