package com.mfino.provision.tools.propertymanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

class Passwords
{
	private String oldPassword;
	private String newPassword;
	boolean passwordIsSet;

	public boolean isPasswordisset()
	{
		return this.passwordIsSet;
	}

	public void setPasswordisset(boolean passwordisset)
	{
		this.passwordIsSet = passwordisset;
	}

	public String getOldPassword()
	{
		return this.oldPassword;
	}

	public void setOldPassword(String oldPassword)
	{
		this.oldPassword = oldPassword;
	}

	public String getNewPassword()
	{
		return this.newPassword;
	}

	public void setNewPassword(String newPassword)
	{
		this.newPassword = newPassword;
	}

}

public class EncryptedKeys
{

	public static Passwords GetPasswords(Logger logger, BufferedReader in)
	{

		String oldPassword = null;
		String newPassword = null;

		// Get old and new encryption key
		// HACK:try-catch are used to avoid null pointer exception generated
		// when Ctrl-c is pressed by user.

		try {
			String condition;
			while (true) {
				condition = in.readLine();
				if (condition.equalsIgnoreCase("yes") || condition.equalsIgnoreCase("no")) {
					break;
				} else {
					logger.log(Level.SEVERE, "Invalid Input. Enter again:");
				}
			}
			if (condition.equalsIgnoreCase("yes") == true) {
				logger.log(Level.FINEST, "Request for change of Encryption key !!");
				oldPassword = CipherUtil.passwordDoubleCheck("old Encryption key:");
				logger.log(Level.FINE, "old password is =" + oldPassword);
				newPassword = CipherUtil.passwordDoubleCheck("new Encryption key:");
				logger.log(Level.FINE, "onew password is =" + newPassword);
				logger.log(Level.FINER, "Encryption key changed");
			} else {
				// ask for Encryption key
				oldPassword = CipherUtil.passwordDoubleCheck("old Encryption key:");
				newPassword = oldPassword;
				logger.log(Level.FINE, "Encryption key is retained");
				logger.log(Level.FINE, "old password is =" + oldPassword);
			}

		} catch (NullPointerException ne) {
			System.exit(0);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "invalid input!!");
		}
		Passwords passwords = new Passwords();
		passwords.setOldPassword(oldPassword);
		passwords.setNewPassword(newPassword);
		passwords.setPasswordisset(true);
		return passwords;

	}
}