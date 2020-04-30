package util;

public class System_Mess {
	public static System_Mess system_Mess = new System_Mess();
	public void ToMessagePrint(String text){
		if(Boolean.parseBoolean(UtilClass.utilClass.getTableName("/parameter.properties", "System_Mess"))){
			System.out.println(text);
		}
	}
}
