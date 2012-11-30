<%@ page language="java" 
	import="gov.cdc.biosense.dpit.util.LinkerQuery,
			gov.cdc.biosense.dpit.beans.*,
			java.util.List,
			java.util.ArrayList" %>

<%!

	public void jspInit() {

		getServletContext();

	}


%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<!-- saved from url=(0046)http://aits-cso-bio2/rtcc/template_103105.html -->
<!-- saved from url=(f0019)http://www.cdc.gov/ -->
<html>

<head>
<title>CDC - PHIX Linker.</title>
<meta http-equiv="Content-Type" content="text/html;CHARSET=iso-8859-1">
<meta content="CDC Centers for Disease Control and Prevention Home Page" name="description">
<meta content="CDC, Centers for Disease Control and Prevention" name="keywords">
<link href="Centers%20for%20Disease%20Control%20and%20Prevention_files/style.css" type="text/css" rel="stylesheet">
<style>.l {
	TEXT-ALIGN: left
}
.c {
	TEXT-ALIGN: center
}
.r {
	TEXT-ALIGN: right
}
.d {
	
}
.t {
	VERTICAL-ALIGN: top
}
.m {
	VERTICAL-ALIGN: middle
}
.b {
	VERTICAL-ALIGN: bottom
}
.border {
	BORDER-RIGHT: windowtext 0.5pt solid; BORDER-TOP: windowtext 0.5pt solid; BORDER-LEFT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid
}
.nr {
	BORDER-RIGHT: medium none
}
.nt {
	BORDER-TOP: medium none
}
.br {
	BORDER-RIGHT: windowtext 0.5pt solid
}
.bl {
	BORDER-LEFT: windowtext 0.5pt solid
}
.bt {
	BORDER-TOP: windowtext 0.5pt solid
}
.bb {
	BORDER-BOTTOM: windowtext 0.5pt solid
}
.content {
	PADDING-RIGHT: 10px; OVERFLOW-Y: auto; PADDING-LEFT: 10px; LEFT: 150px; PADDING-BOTTOM: 10px; WIDTH: 690px; PADDING-TOP: 10px; POSITION: absolute; TOP: 80px; HEIGHT: 350px
}
.tn {
	BORDER-RIGHT: windowtext 0.5pt solid; PADDING-RIGHT: 5px; BORDER-TOP: windowtext 0.5pt solid; PADDING-LEFT: 5px; PADDING-BOTTOM: 5px; MARGIN: 5px; BORDER-LEFT: windowtext 0.5pt solid; WIDTH: 120px; PADDING-TOP: 5px; BORDER-BOTTOM: windowtext 0.5pt solid; POSITION: relative; HEIGHT: 120px; TEXT-ALIGN: center; align: center
}
.bar {
	WIDTH: 300px
}
.bar2 {
	WIDTH: 110px
}
.cell {
	PADDING-RIGHT: 1px; PADDING-LEFT: 1px; WHITE-SPACE: nowrap; mso-ignore: padding
}
#color {
	BORDER-RIGHT: #000000 1px solid; BORDER-TOP: #000000 1px solid; Z-INDEX: 999; VISIBILITY: hidden; BORDER-LEFT: #000000 1px solid; WIDTH: 261px; BORDER-BOTTOM: #000000 1px solid; POSITION: absolute; HEIGHT: 135px; BACKGROUND-COLOR: #ffffff; layer-background-color: #ffffff
}
#display {
	Z-INDEX: 999; LEFT: 4px; VISIBILITY: hidden; WIDTH: 50px; POSITION: absolute; TOP: 10px; HEIGHT: 50px; BACKGROUND-COLOR: #000000; layer-background-color: #000000
}
#old {
	Z-INDEX: 999; LEFT: 4px; VISIBILITY: hidden; WIDTH: 50px; POSITION: absolute; TOP: 71px; HEIGHT: 50px; BACKGROUND-COLOR: #000000; layer-background-color: #000000
}
#colorTable {
	Z-INDEX: 999; LEFT: 60px; VISIBILITY: hidden; WIDTH: 320px; POSITION: absolute; TOP: 0px; HEIGHT: 200px
}
</style>
<link href="Centers%20for%20Disease%20Control%20and%20Prevention_files/bs.css" type="text/css" rel="stylesheet">
<!-- End Survey scripts -->
<meta content="Microsoft FrontPage 5.0" name="GENERATOR">
<!--<body onload="resizeVerticalDiv(divScroller)">-->
</head>

