/*
   Copyright 2012  U.S. Centers for Disease Control and Prevention

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
package gov.cdc.phix.nnd;

import gov.cdc.phix.nnd.persistence.PersistenceMgr;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(targetNamespace = "http://phix.cdc.gov/", serviceName = "NNDService")  
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)

public class NNDService
{
  static PersistenceMgr pMgr = new PersistenceMgr();
  
  @WebMethod
  @WebResult(name="nndResult")  
  public NNDResult detectNotifiableDisease(
                                           @WebParam(name="testcode")
                                           String testcode,
                                           @WebParam(name="resultcode")
                                           String resultcode)
  {    
    System.out.println("NNDService.detectNotifiableDisease(" + testcode + ", " + resultcode + ")");
    
    NNDResult result = new NNDResult();
    result.testcode = testcode;
    result.resultcode = resultcode;
    
    boolean reportable = pMgr.detectNND(testcode, resultcode);
    
    if (reportable) 
      result.reportable = "TRUE";
    else 
      result.reportable = "FALSE";
    
    return result;
  }
}
