package eu.asmoljo.wasmaster.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;

import javax.inject.Named;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.exception.ConnectorException;

import eu.asmoljo.wasmaster.client.properties.ClientConnectionProperties;
import eu.asmoljo.wasmaster.helper.ConfigObjectHelper;
import eu.asmoljo.wasmaster.services.ApplicationEnvironment;
import eu.asmoljo.wasmaster.services.DataSourceBuilder;
import eu.asmoljo.wasmaster.services.ThreadPoolManager;
import eu.asmoljo.wasmaster.test.Test1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;


public class WasmasterClient {

	private static AdminClient adminClient;
	private static ArrayList<String> ndList = new ArrayList<String>();
	private static ArrayList<String> connectorPROPERTIES;
	
	static String mode = null;
	static String soapServer = null;
	static String actionName = null;
	static String param4 = null;
	static String param5 = null;
	static String param6 = null;
	static String param7 = null;
	static String param8 = null;
	static String param9 = null;
	//ovo ces morat napravit za svaku klasu-akciju posebne poruke jer ce i primati razlicite parametre
	static String SCRIPT_ERROR_MSG="[For SCRIPT MODE use: mode{interact/script}, server, action{pae/pdse/tpm},tpname{WebContainer/ORB.thread.pool/TCPChannel.DCS/...}, maximumSize, minimumSize, inactivityTimeout,restart{restart=yes/restart=no}]";
	static String INTERACT_ERROR_MSG="[For INTERACTIVE MODE use: mode{interact/script}, server, action{pae/pdse/tpm}]";
	
	@Autowired()
	@Named(value="messageSource")
	static ResourceBundleMessageSource rbms;
	
	
	
	public static void main(String[] args) throws Throwable {
		
		mode = args[0];
		soapServer = args[1];
		actionName = args[2];
		
		
		
		if (mode.equalsIgnoreCase("interact")){
			WasmasterClient.createAdminClient(soapServer);
			WasmasterClient.action(actionName);
		}
		else if (mode.equalsIgnoreCase("script")) {
			WasmasterClient.createAdminClient(soapServer);
			param4 = args[3];
			param5 = args[4];
			param6 = args[5];
			param7 = args[6];
			param8 = args[7];
			param9 = args[8];
			WasmasterClient.action(actionName);
		}
		else {
			System.out.println("Unknown model!");
			System.out.println(SCRIPT_ERROR_MSG);
			System.out.println(INTERACT_ERROR_MSG);
			System.out.println("without commas!");
			System.exit(0);
		}
		
		
		
	}
	
	
	
	
	//OVO CE POSLUZIT AKO SE BUDE KORISTIO GUI
	private  void getNDList() throws IOException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("resources\\NDList.properties");
		ResourceBundle ndBundle = new PropertyResourceBundle(is);
		Set<String> ndKeySet = ndBundle.keySet();
	}
	
	     
	
	
	
	
	
	
	//KREIRANJE ADMIN CLIENTA
	public static void createAdminClient(String soapServer) throws Throwable {

	
		// Setiranje Properties objekta za JMX connector attributes
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
		

			
	}
	
	
	
	
	
	
	
	
		//POKRETANJE AKCIJE
		public static void action(String actionName) throws Throwable  {
			
			if (actionName.equalsIgnoreCase("pae")){
				System.out.println("Selected action is: " +actionName);	
				ApplicationEnvironment ae = new ApplicationEnvironment(adminClient);
				ae.getJ2EEApplicationTypeMbeans();
			}
			else if (actionName.equalsIgnoreCase("pdse")) {
				System.out.println("Selected action is: " +actionName);
			}
			else if (actionName.equalsIgnoreCase("dsb")) {
				System.out.println("Selected action is: " +actionName);
				DataSourceBuilder dsb = new DataSourceBuilder(adminClient);
				dsb.createDataSourceScript("ServerCluster","test1_clus", "DB2 Universal JDBC Driver Provider (XA)","Testni2 JDBC Provider ASMOLJO","Description for Testni2 JDBC Provider ASMOLJO");
			}
			else if (actionName.equalsIgnoreCase("tpm")) {
				System.out.println("Selected action is: " +actionName+"\n");
				ThreadPoolManager tpm = new ThreadPoolManager(adminClient);
					if(mode.equalsIgnoreCase("script")){
						tpm.changeAttributesScript(param4,param5,param6,param7,param8,param9);
					}
					else{
						tpm.changeAttributesInteractive();
					}
			}
			else if(actionName.equalsIgnoreCase("com")){
				System.out.println("Selected action is: " +actionName+"\n");
				ConfigObjectHelper com = new ConfigObjectHelper(adminClient);
				com.printAllConfObjectTypes();
				com.printAllAttributeNamesForObjectType(param4);
			}
			else if(actionName.equalsIgnoreCase("t1")){
				System.out.println("Selected action is: " +actionName+"\n");
				Test1 t = new Test1(adminClient, actionName);
				t.test();
			}
			else {
				System.out.println("Action name: "+actionName+" is incorrect! {pae/pdse/mtp}");
				System.out.println("...closing program...");
				System.out.println("Stop!");
				System.exit(0);
			}
		}
	
	
	
	
	
	
	
}

