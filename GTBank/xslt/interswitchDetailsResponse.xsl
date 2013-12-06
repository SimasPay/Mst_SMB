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
	
	<xsl:template match="GetDetailsResponse">
		<xsl:element name="CustomerInformationResponse">
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="InstitutionId">
		<xsl:element name="MerchantReference">
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="Mdn">
		<xsl:element name="CustReference">
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="AlternateMdn">
		<xsl:element name="CustomerReferenceAlternate">
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="CustomerDescription">
		<xsl:element name="CustomerReferenceDescription">
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="Dob"/>
	
	<xsl:strip-space  elements="*"/>
	
</xsl:stylesheet>