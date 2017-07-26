package fi.vismaconsulting.alfresco.repo.content.transform.exiftool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;
import org.springframework.beans.factory.InitializingBean;

/**
 * Author: Peter Mikula <peter.mikula@proactum.fi>
 * Created: 5/20/15 12:58 PM
 */
public class TikaMediaTypePatch implements InitializingBean {

    private static final Log logger = LogFactory.getLog(TikaMediaTypePatch.class);

    private TikaConfig tikaConfig;

    public void setTikaConfig(TikaConfig tikaConfig) {
        this.tikaConfig = tikaConfig;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        MediaType correctType = new MediaType("application", "x-indesign");
        MediaType incorrectType = new MediaType("application", "x-adobe-indesign");

        MediaTypeRegistry registry = tikaConfig.getMediaTypeRegistry();
        if (registry.getTypes().contains(incorrectType) && !registry.getAliases(incorrectType).contains(correctType)) {
            logger.info("Patching TIKA media type registry to provide application/x-indesign alias for indd files.");
            registry.addAlias(incorrectType, correctType);
        }
    }
}
