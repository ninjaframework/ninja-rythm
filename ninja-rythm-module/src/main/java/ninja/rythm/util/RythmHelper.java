package ninja.rythm.util;

import ninja.Result;
import ninja.Route;

/**
 * Helper methods for Rythm engines
 * 
 * @author Sojin
 */
public class RythmHelper {

    public String getRythmTemplateForResult(Route route,
                                            Result result,
                                            String suffix) {
        if (result.getTemplate() == null) {
            Class<?> controller = route.getControllerClass();

            // and the final path of the controller will be something like:
            // /some/package/submoduleName/ControllerName/templateName.ftl.html
            return String.format("/%s/%s%s", controller.getSimpleName(), route
                    .getControllerMethod().getName(), suffix);
        } else {
            return result.getTemplate();
        }
    }

}