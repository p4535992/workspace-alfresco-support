package it.abd.alfresco.asl1cc.action;

import net.sf.jasperreports.repo.RepositoryService;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.LimitBy;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.DynamicNamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceException;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.ui.common.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.abd.alfresco.lib.AlfrescoLib;
import it.abd.webdesktop.client.exception.WebdesktopException;

/**
 * Classe usata per contare i risultati di determinate query su presidio
 * 
 * "1": codicePresidioRicovero = "CR00CC" = Città di castello
   "3": codicePresidioRicovero = "CR00UM" = Umbertide
   "26": codicePresidioRicovero = "CR00BR" = Brance/Gubbio e Gualdo Tadino
   "84": codicePresidioRicovero = "21062" = Media Valle del Tevere (non supportata ancora come codice)
 * @author Pancio
 *
 */
public class QueryContatoreAction extends ActionExecuterAbstractBase {
	
	private String NAMESPACE_ASL1CC_ASPECT = "extension.Asl1ccModel";
    private static Log logger = LogFactory.getLog(QueryContatoreAction.class); 
    
	private NodeService nodeService;
	private SearchService searchService;
	private ContentService contentService;

	
	//private String searchQueryBasePath;
	private String searchLanguageQuery;
	//private String searchQueryAll;
	private String searchStartYear;
	private String searchEndYear;

