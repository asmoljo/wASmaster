package eu.asmoljo.wasmaster.test;

import eu.asmoljo.wasmaster.helper.MBeanList;
import eu.asmoljo.wasmaster.services.properties.ServiceProperties;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.ObjectName;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;

public class Test1 {

	
	AdminClient ac;
	ConfigService configService;
	Session session;
	ServiceProperties serviceProperties;
	
	
	public Test1(AdminClient adminClient, String servicePropertyFile) throws Throwable{
		ac = adminClient;
		configService = new ConfigServiceProxy(ac);
		session = new Session("wasmaster", false);
		serviceProperties = new ServiceProperties(servicePropertyFile);
		
	}

	
    
    public void test() throws ConfigServiceException, ConnectorException, AttributeNotFoundException{
    	

    	

    	
    	
        //Direktan poziv kompletnog scopea
    	ObjectName scope = configService.resolve(session, serviceProperties.getServiceProperty("ScopeType")+"="+serviceProperties.getServiceProperty("ScopeName"))[0] ;
    	
        
        ObjectName[] jdbcProviderList = configService.resolve(session, scope,"JDBCProvider");
        ObjectName jdbcProvider = MBeanList.getMBeanFromListBasedOnAttributeName(jdbcProviderList, "name",serviceProperties.getServiceProperty("JDBCProviderName"),configService);
        
        ObjectName[]  dataSourceList = configService.resolve(session, jdbcProvider,"DataSource");
        ObjectName dataSource = MBeanList.getMBeanFromListBasedOnAttributeName(dataSourceList, "name",serviceProperties.getServiceProperty("DataSourceName"),configService);
        
        //Direktan poziv connection poolu jer postoji samo jedan za svaki datasource
        ObjectName connectionPool = configService.resolve(session, dataSource,"ConnectionPool")[0];
       
        

        

        System.out.println("--------------------SCOPE-------------------------------");
        System.out.println(ConfigServiceHelper.getConfigDataId(scope)+"----"+ConfigServiceHelper.getConfigDataType(scope)+"----"+ConfigServiceHelper.getDisplayName(scope));
        System.out.println("---------------------------------------------------------");
        
        
        System.out.println("--------------------JDBCProvider-------------------------------");
        System.out.println(ConfigServiceHelper.getConfigDataId(jdbcProvider)+"----"+ConfigServiceHelper.getConfigDataType(jdbcProvider)+"----"+ConfigServiceHelper.getDisplayName(jdbcProvider));
        System.out.println("---------------------------------------------------------");
        
        
        System.out.println("--------------------DataSource-------------------------------");
        System.out.println(ConfigServiceHelper.getConfigDataId(dataSource)+"----"+ConfigServiceHelper.getConfigDataType(dataSource)+"----"+ConfigServiceHelper.getDisplayName(dataSource));
        System.out.println("---------------------------------------------------------");
        
        System.out.println("--------------------ConnectionPool-------------------------------");
        System.out.println(ConfigServiceHelper.getConfigDataId(connectionPool)+"----"+ConfigServiceHelper.getConfigDataType(connectionPool)+"----"+ConfigServiceHelper.getDisplayName(connectionPool));
        System.out.println("---------------------------------------------------------");
        
        
        
        //Kreiranje liste sa atributima od ConnectionPoola,ispis trenutnih i buducih vrijednosti atributa
        System.out.println("Attribute list:");
        String[] attributeNameList = new String[]{"connectionTimeout","maxConnections","minConnections","reapTime","unusedTimeout","agedTimeout","purgePolicy"};
        AttributeList connectionPoolAttributeList = configService.getAttributes(session, connectionPool, attributeNameList, false);
        
        System.out.println ("connectionTimeout= " + (long)ConfigServiceHelper.getAttributeValue(connectionPoolAttributeList, "connectionTimeout") + "--> new :" + serviceProperties.getServiceProperty("connectionTimeout"));
        System.out.println ("maxConnections= " + (Integer)ConfigServiceHelper.getAttributeValue(connectionPoolAttributeList, "maxConnections") + "--> new :" + serviceProperties.getServiceProperty("maxConnections"));
        System.out.println ("minConnections= " + (Integer)ConfigServiceHelper.getAttributeValue(connectionPoolAttributeList, "minConnections") + "--> new :" + serviceProperties.getServiceProperty("minConnections"));
        System.out.println ("reapTime= " + (long)ConfigServiceHelper.getAttributeValue(connectionPoolAttributeList, "reapTime") + "--> new :" + serviceProperties.getServiceProperty("reapTime"));
        System.out.println ("unusedTimeout= " + (long)ConfigServiceHelper.getAttributeValue(connectionPoolAttributeList, "unusedTimeout") + "--> new :" + serviceProperties.getServiceProperty("unusedTimeout"));
        System.out.println ("agedTimeout= " + (long)ConfigServiceHelper.getAttributeValue(connectionPoolAttributeList, "agedTimeout") + "--> new :" + serviceProperties.getServiceProperty("agedTimeout"));
        System.out.println ("purgePolicy= " + (String)ConfigServiceHelper.getAttributeValue(connectionPoolAttributeList, "purgePolicy") + "--> new :" + serviceProperties.getServiceProperty("purgePolicy"));
        
        
        AttributeList newAttributeList = new AttributeList();
        
        for(String attribute:attributeNameList){
        	newAttributeList.add(new Attribute(attribute, serviceProperties.getServiceProperty(attribute)));
        }
        
        configService.setAttributes(session,  connectionPool, newAttributeList);
        configService.save(session, false);
        
        
        
        
        
        
        
        
        //AttributeList dataSourceAttributeList= configService.getAttributes(session, dataSource, new String[]{"name","jndiName","description","providerType","authMechanismPreference","authDataAlias","datasourceHelperClassname","provider","connectionPool","properties","relationalResourceAdapter"}, false);
        //System.out.println(dataSourceAttributeList.size());
        //for(Object a : dataSourceAttributeList){
        //	System.out.println(a.toString());
        //}
        
        
        
       /* System.out.println("--------------------WASDataSourceDefinition-------------------------------");
        System.out.println(ConfigServiceHelper.getConfigDataId(WASDataSourceDefinition)+"----"+ConfigServiceHelper.getConfigDataType(WASDataSourceDefinition)+"----"+ConfigServiceHelper.getDisplayName(WASDataSourceDefinition));
        System.out.println(configService.getAttributesMetaInfo("WASDataSourceDefinition"));
        System.out.println("---------------------------------------------------------");
        AttributeList WASDataSourceDefinitionAttributeList= configService.getAttributes(session, WASDataSourceDefinition, new String[]{"refName","className","serverName","portNumber","databaseName","url","user","password","transactional","isolationLevel","initialPoolSize"}, false);
        System.out.println(WASDataSourceDefinitionAttributeList.size());
        for(Object a : WASDataSourceDefinitionAttributeList){
        	System.out.println(a.toString());
        }*/
        
        
        
        

        //AttributeList connectionPoolAttributeList= configService.getAttributes(session, connectionPool, new String[]{"connectionTimeout","maxConnections","minConnections","reapTime","unusedTimeout","agedTimeout","purgePolicy"}, false);
       // System.out.println(connectionPoolAttributeList.size());
        //for(Object a : connectionPoolAttributeList){
        //	System.out.println(a.toString());
       // }
        
        
        
        

        
        
        
        
        
        
        //ObjectName[] test3 = configService.resolve(session, test2,"ConnectionPool");
        
        //ObjectName test4 = configService.resolve(session, test2, "ConnectionPool")[1];
        
        //ObjectName test5 = configService.resolve(session, test2,"DataSource" )[0];
        
        
       
        
        
       
        
 
        
        /*
        System.out.println("---------------------------------------------------");
        System.out.println(ConfigServiceHelper.getConfigDataId(test2));
        System.out.println(ConfigServiceHelper.getConfigDataType(test2));
        System.out.println(ConfigServiceHelper.getDisplayName(test2));
        
        System.out.println("---------------------------------------------------");
        System.out.println(ConfigServiceHelper.getConfigDataId(test3));
        System.out.println(ConfigServiceHelper.getConfigDataType(test3));
        System.out.println(ConfigServiceHelper.getDisplayName(test3));
        
        
        System.out.println("---------------------------------------------------");
        System.out.println(ConfigServiceHelper.getConfigDataId(test4));
        System.out.println(ConfigServiceHelper.getConfigDataType(test4));
        System.out.println(ConfigServiceHelper.getDisplayName(test4));
        
        System.out.println("---------------------------------------------------");
        System.out.println(ConfigServiceHelper.getConfigDataId(test));
        System.out.println(ConfigServiceHelper.getConfigDataType(test));
        System.out.println(ConfigServiceHelper.getDisplayName(test));
        
        
        */
        
        
    	
    	
    	//System.out.println(configService.getAttribute(session, test, "entries"));
    	
    	/*ArrayList on1 = (ArrayList)configService.getAttribute(session, test, "entries");
    	ArrayList novaVarijabla = new ArrayList();
    	novaVarijabla.add("symbolicName = TRALALA");
    	novaVarijabla.add("value = /aFHDOHDFOIwheoihdFOIEWFDWFDrewwerwqerwe");
    	on1.add(novaVarijabla);
    	AttributeList attrList = new AttributeList();
		//attrList.add(new Attribute("entries",(Attribute)on1 ));
		configService.setAttributes(session,  test, attrList);*/
    	

    	
    }
    
	
	
	
}
