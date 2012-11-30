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

import gov.cdc.phlissa.hub.persistence.PersistenceMgr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.conf.ProfileException;
import ca.uhn.hl7v2.conf.check.DefaultValidator;
import ca.uhn.hl7v2.conf.parser.ProfileParser;
import ca.uhn.hl7v2.conf.spec.RuntimeProfile;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * Perform structural validation on HL7 messages using conformance profiles.
 * 
 * @version $Id: //PHLISSA_HUB/StructuralValidationService/src/gov/cdc/phlissa/hub/structuralvalidation/StructuralValidator.java#10 $
 */
public class StructuralValidator
{
  // Maps hl7 version (String) --> map containing conformance profiles.
  // Each map containing conformance profiles:  Map<String, RuntimeProfile>, where
  //   {msg version}_{trigger event} OR {customProfileName} (String) --> conformance profile (RuntimeProfile)
  private Map<String, Map<String, RuntimeProfile> > profileMap = null;
  
  private PersistenceMgr pm = null;
  
  private Pattern typePattern = null;
  private Pattern eventPattern = null;
  private Pattern hl7Pattern = null;
 
  
  /**
   * Constructor.  Initialize conformance profiles.  Pre-compile patterns that
   * will be used to extract MSH values necessary for loading profiles.
   */
  StructuralValidator()
  {
    profileMap = new HashMap<String, Map<String, RuntimeProfile> >();    
    pm = new PersistenceMgr();
    
    // Extract ninth field of header up to first ^ (MSH-9-1)
    typePattern = Pattern.compile("(?s)(?:[^\\|]*\\|){8}([^\\|\\^]+)\\^.*");
    // Extract ninth field of header after first ^, until next ^ or | (MSH-9-2)
    eventPattern = Pattern.compile("(?s)(?:[^\\|]*\\|){8}[^\\|\\^]+\\^([^\\|\\^]+)[\\^\\|].*");
    // Extract twelfth field of header (MSG-12)
    hl7Pattern = Pattern.compile("(?s)(?:[^\\|]*\\|){11}([\\.0-9]+).*");
  }

  /**
   * Using HAPI, validate the provided HL7 message using the appropriate conformance profile.
   * Returns any validation errors formatted as a single String.
   * 
   * @param rawMsg HL7 message to be validated
   * @param regexpFilters pipe(|)-delimited list of regular expressions for errors that should be ignored if they are encountered
   * @param customProfileName optional profile name that specifies which profile to use, if not the unnamed default.  May be null or "" if unused.
   * @return String containing all validation errors, if any.  Empty string if none.
   * @throws StructuralValidationFault if non-validation errors are encountered
   */
  String validateSingleErrorString(String rawMsg, String regexpFilters, String customProfileName) throws StructuralValidationFault
  {
    String errorString = "";
    HL7Exception[] exceptions = validate(rawMsg, true, customProfileName);
    int errNum = 0;
    int filteredCnt = 0;
    
    for (int i = 0; i < exceptions.length; i++) 
    {
      String error = exceptions[i].getClass().getSimpleName() + " - " + exceptions[i].getMessage();      
      boolean ignore = false;
      
      if (regexpFilters.length() > 0)
      {
        String[] regexpArray = regexpFilters.split("\\|"); 
        for (int j=0; j<regexpArray.length; j++)
        {
          if (error.matches(regexpArray[j]))
          {
            System.out.println("Error matches regexpFilter, will ignore: " + regexpArray[j] + " : " + error);
            ignore = true;
            ++filteredCnt;
            break;
          }
        }
      }
      
      if (! ignore)
        errorString += " " + Integer.toString(++errNum) + ")  " + error + " |";
    }
    
    System.out.println("ERRORS: " + exceptions.length + ", FILTERED: " + filteredCnt + ", ERRORS AFTER FILTERING: " + (exceptions.length - filteredCnt) );
    
    return errorString;
  }
  
