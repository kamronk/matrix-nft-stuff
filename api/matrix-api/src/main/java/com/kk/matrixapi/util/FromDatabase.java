package com.kk.matrixapi.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.kk.matrixapi.db.DbPooledDataSource;

public class FromDatabase {
	
	public static void main(String[] args) {

//		String pathForNewClasses = "/Users/kamronkennedy/eclipse-workspace/RepFlow/src/entity/";
		String pathForNewClasses = "C:\\Users\\kamro\\repos\\matrix-fight-club\\com.kk.matrix-api\\src\\main\\java\\com\\kk\\matrixapi\\model\\db\\";
		boolean useServerContext = false;
		
		ArrayList<String> tableNames = getTableNames();
		
		String dataSourceObjName = "DbPooledDataSource";
		String dataSourceJavaPackage = "com.kk.matrixapi.db";
		String packageName = "com.kk.matrixapi.model.db";
		
		for (String tableName : tableNames) {
			String fileName = StrBuilder.toJavaClassName(tableName) + ".java";
			System.out.println("#" + fileName);
			String javaClass = getJavaClassFileBody(tableName, packageName, dataSourceObjName, dataSourceJavaPackage, useServerContext);
			if (javaClass == null) {
				continue;
			}
//			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~`\n"
//					+ "" + javaClass + "\n"
//							+ "~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			
			try {
				File existingFile = new File(pathForNewClasses + fileName);
				if (existingFile.exists()) {
					existingFile.delete();
				}
				BufferedWriter out = new BufferedWriter(new FileWriter(pathForNewClasses + fileName));
				out.write(javaClass);
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	private static String getJavaClassFileBody(String tableName, String packageName, String dataSourceObjName, String dataSourceJavaPackage, boolean useServerContext) {

		String className = StrBuilder.toJavaClassName(tableName);
		
		ArrayList<String> props = new ArrayList<String>(); 
			
		ArrayList<String> columnNames = getColumnNames(tableName);
		
		for (String columnRaw : columnNames) {
			props.add(columnRaw);
		}
		
		String javaClass = "";

		javaClass += "package " + packageName + ";\n\n";

		javaClass += "import " + dataSourceJavaPackage + "." + dataSourceObjName + ";\n";
		javaClass += "import java.sql.Connection;\n";
		javaClass += "import java.sql.PreparedStatement;\n";
		javaClass += "import java.sql.ResultSet;\n";
		javaClass += "import java.sql.SQLException;\n";
		javaClass += "import java.sql.Statement;\n";
		javaClass += "import java.util.ArrayList;\n\n";
		javaClass += "import java.util.Date;\n\n";
		
		if (useServerContext)
			javaClass += "import javax.servlet.ServletContext;\n\n";

		javaClass += "public class " + className + " {\n\n";
		
		//declare class properties
		for(String propRaw : props) {
			String propName = StrBuilder.toJavaPropName(propRaw.split("~")[0].trim());
			String propType = StrBuilder.toJavaType(propRaw.split("~")[1].trim());
			
			javaClass += "\t" + "private " + propType + " " + propName + ";\n";
		}
		javaClass += "\n";

		//getters & setters
		for(String propRaw : props) {
			String propName = StrBuilder.toJavaPropName(propRaw.split("~")[0].trim());
			String propType = StrBuilder.toJavaType(propRaw.split("~")[1].trim());
			
			//getter
			javaClass += "\t" + "public " + propType + " get" + StrBuilder.toJava(propName, true) + "(){\n" + 
					"\t\t" + "return this." + propName + ";\n\t}\n";
			//setter
			javaClass += "\t" + "public void set" + StrBuilder.toJava(propName, true) + "(" + propType + " " + propName + "In){\n" + 
					"\t\t" + "this." + propName + " = " + propName + "In;\n\t}\n";
			javaClass += "\n";
		}

		//Constructor (with all params)
		String constBody = "\n";
		String constDeclarationParams = "";
		boolean firstConstProp = true;
		for(String propRaw : props) {
			String propName = StrBuilder.toJavaPropName(propRaw.split("~")[0].trim());
			String propType = StrBuilder.toJavaType(propRaw.split("~")[1].trim());
			if (firstConstProp) {
				firstConstProp = false;
				constDeclarationParams += propType + " " +  propName;
			} else {
				constDeclarationParams += ", " + propType + " " +  propName;
			}
			constBody += "\t\t" + "this." + propName + " = " + propName + ";\n";
		}
		javaClass += "\t" + "public " + className + "(" + constDeclarationParams + "){" + constBody + "\t}\n";
		
		//Constructor (empty)
		javaClass += "\t" + "public " + className + "(){}\n";
		
		//static select *
		String selectSql = "SELECT * FROM " + tableName + "";
		String methodSelect = 
				"\t" + "public static ArrayList<" + className + "> get" + className + "s(" + ((useServerContext) ? "ServletContext context" : "") + "){" + "\n" +
				"\t\t" + "ArrayList<" + className + "> elems = new ArrayList<" + className + ">();" + "\n" +
				"\t\t" + "try {" + "\n" +
				"\t\t\t" + "Connection connection = " + dataSourceObjName + ".getConnection(" + ((useServerContext) ? "context" : "") + ");" + "\n" +
				"\t\t\t" + "String selectQry = \" " + selectSql + " \"" + ";\n" +
				"\t\t\t" + "Statement stmt = connection.createStatement();" + "\n" +
				"\t\t\t" + "ResultSet rs = stmt.executeQuery(selectQry);" + "\n" +
				"\t\t\t" + "while (rs.next()) {" + "\n";

		String selectSetBody = "";
		for(int i = 0; i < props.size(); i++) {
			String propRaw = props.get(i);
			String propName = StrBuilder.toJavaPropName(propRaw.split("~")[0].trim());
			String propType = StrBuilder.toJavaType(propRaw.split("~")[1].trim());
			if (i == 0) 
				selectSetBody += "\t\t\t\t\t" + "rs.get" + StrBuilder.toJava(propType, true) + "(" + (i+1) + ")\n";
			else
				selectSetBody += "\t\t\t\t\t" + ", rs.get" + StrBuilder.toJava(propType, true) + "(" + (i+1) + ")\n";
		}
		
		methodSelect += 
				"\t\t\t\t" + "elems.add(new " + className + " ( " + "\n" +
						selectSetBody + "\n" +
				"\t\t\t\t" + "));" + "\n";
		

		methodSelect += 
				"\t\t" + "}" + "\n" +
				"\t\t" + "rs.close();" + "\n" +
				"\t\t" + "stmt.close();" + "\n" +
				"\t\t" + "connection.close();" + "\n" +
				"\t\t" + "} catch (NumberFormatException nfe) {" + "\n" +
				"\t\t\t" + "nfe.printStackTrace();" + "\n" +
				"\t\t" + "} catch (SQLException se) {" + "\n" +
				"\t\t\t" + "se.printStackTrace();" + "\n" +
				"\t\t" + "}" + "\n" +
				"\t\t" + "return elems;" + "\n" +
				"\t" + "}" + "\n";
		
		javaClass += "\n" + methodSelect + "\n";
		
		
		//int insert
		String insertSql = "INSERT INTO " + tableName + " (";
		for (int i = 0; i < columnNames.size(); i++) {
			String columnRaw = columnNames.get(i);
			String columnName = columnRaw.split("~")[0].trim();
			insertSql += (i == 0) ? columnName : ", " + columnName;
		}
		insertSql += ") VALUES (";

		String preparedStatementSetBody = "";
		String preparedStatmentObjName = "prep" + className + "Stat";
		for(int i = 0; i < props.size(); i++) {
			String propRaw = props.get(i);
			String propName = StrBuilder.toJavaPropName(propRaw.split("~")[0].trim());
			String propType = StrBuilder.toJavaType(propRaw.split("~")[1].trim());
			
			insertSql += (i == 0) ? "?" : ",?";
			
			if (propType.equals("Date")) {
				propName = "new java.sql.Date(" + propName + ".getTime())";
			}
			
			preparedStatementSetBody += "\t\t\t" + preparedStatmentObjName + ".set" + StrBuilder.toJava(propType, true) + "(" + (i+1) + ", " + propName + ");\n";
			
		}

		insertSql += ")";
		
		String methodInsert = "\t" + "public int insert" + className + "(";
		for(int i = 0; i < props.size(); i++) {
			String propRaw = props.get(i);
			String propName = StrBuilder.toJavaPropName(propRaw.split("~")[0].trim());
			String propType = StrBuilder.toJavaType(propRaw.split("~")[1].trim());

			methodInsert += (i == 0) ? propType + " " + propName : ", " + propType + " " + propName;
			
		}
		methodInsert += "" + ((useServerContext) ? ", ServletContext context" : "") + ") {\n";

		methodInsert +=
			"\t\t" + "int retVar = 0;" + "\n" +
			"\t\t" + "try {" + "\n" +
			"\t\t\t" + "Connection connection" + className + " = " + dataSourceObjName + ".getConnection(" + ((useServerContext) ? "context" : "") + ");" + "\n" +
			"\t\t\t" + "String insert" + className + "stmtStr = \"" + insertSql + "\";" + "\n" +
			"\t\t\t" + "PreparedStatement " + preparedStatmentObjName + " = connection" + className + ".prepareStatement(insert" + className + "stmtStr);" + "\n" +
			"" + "" + preparedStatementSetBody + "" + "" +
			"\t\t\t" + "" + preparedStatmentObjName + ".executeUpdate();" + "\n" +
			"\t\t\t" + "ResultSet generatedKeys = " + preparedStatmentObjName + ".getGeneratedKeys();" + "\n" +
			"\t\t\t" + "if(generatedKeys.next()){" + "\n" +
			"\t\t\t\t" + "retVar = generatedKeys.getInt(1);" + "\n" +
			"\t\t\t" + "}" + "\n" +
			"\t\t\t" + "generatedKeys.close();" + "\n" +
			"\t\t\t" + "" + preparedStatmentObjName + ".close();" + "\n" +
			"\t\t\t" + "connection" + className + ".close();" + "\n" +
			"\t\t" + "} catch (NumberFormatException nfe) {" + "\n" +
			"\t\t\t" + "nfe.printStackTrace();" + "\n" +
			"\t\t" + "} catch (SQLException e) {" + "\n" +
			"\t\t\t" + "e.printStackTrace();" + "\n" +
			"\t\t" + "}" + "\n" + 
			"\t\treturn retVar;" + "\n" +
			"\t}";
		
		javaClass += "" + methodInsert + "\n\n";
		
		//boolean update
		String pkColumnRaw = getPkColumn(tableName);
		String updateSql = "UPDATE " + tableName + " ";
		for (int i = 0; i < columnNames.size(); i++) {
			String columnRaw = columnNames.get(i);
			String columnName = columnRaw.split("~")[0].trim();
			updateSql += (i == 0) ? "SET " + columnName + " = ? " : ", " + columnName + " = ? ";
		}
		updateSql += " WHERE " + pkColumnRaw.split("~")[0] + " = ?";

		String methodUpdate = "\t" + "public boolean update" + className + "(";
		
		String preparedStatementUpdateBody = "";
		for(int i = 0; i < props.size(); i++) {
			String propRaw = props.get(i);
			String propName = StrBuilder.toJavaPropName(propRaw.split("~")[0].trim());
			String propType = StrBuilder.toJavaType(propRaw.split("~")[1].trim());
			methodUpdate += (i==0) ? propType + " " + propName : ", " + propType + " " + propName;

			if (propType.equals("Date")) {
				propName = "new java.sql.Date(" + propName + ".getTime())";
			}
			
			preparedStatementUpdateBody += "\t\t\tstat.set" + StrBuilder.toJava(propType, true) + "(" + (i+1) + ", " + propName + ");\n";
		}
		methodUpdate += "" + ((useServerContext) ? ", ServletContext context" : "") + ") {\n";

		methodUpdate += 
			"\t\t" + "boolean retVar = false;" + "\n" +
			"\t\t" + "try {" + "\n" +
			"\t\t\t" + "Connection connection = " + dataSourceObjName + ".getConnection(" + ((useServerContext) ? "context" : "") + ");" + "\n" +
			"\t\t\t" + "String stmtStr = \"" + updateSql + "\";" + "\n" +
			"\t\t\t" + "PreparedStatement stat = connection.prepareStatement(stmtStr);" + "\n" +
			"" + "" + preparedStatementUpdateBody + "" + "" +
			"\t\t\t" + "stat.executeUpdate();" + "\n" +
			"\t\t\t" + "retVar = true;" + "\n" +
			"\t\t\t" + "stat.close();" + "\n" +
			"\t\t\t" + "connection.close();" + "\n" +
			"\t\t" + "} catch (NumberFormatException nfe) {" + "\n" +
			"\t\t\t" + "nfe.printStackTrace();" + "\n" +
			"\t\t" + "} catch (SQLException e) {" + "\n" +
			"\t\t\t" + "e.printStackTrace();" + "\n" +
			"\t\t" + "}" + "\n" +
			"\t\t" + "return retVar;" + "\n" +
			"\t}";

		javaClass += "" + methodUpdate + "\n\n";

		//boolean delete
		if (pkColumnRaw == "") {
			return null;
		}
		String pkObjType = StrBuilder.toJavaType(pkColumnRaw.split("~")[1]);
		String pkObjName = StrBuilder.toJavaPropName(pkColumnRaw.split("~")[0]);
		String methodDelete = "\t" + "public boolean delete" + className + "(" + pkObjType + " " + pkObjName + "" + ((useServerContext) ? ", ServletContext context" : "") + ") {\n";

		methodDelete += 
			"\t\t" + "boolean retVar = false;" + "\n" +
			"\t\t" + "try {" + "\n" +
			"\t\t\t" + "Connection connection = " + dataSourceObjName + ".getConnection(" + ((useServerContext) ? "context" : "") + ");" + "\n" +
			"\t\t\t" + "String stmtStr = \"DELETE FROM " + tableName + " WHERE " + pkColumnRaw.split("~")[0] + " = ?\";" + "\n" +
			"\t\t\t" + "PreparedStatement stat = connection.prepareStatement(stmtStr);" + "\n" +
			"\t\t\t" + "stat.set" + StrBuilder.toJava(StrBuilder.toJavaType(pkColumnRaw.split("~")[1]), true) + "(1, " + pkObjName + ");" + "\n" +
			"\t\t\t" + "stat.executeUpdate();" + "\n" +
			"\t\t\t" + "retVar = true;" + "\n" +
			"\t\t\t" + "stat.close();" + "\n" +
			"\t\t\t" + "connection.close();" + "\n" +
			"\t\t" + "} catch (NumberFormatException nfe) {" + "\n" +
			"\t\t\t" + "nfe.printStackTrace();" + "\n" +
			"\t\t" + "} catch (SQLException e) {" + "\n" +
			"\t\t\t" + "e.printStackTrace();" + "\n" +
			"\t\t" + "}" + "\n" +
			"\t\t" + "return retVar;" + "\n" +
			"\t}";

		javaClass += methodDelete + "\n";
		
		//done
		javaClass += "\n}\n";
		
		return javaClass;
	}

	public static ArrayList<String> getTableNames(){

		ArrayList<String> retVar = new ArrayList<String>();
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String query = "show tables";
			PreparedStatement stat = connection.prepareStatement(query);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				retVar.add(rs.getString(1));
			} 
			rs.close();
			stat.close();
			connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			System.out.println("FromDatabase.java (class) :: "
					+ nfe.getLocalizedMessage());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("FromDatabase.java (class) :: " + e.getErrorCode()
					+ " " + e.getLocalizedMessage());
		}
		return retVar;
		
	}

	public static ArrayList<String> getColumnNames(String tableName){

		ArrayList<String> retVar = new ArrayList<String>();
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String query = "SHOW COLUMNS FROM " + tableName;
			PreparedStatement stat = connection.prepareStatement(query);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				retVar.add(rs.getString(1) + "~" + rs.getString(2));
			} 
			rs.close();
			stat.close();
			connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			System.out.println("FromDatabase.java (class) :: "
					+ nfe.getLocalizedMessage());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("FromDatabase.java (class) :: " + e.getErrorCode()
					+ " " + e.getLocalizedMessage());
		}
		return retVar;
		
	}

	public static String getPkColumn(String tableName){

		String retVar = "";
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String query = "SHOW COLUMNS FROM " + tableName + "";
			PreparedStatement stat = connection.prepareStatement(query);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				if (rs.getString(4).equals("PRI")) retVar = rs.getString(1) + "~" + rs.getString(2);
			} 
			rs.close();
			stat.close();
			connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			System.out.println("FromDatabase.java (class) :: "
					+ nfe.getLocalizedMessage());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("FromDatabase.java (class) :: " + e.getErrorCode()
					+ " " + e.getLocalizedMessage());
		}
		return retVar;
		
	}

}
