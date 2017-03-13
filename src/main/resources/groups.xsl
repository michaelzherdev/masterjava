<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" />

    <xsl:template match="/" >
        <xsl:param name="projectName" />

        <html>
            <head>
            </head>
            <body>
                <table border="1">
                    <tr>
                        <th>Name</th>
                        <th>Status</th>

                    </tr>
                    <xsl:for-each select="/*[name()='Payload']/*[name()='Projects']/*[name()='Project'][@name=$projectName]/*[name()='Groups']/*[name()='Group']">
                        <tr>
                            <td><xsl:value-of select="@name"/></td>
                            <td><xsl:value-of select="@status"/></td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>