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
package gov.cdc.phlissa.hub.routing.persistence;

import gov.cdc.phlissa.hub.routing.RoutingData;

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
 * @version $Id: //PHLISSA_HUB/ComponentRoutingService/src/gov/cdc/phlissa/hub/routing/persistence/PersistenceMgr.java#11 $
 */
public class PersistenceMgr
{
  private Connection conn = null;
  private PreparedStatement routingStmt = null;

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
        routingStmt = conn.prepareStatement(props.getProperty("retrieve_routing_data_stmt"));
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
      if (routingStmt != null && !routingStmt.isClosed() )
        routingStmt.close();   
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
   * Retrieve component routing data from the Hub database based on supplied parameters,
   * or throws NoResultsFoundException if no results are found.
   * 
   * @param msgType MSH-9-1: message type; "OUL"
   * @param triggerEvent MSH-9-2: trigger event; "R22"
   * @param hl7Version HL7 versions number; "2.6"
   * @param sendingFacility MSH-4; "LAB1"
   * @param receivingFacility MSH-6; "LAB1"
   * @return RoutingData structure
   * @throws NoResultsFoundException if no results are found based on supplied criteria
   * @throws PersistenceException if any kind of error is encountered
   *
   */
  public RoutingData retrieveRoutingData(String msgType, 
                                         String triggerEvent, 
                                         String hl7Version, 
                                         String sendingFacility, 
                                         String receivingFacility)
    throws NoResultsFoundException, PersistenceException
  {    
    RoutingData routingData = null;
    
    try
    {
      routingStmt.setString(1, msgType);
      routingStmt.setString(2, triggerEvent);
      routingStmt.setString(3, hl7Version);
      routingStmt.setString(4, sendingFacility);
      routingStmt.setString(5, receivingFacility);
      
      ResultSet rs = routingStmt.executeQuery();
      System.out.println(routingStmt.toString());
      
      if (rs.next()) 
      {
        int idx = 0;
        routingData = new RoutingData();
        routingData.structuralValidation = rs.getBoolean(++idx);
        routingData.structuralValidationRegexpFilters = rs.getString(++idx);        
        routingData.vocabTranslation = rs.getBoolean(++idx);
        routingData.codeValidation = rs.getBoolean(++idx);
        routingData.subscription = rs.getBoolean(++idx);
        routingData.anonymization = rs.getBoolean(++idx);
        routingData.addSFTSegment = rs.getBoolean(++idx);
        routingData.addSPMSegment = rs.getBoolean(++idx);
        routingData.translateToVersion = rs.getString(++idx);
        routingData.vocabTranslationMSSProfile = rs.getString(++idx);
        routingData.codeValidationMSSProfile = rs.getString(++idx);
        routingData.customProfileName = rs.getString(++idx);
        routingData.sendingFacilityDirectEmailAddress = rs.getString(++idx);        
        routingData.sendingFacilityNotificationEmail = rs.getString(++idx);
        routingData.sendingFacilityErrorNotificationEmail = rs.getString(++idx);
        routingData.sendingFacilityWebSvcHost = rs.getString(++idx);
        routingData.sendingFacilityXportType = rs.getString(++idx);
        routingData.sendingFacilityHubHost = rs.getString(++idx);
        routingData.receivingFacilityDirectEmailAddress = rs.getString(++idx);
        routingData.receivingFacilityWebSvcHost = rs.getString(++idx);
        routingData.receivingFacilityXportType = rs.getString(++idx);
        routingData.receivingFacilityHubHost = rs.getString(++idx);
      }
      rs.close();
      
    }
    catch (SQLException e)
    {
      System.err.println(e);
      throw new PersistenceException(e);
    }
    
    if (routingData == null)
    {
      throw new NoResultsFoundException("No component routing configuration found for message: " +
        "msgType=" + msgType + ", triggerEvent=" + triggerEvent + ", hl7Version=" + hl7Version + 
        ", sendingFacility=" + sendingFacility + ", receivingFacility=" + receivingFacility);
    }
    
    return routingData;
  }
  
}