  /**
   * Using HAPI, validate the provided HL7 message using the appropriate conformance profile.
   * Returns any validation errors formatted as a String[] array.
   * 
   * @param rawMsg HL7 message to be validated
   * @param regexpFilters pipe(|)-delimited list of regular expressions for errors that should be ignored if they are encountered
   * @param customProfileName optional profile name that specifies which profile to use, if not the unnamed default.  May be null or "" if unused.
   * @return String[] array containing validation errors, if any.  Empty array if none.
   * @throws StructuralValidationFault if non-validation errors are encountered
   */  
  String[] validateErrorArray(String rawMsg, String regexpFilters, String customProfileName) throws StructuralValidationFault
  {
    HL7Exception[] exceptions = validate(rawMsg, false, customProfileName);    
    List<String> errorList = new ArrayList<String>();
    
    for (int i=0; i<exceptions.length; i++)
    {
      String error = exceptions[i].getClass().getSimpleName() + " - " + exceptions[i].getMessage();      
      boolean ignore = false;
      
      if (regexpFilters.length() > 0)
      {
        String[] regexpArray = regexpFilters.split("\\|"); 
        for (int j=0; j<regexpArray.length; j++)
        {
          if (error.matches(regexpArray[j]))
          {
            System.out.println("Error matches regexpFilter, will ignore: " + regexpArray[j] + " : " + error);
            ignore = true;
            break;
          }
        }
      }
          
      if (! ignore)
        errorList.add(error);
             
    }
    
    String[] errorArray = errorList.toArray(new String[0]);
    
    System.out.println("ERRORS: " + exceptions.length + ", FILTERED: " + (exceptions.length - errorArray.length) + ", ERRORS AFTER FILTERING: " + errorArray.length);
    return errorArray;
  }
 
  
  /**
   * Internal method for validating the provided HL7 message using the appropriate conformance profile.
   * 
   * @param rawMsg HL7 message to be validated
   * @param singleError true if errors should be concatenated into a single error, false otherwise
   * @param customProfileName optional profile name that specifies which profile to use, if not the unnamed default.  May be null or "" if unused.
   * @return String containing all validation errors, if any.  Empty string if none.
   * @throws StructuralValidationFault if non-validation errors are encountered
   */
  private HL7Exception[] validate(String rawMsg, boolean singleError, String customProfileName) throws StructuralValidationFault
  {
    HL7Exception[] exceptions = null;
    
    try
    {                    
      // ensure msg is in correct format
      StringReader sr = new StringReader(rawMsg);
      BufferedReader reader = new BufferedReader(sr);
      String line;
      StringBuffer sb = new StringBuffer();
      
      while ((line=reader.readLine()) != null) 
      {
        if (line.length() < 3) continue;  // Ignore empty lines
        if (line.substring(0, 1).equals("#")) continue;  // Ignore Lines that have been commented out
        sb.append(line + "\r");  // Add back the carriage return control character used to separate segment records
      }
      reader.close();          
      String msg = sb.toString(); 
      
      // Extract message type, trigger event, and HL7 version from message before touching HAPI
      Matcher typeMatcher = typePattern.matcher(msg);
      Matcher eventMatcher = eventPattern.matcher(msg);
      Matcher hl7Matcher = hl7Pattern.matcher(msg);
      RuntimeProfile profile = null;
      
      if (typeMatcher.matches() && eventMatcher.matches() && hl7Matcher.matches() )
      {
        profile = fetchProfile(typeMatcher.group(1), eventMatcher.group(1), hl7Matcher.group(1), customProfileName);
      }
      else
      {
        System.err.println("Could not extract MSH-9-1, MSH-9-2, or MSH-12 (msg type, event trigger, HL7 verison) from message.");
        throw new StructuralValidationFault("Could not extract MSH-9-1, MSH-9-2, or MSH-12 (msg type, event trigger, HL7 verison) from message.");
      }
             
      // parse the message with HAPI
      Message validMessage = new PipeParser().parse(msg);
      
      // Create a conformance validator, and validate
      DefaultValidator validator = new DefaultValidator();   
      
      exceptions = validator.validate(validMessage, profile.getMessage());
    }
    catch (IOException e)
    {
      System.out.println(e + ", " + e.getMessage() );
      throw new StructuralValidationFault(e.getMessage(), e);
    }    
    catch (HL7Exception e)
    {
      System.out.println(e + ", " + e.getMessage() );
      throw new StructuralValidationFault(e.getMessage(), e);
    }
    catch (ProfileException e)
    {
      System.out.println(e + ", Could not find conformance profile for message: " + e.getMessage() );
      throw new StructuralValidationFault("Could not find conformance profile for message: " + e.getMessage(), e);
    }
    
    return exceptions;
  }
  
  
  /**
   * Returns a RuntimeProfile from the cache or database.  If loaded from database, adds
   * runtime profile into cache.   
   * 
   * @param msgType MSH-9-1: message type; "OUL"
   * @param triggerEvent MSH-9-2: trigger event; "R22"
   * @param hl7Version HL7 versions number; "2.6"
   * @param customProfileName optional profile name that specifies which profile to use, if not the unnamed default.  May be null or "" if unused.
   * @return RuntimeProfile for the msgType and hl7Version, or null if none
   * @throws ProfileException re-thrown
   */
  private synchronized RuntimeProfile fetchProfile(String msgType, String triggerEvent, String hl7Version, String customProfileName) throws ProfileException
  {
    System.out.println("Fetching conformance profile for msg: " + msgType + ", " + triggerEvent + ", " + hl7Version + ". customProfileName=" + customProfileName);
    
    String profileNameKey;
    if (customProfileName != null && customProfileName.length() > 0)
      profileNameKey = customProfileName;
    else
      profileNameKey = msgType + "_" + triggerEvent;
    
    RuntimeProfile runtimeProfile = null;
    Map<String, RuntimeProfile> type2profMap;
    
    if (profileMap.containsKey(hl7Version))
    {   
      type2profMap = profileMap.get(hl7Version);
    }
    else
    {
      type2profMap = new HashMap<String, RuntimeProfile>();
      profileMap.put(hl7Version, type2profMap);
    }
    
    // cache hit
    if (type2profMap.containsKey(profileNameKey))
    {
      System.out.println("Found profile in cache");
      runtimeProfile = type2profMap.get(profileNameKey);
    }
    // cache miss... load profile from database instead
    else
    {
      System.out.println("Profile not in cache, loading profile from database");
      
      String profileStr;
      if (customProfileName != null && customProfileName.length() > 0)
        profileStr = pm.retrieveProfileAsString(customProfileName);
      else
        profileStr = pm.retrieveProfileAsString(msgType, triggerEvent, hl7Version);
      
      if (profileStr != null)
      {
        ProfileParser profileParser = new ProfileParser(false);    
        runtimeProfile = profileParser.parse(profileStr);
        // add profile to cache
        type2profMap.put(profileNameKey, runtimeProfile);
      }      
    }
    
    return runtimeProfile;
  }
  
  // for testing
  public static void main(String[] args)
  {
    System.out.println("test");
    
    String regexpFilters = ".*eatpork.*|.*eatbeef.*|.*random.*";
    String error = "This is some random error";
    boolean ignore = false;
    if (regexpFilters.length() > 0)
    {
      String[] regexpArray = regexpFilters.split("\\|"); 
      for (int j=0; j<regexpArray.length; j++)
      {
        System.out.println("Elem " + j + ":" + regexpArray[j]);
        if (error.matches(regexpArray[j]))
        {
          System.out.println("Error matches regexpFilter, will ignore: " + regexpArray[j] + " : " + error);
          ignore = true;
          break;
        }
      }
    
    }
  }
  
  
}
  

