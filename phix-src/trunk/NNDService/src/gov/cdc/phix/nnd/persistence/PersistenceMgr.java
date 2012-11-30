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
package gov.cdc.phix.nnd.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class PersistenceMgr
{
  private Connection conn = null;
  private PreparedStatement nndQuery = null;

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
        nndQuery = conn.prepareStatement(props.getProperty("search_nnd_stmt"));
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
      if (nndQuery != null && !nndQuery.isClosed() )
        nndQuery.close();   
    }
    catch (SQLException e)
    {
      System.err.println(e);
    }
  } 
  
  
  /**
   * Search for NND conditions based on testcode and resultcode, returning whether or not 
   * a reportable NND was detected.
   * 
   * @param testcode
   * @param resultcode
   * @return true if a reportable condition was detected, false otherwise
   */
  public boolean detectNND(String testcode, String resultcode)
  {    
    boolean reportable = false;
    
    try
    {
      nndQuery.setString(1, testcode);
      nndQuery.setString(2, resultcode);
      
      ResultSet rs = nndQuery.executeQuery();
      System.out.println(nndQuery.toString());
      
      if (rs.next()) 
      {
        int id = rs.getInt(1);
        
        if (id > 0)
        {
          System.out.println("id=" + id + ", REPORTABLE CONDITION DETECTED FOR testcode=" + testcode + ", resultcode=" + resultcode);
          reportable = true;
        }
        
      }
      rs.close();
      
    }
    catch (SQLException e)
    {
      System.err.println(e);      
    }
    
    return reportable;
  }  
  
  
}
