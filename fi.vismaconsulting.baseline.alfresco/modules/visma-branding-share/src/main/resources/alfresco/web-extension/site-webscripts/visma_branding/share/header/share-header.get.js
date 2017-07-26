

var DEFAULT_LOGO_SRC = url.context + "/res/themes/" + theme + "/images/app-logo";

process(model.jsonModel.widgets);

function process(widgets){
    for(var i = 0; i < widgets.length; i++){
        var widget = widgets[i];
        if (widget.id === 'HEADER_LOGO') {
            if (widget.config.logoSrc.indexOf(DEFAULT_LOGO_SRC) === 0){
                widget.config.logoSrc = url.context + "/res/images/visma-logo.png";
            }
        } else if (widget.config.widgets) {
            process(widget.config.widgets);
        }
    }
}
