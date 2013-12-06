<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

 <xsl:param name="ename">ChannelId</xsl:param>
 <xsl:param name="evalue">11</xsl:param>

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
	
	<xsl:strip-space  elements="*"/>
	<xsl:template match="CashInRequest">
		<xsl:element name="CashInRequest">
			<xsl:apply-templates  select="node()"/>
			<xsl:element name="{$ename}"><xsl:value-of select="$evalue"/></xsl:element>
		</xsl:element>
	</xsl:template>
	<!-- 
	<xsl:template match="PaymentNotificationRequest/Payments">
  		<xsl:apply-templates select="node()" />
	</xsl:template>
	 -->
</xsl:stylesheet>