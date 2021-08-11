package edu.ucla.library.libservices.geffen.beans;

public class BruinPatron
{
  private String levelOfIssue;
  private String uid;
  private String firstName;
  private String lastName;
  private String middleName;
  private String email;
  
  public BruinPatron()
  {
    super();
  }

  public void setLevelOfIssue( String levelOfIssue )
  {
    this.levelOfIssue = levelOfIssue;
  }

  public String getLevelOfIssue()
  {
    return levelOfIssue;
  }

  public void setUid( String uid )
  {
    this.uid = uid;
  }

  public String getUid()
  {
    return uid;
  }

  public void setFirstName( String firstName )
  {
    this.firstName = firstName;
  }

  public String getFirstName()
  {
    return firstName;
  }

  public void setLastName( String lastName )
  {
    this.lastName = lastName;
  }

  public String getLastName()
  {
    return lastName;
  }

  public void setMiddleName( String middleName )
  {
    this.middleName = middleName;
  }

  public String getMiddleName()
  {
    return middleName;
  }

  public void setEmail( String email )
  {
    this.email = email;
  }

  public String getEmail()
  {
    return email;
  }
}
///current Issue Number, UID, First, Middle, Last, E-mail
