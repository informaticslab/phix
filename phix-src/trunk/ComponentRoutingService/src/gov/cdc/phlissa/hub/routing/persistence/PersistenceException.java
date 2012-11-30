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
package gov.cdc.phlissa.hub.routing.persistence;

/**
 * Exception indicating an error has occurred accessing the database.
 * 
 * @version $Id: //PHLISSA_HUB/ComponentRoutingService/src/gov/cdc/phlissa/hub/routing/persistence/PersistenceException.java#2 $
 */
public class PersistenceException extends Exception
{
  private static final long serialVersionUID = 1L;
  
  public PersistenceException()
  {
  }

  public PersistenceException(String message)
  {
    super(message);
  }

  public PersistenceException(Throwable cause)
  {
    super(cause);
  }

  public PersistenceException(String message, Throwable cause)
  {
    super(message, cause);
  }

}
