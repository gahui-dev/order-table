package com.java.POSSystem.Model;

public class UserInfo {
	private String UserID;
	private String BusinessName;
	private String UserName;

	public UserInfo(String userid, String businessname, String username)
	{
		UserID = userid;
		BusinessName = businessname;
		UserName = username;
	}

	public String getUserID()
	{
		return UserID;
	}

	public String getBusinessName()
	{
		return BusinessName;
	}

	public String getUserName()
	{
		return UserName;
	}
}
