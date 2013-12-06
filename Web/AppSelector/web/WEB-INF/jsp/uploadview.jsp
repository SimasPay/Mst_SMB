<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<%@ page import="java.util.*" %>


<json:object prettyPrint="true">	
	<json:property name="success" value="true" />
	<json:property name="version" value="${model.version}" /> 
</json:object>