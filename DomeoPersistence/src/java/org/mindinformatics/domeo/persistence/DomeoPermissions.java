package org.mindinformatics.domeo.persistence;

public class DomeoPermissions {

	public static final String PUBLIC_KEY  = "permissions_!DOMEO_NS!_permissions.permissions_!DOMEO_NS!_accessType";
	public static final String GROUP_KEY   = "permissions_!DOMEO_NS!_permissions.permissions_!DOMEO_NS!_accessDetails.permissions_!DOMEO_NS!_allowedGroups.@id";
	public static final String PRIVATE_KEY = "permissions_!DOMEO_NS!_permissions.permissions_!DOMEO_NS!_accessType";
	
	public static final String PUBLIC_VALUE  = "urn:domeo:access:public";

	private String publicValue;
	private String privateValue;
	private String[] groupValues;

	/**
	 * Domeo Permissions used for filtering queries
	 * Make sure that any empty parameters get re-assigned as null.
	 * @param publicKeyValue - public permission
	 * @param privateKeyValue - private permission
	 * @param groupKeyValues - array of group permissions
	 */
	public DomeoPermissions(String publicValue, String privateValue, String[] groupValues) {
		this.publicValue = publicValue    == "" ? null : publicValue;
		this.privateValue = privateValue  == "" ? null : privateValue;;
		this.groupValues = ((groupValues != null) && (groupValues.length == 0)) ? null : groupValues;;
	}

	
	String getPublicValue  () { return publicValue;  }
	String getPrivateValue () { return privateValue; }
	String[] getGroupValues() { return groupValues;  }

	
	/**
	 * Rules:
	 *   1. If there are no public, private or group values, return null (ERROR)
	 *   2. If there is only public, only private, or only 1 group, then do a
	 *      basic term filter
	 *   3. Build filter comprised of Boolean "should" term query clauses
	 * @return
	 */
	public String buildQueryFilter() {
		// 1. No permissions is really an error condition although we can recover gracefully
		if ((publicValue == null) && (privateValue == null) && (groupValues == null)) {
			return "";
		}
		
		// 2. If only one value then treat as a term filter
		boolean publicOnly = false, privateOnly = false, oneGroupOnly = false;
		publicOnly  = (publicValue  != null) && ((privateValue == null) && (groupValues == null));
		privateOnly = (privateValue != null) && ((publicValue  == null) && (groupValues == null));
		oneGroupOnly = ((groupValues != null) && (groupValues.length == 1)) && ((publicValue == null) && (privateValue == null));
		
		if (publicOnly) {
			String filter = ", \"filter\" : { \"term\" : { \"" +
					 PUBLIC_KEY + "\" : " + "\"" + publicValue + "\" } } ";
			 return filter;
		}
		else if (privateOnly) {
			 String filter = ", \"filter\" : { \"term\" : { \"" +
					 PRIVATE_KEY + "\" : " + "\"" + privateValue + "\" } } ";
			 return filter;
		}
		else if (oneGroupOnly) {
			String filter = ", \"filter\" : { \"term\" : { \"" +
					 GROUP_KEY + "\" : " + "\"" + groupValues[0] + "\" } } ";
			 return filter;
		}
		
		// 3. public, private, group are must clauses in bool
		StringBuffer sb = new StringBuffer(", \"filter\" : { \"bool\" : { \"should\" : [ ");  //array ok even if just one
		boolean needComma = false;

		if (publicValue != null) {
			sb.append("{ \"term\" : { \"" + PUBLIC_KEY + "\" : \"" + publicValue + "\" } }");
			needComma = true;
		}

		if (privateValue != null) {
			if (needComma) {
				sb.append(", ");
			}
			sb.append("{ \"term\" : { \"" + PRIVATE_KEY + "\" : \"" + privateValue + "\" } }");
			needComma = true;
		}

		// Must clause of 1 or more boolean shoulds for group keys
		if (groupValues != null) {
			if (needComma) {
				sb.append(", ");
			}
			sb.append("{ \"bool\" : { \"should\" : [ ");
			needComma = false;
			for (int i = 0; i < groupValues.length; i++) {
				if (needComma) {
					sb.append(",");
				}
				sb.append("{ \"term\" : { \"" + GROUP_KEY + "\" : \"" + groupValues[i] + "\" } }");
				needComma = true;
			}
			sb.append("] } }");
		}
		
		sb.append("] } }");
		
		return sb.toString();
	}
}
