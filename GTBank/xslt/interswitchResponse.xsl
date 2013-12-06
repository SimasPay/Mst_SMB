<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- standard copy template -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*" />
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="CashInResponse">
		<PaymentNotificationResponse>
			<Payments>
				<Payment>
					<xsl:apply-templates select="@*" />
					<xsl:apply-templates />
				</Payment>
			</Payments>
		</PaymentNotificationResponse>
	</xsl:template>


	<xsl:template match="CashInStatusResponse">
		<xsl:element name="PaymentStatusResponse">
			<xsl:apply-templates select="@*" />
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<!-- Do some adjustments for the address -->
	<xsl:template match="ResponseCode">
		<xsl:choose>
			<xsl:when test="/CashInResponse/ResponseCode='100'">
				<xsl:element name="Status">
					<xsl:value-of select='0' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/CashInResponse/ResponseCode='101'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/CashInResponse/ResponseCode='102'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/CashInResponse/ResponseCode='103'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/CashInResponse/ResponseCode='104'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/CashInResponse/ResponseCode='105'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/CashInResponse/ResponseCode='106'">
				<xsl:element name="Status">
					<xsl:value-of select="1" />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/CashInResponse/ResponseCode='107'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/CashInResponse/ResponseCode='108'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/CashInResponse/ResponseCode='109'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/CashInResponse/ResponseCode='110'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/CashInResponse/ResponseCode='111'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/CashInResponse/ResponseCode='112'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			
			<xsl:when test="/GetDetailsResponse/Customers/Customer/ResponseCode='100'">
				<xsl:element name="Status">
					<xsl:value-of select='0' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/GetDetailsResponse/Customers/Customer/ResponseCode='101'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/GetDetailsResponse/Customers/Customer/ResponseCode='102'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/GetDetailsResponse/Customers/Customer/ResponseCode='103'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/GetDetailsResponse/Customers/Customer/ResponseCode='104'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/GetDetailsResponse/Customers/Customer/ResponseCode='105'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/GetDetailsResponse/Customers/Customer/ResponseCode='106'">
				<xsl:element name="Status">
					<xsl:value-of select="1" />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/GetDetailsResponse/Customers/Customer/ResponseCode='107'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/GetDetailsResponse/Customers/Customer/ResponseCode='108'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/GetDetailsResponse/Customers/Customer/ResponseCode='109'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/GetDetailsResponse/Customers/Customer/ResponseCode='110'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/GetDetailsResponse/Customers/Customer/ResponseCode='111'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/GetDetailsResponse/Customers/Customer/ResponseCode='112'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			
			<xsl:when test="/GetDetailsResponse/ResponseCode='100'">
				<xsl:element name="Status">
					<xsl:value-of select='0' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/GetDetailsResponse/ResponseCode='101'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/GetDetailsResponse/ResponseCode='102'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/GetDetailsResponse/ResponseCode='103'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/GetDetailsResponse/ResponseCode='104'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/GetDetailsResponse/ResponseCode='105'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/GetDetailsResponse/ResponseCode='106'">
				<xsl:element name="Status">
					<xsl:value-of select="1" />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/GetDetailsResponse/ResponseCode='107'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/GetDetailsResponse/ResponseCode='108'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/GetDetailsResponse/ResponseCode='109'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/GetDetailsResponse/ResponseCode='110'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/GetDetailsResponse/ResponseCode='111'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
			<xsl:when test="/GetDetailsResponse/ResponseCode='112'">
				<xsl:element name="Status">
					<xsl:value-of select='1' />
				</xsl:element>
			</xsl:when>
		</xsl:choose>

		<xsl:apply-templates select="Status" />

	</xsl:template>



	<xsl:template match="ReferenceNumber" />
	<xsl:strip-space elements="*" />
</xsl:stylesheet>