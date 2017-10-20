package org.exoplatform.intercom.respond;

import juzu.Path;
import juzu.Response;
import juzu.View;
import juzu.template.Template;

import javax.inject.Inject;

public class RespondController {
    @Inject
    @Path("index.gtmpl")
    Template index;


    @View
    public Response.Content index() throws Exception {
        return index.ok();
    }
}
