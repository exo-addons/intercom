@Application
@Portlet(name="RespondPortlet")
@Scripts(
        {
                @Script(value = "assets/js/jquery/jquery-3.2.1.min.js", id = "jquery",location = AssetLocation.SERVER),
                @Script(value = "assets/js/intercom/respond.js",depends = "jquery",location = AssetLocation.SERVER),
                @Script(value = "assets/js/app.js", location = AssetLocation.SERVER)
        }
)
@Stylesheets(
        {
                @Stylesheet(id = "respond", value = "/org/exoplatform/intercom/respond/assets/respond.css", location = AssetLocation.APPLICATION)
        }
)
@Less(value = "respond.less", minify = true)

@Assets("*")
package org.exoplatform.intercom.respond;
import juzu.Application;
import juzu.plugin.asset.Assets;
import juzu.asset.AssetLocation;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Scripts;
import juzu.plugin.asset.Stylesheet;
import juzu.plugin.asset.Stylesheets;
import juzu.plugin.less.Less;
import juzu.plugin.portlet.Portlet;