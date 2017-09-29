package it.abd.alfresco_allinone_abd.platformsample;

public class Test {

	public static void main(String[] args) {
       String aa = "test|df|dgf";
		
		String bb = "test||";
		
		String[] asplit = aa.split("\\|");
		String[] bsplit = bb.split("\\|");
		System.out.println(asplit.length);
		System.out.println(bsplit.length);

	}

}
