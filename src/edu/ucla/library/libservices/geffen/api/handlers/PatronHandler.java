package edu.ucla.library.libservices.geffen.api.handlers;

import edu.ucla.library.libservices.geffen.beans.VgrPatron;
import edu.ucla.library.libservices.voyager.api.core.ApiRequest;
import edu.ucla.library.libservices.voyager.api.core.ApiResponse;
import edu.ucla.library.libservices.voyager.api.core.ApiServer;
import edu.ucla.library.libservices.geffen.utility.CodeIdPair;
import org.apache.commons.lang.StringUtils;

public class PatronHandler
{
  private VgrPatron thePatron;
  //private static final String PATTERN = "[0-9]{9}";

  public PatronHandler()
  {
    super();
  }

  public void setThePatron( VgrPatron thePatron )
  {
    this.thePatron = thePatron;
  }

  private VgrPatron getThePatron()
  {
    return thePatron;
  }

  public CodeIdPair addPatron( ApiServer server, String appCode )
  {
    ApiRequest request;
    ApiResponse response;
    CodeIdPair results;

    results = new CodeIdPair();

    request = new ApiRequest( appCode, "ADDPATRON" );
    request.addParameter( "TC", "1" );
    request.addParameter( "LN", getThePatron().getLastName().trim() );
    request.addParameter( "FN", getThePatron().getFirstName().trim() );
    request.addParameter( "MN", getThePatron().getMiddleName().trim() );
    request.addParameter( "TITL", "" );
    request.addParameter( "SN", "" );
    request.addParameter( "IN", getThePatron().getIdentifier().trim() );
    request.addParameter( "ED", getThePatron().getEndDate().trim() );
    request.addParameter( "PD", "20361231" );
    request.addParameter( "GI", "61" );
    //if ( getThePatron().getIdentifier().trim().matches(PATTERN) )
    request.addParameter( "PB", StringUtils.leftPad( getThePatron().getIdentifier().trim(), 9, '0' ).concat( getThePatron().getLevelOfIssue() ) );
    //else
      //request.addParameter( "PB", "" );
    request.addParameter( "EAD", getThePatron().getEmailAddress().trim() );
    request.addParameter( "A1", getThePatron().getAddressLine1().trim() );
    request.addParameter( "A2", "" );
    request.addParameter( "A3", "" );
    request.addParameter( "A4", "" );
    request.addParameter( "A5", "" );
    request.addParameter( "CY", getThePatron().getCity().trim() );
    request.addParameter( "SP", getThePatron().getState().trim() );
    request.addParameter( "ZP", getThePatron().getZip().trim() );
    request.addParameter( "CT", getThePatron().getCountry().trim() );
    request.addParameter( "PH", null );
    request.addParameter( "OP", "" );
    request.addParameter( "PI", "" );

    server.send( request.toString() );
    response = new ApiResponse( server.receive() );

    results.setReturnCode( response.getReturnCode() );

    if ( results.getReturnCode() == 0 )
    {
      results.setNewID( response.getSingleValueInt( "PI" ) );
    }
    else
    {
      System.out.print( "\tresult code = " + results.getReturnCode() );
    }

    return results;
  }

  public CodeIdPair updatePatron( ApiServer server, String appCode, 
                                  int patID, String key )
  {
    ApiRequest request;
    ApiResponse response;
    CodeIdPair results;

    results = new CodeIdPair();

    request = new ApiRequest( appCode, "UPDPATRON" );
    request.addParameter( "PI", patID );
    request.addParameter( "TC", "1" );
    request.addParameter( "LN", getThePatron().getLastName() );
    request.addParameter( "FN", getThePatron().getFirstName() );
    request.addParameter( "MN", getThePatron().getMiddleName() );
    request.addParameter( "TITL", "" );
    request.addParameter( "SN", "" );
    request.addParameter( "IN", getThePatron().getIdentifier() );
    request.addParameter( "ED", getThePatron().getEndDate().toString().concat(" 23:59:59") );
    request.addParameter( "PD", "20361231" );
    request.addParameter( "UBID", "1@".concat( key ) );

    server.send( request.toString() );
    response = new ApiResponse( server.receive() );

    results.setReturnCode( response.getReturnCode() );

    return results;
  }

  public CodeIdPair updatePatronAddress( ApiServer server, String appCode, 
                                  int patID, String key )
  {
    ApiRequest request;
    ApiResponse response;
    CodeIdPair results;

    results = new CodeIdPair();

    request = new ApiRequest( appCode, "UPDPADDRSS" );
    request.addParameter( "PI", patID );
    request.addParameter( "AT", 1 );
    request.addParameter( "ST", "N" );
    request.addParameter( "AP", "N" );
    request.addParameter( "A1", getThePatron().getAddressLine1() );
    request.addParameter( "A2", getThePatron().getAddressLine2() );
    request.addParameter( "A3", "" );
    request.addParameter( "A4", "" );
    request.addParameter( "A5", "" );
    request.addParameter( "CY", getThePatron().getCity() );
    request.addParameter( "SP", getThePatron().getState() );
    request.addParameter( "ZP", getThePatron().getZip() );
    request.addParameter( "CT", getThePatron().getCountry() );
    request.addParameter( "FD", "" );
    request.addParameter( "ED", "" );
    request.addParameter( "PH", null );
    request.addParameter( "UBID", "1@".concat( key ) );

    server.send( request.toString() );
    response = new ApiResponse( server.receive() );

    results.setReturnCode( response.getReturnCode() );

    return results;
  }

  public CodeIdPair updatePatronEmail( ApiServer server, String appCode, 
                                  int patID, String key )
  {
    ApiRequest request;
    ApiResponse response;
    CodeIdPair results;

    results = new CodeIdPair();

    request = new ApiRequest( appCode, "UPDPADDRSS" );
    request.addParameter( "PI", patID );
    request.addParameter( "AT", 3 );
    request.addParameter( "ST", "N" );
    request.addParameter( "AP", "N" );
    request.addParameter( "A1", getThePatron().getEmailAddress() );
    request.addParameter( "A2", getThePatron().getAddressLine2() );
    request.addParameter( "A3", "" );
    request.addParameter( "A4", "" );
    request.addParameter( "A5", "" );
    request.addParameter( "CY", getThePatron().getCity() );
    request.addParameter( "SP", getThePatron().getState() );
    request.addParameter( "ZP", getThePatron().getZip() );
    request.addParameter( "CT", getThePatron().getCountry() );
    request.addParameter( "FD", "" );
    request.addParameter( "ED", "" );
    request.addParameter( "PH", null );
    request.addParameter( "UBID", "1@".concat( key ) );

    server.send( request.toString() );
    response = new ApiResponse( server.receive() );

    results.setReturnCode( response.getReturnCode() );

    if ( results.getReturnCode() == 0 )
    {
      results.setNewID( response.getSingleValueInt( "PI" ) );
    }

    return results;
  }
}
