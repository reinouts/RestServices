package restservices.publish;

import restservices.RestServices;
import restservices.proxies.ServiceDefinition;

public class SwaggerServiceDescriber {

	private RestServiceRequest rsr;
	private ServiceDefinition def;

	private final String baseUrl = RestServices.getServiceUrl("");
	
	public SwaggerServiceDescriber(RestServiceRequest rsr, ServiceDefinition def) {
		this.rsr = rsr;
		this.def = def;
	}
	
	/*
	 * The Resource Listing serves as the root document for the API description. 
	 * It contains general information about the API and an inventory of the available resources.
	 *
	 * By default, this document SHOULD be served at the /api-docs path.
	 */
	public static void serveServiceOverview(RestServiceRequest rsr) {

		rsr.datawriter.object()
			.key("apiVersion").value(RestServices.VERSION)
			.key("swaggerVersion").value("1.2")
			.key("info").object()
				.key("title").value("RestServices")
				.key("description").value("Mendix RestServices module description")
				.endObject()
			.key("apis").array();

			for (String service : RestServices.getServiceNames()) {
				rsr.datawriter.object()
				.key("path").value("/" + service)
				.key("description").value("Operations about " + service)
				.endObject();
			}
		
		rsr.datawriter.endArray().endObject();
	}
	
	protected static void listOperations(RestServiceRequest rsr)
	{
		rsr.datawriter.array();
		
//		RestServices.getServiceNames()
//		RestServices.getService(name)
	}
}
