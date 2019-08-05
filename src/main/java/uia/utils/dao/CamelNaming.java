package uia.utils.dao;

public class CamelNaming {

	public static String lower(String value) {
		String[] data = value.split("_");
		StringBuilder b = new StringBuilder();
		b.append(data[0].toLowerCase());
		for(int i=1; i<data.length; i++) {
			b.append(data[i].substring(0,  1).toUpperCase()).append(data[i].substring(1).toLowerCase());
		}
		return b.toString();
	}
	
	public static String upper(String value) {
		String[] data = value.split("_");
		StringBuilder b = new StringBuilder();
		for(int i=0; i<data.length; i++) {
			b.append(data[i].substring(0,  1).toUpperCase()).append(data[i].substring(1).toLowerCase());
		}
		return b.toString();
	}
}
