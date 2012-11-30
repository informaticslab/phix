/*
   Copyright 2011  U.S. Centers for Disease Control and Prevention

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package gov.cdc.phix.connector;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import gov.cdc.phix.connector.mirthclient.PHIXClient;

@WebService(targetNamespace = "http://phix.cdc.gov/", serviceName = "PHIXConnectorService")  
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)

/**
 * Web service providing synchronous access to two PHIXs.  Client invokes postMessage(), providing HL7 message.
 * The HL7 message is sent to PHIX1.  The transformed result message is received from PHIX2 via postResult(), 
 * then returned to the client in the web service response to postMessage().
 * 
 * Only one message can be processed at a time.  If subsequent calls to postMessage() are made while the first 
 * call is awaiting results, those threads will block until the response has been returned.
 * 
 * @version $Id: //PHLISSA_HUB/PHIXConnectorService/src/gov/cdc/phix/connector/PHIXConnectorService.java#3 $
 */
public class PHIXConnectorService
{
  private static long timeoutSec = 0; // Timeout waiting on PHIX2 response.  Set in props file.
  
  private static final Lock LOCK = new ReentrantLock();
  
  private static final Condition RESULT_READY = LOCK.newCondition();
  private static final Condition SERVICE_MESSAGE_READY = LOCK.newCondition();
  
  private static String result = null;
  private static String errors = null;
  private static boolean servicingMsg = false;
  
  
  /**
   * Constructor.  Read properties file 
   */
  public PHIXConnectorService()
  {
    if (timeoutSec == 0)
    {
      try
      {         
        Properties props = new Properties();                
        props.load(getClass().getResourceAsStream("connector.properties"));
        timeoutSec = Long.parseLong(props.getProperty("timeout_sec"));               
      }
      catch (IOException e)
      {
        System.err.println(e);
      }
    }
    
    System.out.println("PHIX2 RESPONSE TIMEOUT SET TO: " + timeoutSec + " sec.");
  }
  
  
  @WebMethod
  @WebResult(name="results")
  public Results postMessage(
                            @WebParam(name="hl7Message")
                            String hl7Message)
    throws PHIXConnectorFault
  { 
    Results results = new Results();
    
    try
    {
      LOCK.lock();
      
      // Can only service one incoming message at a time. Make this service thread block 
      // if another is currently waiting for a result.
      if (servicingMsg)
        SERVICE_MESSAGE_READY.await();
            
      servicingMsg = true;
      
      try
      {
        PHIXClient phix = PHIXClient.getInstance();
        phix.deliverMessage(hl7Message);
      }
      catch(Exception e)
      {      
        String error = "PHIX1 web service source connector not reachable:\n\n" + e.getMessage(); 
        System.err.println(error);
        throw new PHIXConnectorFault(error);
      }
      
      // wait for the result to be ready, or until timeout occurs
      if (! RESULT_READY.await(timeoutSec, TimeUnit.SECONDS) )
        throw new PHIXConnectorFault("Did not receive results from PHIX2 after " + timeoutSec + " sec.  Giving up.");
        
      results.setTransformedMessage(result);
      results.setErrors(errors);
      
      result = null;
      errors = null;
    }
    catch (InterruptedException e)
    {
      System.err.println(e.getMessage() );     
    }
    finally
    {
      servicingMsg = false;
      SERVICE_MESSAGE_READY.signal(); // wake up another postMessage() service thread, if any
      LOCK.unlock();
    }
    
    return results;
  }
  
  
  @WebMethod
  @WebResult(name="status")
  public String postResult(    
                            @WebParam(name="hl7Result")
                            String hl7Result,
                            @WebParam(name="errors")
                            String errors)
    throws PHIXConnectorFault
  {
    try
    {      
      LOCK.lock();
      
      if (! servicingMsg)
        throw new PHIXConnectorFault("Not currently awaiting results!");
      
      result = hl7Result;
      PHIXConnectorService.errors = errors;      
    }
    finally
    {
      RESULT_READY.signal();
      LOCK.unlock();
    }
    
    return "Message result successfully received";
  }  
  
}
