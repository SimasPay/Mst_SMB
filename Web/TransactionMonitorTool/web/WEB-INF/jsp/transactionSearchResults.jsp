<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="json" uri="/WEB-INF/tld/json.tld" %>
<%@ page import="java.util.*" %>

<json:object prettyPrint="true">	
	<json:property name="success" value="true" />
	<json:property name="total" value="${model.total}" />
	<json:array name="results" var="result" items="${model.results}" >
		<json:object>
			<json:property name="ID" value="${result.ID}" />
			<json:property name="transactionName" value="${result.transactionName}" />
			<json:property name="transactionAmount" value="${result.transactionAmount}" />
			<json:property name="calculatedCharge" value="${result.calculatedCharge}" />
			<json:property name="transferStatusText" value="${result.transferStatusText}" />
			<json:property name="failureReason" value="${result.failureReason}" />			
			<json:property name="transactionTime" value="${result.transactionTime}" />
			<json:property name="sourceMDN" value="${result.sourceMDN}" />			
			<json:property name="destMDN" value="${result.destMDN}" />
			<json:property name="sourcePartnerCode" value="${result.sourcePartnerCode}" />
			<json:property name="destPartnerCode" value="${result.destPartnerCode}" />			
			<json:property name="MFSBillerCode" value="${result.MFSBillerCode}" />
			<json:property name="accessMethodText" value="${result.accessMethodText}" />			
			<json:property name="serviceName" value="${result.serviceName}" />		
		</json:object>
	</json:array>
</json:object>