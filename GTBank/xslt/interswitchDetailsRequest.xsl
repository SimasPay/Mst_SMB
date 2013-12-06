<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
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
	
	<xsl:template match="CustomerInformationRequest">
		<xsl:element name="GetDetailsRequest">
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="TargetMdn">
		<xsl:element name="Mdn">
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="MerchantReference">
		<xsl:element name="InstitutionId">
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
		
	<xsl:template match="PaymentItemCategoryCode"/>
	<xsl:template match="PaymentItemCode"/>
	<xsl:template match="RequestReference"/>
	<xsl:template match="TerminalId"/>
	
	<xsl:strip-space  elements="*"/>
	
</xsl:stylesheet>