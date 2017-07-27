package org.alfresco.plugin.digitalSigning.utils;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.http.ParseException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

public class HandleFile extends org.springframework.extensions.webscripts.AbstractWebScript
{
  ServiceRegistry service;
  
  public HandleFile() {}
  
  public void setServiceRegistry(ServiceRegistry service)
  {
    this.service = service;
  }
  
  public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException
  {
    try {
      PDDocument pdf = null;
      InputStream is = null;
      InputStream cis = null;
      File tempDir = null;
      File tempFile = null;
      ContentWriter writer = null;
      NodeRef savefolder = null;
      String namefile = null;
      String namefilefinal = null;
      
      try
      {
        String[] pages = req.getParameter("pagenumber").split(",");
        
        int insertAt = Integer.valueOf(pages[0]).intValue();
        
        int x = Integer.valueOf(req.getParameter("cordinate_x")).intValue();
        int y = Integer.valueOf(req.getParameter("cordinate_y")).intValue();
        
        NodeRef noderefSource = new NodeRef(req.getParameter("sourcenodeRef"));
        NodeRef noderef = new NodeRef(req.getParameter("nodeRef"));
        
        ContentReader ctnodeRef = service.getFileFolderService().getReader(noderef);
        ContentReader ctnodeRefSource = service.getFileFolderService().getReader(noderefSource);
        
        is = ctnodeRefSource.getContentInputStream();
        
        cis = ctnodeRef.getContentInputStream();
        
        byte[] imagebytes = new byte[50000];
        cis.read(imagebytes);
        
        File alfTempDir = org.alfresco.util.TempFileProvider.getTempDir();
        tempDir = new File(alfTempDir.getPath() + File.separatorChar + noderefSource.getId());
        tempDir.mkdir();
        String fileName = "";
        
        if (!service.getNodeService().getParentAssocs(noderefSource).isEmpty()) {
          ChildAssociationRef ref = (ChildAssociationRef)service.getNodeService().getParentAssocs(noderefSource).get(0);
          savefolder = ref.getParentRef();
        }
        
        if (!fileName.equals("")) {
          tempFile = new File(alfTempDir.getPath() + File.separatorChar + noderefSource.getId() + File.separatorChar + fileName + ".pdf");
        }
        else {
          namefile = service.getFileFolderService().getFileInfo(noderefSource).getName();
          List<FileInfo> files = service.getFileFolderService().listFiles(savefolder);
          for (int i = 1; i <= 10000; i++) {
            String[] splittedname = namefile.split("[.]");
            namefilefinal = splittedname[0] + "-" + i + "." + splittedname[1];
            for (FileInfo file : files) {
              if (file.getName().equals(namefilefinal)) {
                break;
              }
            }
            break;
          }
          tempFile = new File(alfTempDir.getPath() + File.separatorChar + noderefSource.getId() + File.separatorChar + namefilefinal);
        }
        
        tempFile.createNewFile();
        Image image = Image.getInstance(imagebytes);
        
        PdfReader pdfReader = new PdfReader(is);
        PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(tempFile));
        PdfContentByte content;
        for (int i = 0; i < pages.length; i++) {
          insertAt = Integer.valueOf(pages[i]).intValue();
          content = pdfStamper.getUnderContent(insertAt);
          content = pdfStamper.getOverContent(insertAt);
          image.scalePercent(50.0F);
          image.setAbsolutePosition(x, y);
          content.addImage(image);
        }
        
        pdfStamper.close();
        pdfReader.close();
        
        for (File file : tempDir.listFiles()) {
          try {
            if (file.isFile())
            {
              NodeRef destinationNode = createDestinationNode(file.getName(), savefolder, noderefSource);
              
              writer = service.getContentService().getWriter(destinationNode, org.alfresco.model.ContentModel.PROP_CONTENT, true);
              
              writer.setEncoding(ctnodeRef.getEncoding());
              
              writer.setMimetype("application/pdf");
              
              writer.putContent(file);
              
              file.delete();
            }
          } catch (FileExistsException e) {
            throw new AlfrescoRuntimeException("Failed to process file.", e);
          }
        }
      }
      catch (IOException e) {
        throw new AlfrescoRuntimeException(e.getMessage(), e);
      } catch (DocumentException e1) {
        e1.printStackTrace();
      } finally {
        if (pdf != null) {
          try {
            pdf.close();
          } catch (IOException e) {
            throw new AlfrescoRuntimeException(e.getMessage(), e);
          }
        }
        if (is != null) {
          try {
            is.close();
          } catch (IOException e) {
            throw new AlfrescoRuntimeException(e.getMessage(), e);
          }
        }
        if (tempDir != null) {
          tempFile.delete();
          tempDir.delete();
        }
      }
      res.getWriter().write("File has been generated sucecessfully.");
    }
    catch (ParseException e)
    {
      e.printStackTrace();
    }
  }
  

  private NodeRef createDestinationNode(String filename, NodeRef destinationParent, NodeRef target)
  {
    NodeService nodeService = service.getNodeService();
    FileInfo fileInfo = null;
    
    fileInfo = service.getFileFolderService().create(destinationParent, filename, nodeService.getType(target));
    


    NodeRef destinationNode = fileInfo.getNodeRef();
    
    return destinationNode;
  }
}
