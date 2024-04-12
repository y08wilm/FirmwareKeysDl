package uk.co.filesecur;

import java.util.Optional;

public class cPanel {

	public static void printf(String str) {
		System.out.println(str);
	}

	public static void helpCmd() {
		printf("Usage: FirmwareKeysDl [OPTION...] [FILE...] [VERSION]... [MODEL]...");
		printf("GNU 'FirmwareKeysDl' lets you get firmware keys from theapplewiki.com, and can");
		printf("be used in automated scripts quite easily.");
		printf("");
		printf("Examples:");
		printf("  FirmwareKeysDl -ivkey 058-2384-003.dmg 7.0.6 iPhone6,1");
		printf("  FirmwareKeysDl -ivkey kernelcache.release.n51 7.0.6 iPhone6,1");
		printf("  FirmwareKeysDl -ivkey iBSS.n51ap.RELEASE.im4p 7.0.6 iPhone6,1");
		printf("  FirmwareKeysDl -ivkey iBEC.n51ap.RELEASE.im4p 7.0.6 iPhone6,1");
		printf("  FirmwareKeysDl -ivkey DeviceTree.n51ap.im4p 7.0.6 iPhone6,1");
		printf("  FirmwareKeysDl -l 7.0.6 iPhone6,1");
		printf("  FirmwareKeysDl -b 7.0.6 iPhone6,1");
		printf("  FirmwareKeysDl -e 14.3 iPhone6,1");
		printf("");
		printf("Main operation mode:");
		printf("  -ivkey, --ivkey               gets the iv+key for the given file");
		printf("  -iv, --iv                     gets only the iv for the given file");
		printf("  -key, --key                   gets only the key for the given file");
		printf("  -l, --l, -list, --list        gets all the keys for a given version");
		printf("  -b, --b, -buildid, --buildid  gets the build id for a given version");
		printf("  -e, --e, -exists, --exists    checks if the version exists for this device");
		printf("  -help, --help, ?              shows this help listing");
	}

	public static void main(String[] arg0) {
		// TODO Auto-generated method stub
		String cmd = String.join(" ", arg0);
		int index;
		if (cmd == null || cmd.trim().length() == 0 || cmd.trim().equals("")) {
			helpCmd();
			return;
		}
		cmd = cmd.trim();
		String command = (index = cmd.indexOf(" ")) != -1 ? cmd.substring(0,
				index) : cmd;
		String rawArgs = cmd.length() > command.length() ? cmd
				.substring(command.length() + 1) : "";
		String[] args = cmd.substring(command.length()).length() > 0 ? rawArgs
				.split(" ") : new String[0];
		if (command.equals("--ivkey") || command.equals("-ivkey")) {
			// TODO: GENERATE GAY TOKEN
			if (args.length != 3) {
				printf("FirmwareKeysDl -ivkey [FILE]... [VERSION]... [MODEL]...");
				return;
			}
			String fn = args[0];
			String version = args[1];
			String model = args[2];
			TheAppleWikiHTTPClient httpClient = new TheAppleWikiHTTPClient(
					version, model);
			httpClient.setBuildId(httpClient.getBuildIdForVersion(version).get());
			String ivkey = httpClient.getFirmwareKey(fn).get();
			printf(ivkey);
		} else if (command.equals("--iv") || command.equals("-iv")) {
			// TODO: GENERATE GAY TOKEN
			if (args.length != 3) {
				printf("FirmwareKeysDl -ivkey [FILE]... [VERSION]... [MODEL]...");
				return;
			}
			String fn = args[0];
			String version = args[1];
			String model = args[2];
			TheAppleWikiHTTPClient httpClient = new TheAppleWikiHTTPClient(
					version, model);
			httpClient.setBuildId(httpClient.getBuildIdForVersion(version).get());
			String ivkey = httpClient.getFirmwareKey(fn).get();
			String iv = ivkey.substring(0, 32);
			String key = ivkey.substring(32);
			printf(iv);
		} else if (command.equals("--iv") || command.equals("-iv")) {
			// TODO: GENERATE GAY TOKEN
			if (args.length != 3) {
				printf("FirmwareKeysDl -ivkey [FILE]... [VERSION]... [MODEL]...");
				return;
			}
			String fn = args[0];
			String version = args[1];
			String model = args[2];
			TheAppleWikiHTTPClient httpClient = new TheAppleWikiHTTPClient(
					version, model);
			httpClient.setBuildId(httpClient.getBuildIdForVersion(version).get());
			String ivkey = httpClient.getFirmwareKey(fn).get();
			String iv = ivkey.substring(0, 32);
			String key = ivkey.substring(32);
			printf(key);
		} else if (command.equals("--l") || command.equals("-l")
				|| command.equals("--list") || command.equals("-list")) {
			// TODO: GENERATE GAY TOKEN
			if (args.length != 2) {
				printf("FirmwareKeysDl -l [VERSION]... [MODEL]...");
				return;
			}
			String version = args[0];
			String model = args[1];
			TheAppleWikiHTTPClient httpClient = new TheAppleWikiHTTPClient(
					version, model);
			httpClient.setBuildId(httpClient.getBuildIdForVersion(version).get());
			FirmwareKeys keys = httpClient.getAllFirmwareKeys();
		} else if (command.equals("--b") || command.equals("-b")
				|| command.equals("--buildid") || command.equals("-buildid")) {
			// TODO: GENERATE GAY TOKEN
			if (args.length != 2) {
				printf("FirmwareKeysDl -b [VERSION]... [MODEL]...");
				return;
			}
			String version = args[0];
			String model = args[1];
			TheAppleWikiHTTPClient httpClient = new TheAppleWikiHTTPClient(
					version, model);
			Optional<String> buildId = httpClient.getBuildIdForVersion(version);
			if (buildId.isPresent()) {
				System.out.println(buildId.get());
			} else {
				System.out.println("build id does not exist");
			}
		} else if (command.equals("--e") || command.equals("-e")
				|| command.equals("--exists") || command.equals("-exists")) {
			// TODO: GENERATE GAY TOKEN
			if (args.length != 2) {
				printf("FirmwareKeysDl -e [VERSION]... [MODEL]...");
				return;
			}
			String version = args[0];
			String model = args[1];
			TheAppleWikiHTTPClient httpClient = new TheAppleWikiHTTPClient(
					version, model);
			Optional<String> buildId = httpClient.getBuildIdForVersion(version);
			if (buildId.isPresent()) {
				System.out.println("true");
			} else {
				System.out.println("false");
			}
		} else {
			helpCmd();
		}
	}

}