	@Override
	protected void executeImpl(Action ruleAction, NodeRef actionedUponNodeRef) {
			try{					
				logger.info("---------------- Inizio Esecuzione Azione QueryContatoreAction, ----------------\r\n");	
				//Map<String,Serializable> mapOut = new LinkedHashMap<>();
				List<String> listOut = new LinkedList<>();
				List<String> listaPresidi = Arrays.asList("1","3","26","84");
				List<Integer> years = fillListWithRangeNumber(Integer.valueOf(searchStartYear),Integer.valueOf(searchEndYear));
				//List<String> listaPresidi = Arrays.asList("26");
				for(Integer annoToSearch : years){
					//mapOut.put("------ ANNO "+annoToSearch+" ------ ", "");
					listOut.add("------ ANNO "+annoToSearch+" ------ ");
					//int annoToSearch = year;//2016;					
					for(String presidioId : listaPresidi){
						
						String currentPresidioName = "";
						String currentPresidioCodice = "";
						switch(presidioId){
							case "1": currentPresidioCodice = "CR00CC"; currentPresidioName = "Città di castello";break;
							case "3": currentPresidioCodice = "CR00UM"; currentPresidioName = "Umbertide";break;
							case "26": currentPresidioCodice = "CR00BR"; currentPresidioName = "Brance/Gubbio e Gualdo Tadino";break;
							case "84": currentPresidioCodice = "21062"; currentPresidioName = "Media Valle del Tevere ";break;
							default: throw new Exception("Il codice_presidio_ospedale '"+presidioId+"'è errato");
						}
						//mapOut.put("------ PRESIDIO ("+presidioId+") "+currentPresidioName+ " ("+annoToSearch+") ------ ", "");
						listOut.add("------ PRESIDIO ("+presidioId+") "+currentPresidioName+ " -------- ");
						//+PATH:"/app:company_home/cm:ASL1/cm:Archivio/cm:Spazio_x0020_Corrente//*" AND +@asl1cc:codice_ospedale:"26" AND TYPE:"cm:folder"
						//+PATH:"/app:company_home/cm:ASL1/cm:Archivio//*" AND +@asl1cc:codice_ospedale:"26" AND TYPE:"cm:folder"
						
						//+PATH:"/app:company_home/cm:ASL1/cm:Archivio//*" AND cm:created:["2016-01-01T00:00:00.000" TO "2017-01-01T00:00:00.000"] AND +@asl1cc:codice_ospedale:"26" AND TYPE:"cm:folder"
						//+PATH:"/app:company_home/cm:ASL1/cm:Archivio//*" AND cm:created:["2016-01-01T00:00:00.000" TO "2017-01-01T00:00:00.000"] AND TYPE:"cm:content"
						//Map<String,Serializable> properties = ruleAction.getParameterValues();
									
						//logger.info("searchQueryAll:'"+searchQueryAll+"' con linguaggio:'"+searchLanguageQuery.toString()+"'");					
						
						String queryAll = "+PATH:\"/app:company_home/cm:ASL1/cm:Archivio//*\" AND +@asl1cc:codice_ospedale:\""+presidioId+"\" AND TYPE:\"cm:folder\"";//searchQueryAll;						
						String queryAllForYearFolderClinicPerPresidio = "+PATH:\"/app:company_home/cm:ASL1/cm:Archivio//*\" AND cm:created:[\""+annoToSearch+"-01-01T00:00:00.000\" TO \""+(annoToSearch+1)+"-01-01T00:00:00.000\"] AND +@asl1cc:codice_ospedale:\""+presidioId+"\" AND TYPE:\"cm:folder\"";
						//String queryAllForYearContentScanNoPresidio = "+PATH:\"/app:company_home/cm:ASL1/cm:Archivio//*\" AND cm:created:[\""+annoToSearch+"-01-01T00:00:00.000\" TO \""+(annoToSearch+1)+"-01-01T00:00:00.000\"] AND TYPE:\"cm:content\"";;
						
						logger.info("queryAll:'"+queryAll+"' con linguaggio:'"+searchLanguageQuery.toString()+"'");
						logger.info("queryAllForYearFolderClinicPerPresidio :'"+queryAllForYearFolderClinicPerPresidio+"' con linguaggio:'"+searchLanguageQuery.toString()+"'");
						String tipoQuery = checkSearchLanguage(searchLanguageQuery);		
								
					    //Set<NodelModelData> permissions = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, tipoQuery.toString(), query);				
						//QueryResult result=repositoryService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE,new Query(Constants.QUERY_LANG_LUCENE, query),true);
						//ResultSet rs=result.getResultSet();
						long countTotal = getCountResult(searchService, queryAll, tipoQuery);				
						long countAnnoToSearch = 0;
						long countScannerizzazioniPerAnnoCartella = 0;
						long countScannerizzazioniPerAnnoScannerizzazione = 0;
						/*
						ResultSet rs = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, tipoQuery.toString(), query);	
						logger.info("Run Query Archive Base:'"+searchQueryBasePath+"' con linguaggio:'"+tipoQuery.toString()+"',count:"+ rs.getNumberFound());
						
						for(int i=0;i<rs.length();i++){					
				            //extension = (resultSet[i].properties['cm:name'].substring(resultSet[i].properties['cm:name'].lastIndexOf(".")));
							ResultSetRow row = rs.getRow(i);
							NodeRef nodeRefFolder = row.getNodeRef();
							Path folderPath = nodeService.getPath(nodeRefFolder);
							//If you need the folder path as prefixed string,
							NamespacePrefixResolver resolver = getNamespaceResolver();					
							String prefixedPath = folderPath.toPrefixString(resolver);
							String query2 = "+PATH:\""+prefixedPath+"//*\" AND +@asl1cc:codice_ospedale:\"26\" AND TYPE:\"cm:folder\"";		
							//logger.info("Run Query Archive Folder ("+i+"):'"+query2+"' con linguaggio:'"+tipoQuery.toString()+"'");
							long count= getCountResult(searchService, query2, tipoQuery);
							logger.info("Run Query Archive Folder ("+i+"):FolderPath:'"+folderPath+"',NodeRef:'"+nodeRefFolder+"',query:'"+query2+"',linguaggio:'"+tipoQuery.toString()+"',count:"+ count);	
							countTotal = countTotal + count;
		
				        }	
						rs.close();
						*/
													
						if(countTotal > 0){					
							//Get tutte le cartelle cliniche per il presidio e per anno cartella
							//ResultSet rs = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, tipoQuery.toString(), queryAllForYearFolderClinicPerPresidio);																					
							ResultSet rs = getResult(searchService,queryAllForYearFolderClinicPerPresidio,tipoQuery.toString());
							for(int i=0;i<rs.length();i++){	
								ResultSetRow row = rs.getRow(i);
								
								Map<String,Serializable> propRow = row.getValues();
								NodeRef nodeRefCartella = row.getNodeRef();
								//java.util.Date date = (java.util.Date)propRow.get("{extension.Asl1ccModel}data_accettazione");
								//cm:created http://www.alfresco.org/model/content/1.0
								//https://community.alfresco.com/thread/170842-method-for-extracting-document-metadata-from-search-results	
								
								//java.util.Date date = (java.util.Date)propRow.get("{http://www.alfresco.org/model/content/1.0}created");
							    //Calendar cal = Calendar.getInstance();
							    //cal.setTime(date);
													
								//if(cal.get(Calendar.YEAR)==annoToSearch){
							        countAnnoToSearch++;							
									//logger.debug("Row("+i+")NodeRef Cartella,'"+nodeRefCartella+"',Date:"+ date.toString()+", Anno check:"+ cal.get(Calendar.YEAR));								
								countScannerizzazioniPerAnnoCartella = countScannerizzazioniPerAnnoCartella + getChildsByExtensions(nodeService, nodeRefCartella,"pdf").size();
								//}
								countScannerizzazioniPerAnnoScannerizzazione = countScannerizzazioniPerAnnoScannerizzazione + getChildsByExtensionsAndYear(nodeService, nodeRefCartella,"pdf",annoToSearch).size();								
					        }	
							rs.close();
							//Get tutte le cartelle cliniche per il presidio e per anno cartella scannerizzazione
//							rs = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, tipoQuery.toString(), queryAll);																					
//							for(int i=0;i<rs.length();i++){	
//								ResultSetRow row = rs.getRow(i);
//								NodeRef nodeRefCartella = row.getNodeRef();			
//								countScannerizzazioniPerAnnoScannerizzazione = countScannerizzazioniPerAnnoScannerizzazione + getChildsByExtensionsAndYear(nodeService, nodeRefCartella,"pdf",annoToSearch).size();								
//					        }	
//							rs.close();
												
						}
//						logger.info("Totale cartelle cliniche per presidio: "+currentPresidioName+" no per anno "+annoToSearch+":"+ countTotal);	
//						logger.info("Totale cartelle per anno: '"+annoToSearch+"' e presidio: "+currentPresidioName+":"+ countAnnoToSearch);	
//						logger.info("Totale scannerizzazioni per anno cartella: '"+annoToSearch+"' e presidio: "+currentPresidioName+":"+ countScannerizzazioniPerAnnoCartella);	
//						logger.info("Totale scannerizzazioni per anno scannerizzazioni: '"+annoToSearch+"' e presidio: "+currentPresidioName+":"+ countScannerizzazioniPerAnnoScannerizzazione);	
																		
//						mapOut.put("Totale cartelle cliniche per presidio: "+currentPresidioName+" no per anno "+annoToSearch+"", countTotal);
//						mapOut.put("Totale cartelle per anno: '"+annoToSearch+"' e presidio: "+currentPresidioName+" ", countAnnoToSearch);
//						mapOut.put("Totale scannerizzazioni per anno cartella: '"+annoToSearch+"' e presidio: "+currentPresidioName+" ", countScannerizzazioniPerAnnoCartella);
//						mapOut.put("Totale scannerizzazioni per anno scannerizzazioni: '"+annoToSearch+"' e presidio: "+currentPresidioName+" ", countScannerizzazioniPerAnnoScannerizzazione);
//						mapOut.put("", "");
						
						logger.info("Totale cartelle cliniche per presidio '"+currentPresidioName+"' = " +countTotal);
						logger.info("Totale cartelle cliniche per anno '"+annoToSearch+"' e presidio '"+currentPresidioName+"' = "+ countAnnoToSearch);
						logger.info("Totale scannerizzazioni per anno cartella clinica'"+annoToSearch+"' e presidio '"+currentPresidioName+"' = " + countScannerizzazioniPerAnnoCartella);
						logger.info("Totale scannerizzazioni per anno scannerizzazioni '"+annoToSearch+"' e presidio '"+currentPresidioName+"' = "+ countScannerizzazioniPerAnnoScannerizzazione);
						
						listOut.add("Totale cartelle cliniche per presidio '"+currentPresidioName+"' = " +countTotal);
						listOut.add("Totale cartelle cliniche per anno '"+annoToSearch+"' e presidio '"+currentPresidioName+"' = "+ countAnnoToSearch);
						listOut.add("Totale scannerizzazioni per anno cartella clinica'"+annoToSearch+"' e presidio '"+currentPresidioName+"' = " + countScannerizzazioniPerAnnoCartella);					
						listOut.add("Totale scannerizzazioni per anno scannerizzazioni '"+annoToSearch+"' e presidio '"+currentPresidioName+"' = "+ countScannerizzazioniPerAnnoScannerizzazione);						
					}
				}
				//logger.info("Results: \r\n" + new PrettyPrintingMap<String, Serializable>(mapOut));
				logger.info("Results: \r\n" + new PrettyPrintingList<String>(listOut) + "\r\n");
				logger.info("---------------- Fine Esecuzione Azione QueryContatoreAction ----------------- \r\n");			
			}catch(Exception e){
				e.printStackTrace();
				logger.error(e.getMessage());
			}
	}
	
