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
package gov.cdc.phlissa.hub.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;


/**
 * Access to database tier.
 * 
 * @version $Id: //PHLISSA_HUB/StructuralValidationService/src/gov/cdc/phlissa/hub/persistence/PersistenceMgr.java#5 $
 */
public class PersistenceMgr
{
  private Connection conn = null;
  private PreparedStatement retProfStmt = null;
  private PreparedStatement retCustomProfStmt = null;
  
  
  /**
   * Initialize connection and prepared statements.
   */
  public PersistenceMgr()
  {
    if (conn == null)
    {
      try
      {         
        Properties props = new Properties();                
        props.load(getClass().getResourceAsStream("database.properties"));
        Class.forName(props.getProperty("jdbc_driver"));
        String url = props.getProperty("url");

        conn = DriverManager.getConnection(url, props);        
        retProfStmt = conn.prepareStatement(props.getProperty("retrieve_profile_stmt"));
        retCustomProfStmt = conn.prepareStatement(props.getProperty("retrieve_custom_profile_stmt"));
      }
      catch (IOException e)
      {
        System.err.println(e);
      }
      catch (ClassNotFoundException e)
      {
        System.err.println(e);
      }
      catch (SQLException e)
      {
        System.err.println(e);
      }
    }
  }
  
  
  /**
   * Release database resources
   */
  public void finalize()
  {
    try
    {
      if (retProfStmt != null && !retProfStmt.isClosed() )
        retProfStmt.close();   
      
      if (retCustomProfStmt != null && !retCustomProfStmt.isClosed() )
        retCustomProfStmt.close();       
    }
    catch (SQLException e)
    {
      System.err.println(e);
    }

    try
    {    
      if (conn != null && !conn.isClosed() )
        conn.close();
    }
    catch (SQLException e)
    {
      System.err.println(e);
    }    
  } 
  
  
  /**
   * Retrieve a custom conformance profile by name as a String
   * 
   * @param customProfileName name of conformance profile to retrieve
   * @return conformance profile as a String, or null if none exists in the DB
   */
  public String retrieveProfileAsString(String customProfileName)
  {
    String profile = null;
    
    try
    {
      retCustomProfStmt.setString(1, customProfileName);
      
      ResultSet rs = retCustomProfStmt.executeQuery();
      while (rs.next()) 
      {
        profile = rs.getString(1); 
      }
      rs.close();
      
    }
    catch (SQLException e)
    {
      System.err.println(e);
    }    
    
    return profile;
  }
  
  
  /**
   * Retrieve a conformance profile as a String, given a valid msgType, triggerEvent and hl7Version combination. 
   * 
   * @param msgType MSH-9-1: message type; "OUL"
   * @param triggerEvent MSH-9-2: trigger event; "R22"
   * @param hl7Version HL7 versions number; "2.6"
   * @return conformance profile as a String, or null if none exists in the DB
   */
  public String retrieveProfileAsString(String msgType, String triggerEvent, String hl7Version)
  {
    String profile = null;
    
    try
    {
      retProfStmt.setString(1, msgType);
      retProfStmt.setString(2, triggerEvent);
      retProfStmt.setString(3, hl7Version);
      
      ResultSet rs = retProfStmt.executeQuery();
      while (rs.next()) 
      {
        profile = rs.getString(1); 
      }
      rs.close();
      
    }
    catch (SQLException e)
    {
      System.err.println(e);
    }    
    
    return profile;
  }
  
}
