package cn.sdkd.ccse.commons.utils;

import org.json.JSONObject;

public interface IJSONCompareDecision {

	public boolean needcompare(JSONObject o1, JSONObject o2, String parentKey, String key);
	
}