	private static long getCountResult(SearchService searchService, String query,String tipoQuery) {
		if(tipoQuery == null || tipoQuery.isEmpty()){
			tipoQuery = SearchService.LANGUAGE_LUCENE;
		}
		SearchParameters sp = new SearchParameters();
		sp.addLocale(new Locale("it", "IT"));
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(tipoQuery);
		sp.setQuery(query);
		sp.setLimitBy(LimitBy.UNLIMITED);
		ResultSet results = searchService.query(sp);		
	    long count = results.getNumberFound();
		results.close();		
		return count;
	}
	
	private static ResultSet getResult(SearchService searchService, String query,String tipoQuery) {
		if(tipoQuery == null || tipoQuery.isEmpty()){
			tipoQuery = SearchService.LANGUAGE_LUCENE;
		}
		SearchParameters sp = new SearchParameters();
		sp.addLocale(new Locale("it", "IT"));
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(tipoQuery);
		sp.setQuery(query);
		sp.setLimitBy(LimitBy.UNLIMITED);
		ResultSet results = searchService.query(sp);			  
		results.close();		
		return results;
	}
	
	private static String checkSearchLanguage(String searchLanguageQuery) throws Exception{
		if(searchLanguageQuery.toUpperCase()=="LUCENE"){
			return SearchService.LANGUAGE_LUCENE;
		}else if(searchLanguageQuery.toUpperCase().equalsIgnoreCase("SOLR-CMIS")){
			return SearchService.LANGUAGE_SOLR_CMIS;
		}else if(searchLanguageQuery.toUpperCase().equalsIgnoreCase("CMIS-ALFRESCO")){
			return SearchService.LANGUAGE_CMIS_ALFRESCO;
		}else if(searchLanguageQuery.toUpperCase().equalsIgnoreCase("CMIS-STRICT")){
			return SearchService.LANGUAGE_CMIS_STRICT;
		}else if(searchLanguageQuery.toUpperCase().equalsIgnoreCase("FTS-ALFRESCO")){
			return SearchService.LANGUAGE_FTS_ALFRESCO;
		}else if(searchLanguageQuery.toUpperCase().equalsIgnoreCase("JCR-XPATH")){
			return SearchService.LANGUAGE_JCR_XPATH;
		}else if(searchLanguageQuery.toUpperCase().equalsIgnoreCase("SOLR-ALFRESCO")){
			return SearchService.LANGUAGE_SOLR_ALFRESCO;
		}else if(searchLanguageQuery.toUpperCase().equalsIgnoreCase("SOLR-FTS-ALFRESCO")){
			return SearchService.LANGUAGE_SOLR_FTS_ALFRESCO;
		}else if(searchLanguageQuery.toUpperCase().equalsIgnoreCase("XPATH")){
			return SearchService.LANGUAGE_XPATH;
		}else{
			throw new Exception("IL tipo di linguaggio query '"+searchLanguageQuery+"' non è previsto da alfresco");
		}										
	}
	
