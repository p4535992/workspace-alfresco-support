package it.abd.alfresco_allinone_abd.actions;

import java.util.List;

import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase; 
import org.alfresco.service.cmr.action.Action; 
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName; 

public class MoveReplacedActionExecuter extends ActionExecuterAbstractBase {
	

	private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MoveReplacedActionExecuter.class);
    //private static java.util.logging.Logger logger =  java.util.logging.Logger.getLogger(MoveReplacedActionExecuter.class); 
	
	public static final String NAME = "move-replaced"; 
	
    public static final String PARAM_DESTINATION_FOLDER = "destination-folder"; 
    
    protected NodeService nodeService;
    protected FileFolderService fileFolderService;

	@Override 
	protected void executeImpl(Action ruleAction, NodeRef actionedUponNodeRef) { 
		
		//The first thing to do is to create the QName object for the replaces
		//association, and check if some association are defined on the current object:
        QName assocReplacesQname = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "replaces"); 
        List<AssociationRef> assocRefs = nodeService.getTargetAssocs(actionedUponNodeRef, assocReplacesQname); 
        if (!assocRefs.isEmpty()) { 
            //Then, in the if statement, we need to browse all associated items:
            NodeRef destinationParent = (NodeRef)ruleAction.getParameterValue(PARAM_DESTINATION_FOLDER); 
            for (AssociationRef assocNode : assocRefs) { 
              try { 
            	  //Finally, we need to process each association:
                  NodeRef replacedDocument = assocNode.getTargetRef(); 
                  if (nodeService.exists(replacedDocument) == true) { 
                	  fileFolderService.move(replacedDocument,destinationParent, null); 
                  }
              } catch (Exception e) { 
            	  logger.error("Error moving the document: " +  assocNode.getTargetRef()); 
              } 
            }     

        } 
		
	} 

	@Override 
	protected void 
	addParameterDefinitions(List<ParameterDefinition> paramList) { 
		paramList.add(new ParameterDefinitionImpl(
				PARAM_DESTINATION_FOLDER,DataTypeDefinition.NODE_REF,true,getParamDisplayLabel(PARAM_DESTINATION_FOLDER))); 
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	} 
	
	
	

}
