package org.alfresco.plugin.digitalSigning.utils;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import javax.imageio.ImageIO;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.http.ParseException;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

public class Picture
  extends AbstractWebScript
{
  ServiceRegistry service;
  
  public void setServiceRegistry(ServiceRegistry service)
  {
    this.service = service;
  }
  
  public void execute(WebScriptRequest req, WebScriptResponse res)
    throws IOException
  {
    try
    {
      try
      {
        String[] pages = req.getParameter("pagenumber").split(",");
        int insertAt = Integer.valueOf(pages[0]).intValue();
        NodeRef noderefSource = new NodeRef(req.getParameter("sourcenodeRef"));
        ContentReader ctnodeRefSource = this.service.getFileFolderService().getReader(noderefSource);
        FileChannel channel = ctnodeRefSource.getFileChannel();
        ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0L, channel.size());
        PDFFile pdffile = new PDFFile(buf);
        PDFPage page = pdffile.getPage(insertAt);
        Rectangle rect = new Rectangle(0, 0, 
          (int)page.getBBox().getWidth(), 
          (int)page.getBBox().getHeight());
        Image img = page.getImage(
          220, 250, 
          rect, 
          null, 
          true, 
          true);
        
        BufferedImage bufferedImage = new BufferedImage(rect.width, rect.height, 1);
        Graphics g = bufferedImage.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        ImageIO.write(bufferedImage, "jpg", res.getOutputStream());
        bufferedImage.flush();
        channel.close();
      }
      catch (IOException e)
      {
        throw new AlfrescoRuntimeException(e.getMessage(), e);
      }
    }
    catch (ParseException e)
    {
      e.printStackTrace();
    }
  }
}