<body>

<table cellSpacing="0" cellPadding="0" border="0">
  <colgroup>
    <col width="140"><col width="4"><col width="700"><col width="4">
    <!-- BEGIN HEADER -->
  </colgroup>
  <tr>
    <td class="bgHdr" colSpan="3">
    <table cellSpacing="2" cellPadding="0" width="753">
      <tr>
        <td class="bgHdr" width="8">
        <img height="1" alt src="Centers%20for%20Disease%20Control%20and%20Prevention_files/s.gif" width="1" border="0"></td>
        <td class="bgHdr" width="597">
        <img border="0" src="Center1.jpg" width="781" height="50"></td>
      </tr>
    </table>
    </td>
    <td class="bgEdge" rowSpan="5">
    <img height="5" alt src="Centers%20for%20Disease%20Control%20and%20Prevention_files/s.gif" width="4" border="0"></td>
  </tr>
  <!-- END HEADER -->
  <tr>
    <td class="sidebarShadow" width="140">
    <img height="4" alt src="Centers%20for%20Disease%20Control%20and%20Prevention_files/s.gif" width="140" border="0"></td>
    <td class="hmHdrShadow" colSpan="2">
    <img height="4" alt src="Centers%20for%20Disease%20Control%20and%20Prevention_files/s.gif" width="500" border="0"></td>
  </tr>
  <tr>
    <!-- START LEFT SIDE BAR -->
    <td></td>
    <td>
    <form method="GET" action="Results.jsp">
      <table width="392">
        <tr>
          <td width="386">&nbsp;</td>
        </tr>
        <tr>
          <td width="386" align="center" class="Header"><b>Patients - PHIX Reference 
          lookup.</b></td>
        </tr>
        <tr>
          <td width="386">&nbsp;</td>
        </tr>
        <tr>
          <td width="386" class="DataEmphasis"><b>Enter the selection criteria by 
          following four steps:</b></td>
        </tr>
        <tr>
          <td width="386" class="Data">&nbsp; 1. Enter the ID values that your are 
          looking for.</td>
        </tr>
        <tr>
          <td width="386" class="Data">&nbsp; 2. Select the ID type.</td>
        </tr>
        <tr>
          <td width="386" class="Data">&nbsp; 3. Click submit.</td>
        </tr>
        <tr>
          <td width="386" class="Data">&nbsp; 4. Enter the Assigning authority (select one when query for hospital Patient or Visit id)</td>
        </tr>
        <tr>
          <td width="386">&nbsp;</td>
        </tr>
         <tr>
         <td>
          <table width="384">
            <tr>
              <td width="113" class="DataEmphasis">Enter Id value: </td>
              <td width="261"><input type="text" name="idValue" size="20"> </td>
            </tr>
          </table>
          </td>
        </tr>
        <tr>
          <td width="386">&nbsp; </td>
        </tr>
        <tr>
          <td width="386" class="DataEmphasis">Select Id Type: </td>
        </tr>
        <tr>
          <td width="386">
          <table width="245" border="1" bordercolor="gray" CELLSPACING=0 CELLPADDING=0>
            <tr>
              <td width="206" class="Data">&nbsp;&nbsp;PHIX patient id:</td>
              <td width="29">
              <input type="radio" value="bioSensePatientId" checked name="idType"></td>
            </tr>
            <tr>
              <td width="206" class="Data">&nbsp;&nbsp;PHIX visit id:</td>
              <td width="29">
              <input type="radio" value="bioSenseVisitId" name="idType"></td>
            </tr>
            <tr>
              <td width="206" class="Data">&nbsp;&nbsp;Hospital patient id:</td>
              <td width="29">
              <input type="radio" value="hospitalPatientId" name="idType"></td>
            </tr>
            <tr>
              <td width="206" class="Data">&nbsp;&nbsp;Hospital visit id:</td>
              <td width="29">
              <input type="radio" value="hospitalVisitId" name="idType"></td>
            </tr>
          </table>
          </td>
        </tr>
                <tr>
          <td width="386">
          <table width="384">
            <tr>
              <td width="113" class="DataEmphasis">Enter Assigning authority: </td>
              <td width="261">
              
<% 
				String strOption;
				String strValue;
				List aAuthorityList = new ArrayList();
				AssigningAuthority theAuthority;
				LinkerQuery testReport = new LinkerQuery();
				String user = request.getUserPrincipal().getName();
				session.setAttribute("user",user);
				//System.out.println ("Query page user name is :" +user+"\n");
				
				aAuthorityList = testReport.getAssigningAuthorities(user);
				
