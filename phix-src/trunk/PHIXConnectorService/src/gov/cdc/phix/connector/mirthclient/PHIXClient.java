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
package gov.cdc.phix.connector.mirthclient;

/**
 * Creates a proxy to the web service source connector running on PHIX1.
 * 
 * @version $Id: //PHLISSA_HUB/PHIXConnectorService/src/gov/cdc/phix/connector/mirthclient/PHIXClient.java#2 $
 */
public class PHIXClient
{
  private static PHIXClient instance = null;
  
  private DefaultAcceptMessageService svc;
  private DefaultAcceptMessage port;
  
  /**
   * Obtain singleton instance of PHIXClient
   * 
   * @return PHIXClient singleton
   */
  public static PHIXClient getInstance()
  {
    if (instance == null)
    {
      instance = new PHIXClient();
    }
    
    return instance;
  }
  
  /**
   * Creates a proxy to the web service source connector running on PHIX1
   */
  private PHIXClient()
  {
    svc = new DefaultAcceptMessageService();
    port = svc.getDefaultAcceptMessagePort();    
  }
  
  /**
   * Delivers an HL7 message to the PHIX1 web service source connector
   * 
   * @param msg an HL7 message
   */
  public void deliverMessage(String msg)
  {   
    String response = port.acceptMessage(msg);
    System.out.println("Message delivered to PHIX WS Connector. ");
    
    if (response != null && response.length() > 0)
      System.out.println("PHIX Response: " + response);
    
  }

  /**
   * For testing.
   * 
   * @param args
   */
  public static void main(String[] args)
  {
    PHIXClient client = new PHIXClient();
    client.deliverMessage("test");
  }
}
