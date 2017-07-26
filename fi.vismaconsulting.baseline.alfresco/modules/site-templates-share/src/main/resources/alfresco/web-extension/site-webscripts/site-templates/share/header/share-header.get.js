var siteService = widgetUtils.findObject(model.jsonModel, "id", "SITE_SERVICE");
if (siteService && siteService.config) {
    var additionalSitePresets = siteService.config.additionalSitePresets
        || (siteService.config.additionalSitePresets = []);

    var json = remote.call("/api/sites");
    if (json.status == 200) {
        var data = JSON.parse(json);
        if (data && data.length) {
            for (var i = 0; i < data.length; i++) {
                var info = data[i];
                if (info.shortName.indexOf("template-") === 0) {
                    additionalSitePresets.push({ value: info.shortName, label: info.title });
                }
            }
        }
    }
}
