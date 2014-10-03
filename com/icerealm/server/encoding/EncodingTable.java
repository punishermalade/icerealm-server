package com.icerealm.server.encoding;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * utility class that translate encoded character into valid HTML entities.
 * @author neilson
 *
 */
public class EncodingTable {

	private static final Logger LOGGER = Logger.getLogger("Icerealm");
	
	/**
	 * the encoding table used for translation
	 */
	private static Map<String, String> ENCODING_TABLE = null;

	/**
	 * translate any special character to a valid HTML entities. Very useful when
	 * there is french accented letters in the string
	 * @param str the string to be translated
	 * @return a string that has HTML entities instead of encoded character. Ex: %E9 = &acute;
	 */
	public static String translateToHTMLEntities(String str) {
		
		if (ENCODING_TABLE == null) {
			generateHashMap();
		}
		
		for (String k : ENCODING_TABLE.keySet()) {
			if (str.contains(k)) {
				str = str.replace(k, ENCODING_TABLE.get(k));
			}
		}
		
		return str;
	}
		
	/**
	 * generate the content of the table.
	 */
	private static void generateHashMap() {
		
		ENCODING_TABLE = new HashMap<String, String>();
		
		// html space when getting it from a form
		ENCODING_TABLE.put("+", " ");
		
		// return/enter button from the keyboard
		ENCODING_TABLE.put("%0D%0A", "<br/>");
		ENCODING_TABLE.put("%C0", "&Agrave;");
		ENCODING_TABLE.put("%C1", "&Aacute;");
		ENCODING_TABLE.put("%C2", "&Acirc;");
		ENCODING_TABLE.put("%C3", "&Atilde;");
		ENCODING_TABLE.put("%C4", "&Auml;");
		ENCODING_TABLE.put("%C5", "&Aring;");
		ENCODING_TABLE.put("%C6", "&AElig;");
		ENCODING_TABLE.put("%C7", "&Ccedil;");
		ENCODING_TABLE.put("%C8", "&Egrave;");
		ENCODING_TABLE.put("%C9", "&Eacute;");
		ENCODING_TABLE.put("%CA", "&Ecirc;");
		ENCODING_TABLE.put("%CB", "&Euml;");
		ENCODING_TABLE.put("%CC", "&Igrave;");
		ENCODING_TABLE.put("%CD", "&Iacute;");
		ENCODING_TABLE.put("%CE", "&Icirc;");
		ENCODING_TABLE.put("%CF", "&Iuml;");
		ENCODING_TABLE.put("%D0", "&ETH;");
		ENCODING_TABLE.put("%D1", "&Ntilde;");
		ENCODING_TABLE.put("%D2", "&Ograve;");
		ENCODING_TABLE.put("%D3", "&Oacute;");
		ENCODING_TABLE.put("%D4", "&Ocirc;");
		ENCODING_TABLE.put("%D5", "&Otilde;");
		ENCODING_TABLE.put("%D6", "&Ouml;");
		ENCODING_TABLE.put("%D8", "&Oslash;");
		ENCODING_TABLE.put("%D9", "&Ugrave;");
		ENCODING_TABLE.put("%DA", "&Uacute;");
		ENCODING_TABLE.put("%DB", "&Ucirc;");
		ENCODING_TABLE.put("%DC", "&Uuml;");
		ENCODING_TABLE.put("%DD", "&Yacute;");
		ENCODING_TABLE.put("%DE", "&THORN;");
		ENCODING_TABLE.put("%DF", "&szlig;");
		ENCODING_TABLE.put("%E0", "&agrave;");
		ENCODING_TABLE.put("%E1", "&aacute;");
		ENCODING_TABLE.put("%E2", "&acirc;");
		ENCODING_TABLE.put("%E3", "&atilde;");
		ENCODING_TABLE.put("%E4", "&auml;");
		ENCODING_TABLE.put("%E5", "&aring;");
		ENCODING_TABLE.put("%E6", "&aelig;");
		ENCODING_TABLE.put("%E7", "&ccedil;");
		ENCODING_TABLE.put("%E8", "&egrave;");
		ENCODING_TABLE.put("%E9", "&eacute;");
		ENCODING_TABLE.put("%EA", "&ecirc;");
		ENCODING_TABLE.put("%EB", "&euml;");
		ENCODING_TABLE.put("%EC", "&igrave;");
		ENCODING_TABLE.put("%ED", "&iacute;");
		ENCODING_TABLE.put("%EE", "&icirc;");
		ENCODING_TABLE.put("%EF", "&iuml;");
		ENCODING_TABLE.put("%F0", "&eth;");
		ENCODING_TABLE.put("%F1", "&ntilde;");
		ENCODING_TABLE.put("%F2", "&ograve;");
		ENCODING_TABLE.put("%F3", "&oacute;");
		ENCODING_TABLE.put("%F4", "&ocirc;");
		ENCODING_TABLE.put("%F5", "&otilde;");
		ENCODING_TABLE.put("%F6", "&ouml;");
		ENCODING_TABLE.put("%F8", "&oslash;");
		ENCODING_TABLE.put("%F9", "&ugrave;");
		ENCODING_TABLE.put("%FA", "&uacute;");
		ENCODING_TABLE.put("%FB", "&ucirc;");
		ENCODING_TABLE.put("%FC", "&uuml;");
		ENCODING_TABLE.put("%FD", "&yacute;");
		ENCODING_TABLE.put("%FE", "&thorn;");
		ENCODING_TABLE.put("%FF", "&yuml;");
		ENCODING_TABLE.put("%20", "&#32;");
		ENCODING_TABLE.put("%21", "&#33;");
		ENCODING_TABLE.put("%22", "&quot;");
		ENCODING_TABLE.put("%23", "&#35;");
		ENCODING_TABLE.put("%24", "&#36;");
		ENCODING_TABLE.put("%25", "&#37;");
		ENCODING_TABLE.put("%26", "&amp;");
		ENCODING_TABLE.put("%27", "&#39;");
		ENCODING_TABLE.put("%28", "&#40;");
		ENCODING_TABLE.put("%29", "&#41;");
		ENCODING_TABLE.put("%2A", "&#42;");
		ENCODING_TABLE.put("%2B", "&#43;");
		ENCODING_TABLE.put("%2C", "&#44;");
		ENCODING_TABLE.put("%2D", "&#45;");
		ENCODING_TABLE.put("%2E", "&#46;");
		ENCODING_TABLE.put("%2F", "&#47;");
		ENCODING_TABLE.put("%3A", "&#58;");
		ENCODING_TABLE.put("%3B", "&#59;");
		ENCODING_TABLE.put("%3C", "&lt;");
		ENCODING_TABLE.put("%3D", "&#61;");
		ENCODING_TABLE.put("%3E", "&#62;");
		ENCODING_TABLE.put("%3F", "&#63;");
		ENCODING_TABLE.put("%40", "&#64;");
		ENCODING_TABLE.put("%5B", "&#91;");
		ENCODING_TABLE.put("%5C", "&#92;");
		ENCODING_TABLE.put("%5D", "&#93;");
		ENCODING_TABLE.put("%5E", "&#94;");
		ENCODING_TABLE.put("%5F", "&#95;");
		ENCODING_TABLE.put("%60", "&#96;");
		ENCODING_TABLE.put("%7B", "&#123;");
		ENCODING_TABLE.put("%7C", "&#124;");
		ENCODING_TABLE.put("%7D", "&#125;");
		ENCODING_TABLE.put("%7E", "&#126;");
		ENCODING_TABLE.put("%A0", "&nbsp;");
		ENCODING_TABLE.put("%A1", "&iexcl;");
		ENCODING_TABLE.put("%A2", "&cent;");
		ENCODING_TABLE.put("%A3", "&pound;");
		ENCODING_TABLE.put("%A4", "&curren;");
		ENCODING_TABLE.put("%A5", "&yen;");
		ENCODING_TABLE.put("%A6", "&brvbar;");
		ENCODING_TABLE.put("%A7", "&sect;");
		ENCODING_TABLE.put("%A8", "&uml;");
		ENCODING_TABLE.put("%A9", "&copy;");
		ENCODING_TABLE.put("%AA", "&ordf;");
		ENCODING_TABLE.put("%AB", "&laquo;");
		ENCODING_TABLE.put("%AC", "&not;");
		ENCODING_TABLE.put("%AD", "&shy;");
		ENCODING_TABLE.put("%AE", "&reg;");
		ENCODING_TABLE.put("%AF", "&macr;");
		ENCODING_TABLE.put("%B0", "&deg;");
		ENCODING_TABLE.put("%B1", "&plusmn;");
		ENCODING_TABLE.put("%B2", "&sup2;");
		ENCODING_TABLE.put("%B3", "&sup3;");
		ENCODING_TABLE.put("%B4", "&acute;");
		ENCODING_TABLE.put("%B5", "&micro;");
		ENCODING_TABLE.put("%B6", "&para;");
		ENCODING_TABLE.put("%B7", "&middot;");
		ENCODING_TABLE.put("%B8", "&cedil;");
		ENCODING_TABLE.put("%B9", "&sup1;");
		ENCODING_TABLE.put("%BA", "&ordm;");
		ENCODING_TABLE.put("%BB", "&raquo;");
		ENCODING_TABLE.put("%BC", "&frac14;");
		ENCODING_TABLE.put("%BD", "&frac12;");
		ENCODING_TABLE.put("%BE", "&frac34;");
		ENCODING_TABLE.put("%BF", "&iquest;");
		ENCODING_TABLE.put("%152", "&#338;");
		ENCODING_TABLE.put("%153", "&#339;");
		ENCODING_TABLE.put("%160", "&#352;");
		ENCODING_TABLE.put("%161", "&#353;");
		ENCODING_TABLE.put("%178", "&#376;");
		ENCODING_TABLE.put("%192", "&#402;");
		ENCODING_TABLE.put("%2013", "&#8211;");
		ENCODING_TABLE.put("%2014", "&#8212;");
		ENCODING_TABLE.put("%2018", "&#8216;");
		ENCODING_TABLE.put("%2019", "&#8217;");
		ENCODING_TABLE.put("%201A", "&#8218;");
		ENCODING_TABLE.put("%201C", "&#8220;");
		ENCODING_TABLE.put("%201D", "&#8221;");
		ENCODING_TABLE.put("%201E", "&#8222;");
		ENCODING_TABLE.put("%2020", "&#8224;");
		ENCODING_TABLE.put("%2021", "&#8225;");
		ENCODING_TABLE.put("%2022", "&#8226;");
		ENCODING_TABLE.put("%2026", "&#8230;");
		ENCODING_TABLE.put("%2030", "&#8240;");
		ENCODING_TABLE.put("%20AC", "&#8364;");
		ENCODING_TABLE.put("%2122", "&#8482;");
	}
}