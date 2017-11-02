@Application
@Portlet(name="RespondPortlet")
@Scripts(
        {
                @Script(value = "js/jquery-3.2.1.min.js", id = "jquery",location = AssetLocation.SERVER),
                @Script(value = "js/respond.js",id ="respond", depends = "jquery",location = AssetLocation.SERVER)

        }
)

@Assets("*")
package org.exoplatform.intercom.respond;

import juzu.Application;
import juzu.asset.AssetLocation;
import juzu.plugin.asset.Assets;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Scripts;
import juzu.plugin.portlet.Portlet;