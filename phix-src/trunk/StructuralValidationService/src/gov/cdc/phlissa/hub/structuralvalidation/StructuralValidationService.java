/*
   Copyright 2011-2012  U.S. Centers for Disease Control and Prevention

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
package gov.cdc.phlissa.hub.structuralvalidation;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(targetNamespace = "http://phlissa.cdc.gov/", serviceName = "StructuralValidationService")  
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)

/**
 * Web service providing capability to structurally validate HL7 messages 
 * against pre-loaded conformance profiles.
 * 
 * @version $Id: //PHLISSA_HUB/StructuralValidationService/src/gov/cdc/phlissa/hub/structuralvalidation/StructuralValidationService.java#7 $
 */
public class StructuralValidationService
{
  private static StructuralValidator validator = new StructuralValidator();

  
  @WebMethod
  @WebResult(name="validationErrors")
  public String validate(
                         @WebParam(name="hl7Message")
                         String hl7Message,
                         @WebParam(name="customProfileName")
                         String customProfileName)
    throws StructuralValidationFault
  {
    System.out.println("StructuralValidationService.validate(): Message length=" + hl7Message.length() + ", customProfileName=" + customProfileName );
    String validationErrors = validator.validateSingleErrorString(hl7Message, "", customProfileName);
    return validationErrors;
  }
  
  @WebMethod
  @WebResult(name="validationErrors")
  public String validateWithFiltering(
                         @WebParam(name="hl7Message")
                         String hl7Message,
                         @WebParam(name="regexpFilters")
                         String regexpFilters,
                         @WebParam(name="customProfileName")
                         String customProfileName)                         
    throws StructuralValidationFault
  {
    System.out.println("StructuralValidationService.validateWithFiltering(): Message length=" + hl7Message.length() + ", regexpFilters=" + regexpFilters + ", customProfileName=" + customProfileName);
    String validationErrors = validator.validateSingleErrorString(hl7Message, regexpFilters, customProfileName);
    return validationErrors;
  }    
  
  @WebMethod
  @WebResult(name="validationErrorArray")
  public String[] validateErrorArray(
                                     @WebParam(name="hl7Message")
                                     String hl7Message,
                                     @WebParam(name="customProfileName")
                                     String customProfileName)                                     
    throws StructuralValidationFault
  {
    System.out.println("StructuralValidationService.validateErrorArray(): Message length=" + hl7Message.length() + ", customProfileName=" + customProfileName );
    String[] validationErrorArray = validator.validateErrorArray(hl7Message, "", customProfileName);
    return validationErrorArray;
  }  
  
  @WebMethod
  @WebResult(name="validationErrorArray")
  public String[] validateErrorArrayWithFiltering(
                                                  @WebParam(name="hl7Message")
                                                  String hl7Message,
                                                  @WebParam(name="regexpFilters")
                                                  String regexpFilters,
                                                  @WebParam(name="customProfileName")
                                                  String customProfileName)                                                  
    throws StructuralValidationFault
  {
    System.out.println("StructuralValidationService.validateErrorArrayWithFiltering(): Message length=" + hl7Message.length() + ", regexpFilters=" + regexpFilters + ", customProfileName=" + customProfileName);
    String[] validationErrorArray = validator.validateErrorArray(hl7Message, regexpFilters, customProfileName);
    return validationErrorArray;
  }    
  
}