package eu.asmoljo.wasmaster.client;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.exception.ConnectorException;



public class WasmasterCli {
	
	@Autowired
	ResourceBundleMessageSource rbms;
	
	public WasmasterCli(){
		

		Properties connectProps = new Properties();
		connectProps.setProperty(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
		connectProps.setProperty(AdminClient.CONNECTOR_SECURITY_ENABLED, "true");
		connectProps.setProperty(AdminClient.USERNAME, rbms.getMessage("Connector_Username", null, null));
		connectProps.setProperty(AdminClient.PASSWORD, rbms.getMessage("Connector_Password", null, null));
		connectProps.setProperty(AdminClient.CONNECTOR_HOST, rbms.getMessage("Connector_Host", null, null));
		connectProps.setProperty(AdminClient.CONNECTOR_PORT, rbms.getMessage("Connector_Port", null, null));
		connectProps.setProperty("javax.net.ssl.trustStore", rbms.getMessage("Trust_store_path", null, null));
		connectProps.setProperty("javax.net.ssl.trustStorePassword", rbms.getMessage("Trust_store_pwd", null, null));
		connectProps.setProperty("java.home", rbms.getMessage("Java_Home", null, null));
		System.setProperty("com.ibm.ssl.performURLHostNameVerification", "true");
		
		/** OVDJE IMA NEKIH PROBLEMA PA SAM ZAKOMENTIRAO A I NEZNAM CEMU SLUZI OVA KLASA OD PRIJE,PA CE VJEROVATNO BIT ZA BRISANJE KASNIJE
		
		// Kreiranje adminClienta na osnovu connector propertya
			try {
				adminClient = AdminClientFactory.createAdminClient(connectProps);
				System.out.println("Tip konekcije na WAS :"+adminClient.getType());
			    System.out.println("Uspjesno spajanje na server "+ connectorPROPERTIES.get(0) + ":"+connectorPROPERTIES.get(1));
			} 
			catch (ConnectorException err) {
				System.out.println(err + "\n"+"Greska kod spajanja klijenta na WAS. Provjerite parametre u properties datotekama i certifikate u trust keystoreu. ");
				System.exit(0);
			}
			
			*/
		
	}
	
	

}
