<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:variable name="page" select="/"/>

<xsl:template match="/">
    <xsl:apply-templates/>
</xsl:template>

<xsl:template match="darkContinent">
    <xsl:apply-templates/>
</xsl:template>

<xsl:template match="noaccess">
<html lang="en">
<head>
<link rel="SHORTCUT ICON" href="/dc/favicon.ico" />
<link rel="stylesheet" href="/dc/bwana.css" type="text/css" media="screen" />
<script type="text/javascript" language="javascript" src="/dc/bwana.js"></script>
<title>Linus Sphinx's Dark Continent</title>
<meta charset="utf-8"/>
</head>
<body>
<table width="100%" height="100%">
<tr><td align="center" valign="middle">
<h1><xsl:value-of select="."/></h1>
<p><i>Passwordus Incorrectum</i></p>
</td></tr>
</table>
</body>
</html>
</xsl:template>

<xsl:template match="frame">
<html lang="en">
<head>
<link rel="SHORTCUT ICON" href="/dc/favicon.ico" />
<link rel="stylesheet" href="/dc/bwana.css" type="text/css" media="screen" />
<script type="text/javascript" language="javascript" src="/dc/bwana.js"></script>
<title>Linus Sphinx's Dark Continent</title>
<meta charset="utf-8"/>
</head>
<body>
<div class="lsideframe" id="lsideframe">
<div class="logo" id="logo"></div>
<div class="map" id="map"></div>
<div class="compass" id="compass">
<map name="compassmap">
<area shape="rect" coords="68,1,87,52" onFocus="blur()" href="javascript:north()"/>
<area shape="rect" coords="68,68,88,125" onFocus="blur()" href="javascript:south()"/>
<area shape="rect" coords="10,52,68,68" onFocus="blur()" href="javascript:west()"/>
<area shape="rect" coords="87,52,140,68" onFocus="blur()" href="javascript:east()"/>
<area shape="rect" coords="68,52,87,68" onFocus="blur()" href="javascript:jump()"/>
<area shape="default" nohref=""/>
</map>
<div style="text-align: center;"><img src="resources/compass9.jpg" border="0" usemap="#compassmap"/></div>
<div>
<span style="text-align: left;"><a href="bwana?cmd=jmp&amp;amt=x" onmouseover="xmlb.src='resources/xml2.jpg';" onmouseout="xmlb.src='resources/xml1.jpg';"><img name="xmlb" src="resources/xml1.jpg" border="0"/></a></span>
<span class="stats" id="stats">Standby</span>
</div>
</div>

</div>
<div class="rsideframe" id="rsideframe">

<form method="" action="" onsubmit="return docmd()">
<div class="guy" id="guy"></div>

<div class="bull" id="bull">
<div class="topbar"></div>
<div class="topleft"></div>
<div class="topright"></div>
<div class="content" id="content"></div>
<div class="lefthand"></div>
<div class="righthand"></div>
<div class="bottomleft"></div>
<div class="bottomright"></div>
</div>

<div class="tfirma" id="tfirma"></div>

</form>
</div>
</body>
</html>
</xsl:template>

<xsl:template match="map">
<map>
<xsl:apply-templates select="content"/>
<div style="text-align: center;">
<xsl:text>There is </xsl:text>
<xsl:value-of select="terra"/>
<xsl:text> here</xsl:text>
</div>
</map>
</xsl:template>

<xsl:template match="bwana">
<bwana>
<xsl:apply-templates select="sack"/>
<span class="guyright" id="guyright">
<div><span align="left" colspan="3"><xsl:value-of select="handle"/></span></div>
<div><span>Health:</span><span><xsl:apply-templates select="health"/></span></div>
<div><span>Skills:</span><span><xsl:apply-templates select="skills"/></span></div>
<div><span>Hunger:</span><span><img border="0" height="9" src="resources/yellowpx.jpg" width="{hunger}"/></span></div>
<div><span>Thirst:</span><span><img border="0" height="9" src="resources/greenpx.jpg" width="{thirst}"/></span></div>
<div><span>Drinks:</span><span><img border="0" height="9" src="resources/bluepx.jpg" width="{drinks}"/></span></div>
<div><span>Savy:</span><span><img border="0" height="9" src="resources/orangepx.jpg" width="{savy}"/></span></div>
<div class="setscript">
<span>Script:</span>
<span><input name="parm" id="parm"  style="font-size: 12px" type="text" size="20" maxlength="20" value="{script}" /></span>
<span class="setbutt"><input border="0" type="image" onmouseover="src='resources/set2.jpg';use(this);" onmouseout="src='resources/set1.jpg';" src="resources/set1.jpg" name="set" accessKey="s" /></span>
</div>
</span>
</bwana>
</xsl:template>

<xsl:template match="sack">
<div>In your sack you have:</div>
<span class="guyleft" id="guyleft">

<span class="guyleftsack" id="guyleftsack">
<xsl:for-each select="item">
<xsl:if test="not(position() mod 2 = 0)">
<div>
<xsl:if test="position() = 1">
<span><input checked="checked" type="radio" name="g1" value="{@code}"/></span><span class="sitem"><xsl:value-of select="."  /></span>
</xsl:if>
<xsl:if test="not(position() = 1)">
<span><input type="radio" name="g1" value="{@code}"/></span><span class="sitem"><xsl:value-of select="."  /></span>
</xsl:if>
</div>
</xsl:if>
</xsl:for-each>
&#160;
</span>

