package edu.ucla.library.libservices.geffen.utility;

public class CodeIdPair
{
  private int returnCode;
  private int newID;
  
  public CodeIdPair()
  {
  }

  public void setReturnCode( int returnCode )
  {
    this.returnCode = returnCode;
  }

  public int getReturnCode()
  {
    return returnCode;
  }

  public void setNewID( int newID )
  {
    this.newID = newID;
  }

  public int getNewID()
  {
    return newID;
  }
}
