package uk.co.filesecur;

import com.google.gson.Gson;

public class GsonBuilder {
	
	public static Gson gson;
	
	static {
		gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
	}

	public static Gson create() {
		// return new com.google.gson.GsonBuilder().setPrettyPrinting().create();
		return gson;
	}

}
