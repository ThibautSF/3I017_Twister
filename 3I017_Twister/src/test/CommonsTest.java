/**
 * 
 */
package test;

import services.classes.Commons;

/**
 * @author Thibaut SIMON-FINE
 *
 */
public class CommonsTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String chaine = "Ce562ci est !?% uNe Str#Ing 65789";
		
		System.out.println(Commons.getOnlyStrings(chaine));
	}

}
