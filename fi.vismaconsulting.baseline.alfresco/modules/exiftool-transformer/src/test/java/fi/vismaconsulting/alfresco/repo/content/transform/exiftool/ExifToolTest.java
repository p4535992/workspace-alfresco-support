package fi.vismaconsulting.alfresco.repo.content.transform.exiftool;

import org.apache.commons.io.FileUtils;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

/**
 * Author: Peter Mikula <peter.mikula@proactum.fi>
 * Created: 5/20/15 9:04 AM
 */
public class ExifToolTest {

    private ExifTool tool;

    private File workDir;

    @Before
    public void setUp() throws IOException {

        File exe = new File("/usr/bin/exiftool");
        if (!exe.isFile()) {
            exe = new File("/usr/local/bin/exiftool");
        }
        Assume.assumeTrue(exe.isFile());
        tool = new ExifTool(exe.getPath());

        workDir = Files.createTempDirectory(getClass().getSimpleName()).toFile();
    }

    @After
    public void cleanUp() throws IOException {
        if (workDir != null) {
            FileUtils.deleteDirectory(workDir);
        }
    }

    @Test
    public void testGetVersion() throws Exception {
        String version = tool.getVersion();
        Assert.assertNotNull(version);
        Assert.assertTrue(version.matches("^\\d+\\.\\d+$"));
    }

    @Test
    public void testExtractThumbnail() throws Exception {
        File sourceFile = resolve("thumbnail.indd");
        File targetFile = tool.extractThumbnail(sourceFile, workDir);

        Assert.assertNotNull(targetFile);
        Assert.assertEquals(3868, targetFile.length());
        assertContentEquals(resolve("thumbnail.jpg"), targetFile);
    }

    @Test
    public void testExtractNoThumbnail() throws Exception {
        File sourceFile = resolve("testidokumentti.indd");
        File targetFile = tool.extractThumbnail(sourceFile, workDir);
        Assert.assertNull(targetFile);
    }

    @Test
    public void testExtractPageImage() throws Exception {
        File sourceFile = resolve("testidokumentti.indd");
        List<File> targetFiles = tool.extractPageImages(sourceFile, workDir);

        Assert.assertNotNull(targetFiles);
        Assert.assertEquals(2, targetFiles.size());

        assertContentEquals(resolve("testidokumentti-p1.jpg"), targetFiles.get(0));
        assertContentEquals(resolve("testidokumentti-p2.jpg"), targetFiles.get(1));
    }

    @Test
    public void testExtractNoPageImage() throws Exception {
        File sourceFile = resolve("thumbnail.indd");
        List<File> targetFiles = tool.extractPageImages(sourceFile, workDir);
        Assert.assertNotNull(targetFiles);
        Assert.assertEquals(0, targetFiles.size());
    }

    protected File resolve(String resourcePath) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(resourcePath);
        Assume.assumeTrue(url != null && "file".equals(url.getProtocol()));
        return new File(url.getPath());
    }

    protected void assertContentEquals(File expectedFile, File actualFile) throws IOException {
        byte[] expected = FileUtils.readFileToByteArray(expectedFile);
        byte[] actual = FileUtils.readFileToByteArray(actualFile);
        Assert.assertArrayEquals(expected, actual);
    }
}