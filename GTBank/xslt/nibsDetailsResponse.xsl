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
	
	<xsl:template name="strip-tags">
    <xsl:param name="text"/>
    <xsl:choose>
        <xsl:when test="contains($text, '&lt;')">
            <xsl:value-of select="substring-before($text, '&lt;')"/>
            <xsl:call-template name="strip-tags">
                    <xsl:with-param name="text" select="substring-after($text, '&gt;')"/>
            </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
            <xsl:value-of select="$text"/>
        </xsl:otherwise>
    </xsl:choose>
	</xsl:template>
	
	<xsl:template match="Customers">
    	<xsl:apply-templates/>
  	</xsl:template>

	 <xsl:template match="Customer">
    	<xsl:apply-templates/>
  	</xsl:template>
	
	<xsl:template match="AlternateMdn"/>
	<xsl:template match="CustomerDescription"/>
	<xsl:template match="OtherName"/>
	<xsl:template match="Email"/>
	<xsl:template match="Phone"/>
	<xsl:template match="ThirdPartyCode"/>
	
	<xsl:template match="PaymentItems"/>
  	<xsl:template match="Merchandize"/>
	<xsl:template match="Amount"/>	
	
	<xsl:strip-space  elements="*"/>
</xsl:stylesheet>