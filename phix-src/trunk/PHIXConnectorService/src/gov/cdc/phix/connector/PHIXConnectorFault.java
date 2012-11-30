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

import javax.xml.ws.WebFault;

/**
 * SOAP Fault thrown by service when errors occur.
 * 
 * @version $Id: //PHLISSA_HUB/PHIXConnectorService/src/gov/cdc/phix/connector/PHIXConnectorFault.java#2 $
 */
@WebFault(name="PHIXConnectorFault", targetNamespace = "http://phix.cdc.gov/")
public class PHIXConnectorFault extends Exception
{
  private static final long serialVersionUID = 1L;  
  protected String code;

  public PHIXConnectorFault(String code)
  {   
    super(code);
    this.code=code;
  }

  public PHIXConnectorFault(String code, Throwable t)
  {
    super(code, t);
    this.code=code;
  }

  public String getCode()
  {
    return code;
  }

  public void setCode(String code)
  {
    this.code = code;
  }

}