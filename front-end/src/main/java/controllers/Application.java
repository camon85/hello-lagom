package controllers;

import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

public class Application extends Controller {

  private WebJarAssets webJarAssets;

  @Inject
  public Application(WebJarAssets webJarAssets) {
    this.webJarAssets = webJarAssets;
  }

  public Result index() {
    return ok(views.html.index.render(this.webJarAssets));
  }

  public Result userStream(String userId) {
    return ok(views.html.index.render(this.webJarAssets));
  }

}
