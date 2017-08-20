package it.abd.alfresco_allinone_abd.transformer;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.transform.AbstractContentTransformer2;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.TransformationOptions;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.mpp.MPPReader;

public class ProjectToPlainText extends AbstractContentTransformer2{

	/**
	 * It does the bulk of the work, 
	 * although the MPXJ library does the heavy lifting. 
	 * The method uses the MPXJ library to iterate through the tasks 
	 * and writes out the task information:
	 */
	@Override
	protected void transformInternal(ContentReader reader, ContentWriter writer, TransformationOptions transformationOptions) throws Exception {
		Writer out = new BufferedWriter(new OutputStreamWriter(writer.getContentOutputStream())); 

		ProjectFile mpp = new MPPReader().read(reader.getContentInputStream()); 
		ProjectProperties projectHeader = mpp.getProjectProperties(); 
		List<net.sf.mpxj.Task> listAllTasks = mpp.getAllTasks(); 
		List<net.sf.mpxj.Resource> listAllResources = mpp.getAllResources(); 

		out.write(projectHeader.getProjectTitle()); 
		for (net.sf.mpxj.Task task : listAllTasks) { 
			out.write("ID:" + task.getID()); 
			out.write(" TASK:" + task.getName()); 
			if (task.getNotes() != null) out.write(" NOTES:" + task.getNotes()); 
			if (task.getContact() != null) out.write(" CONTACT:" + task.getContact()); 
			out.write("\r\n"); 
		} 
		for (net.sf.mpxj.Resource resource : listAllResources) { 
			out.write("RESOURCE:" + resource.getName()); 
			if (resource.getEmailAddress() != null) 
				out.write(" EMAIL:" + resource.getEmailAddress()); 
			if (resource.getNotes() != null) 
				out.write("  NOTES:" + resource.getNotes()); 
			out.write("\r\n"); 
		} 

		out.flush(); 

		if (out != null) { 
			out.close(); 
		} 

	}

	public boolean isTransformable(String sourceMimetype, String targetMimetype, TransformationOptions options) { 
		if ("application/vnd.ms-project".equalsIgnoreCase(sourceMimetype) && 
				MimetypeMap.MIMETYPE_TEXT_PLAIN .equalsIgnoreCase(targetMimetype)) {
			return true; 
		}else{
			return false; 
		}
	} 


}