%>
                <select size="1" name="AssigngAuthority">
                		<option selected>Select one from the list</option>
<%
                for (int i = 0; i < aAuthorityList.size(); i++) {
                	
                		theAuthority = new AssigningAuthority();
                		theAuthority = (AssigningAuthority)aAuthorityList.get(i);
                	    strOption = theAuthority.getDsAssigningAuthority() + 
                	    				" - " +
                	    				theAuthority.getFacilityCode() +
                	    				" - " +
                	    				theAuthority.getIdentifierType();
                	    	strValue = theAuthority.getLinkerAssigningAuthority().toString();

%>
                	    	
                	    	<option value= <%= strValue %>><%= strOption %></option>
<%   	
                }
%>             
  			   </select>
 			</td>
            </tr>
          </table>
          </td>
         </tr>
        
        <tr>
          <td width="386">&nbsp;</td>
        </tr>
        <tr>
          <td width="386" align="center">
          <input type="submit" value="Submit" name="B1"> </td>
        </tr>
        <tr>
          <td width="386">&nbsp;</td>
        </tr>
        <tr>
          <td width="386">&nbsp;</td>
        </tr>
      </table>
    </form>
    </td>
  </tr>
  <!-- BEGIN FOOTER NAV -->
  <tr>
    <td bgColor="white" colSpan="3">
    <table cellSpacing="0" cellPadding="0" width="100%" border="0">
      <tr>
        <td class="ftrBar">
        <img height="1" alt src="Centers%20for%20Disease%20Control%20and%20Prevention_files/s.gif" width="760" border="0"></td>
      </tr>
      <tr>
        <td class="bgftrTop">
        <img height="3" alt src="Centers%20for%20Disease%20Control%20and%20Prevention_files/s.gif" width="1" border="0"></td>
      </tr>
      <tr>
        <td class="bgftrTop" align="left">
 
 
 
        </td>
      </tr>
      <tr>
        <td class="bgftrTop">
        <img height="3" alt src="Centers%20for%20Disease%20Control%20and%20Prevention_files/s.gif" width="1" border="0"></td>
      </tr>
      <tr>
        <td class="ftrBar">
        <img height="1" alt src="Centers%20for%20Disease%20Control%20and%20Prevention_files/s.gif" width="760" border="0"></td>
      </tr>
    </table>
    </td>
  </tr>
  <!-- END FOOTER NAV -->
  <!-- BEGIN FOOTER -->
  <tr>
    <td bgColor="#e5e5e5" colSpan="3">
    <table cellSpacing="0" cellPadding="0" width="760" border="0">
      <tr>
        <td class="bgftrBottom" vAlign="top">
        <table cellSpacing="0" cellPadding="0" border="0">
          <tr>
            <td class="bgftrBottom">
            <img height="1" alt src="Centers%20for%20Disease%20Control%20and%20Prevention_files/s.gif" width="12" border="0"></td>
            <td class="bgftrBottom"><br>
            <img height="5" alt src="Centers%20for%20Disease%20Control%20and%20Prevention_files/s.gif" width="1" border="0"><br>
            <span class="txtftrBottom">Centers for Disease Control and Prevention, 
            1600 Clifton Rd, Atlanta, GA 30333, U.S.A<br>
            Public Inquiries: 1-800-532-9929  / Option 7</span></td>
            <td class="bgftrBottom">
            <img height="1" alt src="Centers%20for%20Disease%20Control%20and%20Prevention_files/s.gif" width="20" border="0"></td>
            <td class="bgftrBottom" vAlign="center" align="right">
            <a href="http://firstgov.gov/">
            <img height="52" alt="FirstGov" src="Centers%20for%20Disease%20Control%20and%20Prevention_files/logo_FirstGov.gif" width="147" border="0"></a><a href="http://www.hhs.gov/"><img height="52" alt="DHHS" src="Centers%20for%20Disease%20Control%20and%20Prevention_files/logo_dhhs.gif" width="58" border="0"></a></td>
            <td class="bgftrBottom">
            <a class="txtFtr" href="http://www.hhs.gov/">Department of Health<br>
            and Human Services</a></td>
          </tr>
        </table>
        </td>
      </tr>
    </table>
    </td>
  </tr>
  <!-- END FOOTER -->
</table>

</body>

</html>
