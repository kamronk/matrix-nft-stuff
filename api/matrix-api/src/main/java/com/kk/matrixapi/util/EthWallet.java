package com.kk.matrixapi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class EthWallet {

	public static void main(String[] args) {
		System.out.println("Balance is " + EthWallet.getDaiBalance("0x292c39c7e2b08cd331f20586084d0f400a0d7d23"));
		System.out.println("Balance is " + EthWallet.getDaiBalance("0x91db9630eaae9814f05ff9c04c48f2d3bb723391"));
		System.out.println("New wallet is " + EthWallet.getNewAddress());
		System.out.println("tx of sent is " + EthWallet.sendToAddress("0x6D1e7c53006F68fa4DFFAF4E5C626eeAf8154985", 0.01));
	}

	public static String sendToAddress(String toAddress, double amount) {
		String response = null;
		long divisor = 1000000000000000000L;
		long wholeAmount = (long) (amount * divisor);
		try {
			String addressRaw = EthWallet.execCmd("ethereum-cli --sendtoaddress " + toAddress + " --password \"Amber20130604\" --erc20 0x4C1f6fCBd233241bF2f4D02811E3bF8429BC27B8 --amount " + wholeAmount + "");
			response = addressRaw.trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	public static String sendToAddressFromAddress(String toAddress, String fromAddress, double amount) {
		String response = null;
		long divisor = 1000000000000000000L;
		long wholeAmount = (long) (amount * divisor);
		try {
			String command = "ethereum-cli --sendtoaddress " + toAddress + " "
					+ "--password \"Amber20130604\" --erc20 0x4C1f6fCBd233241bF2f4D02811E3bF8429BC27B8 "
					+ "--amount " + wholeAmount + " "
					+ "--from " + fromAddress;
			String addressRaw = EthWallet.execCmd(command);
			response = addressRaw.trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	public static String getNewAddress() {
		String address = null;
		try {
			String addressRaw = EthWallet.execCmd("ethereum-cli --getnewaddress --password \"Amber20130604\"");
			address = addressRaw.trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return address;
	}

	public static double getDaiBalance(String address) {
		double balance = -1;
		try {
			String balanceRaw = EthWallet.execCmd("ethereum-cli --getbalance " + address + " --erc20 0x4C1f6fCBd233241bF2f4D02811E3bF8429BC27B8");
			long balanceLong = Long.parseLong(balanceRaw.trim());
			long divisor = 1000000000000000000L;
			balance = (double) balanceLong / divisor;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return balance;
		
	}
	
	private static String execCmd(String cmd) throws java.io.IOException {
		boolean isWindows = System.getProperty("os.name")
				  .toLowerCase().startsWith("windows");
		if (isWindows) {
			cmd = "cmd.exe /c " + cmd;
		    java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
		    return s.hasNext() ? s.next() : "";
		} else {

			
			String[] mainCommand = {"/bin/bash", "-c", cmd};
			
			ProcessBuilder pb = new ProcessBuilder(mainCommand);
			Process p = pb.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ( (line = reader.readLine()) != null) {
			   builder.append(line);
			   builder.append(System.getProperty("line.separator"));
			}
			String result = builder.toString();
			return result;
		}
	}
}
