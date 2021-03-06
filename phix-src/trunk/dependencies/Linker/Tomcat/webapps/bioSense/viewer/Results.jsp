<%@ page language="java" 
	import="gov.cdc.biosense.dpit.beans.*, 
			gov.cdc.biosense.dpit.util.LinkerQuery,
			java.util.List, java.util.ArrayList" %>

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
<title>Centers for Disease Control and Prevention</title>
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

<% 		
		LinkerQuery testReport = new LinkerQuery();
		Results newReport = new Results();
		Long bioSensePatientId;
		Long bioSenseVisitId;
		Long assigningAuth;
    	
        String idType = request.getParameter("idType");
        String idValue = request.getParameter("idValue");
        String assigningAuthority = request.getParameter("AssigngAuthority");
        	
        Patient thePatient = new Patient();
        Visit theVisit;
        List visits = new ArrayList();
        String user = (String)session.getAttribute("user");
		//System.out.println ("Result page user name is :" +user+"\n");
		boolean validRequest = true;
        
        try { 
	        if (idType.equalsIgnoreCase("bioSensePatientId")) { 
	        		bioSensePatientId = new Long(idValue);
	        		newReport = testReport.getResultsByBSPID(bioSensePatientId, user);
	        } else if (idType.equalsIgnoreCase("bioSenseVisitId")) {
	        		bioSenseVisitId = new Long(idValue);
	        		newReport = testReport.getResultsByBSVID(bioSenseVisitId, user);
	        } else if (idType.equalsIgnoreCase("hospitalPatientId")) {
	        		assigningAuth = new Long(assigningAuthority);
	        		newReport = testReport.getResultsByDSPID(idValue, assigningAuth, "");
	        } else if (idType.equalsIgnoreCase("hospitalVisitId")) {
	        		assigningAuth = new Long(assigningAuthority);
	        		newReport = testReport.getResultsByDSVID(idValue, assigningAuth, "");
	        }
        } catch ( Exception e) {       	
        		validRequest = false;
        }
    		
    		thePatient = (Patient)newReport.getPatient();
    		visits = newReport.getVisits();
    		
    		if (thePatient == null)	
    			validRequest = false;

        if (validRequest) {
%>




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
    <table width="390">
      <tr>
        <td width="384">&nbsp;</td>
      </tr>
      <tr>
        <td width="384" align="center" class="Header" ><b>Patients - PHIX Reference 
        lookup.</b></td>
      </tr>
      <tr>
        <td width="384">&nbsp;</td>
      </tr>
      <tr>
        <td class="DataEmphasis" ><b>1. Patient Information:</b></td>
      </tr>
      <tr>
        <td width="384">
        <table width="216">
          <tr>
            <td width="11"></td>
            <td width="123" class="Data" ><b>First Name:</b></td>
            <td width="68" class="Data" ><%= thePatient.getFirstName() %></td>
          </tr>
          <tr>
            <td width="11"></td>
            <td width="123" class="Data" ><b>Last Name:</b></td>
            <td width="68" class="Data" ><%= thePatient.getLastName() %></td>
          </tr>
          <tr>
            <td width="11"></td>
            <td width="123" class="Data" ><b>Hospital Patient Id:</b></td>
            <td width="68">
 <%
			if (newReport.getDsMergeList().size() > 0) {
		
				for (int i = 0; i < newReport.getDsMergeList().size(); i++) {
%>
					<%=newReport.getDsMergeList().get(i).toString() %>&nbsp;merged to&nbsp;
<%
				}
			}
%> 
            
            
           <%= thePatient.getSDSPatientID() %></td>
          </tr>
          <tr>
            <td width="11"></td>
            <td width="123" class="Data" ><b>PHIX Id:</b></td>
            <td width="68" class="Data" >
       
<%
			if (newReport.getPatientMergeList().size() > 0) {
		
				for (int i = 0; i < newReport.getPatientMergeList().size(); i++) {
%>
					<%=newReport.getPatientMergeList().get(i).toString() %>&nbsp;merged to&nbsp;
<%
				}
			}
%> 
          
            <%= thePatient.getSBioSensePatientID().toString() %></td>
          </tr>
          <tr>
            <td width="11"></td>
            <td width="123" class="Data" ><b>Facility:</b></td>
            <td width="68"><%= thePatient.getSDSAssigningAuth() %></td>
          </tr>
        </table>
        </td>
      </tr>
      <tr>
        <td width="384">&nbsp;</td>
      </tr>
      </td>
      </tr>
      <tr>
        <td width="384">&nbsp;</td>
      </tr>
      <tr>
        <td class="DataEmphasis" ><b>2. Visit Information:</b></td>
      </tr>

        <td>
        <table width="341" border="1" color="1" CELLSPACING=0 CELLPADDING=0 >
          <tr>
            
            <td width="151" align="center" class="Data" ><b>PHIX Visit Id</b></td>
            <td width="143" align="center" class="Data" ><b>Hospital Visit Id</b></td>
          </tr>

<%
			for (int i = 0; i < visits.size(); i++) {
				
				theVisit = new Visit();
				theVisit = (Visit) visits.get(i);
%>

          <tr>
            
            <td width="151" class="Data" >&nbsp;&nbsp;<%= theVisit.getSBioSenseVisitID().toString() %></td>
            <td width="143" class="Data" >&nbsp;&nbsp;<%= theVisit.getSDSVisitID() %></td>
          </tr>

<%	
			}
%>	

        </table>

<%
        } else {
%>
        		<table>
        		<tr><td>&nbsp;</td></tr>
        		<tr><td>&nbsp;</td></tr>
        		<tr>
        		<td class="DataEmphasis" >&nbsp;&nbsp;&nbsp;
        		Patient or visit not found. Try again.
        		</td>
        		</tr>
<%
        }
%>  

        </td>
      </tr>
      <tr>
        <td width="384">&nbsp; </td>
      </tr>
      <tr>
        <td width="384" align="center">
        <a href="Query.jsp">Click here for another 
        query.</a></td>
      </tr>
      <tr>
        <td width="384"></td>
      </tr>
      <tr>
        <td width="384">&nbsp;</td>
      </tr>
      <tr>
        <td width="384"></td>
      </tr>
      <tr>
        <td width="384">&nbsp;</td>
      </tr>
      <tr>
        <td width="384">&nbsp;</td>
      </tr>
    </table>
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
