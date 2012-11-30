package gov.cdc.phix.xdr;

import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.log4j.Logger;

import org.openhealthtools.ihe.atna.auditor.XDSSourceAuditor;
import org.openhealthtools.ihe.common.hl7v2.CX;
import org.openhealthtools.ihe.common.hl7v2.Hl7v2Factory;
import org.openhealthtools.ihe.common.hl7v2.format.Config;
import org.openhealthtools.ihe.utils.OID;
import org.openhealthtools.ihe.xds.document.DocumentDescriptor;
import org.openhealthtools.ihe.xds.document.XDSDocument;
import org.openhealthtools.ihe.xds.document.XDSDocumentFromFile;
import org.openhealthtools.ihe.xds.response.XDSResponseType;
//import org.openhealthtools.ihe.xds.response.XDSStatusType;
import org.openhealthtools.ihe.xds.source.B_Source;
import org.openhealthtools.ihe.xds.source.SubmitTransactionData;

public class XDRSender
{
  public static final String XDR_RECEIVER = "https://phdemo2.atlasdev.com/AtlasConnect/XdsService.svc"; // ATLAS- EXTERNAL
  public static final String POLLING_DIRECTORY="resources/OUTBOUND_XDR";
  public static final long POLLING_INTERVAL_SEC = 10L;
  
  private static final Logger logger = Logger.getLogger(XDRSender.class);
  private B_Source source = null;  

  
  /**
   * Poll for new XML documents.  If found, send via XDR push to XDR_RECEIVER.
   * 
   * @param args
   * @throws InterruptedException
   */
  public static void main(String[] args) throws InterruptedException
  {
    XDRSender xdr = new XDRSender();
    XMLFilenameFilter xmlFilter = new XMLFilenameFilter();
    File dir = new File(POLLING_DIRECTORY);

    for(;;)
    {
      logger.debug("Checking directory for XML files: " + POLLING_DIRECTORY);
      
      File[] contents = dir.listFiles(xmlFilter);
      
      for (int i=0; i<contents.length; i++)
      {
        String filename = contents[i].getName();
        
        logger.debug("Discovered XML file: " + filename );
        
        try
        {
          xdr.send(filename);
        }
        catch(Throwable t)
        {
          System.out.println(t);
        }
        
        File renamedFile = new File(POLLING_DIRECTORY + "\\" + filename + ".PROCESSED");
        contents[i].renameTo(renamedFile);
      }      
      
      Thread.sleep(POLLING_INTERVAL_SEC * 1000L);
    }
  }
  
  public void init() throws Exception 
  {
    File conf = new File("resources/conf/submitTest_log4j.xml");
    org.apache.log4j.xml.DOMConfigurator.configure(conf.getAbsolutePath());    
  }
  
