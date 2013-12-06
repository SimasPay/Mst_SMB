<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="json" uri="/WEB-INF/tld/json.tld" %>
<%@ page import="java.util.*" %>

<json:object prettyPrint="true">	
	<json:property name="success" value="true" />
	<json:property name="total" value="${model.total}" />
	<json:array name="results" var="result" items="${model.results}" >
		<json:object>
			<json:property name="ID" value="${result.ID}" />
			<json:property name="serviceChargeTransactionLogID" value="${result.serviceChargeTransactionLogID}" />
			<json:property name="transactionID" value="${result.transactionID}" />
			<json:property name="transactionTime" value="${result.transactionTime}" />
			<json:property name="transactionName" value="${result.transactionName}" />
			<json:property name="sourceMDN" value="${result.sourceMDN}" />			
			<json:property name="destMDN" value="${result.destMDN}" />
			<json:property name="sourcePocketID" value="${result.sourcePocketID}" />			
			<json:property name="destPocketID" value="${result.destPocketID}" />
			<json:property name="creditAmount" value="${result.creditAmount}" />
			<json:property name="debitAmount" value="${result.debitAmount}" />			
			<json:property name="sourcePocketBalance" value="${result.sourcePocketBalance}" />
			<json:property name="destPocketBalance" value="${result.destPocketBalance}" />			
			<json:property name="sourcePocketClosingBalance" value="${result.sourcePocketClosingBalance}" />
			<json:property name="destPocketClosingBalance" value="${result.destPocketClosingBalance}" />
			<json:property name="transferStatusText" value="${result.transferStatusText}" />
			<json:property name="commodityText" value="${result.commodityText}" />
			<json:property name="accessMethodText" value="${result.accessMethodText}" />
		</json:object>
	</json:array>
</json:object>