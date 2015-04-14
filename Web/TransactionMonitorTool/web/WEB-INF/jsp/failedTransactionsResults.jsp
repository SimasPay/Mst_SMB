<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="json" uri="/WEB-INF/tld/json.tld" %>
<%@ page import="java.util.*" %>

<json:object prettyPrint="true">	
	<json:property name="success" value="true" />
		<json:array name="results" var="result" items="${model.results}" >
		<json:object>
			<json:property name="mobileNumber" value="${result.mobileNumber}" />
			<json:property name="refID" value="${result.refID}" />
			<json:property name="amount" value="${result.amount}" />
			<json:property name="transactionType" value="${result.transactionType}" />
			<json:property name="channelName" value="${result.channelName}" />
			<json:property name="txnDateTime" value="${result.txnDateTime}" />
			<json:property name="rcCode" value="${result.rcCode}" />
			<json:property name="reason" value="${result.reason}" />	
		</json:object>
	</json:array>
</json:object>