	/**
	 * Metodo per creare un namespace resolver di default
	 * @return the resolver
	 */
	private static DynamicNamespacePrefixResolver getNamespaceResolver() {
	    DynamicNamespacePrefixResolver resolver = new DynamicNamespacePrefixResolver(null);
	    resolver.registerNamespace(NamespaceService.CONTENT_MODEL_PREFIX, NamespaceService.CONTENT_MODEL_1_0_URI);
	    resolver.registerNamespace(NamespaceService.APP_MODEL_PREFIX, NamespaceService.APP_MODEL_1_0_URI);
	    resolver.registerNamespace(SiteModel.SITE_MODEL_PREFIX, SiteModel.SITE_MODEL_URL);
	    return resolver;
	}
	
	public static String getPathWithoutLastNode(NodeService nodeService, NodeRef nr) {
	    Path fullPath = nodeService.getPath(nr);
	    Path path = fullPath.subPath(4, fullPath.size());
	    String pathStr = path.toPrefixString(getNamespaceResolver());       
	    return pathStr;
	}
	
	/**
	 * Metodo che prende la path da un rifeirmento al nodo
	 * @param nodeService
	 * @param nr
	 * @param resolver
	 * @return
	 */
	public static String getPath(NodeService nodeService, NodeRef nr,NamespacePrefixResolver resolver){		
		Path path = nodeService.getPath(nr);
		//If you need the folder path as prefixed string,
		if(resolver == null){
			resolver = getNamespaceResolver();					
		}
		return path.toPrefixString(resolver);
	}
	
//	public static List<NodeRef> getTargetFolderPaths(NodeService nodeService, NodeRef nr) {
//	    List<NodeRef> res = new ArrayList<NodeRef>();
//	    if (!nodeService.exists(nr)) {
//	        System.out.println("WARN. node is not exists: " + nr);
//	        return res;
//	    }
//	    String pathStr = getPathWithoutLastNode(nodeService,nr);
//	    ResultSet resultSet = searchService.query(new StoreRef(StoreRef.PROTOCOL_WORKSPACE,"SpacesStore"), 
//	            SearchService.LANGUAGE_LUCENE,
//	            MessageFormat.format("PATH:\"//{0}\"", pathStr));
//	    for (ResultSetRow row : resultSet) {
//	        NodeRef remoteFolder = row.getNodeRef();
//	        if (!siteService.getSite(remoteFolder).getShortName()
//	            .equals(Utils.getSiteShortName(Constants.MANAGER_SITE))) {          
//	                res.add(remoteFolder);
//	        }
//	    }
//	    resultSet.close();
//	    return res;
//	}
	
