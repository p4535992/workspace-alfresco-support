<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/documentlibrary/action/action.lib.js">

/**
 * Add / Remove Aspects action
 * @method POST
 */

function jsonToArray(p_name)
{
   var array = [];
   
   if (json.has(p_name))
   {
      var jsonArray = json.get(p_name);
      for (var i = 0, ii = jsonArray.length(); i < ii; i++)
      {
         array.push(jsonArray.get(i));
      }
   }
   
   return array;
}


/**
 * Entrypoint required by action.lib.js
 *
 * @method runAction
 * @param p_params {object} Object literal containing files array
 * @return {object|null} object representation of action results
 */
function runAction(p_params)
{
	 logger.error("CHECKOUT4");
   var result,
      assetNode = p_params.destNode;
 logger.error("CHECKOUT5");
   try
   {
      result =
      {
         nodeRef: assetNode.nodeRef.toString(),
         action: "manageAspects",
         success: false
      }
 logger.error("CHECKOUT6");
      result.id = assetNode.name;
      result.type = assetNode.isContainer ? "folder" : "document";
 logger.error("CHECKOUT7");
      var added = jsonToArray("added"),
         removed = jsonToArray("removed"),
         isTaggable = false,
         i, ii;
 logger.error("CHECKOUT8");
      // Aspects to be removed
      for (i = 0, ii = removed.length; i < ii; i++)
      {
         if (assetNode.hasAspect(removed[i]))
         {
            assetNode.removeAspect(removed[i]);
            isTaggable = isTaggable || (removed[i] == "cm:taggable");
         }
      }
 logger.error("CHECKOUT9");
      // Aspects to be added
      for (i = 0, ii = added.length; i < ii; i++)
      {
		   logger.error("CHECKOUT9.1: ID=" + i + ",APSECT=" +added[i] + ",NodeRef=" + assetNode.nodeRef.toString());
		   logger.error(assetNode.aspects);
		   logger.error(assetNode.hasAspect(added[i]));
         if (!assetNode.hasAspect(added[i]))
         {
			 logger.error("CHECKOUT9.2: ID=" + i);
            assetNode.addAspect(added[i]);
			logger.error("CHECKOUT9.3: ID=" + i);
            isTaggable = isTaggable || (added[i] == "cm:taggable");
			 logger.error("CHECKOUT9.4: ID=" + i);
         }
		 logger.error("CHECKOUT9.5: ID=" + i);
      }
   logger.error("CHECKOUT10");
      // Update the tag scope? This would be nice, but will be quite slow & synchronous.
      // We'll send back the flag anyway and the client can fire off a REST API call to do it.
      if (isTaggable)
      {
         // assetNode.tagScope.refresh();
         result.tagScope = true;
      }
      logger.error("CHECKOUT11");
      result.success = true;
   }
   catch (e)
   {
	    logger.error("CHECKOUT12:" + e);
	    logger.error("CHECKOUT13:" + e.stack);
      result.success = false;
	  logger.error("CHECKOUT14:" + e);
	    logger.error("CHECKOUT15:" + e.stack);
   }
   logger.error("CHECKOUT16:" + result);
   return [result];
}

/* Bootstrap action script */
main();
