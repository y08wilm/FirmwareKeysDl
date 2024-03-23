package uk.co.filesecur;

import java.util.HashMap;
import java.util.UUID;

import com.google.gson.Gson;

public class FirmwareKeys {

	private HashMap<String, String> keys = new HashMap<>();

	private UUID uuid = UUID.randomUUID();

	public FirmwareKeys() {
	}

	public FirmwareKeys(String json) {
		readFromJson(json);
	}

	public void readFromJson(String json) {
		Gson gson = GsonBuilder.create();
		FirmwareKeys clazz = gson.fromJson(json, FirmwareKeys.class);
		uuid = clazz.uuid;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof FirmwareKeys) {
			FirmwareKeys firmwareKeys = (FirmwareKeys) obj;
			if (uuid.toString().equals(firmwareKeys.uuid.toString())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return exportAsJson();
	}

	public String exportAsJson() {
		Gson gson = GsonBuilder.create();
		return gson.toJson(this);
	}
	
	public HashMap<String, String> getKeys() {
		return keys;
	}

}
