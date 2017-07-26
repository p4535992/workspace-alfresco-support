package fi.vismaconsulting.alfresco.repo.content.transform.exiftool;

import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.util.exec.RuntimeExec;
import org.alfresco.util.exec.RuntimeExec.ExecutionResult;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Peter Mikula <peter.mikula@proactum.fi>
 * Created: 5/20/15 8:29 AM
 */
public class ExifTool {

    private String executable = "/usr/bin/exiftool";

    private String versionCmd = "@EXE@ -m -v0 -ver";
    private String thumbnailCmd = "@EXE@ -m -v0 -b -ThumbnailImage -W %f-thumb.%s @IN@";
    private String pageImageCmd = "@EXE@ -m -v0 -b -PageImage -W %f-page%c.%s @IN@";

    public ExifTool(String executable) {
        this.executable = executable;
    }

    public String getVersion() {
        ExecutionResult result = exec(versionCmd, Collections.<String, Object>emptyMap());
        if (!result.getSuccess()){
            throw new ContentIOException("Failed to perform Exiftool transformation: \n" + result);
        }
        return result.getStdOut().trim();
    }

    public List<File> extractPageImages(File sourceFile, File workDir) throws IOException {
        Map<String,String> params = Collections.singletonMap("IN", sourceFile.getAbsolutePath());
        ExecutionResult result = exec(pageImageCmd, params, workDir.getAbsolutePath());
        if (!result.getSuccess()){
            throw new ContentIOException("Failed to perform Exiftool transformation: \n" + result);
        }

        // parse results
        String baseName = FilenameUtils.getBaseName(sourceFile.getName());
        final Pattern pattern = Pattern.compile(Pattern.quote(baseName) + "-page(\\d*)\\.[^.]+");
        File[] files = workDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return pattern.matcher(name).matches();
            }
        });

        if (files == null || files.length == 0) {
            return Collections.emptyList();
        }

        // we need to sort pages numerically!
        List<File> list = new ArrayList<File>(Arrays.asList(files));
        Collections.sort(list, new Comparator<File>(){
            @Override
            public int compare(File f1, File f2) {
                int result = Long.compare(getIndex(f1), getIndex(f2));
                return result == 0 ? f1.compareTo(f2) : result;
            }
            protected long getIndex(File f){
                long index = Long.MAX_VALUE;
                Matcher matcher = pattern.matcher(f.getName());
                if (matcher.matches()){
                    String value = matcher.group(1);
                    index = value.isEmpty() ? 0 : Long.valueOf(value);
                }
                return index;
            }
        });
        return list;
    }

    public File extractThumbnail(File sourceFile, File workDir) throws IOException {
        Map<String,String> params = Collections.singletonMap("IN", sourceFile.getAbsolutePath());
        ExecutionResult result = exec(thumbnailCmd, params, workDir.getAbsolutePath());
        if (!result.getSuccess()){
            throw new ContentIOException("Failed to perform Exiftool transformation: \n" + result);
        }

        String baseName = FilenameUtils.getBaseName(sourceFile.getName());
        final Pattern pattern = Pattern.compile(Pattern.quote(baseName) + "-thumb\\.[^.]+");
        File[] files = workDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return pattern.matcher(name).matches();
            }
        });

        return files == null || files.length == 0 ? null : files[0];
    }

    protected ExecutionResult exec(String cmd, Map<String,?> params){
        return exec(cmd, params, "${java.io.tmpdir}");
    }

    protected ExecutionResult exec(String cmd, Map<String,?> params, String workDir){
        RuntimeExec re = mkExec(cmd, params, workDir);
        return re.execute();
    }

    protected RuntimeExec mkExec(String cmd, Map<String,?> params, String workDir) {
        List<String> args = new ArrayList<String>(16);
        for(String token : cmd.split("\\s+")) {
            if ("@EXE@".equals(token)){
                token = executable;
            } else if (token.startsWith("@") && token.endsWith("@")){
                Object value = params.get(token.substring(1, token.length()-1));
                token = value == null ? "" : String.valueOf(value);
            }
            args.add(token);
        }

        RuntimeExec re = new RuntimeExec();
        re.setCommand(args.toArray(new String[args.size()]));
        re.setProcessDirectory(workDir);
        re.setWaitForCompletion(true);
        re.setCharset("UTF-8");
        return re;
    }
}
