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
package gov.cdc.phlissa.hub.routing;

import gov.cdc.phlissa.hub.routing.persistence.NoResultsFoundException;
import gov.cdc.phlissa.hub.routing.persistence.PersistenceMgr;
import gov.cdc.phlissa.hub.routing.persistence.PersistenceException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(targetNamespace = "http://phlissa.cdc.gov/", serviceName = "ComponentRoutingService")  
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)

/**
 * Web service that obtains Hub component routing information based on organizational 
 * and Hl7 message header data.
 * 
 * @version $Id: //PHLISSA_HUB/ComponentRoutingService/src/gov/cdc/phlissa/hub/routing/ComponentRoutingService.java#3 $
 */
public class ComponentRoutingService
{
  static PersistenceMgr pMgr = new PersistenceMgr();
  
  @WebMethod
  @WebResult(name="routingData")
  public RoutingData getRoutingData(
                                    @WebParam(name="msgType")
                                    String msgType, 
                                    @WebParam(name="triggerEvent")
                                    String triggerEvent, 
                                    @WebParam(name="hl7Version")
                                    String hl7Version, 
                                    @WebParam(name="sendingFacility")
                                    String sendingFacility, 
                                    @WebParam(name="receivingFacility")
                                    String receivingFacility)
    throws ComponentRoutingFault
  {
    System.out.println("getRoutingData():");
    System.out.println("  msgType=" + msgType);
    System.out.println("  triggerEvent=" + triggerEvent);
    System.out.println("  hl7Version=" + hl7Version);
    System.out.println("  sendingFacility=" + sendingFacility);
    System.out.println("  receivingFacility=" + receivingFacility);
    
    RoutingData routingData = null;
    
    try
    {
      routingData = pMgr.retrieveRoutingData(msgType, triggerEvent, hl7Version, sendingFacility, receivingFacility);      
      System.out.println(routingData);
    }
    catch (NoResultsFoundException e)
    {      
      System.out.println("No results returned for parameters:\n" + e);
      throw new ComponentRoutingFault(e.getMessage() );
    }
    catch (PersistenceException e)
    {
      throw new ComponentRoutingFault(e.getMessage() );
    }
    
    return routingData;
  }  
}
