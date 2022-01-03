package com.kk.matrixapi.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class StrBuilder {

	public static String toJavaType(String mysqlDataType) {

		//default return is String, but this is here for reference
		List<String> stringBasedDataTypes = Arrays.asList(
			new String[]{
				"CHAR",
				"VARCHAR",
				"TINYTEXT",	
				"TEXT",
				"BLOB",
				"MEDIUMTEXT",
				"MEDIUMBLOB",	
				"LONGTEXT",
				"LONGBLOB"
		});
		
		List<String> floatBasedDataTypes = Arrays.asList(
				new String[]{
					"FLOAT"
		});

		List<String> intBasedDataTypes = Arrays.asList(
			new String[]{
				"TINYINT",
				"SMALLINT",
				"MEDIUMINT",
				"INT",
				"BIGINT"
		});

		List<String> doubleBasedDataTypes = Arrays.asList(
			new String[]{
				"DOUBLE",
				"DECIMAL"
		});

		List<String> dateBasedDataTypes = Arrays.asList(
			new String[]{
				"DATE",
				"DATETIME",
				"TIMESTAMP",
				"TIME",
				"YEAR"
		});
		
		String standardizedString = mysqlDataType.trim().toUpperCase().split("\\(")[0].trim();
		
		if (floatBasedDataTypes.contains(standardizedString)) {
			return "float";
		} else if (intBasedDataTypes.contains(standardizedString)) {
			return "int";
		} else if (doubleBasedDataTypes.contains(standardizedString)) {
			return "double";
		} else if (dateBasedDataTypes.contains(standardizedString)) {
			return "Date";
		}
		
		return "String";
	}
	
	
	public static String toJavaClassName(String input) {
		return toJava(input, true);
	}
	
	public static String toJavaPropName(String input) {
		return toJava(input, false);
	}
	
	public static String toJava(String input, boolean isClassName) {
		String retVar = "";
		
		char[] chars = input.toCharArray();
		
		boolean needsToBeCapital = isClassName;
		for(char charac : chars) {
			
			if (charac == '_' || charac == ' ') {
				needsToBeCapital = true;
				continue;
			}

		    if (Pattern.compile("[a-zA-Z]").matcher("" + charac).find( )) {
		    	
				if (needsToBeCapital || Character.isUpperCase(charac)) {
					
					retVar += ("" + charac).toUpperCase();
					
				} else {

					retVar += ("" + charac).toLowerCase();
					
				}
				
		    }

			needsToBeCapital = false;
		}
		
		
		return retVar;
	}

}
