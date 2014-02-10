package edu.ntust.transferability;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash
{
	public static String sha256(String base) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(base.getBytes("UTF-8"));
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < hash.length; i++)
		{
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}

		return hexString.toString();
	}

	public static String sha256(File file) throws Exception
	{
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		InputStream fis = new FileInputStream(file);
		int n = 0;
		byte[] buffer = new byte[8192];
		while (n != -1)
		{
			n = fis.read(buffer);
			if (n > 0)
			{
				digest.update(buffer, 0, n);
			}
		}
		fis.close();

		byte[] hash = digest.digest();
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < hash.length; i++)
		{
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}

		return hexString.toString();
	}

	public static String sha1(File file) throws Exception
	{
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		InputStream fis = new FileInputStream(file);
		int n = 0;
		byte[] buffer = new byte[8192];
		while (n != -1)
		{
			n = fis.read(buffer);
			if (n > 0)
			{
				digest.update(buffer, 0, n);
			}
		}
		fis.close();

		byte[] digestBytes = digest.digest();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < digestBytes.length; i++)
		{
			sb.append(Integer.toString((digestBytes[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}

	public static String sha1(String input) throws NoSuchAlgorithmException
	{
		MessageDigest mDigest = MessageDigest.getInstance("SHA-1");
		byte[] result = mDigest.digest(input.getBytes());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < result.length; i++)
		{
			sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}
}