	private String retrieveFullParentQNamePath(NodeService nodeService, NodeRef nodeRef){      
      ChildAssociationRef car = nodeService.getPrimaryParent(nodeRef);
      NodeRef parent = car.getParentRef();
      Path path = nodeService.getPath(parent);
      return path.toPrefixString(getNamespaceResolver());      
   }
	
   /**
    * Metodo per prendere un nodo figlio dal nome 
    * @param nodeService
    * @param nodeRef
    * @param siblingName
    * @return il riferimento al nodo figlio
    */
   private NodeRef getSibling(NodeService nodeService,NodeRef nodeRef, String siblingName){	      
      ChildAssociationRef car = nodeService.getPrimaryParent(nodeRef);
      NodeRef parent = car.getParentRef();
      return nodeService.getChildByName(parent, ContentModel.ASSOC_CONTAINS, siblingName);
   }
	
   /**
    * Prendi tutti i figli del nodo corrente che sono file di una specifica estensione
    * @param nodeService
    * @param nodeRef
    * @return list dei riferimetni ai docuemnti pdf  figlio
    */
	private List<ChildAssociationRef> getChildsByExtensions(NodeService nodeService,NodeRef nodeRef,String myEstension){
		//Solo contenuti non cartelle
		Set<QName> types = new HashSet<>();
		types.add(ContentModel.TYPE_CONTENT);
		
		List<ChildAssociationRef> nodes = nodeService.getChildAssocs(nodeRef,types);
		List<ChildAssociationRef> onlyExt = new ArrayList<ChildAssociationRef>();
		for (ChildAssociationRef element : nodes) {
		   //Documento con extensions voluta all'internode l folder riferito con ricercaricorsiva all'intenro delle cartelle
		   NodeRef elNodeRef = element.getChildRef();
		   //logger.debug("NodeRef:"+elNodeRef+", Values:"+ Arrays.toString(nodeService.getProperties(elNodeRef).entrySet().toArray()));
		   String fileSannerizzazioneNome = (String) nodeService.getProperty(elNodeRef, ContentModel.PROP_NAME);  
		   //deve essere Identico a elNodeRef
		   NodeRef elNOdeRefChild2 = nodeService.getChildByName(nodeRef, ContentModel.ASSOC_CONTAINS,fileSannerizzazioneNome);
		   
		   //QName name = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,"name");
		   //String fileSannerizzazioneNome = (String)nodeService.getProperty(elNodeRef,name);		 
		   //logger.debug("NodeRef1:"+elNodeRef +",Nome File 1:"+fileSannerizzazioneNome );//, Nome File 2:" + element.getTypeQName().toString());
		   //è nullo
		   //NodeRef elNOdeRefChild = nodeService.getChildByName(elNodeRef, ContentModel.ASSOC_CONTAINS,fileSannerizzazioneNome);//getChildNodeRef(elNodeRef,fileSannerizzazioneNome,nodeService);  		
		   String extension = fileSannerizzazioneNome.substring(fileSannerizzazioneNome.lastIndexOf(".")).replace(".", "");
		   //logger.debug("NodeRef1:"+elNodeRef + ", NodeRef2:"+elNOdeRefChild2 + ", Nome File 1:"+fileSannerizzazioneNome +", Extensions:" + extension);		  
		   if (myEstension.equalsIgnoreCase(extension) || extension.contains(myEstension)) {
			   onlyExt.add(element);
		   }
		}
		return onlyExt;
	}
	
