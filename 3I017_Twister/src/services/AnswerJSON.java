/**
 * 
 */
package services;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Thibaut SIMON-FINE
 *
 */
public class AnswerJSON {

	public static JSONObject defaultJSONError(String message, int codeErreur) throws JSONException{
		JSONObject errJSON = new JSONObject();
		errJSON.put("Status","KO");
		errJSON.put("message", message);
		errJSON.put("id", codeErreur);
		return errJSON;
	}
	
	

	public static JSONObject defaultJSONAccept() throws JSONException{
		JSONObject json = new JSONObject();
		json.put("Status","OK");
		return json;
	}
}
