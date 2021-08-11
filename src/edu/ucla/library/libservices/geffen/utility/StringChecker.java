package edu.ucla.library.libservices.geffen.utility;

public class StringChecker
{
  public StringChecker()
  {
    super();
  }

  public static boolean isEmpty( String value )
  {
    return value == null || value.length() == 0 || value.equals( "" );
  }

  public static boolean isEmpty( Object value )
  {
    return value == null || isEmpty( value.toString() );
  }
}
