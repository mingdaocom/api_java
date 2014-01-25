package com.mingdao.sdk;

public class Config {
	
	public static final String AUTHURL="https://api.mingdao.com/oauth2/authorize?";
	public static final  String ACCEURL="https://api.mingdao.com/oauth2/access_token?";
	
	public static final  String APPKEY="A97C54BD164A61E2B01E27EBCA197540";//需要换成您的应用的appkey
	public static final  String APPSECRET="E565D6DB7E3FA4EA81CC6C7253814393";//需要换成您的应用的appSecret
	public static final  String RESPONSE_TYPE="token";//token或者code
	public static final  String REDIRECT_URI="http://localhost:8080/api_java/receive.jsp";//需要换成您的应用设置的回调地址
	
	public static String getAuthorizeUrl(){
		String url=AUTHURL
			+"app_key="+APPKEY
			+"&redirect_uri="+REDIRECT_URI
			+"&response_type="+RESPONSE_TYPE;
		return url;
	}
	
	public static String getAccessTokenUrl(String code){
		String url=ACCEURL
			+"app_key="+APPKEY
			+"&app_secret="+APPSECRET
			+"&grant_type=authorization_code"
			+"&format=json"
			+"&redirect_uri="+REDIRECT_URI
			+"&code="+code;
		return url;
	}
	public static String getAccessTokenByCode(String code){
		String url =getAccessTokenUrl(code);
		String result=HttpUtil.httpByGet2StringSSL(url, null, null);
		return result;
	}
}