	private List<ChildAssociationRef> getChildsByExtensionsAndYear(NodeService nodeService,NodeRef nodeRef,String myEstension,int annoToSearch){
		//Solo contenuti non cartelle
		Set<QName> types = new HashSet<>();
		types.add(ContentModel.TYPE_CONTENT);		
		List<ChildAssociationRef> nodes = nodeService.getChildAssocs(nodeRef,types);
		List<ChildAssociationRef> onlyExt = new ArrayList<ChildAssociationRef>();
		for (ChildAssociationRef element : nodes) {
		   //Documento con extensions voluta all'internode l folder riferito con ricercaricorsiva all'intenro delle cartelle
		   NodeRef elNodeRef = element.getChildRef();
		   //logger.debug("NodeRef:"+elNodeRef+", Values:"+ Arrays.toString(nodeService.getProperties(elNodeRef).entrySet().toArray()));
		   String fileSannerizzazioneNome = (String) nodeService.getProperty(elNodeRef, ContentModel.PROP_NAME);  
		   //deve essere Identico a elNodeRef
		   NodeRef elNOdeRefChild2 = nodeService.getChildByName(nodeRef, ContentModel.ASSOC_CONTAINS,fileSannerizzazioneNome);
		   String extension = fileSannerizzazioneNome.substring(fileSannerizzazioneNome.lastIndexOf(".")).replace(".", "");
		   //logger.debug("NodeRef1:"+elNodeRef + ", NodeRef2:"+elNOdeRefChild2 + ", Nome File 1:"+fileSannerizzazioneNome +", Extensions:" + extension+", Year:" + annoToSearch);	
		   //Se è un pdf
		   if (myEstension.equalsIgnoreCase(extension) || extension.contains(myEstension)) {			  
			    //java.util.Date date = (java.util.Date)propRow.get("{http://www.alfresco.org/model/content/1.0}created");
			    java.util.Date date = (java.util.Date)nodeService.getProperty(elNodeRef, ContentModel.PROP_CREATED); 
			    Calendar cal = Calendar.getInstance();
			    cal.setTime(date);	
			    //Se è dell'annno cercato
				if(cal.get(Calendar.YEAR)==annoToSearch){
				   onlyExt.add(element);
				}
		   }
		}
		return onlyExt;
	}
	
	
	
