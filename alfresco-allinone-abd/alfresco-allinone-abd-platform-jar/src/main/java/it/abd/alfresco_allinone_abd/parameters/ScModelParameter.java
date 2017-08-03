package it.abd.alfresco_allinone_abd.parameters;



import org.alfresco.api.AlfrescoPublicApi;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
* Sc Model Constants
*/
@AlfrescoPublicApi
public interface ScModelParameter
{
   //
   // System Model Definitions
   //
    public final static String SOMECO_MODEL_PREFIX= "sc"; 
    public final static String SOMECO_MODEL_URI = "http://www.someco.com/model/content/1.0"; 

//    public final static String SC_MODEL = "http://www.alfresco.org/model/content/1.0";
//    public final static String SC_MODEL_URI = "http://www.alfresco.org/model/content/1.0";
//    public final static String SC_PREFIX = "sc";
//
//    public final static QName TYPE_BASE = QName.createQName(SC_MODEL_URI, "doc");
//    public final static QName ASSOC_RELATEDDOCUMENTS_LINK = QName.createQName(SC_MODEL_URI, "relatedDocuments");
//    public final static QName ASPECT_GENERALCLASSIFIABLE = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "generalclassifiable");

    
    public final static QName TYPE_DOC = QName.createQName(SOMECO_MODEL_URI, "doc"); 
    public final static QName TYPE_MARKETINGDOC = QName.createQName(SOMECO_MODEL_URI, "marketingDoc"); 
    public final static QName TYPE_WHITEPAPER = QName.createQName(SOMECO_MODEL_URI, "whitepaper"); 
    public final static QName TYPE_HRDOC = QName.createQName(SOMECO_MODEL_URI, "hrDoc"); 
    public final static QName TYPE_SALESDOC = QName.createQName(SOMECO_MODEL_URI, "salesDoc"); 
    public final static QName TYPE_HRPOLICY = QName.createQName(SOMECO_MODEL_URI, "hrPolicy"); 
    public final static QName TYPE_LEGALDOC = QName.createQName(SOMECO_MODEL_URI, "legalDoc"); 
    
    public final static QName ASPECT_WEBABLE = QName.createQName(SOMECO_MODEL_URI, "webable"); 
    public final static QName ASPECT_CLIENTRELATED = QName.createQName(SOMECO_MODEL_URI, "clientRelated"); 
    
    public final static QName PROP_CAMPAIGN = QName.createQName(SOMECO_MODEL_URI, "campaign"); 
    public final static QName PROP_PUBLISHED = QName.createQName(SOMECO_MODEL_URI, "published"); 
    public final static QName PROP_ISACTIVE = QName.createQName(SOMECO_MODEL_URI, "isActive"); 
    public final static QName PROP_CLIENTNAME = QName.createQName(SOMECO_MODEL_URI, "clientName"); 
    public final static QName PROP_PROJECTNAME = QName.createQName(SOMECO_MODEL_URI, "projectName"); 
    public final static QName ASSOC_RELATEDOCUMENTS =  QName.createQName(SOMECO_MODEL_URI, "relatedDocuments"); 
  

  

 

//   static final QName PROP_STORE_PROTOCOL = QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, "store-protocol");
//   static final QName PROP_STORE_IDENTIFIER = QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, "store-identifier");
//   static final QName PROP_NODE_UUID = QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, "node-uuid");
//   static final QName PROP_NODE_DBID = QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, "node-dbid");

}