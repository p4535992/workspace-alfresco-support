package it.abd.alfresco_allinone_abd.actions;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase; 
import org.alfresco.service.cmr.action.Action; 
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService; 
import org.alfresco.service.namespace.QName;

import it.abd.alfresco_allinone_abd.parameters.ScModelParameter;

public class SetWebFlag extends ActionExecuterAbstractBase { 
	
	protected  NodeService nodeService;
	public final static String NAME = "set-web-flag"; 

    public final static String PARAM_ACTIVE = "active"; 

    @Override 
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef) { 
        boolean activeFlag = true; 

        if (action.getParameterValue(PARAM_ACTIVE) != null){    
        	activeFlag = (Boolean) action.getParameterValue(PARAM_ACTIVE);
        }
        
        Map<QName, Serializable> properties = nodeService.getProperties(actionedUponNodeRef); 
        properties.put(ScModelParameter.PROP_ISACTIVE, activeFlag);
        
        if (activeFlag) { 
            properties.put(ScModelParameter.PROP_PUBLISHED, new Date()); 
        } 
        
        // if the aspect has already been added, set the properties 
        if (nodeService.hasAspect(actionedUponNodeRef,ScModelParameter.ASPECT_WEBABLE)) { 
        	nodeService.setProperties(actionedUponNodeRef, properties); 
        } else { 
        	// otherwise, add the aspect and set the properties 
        	nodeService.addAspect(actionedUponNodeRef, ScModelParameter.ASPECT_WEBABLE, properties); 
        }



    } 
	
    @Override 
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {  	
        paramList.add(new ParameterDefinitionImpl(PARAM_ACTIVE, DataTypeDefinition.BOOLEAN,false, getParamDisplayLabel(PARAM_ACTIVE))); 

    }

        
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	} 

} 

