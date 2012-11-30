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
package gov.cdc.phlissa.hub.routing;

/**
 * Structure holding organization-based component routing information for the Hub.
 * This data determines which Hub components are reached based on a specific
 * incoming message and configuration stored in the Hub database.
 * 
 * @version $Id: //PHLISSA_HUB/ComponentRoutingService/src/gov/cdc/phlissa/hub/routing/RoutingData.java#10 $
 */
public class RoutingData
{
  // component_routing columns
  public boolean structuralValidation;
  public String structuralValidationRegexpFilters;
  public boolean vocabTranslation;
  public boolean codeValidation;
  public boolean subscription;
  public boolean anonymization;
  public boolean addSFTSegment;
  public boolean addSPMSegment;
  public String translateToVersion;
  public String vocabTranslationMSSProfile;
  public String codeValidationMSSProfile;
  public String customProfileName;
  
  // organization columns
  public String sendingFacilityDirectEmailAddress;
  public String sendingFacilityNotificationEmail;
  public String sendingFacilityErrorNotificationEmail;
  public String sendingFacilityWebSvcHost;
  public String sendingFacilityXportType;
  public String sendingFacilityHubHost;
  
  public String receivingFacilityDirectEmailAddress;
  public String receivingFacilityWebSvcHost;
  public String receivingFacilityXportType;
  public String receivingFacilityHubHost;
}
