package com.intercepter;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

@Intercepts({ @Signature(args = {Statement.class }, method = "handleResultSets", type = ResultSetHandler.class) })
public class MybatisResultHandlerInterceptor implements Interceptor {

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object preResult = invocation.proceed();
		
		if (preResult instanceof List)
			preResult = convert2Camel((List)preResult);
		
		return preResult;
	}
	
	private List convert2Camel(List preResult) {
		List<Object> list = (List) preResult;
		List<Object> result = new ArrayList<Object>();
		for(Object data : list) {
			if(data instanceof Map){
				data = convert2Camel((Map) data);
				result.add(data);
			}else {
				result.add(data);
			}
		}
		return result;
	}
	
	private Map convert2Camel(Map preResult) {
		Map<String, Object> map = (Map<String, Object>) preResult;
		Map<String, Object> result = new HashMap<String, Object>();
		Iterator<String> i = map.keySet().iterator();
		while(i.hasNext()) {
			String key = i.next();
			Object value = map.get(key);
			key = convert2Camel(key);
			result.put(key, value);
		}
		return result;
	}
	
	private String convert2Camel(String preResult) {
		if(preResult.indexOf("_")<0 && Character.isLowerCase(preResult.charAt(0))) {
			return preResult;
		}
		boolean changeUpper = false;
		int len = preResult.length();
		StringBuilder result = new StringBuilder();
		
		for(int i=0; i<len; i++) {
			char Char = preResult.charAt(i);
			if(Char == '_') {
				changeUpper = true;
			}else {
				if(changeUpper == true) {
					result.append(Character.toUpperCase(Char));
					changeUpper = false;
				}else {
					result.append(Character.toLowerCase(Char));
				}
			}
		}
		return result.toString();
	}

}
