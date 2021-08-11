package edu.ucla.library.libservices.geffen.main;

import edu.ucla.library.libservices.geffen.beans.VgrPatron;

import edu.ucla.library.libservices.voyager.api.core.ApiServer;

import edu.ucla.library.libservices.voyager.api.core.VoyagerException;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import edu.ucla.library.libservices.geffen.api.factory.VoyagerConnectionFactory;
import edu.ucla.library.libservices.geffen.api.handlers.PatronHandler;
import edu.ucla.library.libservices.geffen.utility.CodeIdPair;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Loader
{
  private static final String VOYAGER_COUNT_QUERY =
    "SELECT count(patron_id) FROM ucladb.patron WHERE institution_id = ?";
  private static final String VOYAGER_ID_QUERY = "SELECT patron_id FROM ucladb.patron WHERE institution_id = ?";
  private static final SimpleDateFormat TO_DATE = new SimpleDateFormat("M/dd/yyyy hh:mm:ss a");
  private static final SimpleDateFormat FROM_DATE = new SimpleDateFormat("yyyyMMdd  23:59:59");

  private static DriverManagerDataSource voyager;
  private static Properties props;
  private static List<VgrPatron> patrons;
  private static ApiServer server;
  private static String machine = null;
  private static String version = null;
  private static String dbKey = "";
  private static int port = 0;

  public Loader()
  {
    super();
  }

  public static void main( String[] args )
  {
    loadProperties( args[ 0 ] );
    makeDbConnection();
    initializeVariables();
    initializeConnection();
    getPatrons( args[ 1 ] );
    loadOrUpdatePatrons();
  }

  private static void loadProperties( String propFile )
  {
    props = new Properties();
    try
    {
      props.load( new FileInputStream( new File( propFile ) ) );
    }
    catch ( IOException ioe )
    {
      ioe.printStackTrace();
      System.exit( -1 );
    }
  }

  private static void fillPatron( String patronLine )
  {
    String[] tokens;
    VgrPatron thePatron;

    tokens = patronLine.split( "," );
    if ( tokens.length == 8 )
    {
      thePatron = new VgrPatron();
      thePatron.setAddressLine1( props.getProperty( "geffen.address1" ) );
      thePatron.setCity( props.getProperty( "geffen.city" ) );
      thePatron.setCountry( props.getProperty( "geffen.country" ) );
      thePatron.setEmailAddress( tokens[ 6 ].replaceAll( "\"", "" ) );
      try
      {
        thePatron.setEndDate( FROM_DATE.format( TO_DATE.parse( tokens[ 7 ].replaceAll( "\"", "" ) ) ) );
      }
      catch ( ParseException pe )
      {
        thePatron.setEndDate( null );
      }
      thePatron.setFirstName( tokens[ 2 ].replaceAll( "\"", "" ) );
      thePatron.setIdentifier( tokens[ 1 ].replaceAll( "\"", "" ) );
      thePatron.setLastName( tokens[ 4 ].replaceAll( "\"", "" ) );
      thePatron.setLevelOfIssue( tokens[ 0 ].replaceAll( "\"", "" ) );
      thePatron.setMiddleName( tokens[ 3 ].replaceAll( "\"", "" ) );
      thePatron.setState( props.getProperty( "geffen.state" ) );
      thePatron.setZip( props.getProperty( "geffen.zip" ) );
      patrons.add( thePatron );
    }
  }

  private static void makeDbConnection()
  {
    voyager = new DriverManagerDataSource();

    voyager.setDriverClassName( props.getProperty( "db.driver" ) );
    voyager.setUrl( props.getProperty( "db.url" ) );
    voyager.setUsername( props.getProperty( "db.user" ) );
    voyager.setPassword( props.getProperty( "db.password" ) );
  }

  private static void initializeVariables()
  {
    machine = props.getProperty( "voyager.server" );
    version = props.getProperty( "voyager.version" );
    port = Integer.parseInt( props.getProperty( "voyager.circsvr" ) );
    dbKey = props.getProperty( "voyager.dbkey" );
  }

  private static void initializeConnection()
  {
    try
    {
      server = VoyagerConnectionFactory.getConnection( props, props.getProperty( "voyager.appcode" ) );
    }
    catch ( VoyagerException ve )
    {
      System.err.println( "Program failed to establish Voyager connection: " + ve.getMessage() );
      ve.printResponse();
      System.exit( -2 );
    }
  }

  private static void getPatrons( String fileName )
  {
    BufferedReader reader;
    String line;
    try
    {
      patrons = new ArrayList<VgrPatron>();
      reader = new BufferedReader( new FileReader( new File( fileName ) ) );
      while ( ( line = reader.readLine() ) != null )
      {
        fillPatron( line );
      }
    }
    catch ( FileNotFoundException fnfe )
    {
      fnfe.printStackTrace();
      System.exit( -2 );
    }
    catch ( IOException ioe )
    {
      ioe.printStackTrace();
      System.exit( -3 );
    }
  }

  private static void loadOrUpdatePatrons()
  {
    for ( VgrPatron thePatron : patrons )
    {
      int count;
      count = new JdbcTemplate( voyager ).queryForInt( VOYAGER_COUNT_QUERY, new Object[]
            { thePatron.getIdentifier().trim() } );
      if ( count != 0 )
      {
        System.out.println( "patron " + thePatron.getIdentifier().trim() + " in system" );
        int id;
        id = new JdbcTemplate( voyager ).queryForInt( VOYAGER_ID_QUERY, new Object[]
              { thePatron.getIdentifier().trim() } );
        updatePatron( thePatron, id );
      }
      else
      {
        System.out.println( "patron " + thePatron.getIdentifier() + " not in system" );
        loadPatron( thePatron );
      }      
    }
  }

  private static void updatePatron( VgrPatron thePatron, int id )
  {
    CodeIdPair results;
    PatronHandler handler;

    handler = new PatronHandler();
    handler.setThePatron( thePatron );

    results =
        handler.updatePatron( server, props.getProperty( "voyager.appcode" ), id, props.getProperty( "voyager.dbkey" ) );
    results =
        handler.updatePatronAddress( server, props.getProperty( "voyager.appcode" ), id, props.getProperty( "voyager.dbkey" ) );
    results =
        handler.updatePatronEmail( server, props.getProperty( "voyager.appcode" ), id, props.getProperty( "voyager.dbkey" ) );
  }

  private static void loadPatron( VgrPatron thePatron )
  {
    CodeIdPair results;
    PatronHandler handler;

    handler = new PatronHandler();
    handler.setThePatron( thePatron );
    results = handler.addPatron( server, props.getProperty( "voyager.appcode" ) );

    if ( results.getReturnCode() == 0 )
    {
      System.out.println( "patron " + thePatron.getIdentifier().trim() + " loaded, new ID = " + results.getNewID() );
    }
    else
    {
      System.err.println( "patron " + thePatron.getIdentifier().trim() + " failed to load" );
    }
  }
}