<span class="guyrightsack" id ="guyrightsack">
<xsl:for-each select="item">
<xsl:if test="position() mod 2 = 0">
<div><span><input type="radio" name="g1" value="{@code}"/></span><span class="sitem"><xsl:value-of select="." /></span></div>
</xsl:if>
</xsl:for-each>
&#160;
</span>

<div class="usebutt">
<xsl:if test="count(item)">
<span><input border="0" type="image" onmouseover="src='resources/use2.jpg';use(this);" onmouseout="src='resources/use1.jpg';" src="resources/use1.jpg" name="use" accessKey="u"/></span>
<span><input border="0" type="image" onmouseover="src='resources/drop2.jpg';use(this);" onmouseout="src='resources/drop1.jpg';" src="resources/drop1.jpg" name="put" accessKey="d" /></span>
</xsl:if>
<span><input id="cmd" type="hidden" name="cmd" value="fuk"/></span>
</div>

</span>
</xsl:template>

<xsl:template match="content">
<xsl:for-each select="r">
<div>
<xsl:for-each select="c">
<span><img src="resources/{.}.jpg" border="0"/></span>
</xsl:for-each>
</div>
</xsl:for-each>
</xsl:template>


<xsl:template match="bull">
<bull>
<xsl:for-each select="action">
<p>
<xsl:value-of select="."/>
</p>
</xsl:for-each>
</bull>
</xsl:template>

<xsl:template match="ground">
<div>Scattered on the ground you find:</div>
<div>
<span class="tleft" id="tfleft">

<span class="gleft" id="gleft">
<xsl:for-each select="item">
<xsl:if test="position() &lt; 4">
<xsl:if test="position() = 1">
<div><span><input type="radio" checked="checked" name="g1" value="{@code}" /></span><span class="sitem"><xsl:value-of select="."/></span></div>
</xsl:if>
<xsl:if test="not(position() = 1)">
<div><span><input type="radio" name="g1" value="{@code}" /></span><span class="sitem"><xsl:value-of select="."/></span></div>
</xsl:if>
</xsl:if>
</xsl:for-each>
&#160;
</span>

<span class="gcenter" id="gcenter">
<xsl:for-each select="item">
<xsl:if test="position() &gt; 3 and position() &lt; 7">
<div><span><input type="radio" name="g1" value="{@code}" /></span><span class="sitem"><xsl:value-of select="."/></span></div>
</xsl:if>
</xsl:for-each>
&#160;
</span>

<span class="gright" id="gright">
<xsl:for-each select="item">
<xsl:if test="position() &gt; 6">
<div><span><input type="radio" name="g1" value="{@code}" /></span><span class="sitem"><xsl:value-of select="."/></span></div>
</xsl:if>
</xsl:for-each>
&#160;
</span>

</span>
<xsl:apply-templates select="others"/>

</div>

<div class="gbutt">
<xsl:if test="count(item)">
<span><input border="0" type="image" onmouseover="src='resources/guse2.jpg';use(this);" onmouseout="src='resources/guse1.jpg';" src="resources/guse1.jpg" name="use" /></span>
<span><input border="0" type="image" onmouseover="src='resources/grab2.jpg';use(this);" onmouseout="src='resources/grab1.jpg';" src="resources/grab1.jpg" name="get" /></span>
</xsl:if>
<xsl:if test="count(treedown)">
<span><input border="0" type="image" onmouseover="src='resources/up2.jpg';use(this);" onmouseout="src='resources/up1.jpg';" src="resources/up1.jpg" name="clm" /></span>
</xsl:if>
<xsl:if test="count(treeup)">
<span><input border="0" type="image" onmouseover="src='resources/down2.jpg';use(this);" onmouseout="src='resources/down1.jpg';" src="resources/down1.jpg" name="clm" /></span>
</xsl:if>
&#160;
</div>

</xsl:template>

<xsl:template match="others">
<span class="tfright" id="tfright">
<div class="hollowlog"><input border="0" type="image" onmouseover="src='resources/log2.jpg';use(this);" onmouseout="src='resources/log1.jpg';" src="resources/log1.jpg" name="log"/></div>
<div>Bwanas here:</div>
<xsl:if test="count(other)">
<div>
<select name="others" id="others" size="4" selcolor="#296e2b" style="width: 140px;">
<xsl:for-each select="other">
<xsl:if test="position() = 1">
<option value="{.}" selected="selected"><xsl:value-of select="."/></option>
</xsl:if>
<xsl:if test="not(position() = 1)">
<option value="{.}"><xsl:value-of select="."/></option>
</xsl:if>
</xsl:for-each>
</select>
</div>
<div style="text-align: center;">
<span><input border="0" type="image" onmouseover="src='resources/attack2.jpg';use(this);" onmouseout="src='resources/attack1.jpg';" src="resources/attack1.jpg" name="kil" /></span>
<span><input border="0" type="image" onmouseover="src='resources/steal2.jpg';use(this);" onmouseout="src='resources/steal1.jpg';" src="resources/steal1.jpg" name="stl" /></span>
</div>
</xsl:if>
</span>
<span class="grighthand"></span>
<span class="gtopright"></span>
</xsl:template>

<!-- Identity transformation -->
<xsl:template match="@*|*">
    <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
</xsl:template>

</xsl:stylesheet>
