<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
 <xsl:param name="ename">ChannelId</xsl:param>
 <xsl:param name="evalue">9</xsl:param>

	<xsl:template match="*">
		<!-- TODO: Auto-generated template -->
	</xsl:template>
	<!-- standard copy template -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>	
	
	<xsl:template match="CustomerPhoneNumber"/>
	
	<xsl:template match="Location"/>
	
	<!-- template for the element removal-->
	<xsl:template match="PaymentNotificationRequest">
  		<xsl:apply-templates select="node()" />
	</xsl:template>
	
	<xsl:template match="PaymentDate">
  	</xsl:template>
	
	<xsl:template match="SettlementDate">
  	</xsl:template>
	
	<!-- template for the element removal-->
	<xsl:template match="Payments">
  		<xsl:apply-templates select="node()" />
	</xsl:template>
	
	<!-- template for the element removal-->
	<xsl:template match="BranchName">  		
	</xsl:template>
	
	<xsl:template match="BankName"/>
	
	<xsl:template match="OtherCustomerInfo"/>
	
	<xsl:template match="BankCode"/>
	
	<xsl:template match="CustomerAddress"/>
	
	<xsl:template match="DepositSlipNumber"/>
	<xsl:template match="PaymentCurrency"/>
	<xsl:template match="PaymentItems"/>
	
	<!-- Do some adjustments for the address -->
	<xsl:template match="Payment">
		<xsl:element name="CashInRequest">
			<xsl:apply-templates />
			<xsl:if test="not(channelId)">
				<xsl:element name="{$ename}"><xsl:value-of select="$evalue"/></xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="PaymentStatusRequest">
		<xsl:element name="CashInStatusRequest">
			<xsl:apply-templates />
			<xsl:if test="not(channelId)">
				<xsl:element name="{$ename}"><xsl:value-of select="$evalue"/></xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	
	<!-- Do some adjustments for the address -->
	<xsl:template match="CustReference">
		<xsl:element name="TargetMdn">
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	<!-- Do some adjustments for the address -->
	<xsl:template match="DepositorName">
		<xsl:element name="InitiatorName">
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	<!-- Do some adjustments for the address -->
	<xsl:template match="CustomerName">
		<xsl:element name="TargetFirstName">
			<xsl:apply-templates />
		</xsl:element>
		<TargetLastName></TargetLastName>
	</xsl:template>
		
	<xsl:strip-space  elements="*"/>
	
</xsl:stylesheet>