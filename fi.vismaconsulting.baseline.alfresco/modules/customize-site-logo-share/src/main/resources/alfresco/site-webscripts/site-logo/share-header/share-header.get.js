<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">

var siteConfig = widgetUtils.findObject(model.jsonModel, "id", "HEADER_SITE_CONFIGURATION_DROPDOWN");
if (siteConfig != null && isSiteManager()) {

	var entry = {
        id : "HEADER_CUSTOMIZE_SITE_LOGO",
        name : "alfresco/menus/AlfMenuItem",
        config : {
            id : "HEADER_CUSTOMIZE_SITE_LOGO",
            label : "link.changeSiteLogo",
            title: "link.changeSiteLogo",
            iconAltText: "link.changeSiteLogo",
            iconClass : "alf-cog-icon",
            targetUrl : "site/" + page.url.templateArgs.site + "/change-site-logo"
        }
    };

    var widgets = siteConfig.config.widgets;

    // try to append after customize dashboard
    for(var pos = 0; pos < widgets.length; pos++) {
        if (widgets[pos].id == 'HEADER_CUSTOMIZE_SITE_DASHBOARD') {
            break;
        }
    }

	widgets.splice(pos + 1, 0, entry);
}

var headerLogoConfig = widgetUtils.findObject(model.jsonModel, "id", "HEADER_LOGO");
if (headerLogoConfig != null) {
	headerLogoConfig.config.logoSrc = getSiteLogoUrl()
}

function getSiteLogoUrl() {
	var logoSrc = context.getSiteConfiguration().getProperty("logo");
	if (logoSrc && logoSrc.length() > 0) {
		// Use the site configured logo as the source for the logo image.
		logoSrc = url.context + "/proxy/alfresco/api/node/"
				+ logoSrc.replace("://", "/") + "/content";
	} else {
		// Use the message bundled configured logo as the logo source.
		// This is theme specific
		var propsLogo = msg.get("header.logo");
		if (propsLogo == "header.logo") {
			propsLogo = "app-logo-48.png";
		}
		logoSrc = url.context + "/res/themes/" + theme + "/images/" + propsLogo;
	}

	var siteId = page.url.templateArgs.site || "";
	if (siteId !== "") {
		var p = sitedata.getPage("site/" + siteId + "/dashboard");
		var siteLogo = p.properties.siteLogo;
		if (siteLogo !== null && typeof siteLogo !== "undefined"
				&& siteLogo.length !== 0) {
			logoSrc = url.context + "/proxy/alfresco/api/node/"
			+ siteLogo.replace("://", "/") + "/content";
		}
	}

	return logoSrc;
}

function isSiteManager() {
	var siteData = getSiteData();
	return siteData != null && siteData.userIsSiteManager;
}
