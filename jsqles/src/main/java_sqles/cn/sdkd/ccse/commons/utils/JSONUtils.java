package cn.sdkd.ccse.commons.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class JSONUtils {
	protected static Log logger = LogFactory.getLog(JSONUtils.class);
	/* 当是JSONObject时，key为某个值时，要比较的属性名称； value为属性名称列表 */
	private static Map<String, String> objectCompareKeyProps = new HashMap<String, String>();

	/*
	 * 当时JSONArray时，名称为key值时，采用的比较策略：strict严格匹配（个数与值都对应相等），contain包含（
	 * JSONArray2的每个都包含在JSONArray1中）
	 */
	private static Map<String, String> arrayCompareStrategy = new HashMap<String, String>();

	/* 当时JSONArray时，名称为key值时，采用的排序策略：指定排序属性列表 */
	private static Map<String, String> arraySortStrategy = new HashMap<String, String>();

	public static Map<String, String> getObjectCompareKeyProps() {
		return objectCompareKeyProps;
	}

	public static void setObjectCompareKeyProps(
			Map<String, String> objectCompareKeyProps) {
		JSONUtils.objectCompareKeyProps = objectCompareKeyProps;
	}

	public Map<String, String> getArrayCompareStrategy() {
		return arrayCompareStrategy;
	}

	public void setArrayCompareStrategy(Map<String, String> arrayCompareStrategy) {
		this.arrayCompareStrategy = arrayCompareStrategy;
	}

	public Map<String, String> getArraySortStrategy() {
		return arraySortStrategy;
	}

	public void setArraySortStrategy(Map<String, String> arraySortStrategy) {
		this.arraySortStrategy = arraySortStrategy;
	}

	/* 第一种方法 */
	/* json2 为测试目标,allKeys为所有层次的key值，使用.连接 */
	public static String compareJson(JSONObject json1, JSONObject json2,
                                     String parentKey, String allKeys, IJSONCompareDecision jsoncd)
			throws JSONException {
		Iterator<String> i = json1.keys();
		allKeys = (allKeys.equalsIgnoreCase("")) ? parentKey
				: (allKeys + "." + parentKey);
		while (i.hasNext()) {
			String key = i.next();
			if (objectCompareKeyProps.containsKey(parentKey)) {
				String props = objectCompareKeyProps.get(parentKey);
				if (!props.contains(key + ",")) {
					logger.info("忽略:" + allKeys);
					continue;
				}
			}
			if ((jsoncd != null)
					&& (!jsoncd.needcompare(json1, json2, parentKey, key))) {
				continue;
			}
			if (json2.has(key)) {
				String r = compareJson(json1.get(key), json2.get(key), key,
						allKeys + "." + key, jsoncd);
				if (r != null) {
					return r;
				}
			} else {
				return "缺少:" + allKeys + "." + key;
			}

		}
		return null;
	}

	public static String compareJson(Object json1, Object json2, String key,
			String allKeys, IJSONCompareDecision jsoncd) throws JSONException {
		if (json1 instanceof JSONObject) {
			return compareJson((JSONObject) json1, (JSONObject) json2, key,
					allKeys, jsoncd);
		} else if (json1 instanceof JSONArray) {
			return compareJson((JSONArray) json1, (JSONArray) json2, key,
					allKeys);
		} else if (json1 instanceof String) {
			return compareJson(json1.toString().trim(), String.valueOf(json2)
					.trim(), key, allKeys);
		} else {
			return compareJson(json1.toString().trim(),
					json2.toString().trim(), key, allKeys);
		}
	}

	/* array中每个元素都是JSONObject，且具有相同的属性，默认按照所有属性排序 */
	public static String compareJson(JSONArray json1, JSONArray json2,
                                     String parentKey, String allKeys) throws JSONException {
		int json1Length = json1.length();
		int json2Length = json2.length();
		if (json1Length != json2Length) {
			return (allKeys + " 查询结果记录条数不一致.");
		}
		/* 查询无结果 */
		if (json1Length == 0) {
			return null;
		}
		ArrayList<JSONObject> json1List = toList(json1);
		ArrayList<JSONObject> json2List = toList(json2);
		Collections.sort(json1List, new JSONArrayComparator());
		Collections.sort(json2List, new JSONArrayComparator(json1List.get(0)
				.names()));
		for (int l = 0; l < json1List.size(); l++) {
			allKeys = "[" + l + "]";
			String r = compareJson(json1List.get(l), json2List.get(l),
					parentKey, allKeys, null);
			if (r != null) {
				return r;
			}
		}
		return null;
	}

	public static String compareJson(String str1, String str2, String key,
			String allKeys) {
		if (str1.equalsIgnoreCase("null")) {
			str1 = "";
		}
		if (str2.equalsIgnoreCase("null")) {
			str2 = "";
		}
		if (!str1.equalsIgnoreCase(str2)) {
			return (allKeys + "的值应该是:" + str1 + ", 而不是:" + str2);
		}
		return null;
	}

	public static ArrayList<JSONObject> toList(JSONArray jsonArray) {
		ArrayList<JSONObject> jsonList = new ArrayList<JSONObject>();
		for (int l = 0; l < jsonArray.length(); l++) {
			Object jsonObj = jsonArray.get(l);
			if (jsonObj instanceof JSONObject) {
				jsonList.add((JSONObject) jsonObj);
			} else {
				/* 忽略不是JSONObject的数组元素 */
			}
		}

		return jsonList;
	}

	static class JSONArrayComparator implements Comparator<JSONObject> {
		JSONArray names;

		public JSONArrayComparator(JSONArray names) {
			this.names = names;
		}

		public JSONArrayComparator() {
			names = null;
		}

		@Override
		public int compare(JSONObject o1, JSONObject o2) {
			if (names == null) {
				names = o1.names();
			}
			for (int i = 0; i < names.length(); i++) {
				String name = names.getString(i);
				Object o = o1.get(name);
				if (!(o instanceof JSONObject) && !(o instanceof JSONArray)) {
					String os = o.toString();
					if (o2.has(name)) {
						String o2s = o2.get(name).toString();
						int r = os.compareToIgnoreCase(o2s);
						if (r != 0) {
							return r;
						}
					} else {
						return -1;
					}
				}
			}
			return 0;
		}
	}

	/* 初始化比较属性列表 */
	static {
		Map<String, String> props = JSONUtils.getObjectCompareKeyProps();
		props.clear();
		props.put("database", "name,files,tables,defaults,rules,");
		props.put("tables",
				"schema_name,type,name,full_name,columns,constraints,indexes,");
		props.put("columns", "Column_name,Type,Length,Prec,Scale,Nullable,");
		props.put("constraints", "constraint_type,constraint_keys,");
		props.put("files", "name,size,filegroup,growth,usage,");
		props.put("indexes", "index_name,index_description,index_keys,");
		props.put("defaults", "schema_name,type,name,text,");
		props.put("rules", "schema_name,type,name,text,");
	}

	public static void main(String[] args) {
		String answer = "{'database':[{'name':'testdb','db_size':'      2.73 MB','owner':'WIN-SUG54QP3D9Q\\Sam','dbid':14,'created':'07 13 2016 ','status':'Status=ONLINE, Updateability=READ_WRITE, UserAccess=MULTI_USER, Recovery=FULL, Version=661, Collation=Chinese_PRC_CI_AS, SQLSortOrder=0, IsAutoCreateStatistics, IsAutoUpdateStatistics, IsFullTextEnabled','compatibility_level':100,'files':[{'name':'testdb','fileid':1,'filename':'D:\\Program Files\\Microsoft SQL Server\\MSSQL.1\\MSSQL\\DATA\\testdb.mdf','filegroup':'PRIMARY','size':'2240 KB','maxsize':'Unlimited','growth':'1024 KB','usage':'data only'},{'name':'testdb_log','fileid':2,'filename':'D:\\Program Files\\Microsoft SQL Server\\MSSQL.1\\MSSQL\\DATA\\testdb_log.LDF','filegroup':null,'size':'560 KB','maxsize':'2147483648 KB','growth':'10%','usage':'log only'}],'tables':[{'schema_name':'dbo','type':'user table','name':'student','full_name':'dbo.student','object_id':261575970,'columns':[{'Column_name':'sno','Type':'char','Computed':'no','Length':10,'Prec':'     ','Scale':'     ','Nullable':'no','TrimTrailingBlanks':'no','FixedLenNullInSource':'no','Collation':'Chinese_PRC_CI_AS'},{'Column_name':'sname','Type':'char','Computed':'no','Length':8,'Prec':'     ','Scale':'     ','Nullable':'yes','TrimTrailingBlanks':'no','FixedLenNullInSource':'yes','Collation':'Chinese_PRC_CI_AS'},{'Column_name':'sage','Type':'char','Computed':'no','Length':10,'Prec':'     ','Scale':'     ','Nullable':'yes','TrimTrailingBlanks':'no','FixedLenNullInSource':'yes','Collation':'Chinese_PRC_CI_AS'},{'Column_name':'ssex','Type':'char','Computed':'no','Length':6,'Prec':'     ','Scale':'     ','Nullable':'yes','TrimTrailingBlanks':'no','FixedLenNullInSource':'yes','Collation':'Chinese_PRC_CI_AS'},{'Column_name':'sdept','Type':'char','Computed':'no','Length':8,'Prec':'     ','Scale':'     ','Nullable':'yes','TrimTrailingBlanks':'no','FixedLenNullInSource':'yes','Collation':'Chinese_PRC_CI_AS'}],'indexes':[{'index_name':'PK__student__DDDF64467F60ED59','index_description':'clustered, unique, primary key located on PRIMARY','index_keys':'sno'}],'constraints':[{'constraint_type':'PRIMARY KEY (clustered)','constraint_name':'PK__student__DDDF64467F60ED59','delete_action':'(n/a)','update_action':'(n/a)','status_enabled':'(n/a)','status_for_replication':'(n/a)','constraint_keys':'sno'}],'data':[]}]}]}";
		answer = answer.replace("\\", "/");
		String eval = "{'database':[{'name':'testdb','tables':[{'schema_name':'dbo','type':'U ','name':'student','object_id':261575970,'full_name':'dbo.student','columns':[{'Column_name':'sno','Type':'char','Computed':'no','Length':10,'Prec':'     ','Scale':'     ','Nullable':'no','TrimTrailingBlanks':'no','FixedLenNullInSource':'no','Collation':'Chinese_PRC_CI_AS'},{'Column_name':'sname','Type':'char','Computed':'no','Length':8,'Prec':'     ','Scale':'     ','Nullable':'yes','TrimTrailingBlanks':'no','FixedLenNullInSource':'yes','Collation':'Chinese_PRC_CI_AS'},{'Column_name':'sage','Type':'char','Computed':'no','Length':10,'Prec':'     ','Scale':'     ','Nullable':'yes','TrimTrailingBlanks':'no','FixedLenNullInSource':'yes','Collation':'Chinese_PRC_CI_AS'},{'Column_name':'ssex','Type':'char','Computed':'no','Length':6,'Prec':'     ','Scale':'     ','Nullable':'yes','TrimTrailingBlanks':'no','FixedLenNullInSource':'yes','Collation':'Chinese_PRC_CI_AS'},{'Column_name':'sdept','Type':'char','Computed':'no','Length':8,'Prec':'     ','Scale':'     ','Nullable':'yes','TrimTrailingBlanks':'no','FixedLenNullInSource':'yes','Collation':'Chinese_PRC_CI_AS'}],'indexes':[{'index_name':'PK__student__DDDF6446117F9D94','index_description':'clustered, unique, primary key located on PRIMARY','index_keys':'sno'}],'constraints':[{'constraint_type':'PRIMARY KEY (clustered)','constraint_name':'PK__student__DDDF6446117F9D94','delete_action':'(n/a)','update_action':'(n/a)','status_enabled':'(n/a)','status_for_replication':'(n/a)','constraint_keys':'sno'}],'data':[]}]}]}";
		eval = eval.replace("\\", "/");
		JSONObject answero = new JSONObject(answer);
		JSONObject evalo = new JSONObject(eval);

		Map<String, String> props = JSONUtils.getObjectCompareKeyProps();
		props.clear();
		props.put("database", "name,files,tables,");
		props.put("tables",
				"schema_name,type,name,full_name,columns,constraints,");
		props.put("columns", "Column_name,Type,Length,Prec,Scale,Nullable,");
		props.put("constraints", "constraint_type,constraint_keys,");
		props.put("files", "name,filegroup,growth,usage,");

		String r = JSONUtils.compareJson(evalo, answero, "", "", null);
		System.out.println(r);
	}

	/* 第二种方法 */
	public static boolean areEqual(Object ob1, Object ob2) throws JSONException {
		Object obj1Converted = convertJsonElement(ob1);
		Object obj2Converted = convertJsonElement(ob2);
		return obj1Converted.equals(obj2Converted);
	}

	private static Object convertJsonElement(Object elem) throws JSONException {
		if (elem instanceof JSONObject) {
			JSONObject obj = (JSONObject) elem;
			Iterator<String> keys = obj.keys();
			Map<String, Object> jsonMap = new HashMap<String, Object>();
			while (keys.hasNext()) {
				String key = keys.next();
				jsonMap.put(key, convertJsonElement(obj.get(key)));
			}
			return jsonMap;
		} else if (elem instanceof JSONArray) {
			JSONArray arr = (JSONArray) elem;
			Set<Object> jsonSet = new HashSet<Object>();
			for (int i = 0; i < arr.length(); i++) {
				jsonSet.add(convertJsonElement(arr.get(i)));
			}
			return jsonSet;
		} else {
			return elem;
		}
	}

	/* 第四种方法 */
	public static boolean jsonObjsAreEqual(JSONObject js1, JSONObject js2)
			throws JSONException {
		if (js1 == null || js2 == null) {
			return (js1 == js2);
		}

		List<String> l1 = Arrays.asList(JSONObject.getNames(js1));
		Collections.sort(l1);
		List<String> l2 = Arrays.asList(JSONObject.getNames(js2));
		Collections.sort(l2);
		if (!l1.equals(l2)) {
			return false;
		}
		for (String key : l1) {
			Object val1 = js1.get(key);
			Object val2 = js2.get(key);
			if (val1 instanceof JSONObject) {
				if (!(val2 instanceof JSONObject)) {
					return false;
				}
				if (!jsonObjsAreEqual((JSONObject) val1, (JSONObject) val2)) {
					return false;
				}
			}

			if (val1 == null) {
				if (val2 != null) {
					return false;
				}
			} else if (!val1.equals(val2)) {
				return false;
			}
		}
		return true;
	}

	/* 第五种方法 */
	public static boolean jsonsEqual(Object obj1, Object obj2)
			throws JSONException

	{
		if (!obj1.getClass().equals(obj2.getClass())) {
			return false;
		}

		if (obj1 instanceof JSONObject) {
			JSONObject jsonObj1 = (JSONObject) obj1;

			JSONObject jsonObj2 = (JSONObject) obj2;

			String[] names = JSONObject.getNames(jsonObj1);
			String[] names2 = JSONObject.getNames(jsonObj1);
			if (names.length != names2.length) {
				return false;
			}

			for (String fieldName : names) {
				Object obj1FieldValue = jsonObj1.get(fieldName);

				Object obj2FieldValue = jsonObj2.get(fieldName);

				if (!jsonsEqual(obj1FieldValue, obj2FieldValue)) {
					return false;
				}
			}
		} else if (obj1 instanceof JSONArray) {
			JSONArray obj1Array = (JSONArray) obj1;
			JSONArray obj2Array = (JSONArray) obj2;

			if (obj1Array.length() != obj2Array.length()) {
				return false;
			}

			for (int i = 0; i < obj1Array.length(); i++) {
				boolean matchFound = false;

				for (int j = 0; j < obj2Array.length(); j++) {
					if (jsonsEqual(obj1Array.get(i), obj2Array.get(j))) {
						matchFound = true;
						break;
					}
				}

				if (!matchFound) {
					return false;
				}
			}
		} else {
			if (!obj1.equals(obj2)) {
				return false;
			}
		}

		return true;
	}
}
