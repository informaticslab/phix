package gov.cdc.phix.xdr;

import java.io.File;
import java.io.FilenameFilter;

public class XMLFilenameFilter implements FilenameFilter
{ 
  public boolean accept(File dir, String name)
  {
    return name.matches(".*xml");      
  }
}