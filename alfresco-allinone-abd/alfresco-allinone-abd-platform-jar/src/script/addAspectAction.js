logger.log("Start Add Aspect Action:" + aspectName);
//var aspectName = "sc:clientRelated"; 
var aspectName = args[0];
var addAspectAction = actions.create("add-features"); 
addAspectAction.parameters["aspect-name"] = aspectName; 
addAspectAction.execute(document); 
logger.log("End Add Aspect Action:" + aspectName);