	public static NodeRef getChildNodeRef(NodeRef parentSpaceNodeRef, String nodeName, NodeService nodeService)
	{
	   //NodeService nodeService = serviceRegistry.getNodeService();
	   NodeRef nodeRef = nodeService.getChildByName(parentSpaceNodeRef, ContentModel.ASSOC_CONTAINS,nodeName);
	   return nodeRef;
	}
	
//	private class PrettyPrintingMap<K, V> {
//	    private Map<K, V> map;
//
//	    public PrettyPrintingMap(Map<K, V> map) {
//	        this.map = map;
//	    }
//
//	    public String toString() {
//	        StringBuilder sb = new StringBuilder();
//	        Iterator<Entry<K, V>> iter = map.entrySet().iterator();
//	        while (iter.hasNext()) {
//	            Entry<K, V> entry = iter.next();
//	            sb.append(entry.getKey());
//	            sb.append('=').append('"');
//	            sb.append(entry.getValue());
//	            sb.append('"');
//	            if (iter.hasNext()) {
//	                sb.append(',').append(' ').append("\r\n");
//	            }
//	        }
//	        return sb.toString();
//
//	    }
//	}
	
	private class PrettyPrintingList<K> {
	    private List<K> list;

	    public PrettyPrintingList(List<K> list) {
	        this.list = list;
	    }

	    public String toString() {
	        StringBuilder sb = new StringBuilder();
	        Iterator<K> iter = list.iterator();
	        while (iter.hasNext()) {
	            K entry = iter.next();
//	            sb.append(entry.getKey());
//	            sb.append('=').append('"');
//	            sb.append(entry.getValue());
//	            sb.append('"');
	            sb.append(entry);
	            if (iter.hasNext()) {
	                sb.append(',').append(' ').append("\r\n");
	            }
	        }
	        return sb.toString();

	    }
	}
	
	/**
	 * Metodo per riempire una lista di un range di numeri stabilito
	 * @param start numero di partenza
	 * @param end numero di fine
	 * @return la lista riempite con il range dei numeri estremi compresi
	 */
	private List<Integer> fillListWithRangeNumber(int start,int end){
		 //List<Integer> values = concat(range(1, 10), range(500, 1000));
		 List<Integer> values = range(start, end);
	     List<Integer> list = new LinkedList<>(values);
	     return list;
	}
	
	/**
	 * Metodo di supporto per la realizzaizone di un range di numeri
	 * @param min il numero minimo
	 * @param max il numero massimo
	 * @return la lista riempite con il range dei numeri estremi compresi
	 */
    private static List<Integer> range(int min, int max) {
        List<Integer> list = new LinkedList<>();
        for (int i = min; i <= max; i++) {
            list.add(i);
        }
        return list;
    }


	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) { }

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	
	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public void setSearchLanguageQuery(String searchLanguageQuery) {
		this.searchLanguageQuery = searchLanguageQuery;
	}

	public void setSearchStartYear(String searchStartYear) {
		this.searchStartYear = searchStartYear;
	}

	public void setSearchEndYear(String searchEndYear) {
		this.searchEndYear = searchEndYear;
	}

//	public void setSearchQueryBasePath(String searchQueryBasePath) {
//		this.searchQueryBasePath = searchQueryBasePath;
//	}
//
//	public void setSearchQueryAll(String searchQueryAll) {
//		this.searchQueryAll = searchQueryAll;
//	}
	
	
	
	
	
	
	

	
	
	
}
