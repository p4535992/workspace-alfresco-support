package fi.vismaconsulting.alfresco.repo.content.transform.exiftool;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.content.transform.ContentTransformerHelper;
import org.alfresco.repo.content.transform.ContentTransformerWorker;
import org.alfresco.repo.content.transform.UnimportantTransformException;
import org.alfresco.service.cmr.repository.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Author: Peter Mikula <peter.mikula@proactum.fi>
 * Created: 5/19/15 1:22 PM
 */
public class ExiftoolContentTransformerWorker extends ContentTransformerHelper
        implements ContentTransformerWorker, InitializingBean {

    private static final Log logger = LogFactory.getLog(ExiftoolContentTransformerWorker.class);

    private String executable;

    public void setExecutable(String executable) {
        this.executable = executable;
    }

    private boolean skipCleanup;

    public void setSkipCleanup(boolean skipCleanup) {
        this.skipCleanup = skipCleanup;
    }

// ---

    private ExifTool tool;

    private boolean available;
    private String versionString;

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            if (StringUtils.isEmpty(executable)){
                for(String file : Arrays.asList("/usr/bin/exiftool", "/usr/local/bin/exiftool")) {
                    if (new File(file).isFile()){
                        executable = file;
                    }
                }
                if (StringUtils.isEmpty(executable)) {
                    executable = "/usr/bin/exiftool";
                }
            }
            tool = new ExifTool(executable);
            versionString = tool.getVersion();
            available = true;
        } catch (Throwable e) {
            logger.error(String.format("%s not available: %s", getClass().getSimpleName(), e.getMessage()));
            logger.debug(e);
            return;
        }
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public String getVersionString() {
        return versionString;
    }

    @Override
    public boolean isTransformable(String sourceMimetype, String targetMimetype, TransformationOptions options) {
        return (sourceMimetype.equals("application/x-indesign") || sourceMimetype.equals("application/x-adobe-indesign"))
                && (targetMimetype.equals("application/pdf") || targetMimetype.equals("image/jpeg"));
    }

    @Override
    public void transform(ContentReader reader, ContentWriter writer, TransformationOptions options) throws Exception {
        MimetypeService mimetypeService = getMimetypeService();

        String sourceMimetype = getMimetype(reader);
        String targetMimetype = getMimetype(writer);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Transforming from %s to %s", sourceMimetype, targetMimetype));
        }

        String sourceExtension = mimetypeService.getExtension(sourceMimetype);
        if (sourceExtension == null) {
            throw new AlfrescoRuntimeException("Unknown extensions for mimetypes: \n" +
                    "   source mimetype: " + sourceMimetype + "\n");
        }

        File sourceFile = null, workDir = null, targetFile = null;
        try {
            sourceFile = File.createTempFile(getClass().getSimpleName() + "_source_", "." + sourceExtension);
            reader.getContent(sourceFile);

            workDir = Files.createTempDirectory(getClass().getSimpleName() + "_target").toFile();

            List<File> pages = tool.extractPageImages(sourceFile, workDir);
            if (pages.isEmpty()) {
                File thumb = tool.extractThumbnail(sourceFile, workDir);
                if (thumb != null) {
                    pages = Collections.singletonList(thumb);
                }
            }

            if (pages.isEmpty()) {
                throw new UnimportantTransformException("embedded image not found.");
            }

            targetFile = pages.get(0);
            if (targetMimetype.equals("application/pdf")) {
                targetFile = createPdf(pages);
                if (targetFile == null) {
                    throw new ContentIOException("Exiftool transformation failed to write output file");
                }
            }
            writer.putContent(targetFile);

        } finally {
            if(!skipCleanup) {
                FileUtils.deleteQuietly(sourceFile);
                FileUtils.deleteQuietly(targetFile);
                FileUtils.deleteQuietly(workDir);
            }
        }
    }

    protected File createPdf(List<File> images) throws IOException, COSVisitorException {
        File file = null;
        PDDocument doc = null;
        try {
            doc = new PDDocument();
            for(File image : images) {
                PDPage page = new PDPage();
                doc.addPage(page);

                // FIXME: we assume thumbnails are jpegs...
                PDXObjectImage ximage = new PDJpeg(doc, new FileInputStream(image));
                PDPageContentStream contentStream = new PDPageContentStream(doc, page);
                contentStream.drawImage(ximage, 0, 0);
                contentStream.close();
                page.setMediaBox(new PDRectangle(ximage.getWidth(), ximage.getHeight()));
            }

            file = File.createTempFile(getClass().getSimpleName() + "_target_", ".pdf");
            doc.save(file);
        } finally {
            if (doc != null) {
                doc.close();
            }
        }
        return file;
    }
}
