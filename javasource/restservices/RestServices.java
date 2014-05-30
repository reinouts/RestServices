package restservices;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;




import restservices.publish.PublishedMicroflow;
import restservices.publish.PublishedService;

import com.mendix.core.Core;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.meta.IMetaObject;

import communitycommons.XPath;

public class RestServices {
	
	/**
	 * Version of the RestServices module
	 */
	public static final String VERSION = "1.0.x";

	/**
	 * Amount of objects that are processed by the module at the same time.
	 * Larger number yields better performance but increases memory consumptions. 
	 * 
	 * Defaults to 1000.
	 */
	public static int BATCHSIZE = 1000;
	public static final int MAXPOLLQUEUE_LENGTH = 10000;
	public static final int LONGPOLL_MAXDURATION = 50; //In seconds

	public static ILogNode LOGPUBLISH = Core.getLogger("RestPublish");
	public static ILogNode LOGCONSUME = Core.getLogger("RestConsume");
	public static ILogNode LOGUTIL = Core.getLogger("RestUtil");
	
	public static final String END_OF_HTTPHEADER = "\r\n\r\n";
	public static final String UTF8 = "UTF-8";
	public static final String BASIC_AUTHENTICATION = "Basic";
	public static final String CURRENTUSER_TOKEN = "'" + XPath.CurrentUser + "'";
	public static final String STYLESHEET = 
	"body { font-family: Arial; font-size: 0.8em; padding: 20px 60px; } " +
	"td {padding: 5px 10px; border: 1px solid #e2e2e2; vertical-align: top; } " +
	"tr { background-color: white; }" +
	".table-nested-odd > tbody > tr {background-color: #e2e2e2; } " +
	"h1 {color: navy; }" +
	"hr {border-style:none; border-bottom: 1px dotted #aaa;}";

	public static final String CONTENTTYPE_TEXTJSON = "text/json";
	public static final String CONTENTTYPE_FORMENCODED = "application/x-www-form-urlencoded";
	public static final String CONTENTTYPE_MULTIPART = "multipart/form-data";
	public static final String CONTENTTYPE_OCTET = "application/octet-stream";

	public static final String HEADER_ETAG = "ETag";
	public static final String HEADER_IFNONEMATCH = "If-None-Match";
	public static final String HEADER_ACCEPT = "Accept";
	public static final String HEADER_AUTHORIZATION = "Authorization";
	public static final String HEADER_CONTENTTYPE = "Content-Type";
	public static final String HEADER_WWWAUTHENTICATE = "WWW-Authenticate";
	
	public final static String PATH_REST = "rest/";
	public static final String PATH_LIST = "list";
	public static final String PATH_FEED = "feed";
	public static final String PATH_CHANGES = "changes";
	public static final String PATH_APIDOCS = "api-docs";
	
	public static final String PARAM_CONTENTTYPE = "contenttype";
	public static final String PARAM_SINCE = "since";
	public static final String PARAM_TIMEOUT = "timeout";
	public static final String PARAM_ABOUT = "about";
	public static final String PARAM_COUNT = "count" ;
	public static final String PARAM_DATA = "data";
	public static final String PARAM_OFFSET = "offset"; 
	public static final String PARAM_LIMIT = "limit"; 

	public static final String CHANGE_DATA = "data";
	public static final String CHANGE_KEY = "key";
	public static final String CHANGE_DELETED = "deleted";
	public static final String CHANGE_SEQNR = "seq";
	public static final String CHANGE_ETAG = "etag";
	public static final String CHANGE_URL = "url";


	static Map<String, PublishedService> services = new HashMap<String, PublishedService>();
	static Map<String, PublishedService> servicesByEntity = new HashMap<String, PublishedService>();
	static Map<String, PublishedMicroflow> microflowServices = new HashMap<String, PublishedMicroflow>();

	public static PublishedService getServiceForEntity(String entityType) {
		if (servicesByEntity.containsKey(entityType))
			return servicesByEntity.get(entityType);
		
		//if not look into super entitites as well!
		IMetaObject meta = Core.getMetaObject(entityType);
		if (meta.getSuperObject() != null) {
			PublishedService superService = getServiceForEntity(meta.getSuperName());
			if (superService != null) {
				servicesByEntity.put(entityType, superService);
				return superService;
			}
		}
		servicesByEntity.put(entityType, null); //no service. Remember that
		return null;
	}
	
	public static void registerService(String name, PublishedService def) {
		PublishedService current = services.put(name,  def);
		
		if (current != null)
			current.dispose();
		
		if (def.isGetObjectEnabled())
			servicesByEntity.put(def.getSourceEntity(), def);
		else {
			current = servicesByEntity.get(def.getSourceEntity());
			if (current != null && current.getName().equals(def.getName())) 
				servicesByEntity.remove(def.getSourceEntity());
		}

		LOGPUBLISH.info("Registered data service '" + def.getName() + "'");
	}

	public static PublishedService getService(String name) {
		return services.get(name);
	}

	public static Set<String> getServiceNames() {
		Set<String> names = new HashSet<String>(services.keySet());
		names.addAll(microflowServices.keySet());
		return names;
	}

	public static String getServiceUrl(String name) {
		return Core.getConfiguration().getApplicationRootUrl() + "rest/" + name + (microflowServices.containsKey(name) ? "" : "/");
	}

	public static void registerPublishedMicroflow(PublishedMicroflow s) {
		microflowServices.put(s.getName(), s);
		LOGPUBLISH.info("Registered microflow service '" + s.getName() + "'");
	}
	
	public static PublishedMicroflow getPublishedMicroflow(String name) {
		return microflowServices.get(name);
	}
}