  public void send(String filename) throws Throwable 
  {
    logger.debug("***************** SUBMIT DOCUMENT TEST ************************");
    logger.debug(formTimestamp());
    
    // temp 2/20/12
    System.setProperty("javax.net.ssl.keyStore","resources/keystores/keystore");
    System.setProperty("javax.net.ssl.keyStorePassword","changeit");
    System.setProperty("javax.net.ssl.trustStore","resources/keystores/NAConn_2012_truststore.jks");
    System.setProperty("javax.net.ssl.trustStorePassword","changeit");
    // temp
    
    System.setProperty("javax.net.debug", "all");     
    
    java.net.URI repositoryURI = null;
    
    try 
    {
      repositoryURI = new java.net.URI(XDR_RECEIVER);
    } 
    catch (URISyntaxException e) 
    {
      logger.fatal("SOURCE URI CANNOT BE SET: \n" + e.getMessage());
      throw e;
    }
    source = new B_Source(repositoryURI);
    
    Config.start(true);
    
    XDSSourceAuditor.getAuditor().getConfig().setAuditorEnabled(false);
    
    SubmitTransactionData txnData = new SubmitTransactionData();
    // invoke transformation for metadata extraction on test file
    logger.debug("Adding input document, and metadata.");
    
    XDSDocument clinicalDocument = new XDSDocumentFromFile(DocumentDescriptor.CDA_R2, POLLING_DIRECTORY + "\\" + filename);
    
    File docEntryFile = new File ("resources/files/docEntry.xml");

    FileInputStream fis = new FileInputStream(docEntryFile);
    String docEntryUUID = txnData.loadDocumentWithMetadata(clinicalDocument, fis);
    fis.close();
    //txnData.dumpMetadataToFile("C:/temp/metadata1.xml");
    
    // TEMP: override uniqueID
    txnData.getDocumentEntry(docEntryUUID).setUniqueId(OID.createOIDGivenRoot("1.3.6.1.4.1.21367.13.60.80"));
    logger.debug("Done setting documentEntry metadata for: " + txnData.getDocumentEntry(docEntryUUID).toString());
    //txnData.dumpMetadataToFile("C:/temp/metadata2.xml");
    
    // re-set document entry patient ID (for LSWIN10)
    CX patientId = Hl7v2Factory.eINSTANCE.createCX();
    patientId.setIdNumber("136141213671320");
    patientId.setAssigningAuthorityName("");
    patientId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.13.20.2000");
    patientId.setAssigningAuthorityUniversalIdType("ISO");
    txnData.getDocumentEntry(docEntryUUID).setPatientId(patientId);
    txnData.getDocumentEntry(docEntryUUID).setMimeType("text/plain");
    //txnData.getDocumentEntry(docEntryUUID).setMimeType("text/xml");
    
    // add submission set metadata
    logger.debug("Applying Submission Set Metadata to the Submission.");
    File submissionSetFile = new File("resources/files/submissionSet.xml");    
    
    fis = new FileInputStream(submissionSetFile);
    txnData.loadSubmissionSet(fis);
    fis.close();
    //txnData.dumpMetadataToFile("C:/temp/metadata3.xml");
    
    // set uniqueID
    txnData.getSubmissionSet().setUniqueId(OID.createOIDGivenRoot("1.3.6.1.4.1.21367.13.60.80"));
    txnData.getSubmissionSet().setSubmissionTime(formDTM());
    //txnData.getSubmissionSet().setSourceId("2.16.840.1.113883.3.391.2.4");
    txnData.getSubmissionSet().setSourceId("1.3.6.1.4.1.21367.13.60.80"); // PHIX    
    
    logger.debug("Submitting Document: " + filename);
    
    //XDSResponseType response = source.submit(true, txnData); // async=true
    XDSResponseType response = source.submit(false, txnData);  // async=false            
        
    logger.debug("Response code: " + response.getStatus().getName());
    logger.debug("Done.");    
    source = null;
  }
  
  /*Forms a time stamp for logging of the form YYYY/MM/DD hh:mm:ss using current system time*/
  private static String formTimestamp(){
    long time = System.currentTimeMillis();
    GregorianCalendar c = new GregorianCalendar();
    c.setTimeInMillis(time);
    StringBuffer timeStamp = new StringBuffer();
    timeStamp.append(c.get(GregorianCalendar.YEAR));
    timeStamp.append("/");
    timeStamp.append(c.get(GregorianCalendar.MONTH) + 1);
    timeStamp.append("/");
    timeStamp.append( c.get(GregorianCalendar.DAY_OF_MONTH));
    timeStamp.append(" ");
    timeStamp.append( c.get(GregorianCalendar.HOUR_OF_DAY));
    timeStamp.append(":");
    timeStamp.append(c.get(GregorianCalendar.MINUTE));
    timeStamp.append(":");
    timeStamp.append(c.get(GregorianCalendar.SECOND));
    return timeStamp.toString();
  }  
  
  /**
   * Forms a HL7 v2.5 DTM time stamp for logging of the form YYYYMMDDHHMMSS
   *  using current system time*/
  private static String formDTM()
  { 
    DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
    String ret = df.format(new Date());
    return ret;
  }


}
