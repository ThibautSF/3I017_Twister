/**
 * 
 */
package services.classes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Thibaut SIMON-FINE
 *
 */
public class Commons {
	public static String getOnlyStrings(String s) {
		Pattern pattern = Pattern.compile("[^a-z A-Z]");
		Matcher matcher = pattern.matcher(s);
		String number = matcher.replaceAll("");
		return number;
	}
}
