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
   static final String SC_MODEL = "http://www.alfresco.org/model/content/1.0";
   static final String SC_MODEL_URI = "http://www.alfresco.org/model/content/1.0";
   static final String SC_PREFIX = "sc";

   static final QName TYPE_BASE = QName.createQName(SC_MODEL_URI, "doc");
   static final QName ASSOC_RELATEDDOCUMENTS_LINK = QName.createQName(SC_MODEL_URI, "relatedDocuments");
   static final QName ASPECT_GENERALCLASSIFIABLE = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "generalclassifiable");
//   static final QName PROP_STORE_PROTOCOL = QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, "store-protocol");
//   static final QName PROP_STORE_IDENTIFIER = QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, "store-identifier");
//   static final QName PROP_NODE_UUID = QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, "node-uuid");
//   static final QName PROP_NODE_DBID = QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, "node-dbid");

}