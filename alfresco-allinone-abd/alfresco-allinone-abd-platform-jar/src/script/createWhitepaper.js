//Set up some variables you'll use later in the script. The document variable 
//is a root object that is available when the script is executed against a document.
var contentType = "whitepaper"; 
var contentName = "wp-"; 
var timestamp = new Date().getTime(); 
var extension = document.name.substr(document.name.lastIndexOf('.') + 1); 
//Write code that will specialize the uploaded document.
document.specializeType("sc:" + contentType); 
//Add a statement that uses the ScriptNode API to add the sc:webable aspect.
document.addAspect("sc:webable"); 
//Add code to set some properties. These properties include out of the box properties
//such as cm:name as well as SomeCo-specific properties.
//Notice the contentName and timestamp variables are being concatenated 
//to make sure the name is unique on successive runs.
document.properties["cm:name"] = contentName + " (" + timestamp + ")." + extension; 
document.properties["sc:isActive"] = true; 
document.properties["sc:published"] = new Date("07/31/2016"); 
//Call save to persist the changes.
document.save();