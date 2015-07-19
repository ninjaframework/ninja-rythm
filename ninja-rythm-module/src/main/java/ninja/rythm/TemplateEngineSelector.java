package ninja.rythm;

import java.net.URL;

import ninja.Context;
import ninja.Result;
import ninja.template.TemplateEngine;
import ninja.template.TemplateEngineHelper;

import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.FileSystem;

import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class TemplateEngineSelector implements TemplateEngine {
    
    private Provider<TemplateEngineHelper> helperProvider;
    private Provider<? extends TemplateEngine>[] engineProviders;

    public TemplateEngineSelector(Provider<TemplateEngineHelper> helperProvider, Provider<? extends TemplateEngine>[] engineProviders) {
        this.helperProvider = helperProvider;
        this.engineProviders = engineProviders;
    }
   

    @Override
    public void invoke(Context context, Result result) {
        String templateName = result.getTemplate();
        if (templateName == null) {
            for (int i=0; i<engineProviders.length; i++) {
                Provider<? extends TemplateEngine> engineProvider = engineProviders[i];
                // get template from provider
                templateName = helperProvider.get().getTemplateForResult(context.getRoute(), result, engineProvider.get().getSuffixOfTemplatingEngine());
                // if exists (or is the last one), call template engine
                if (i == engineProviders.length-1 || getTemplateExists(templateName)) {
                    engineProvider.get().invoke(context, result);
                    break;
                }
            }
        } else {
            for (int i=0; i<engineProviders.length; i++) {
                Provider<? extends TemplateEngine> engineProvider = engineProviders[i];
                // check template suffix
                if (i == engineProviders.length-1 || templateName.endsWith(engineProvider.get().getSuffixOfTemplatingEngine())) {
                    engineProvider.get().invoke(context, result);
                    break;
                }
            }
        }
    }

    @Override
    public String getSuffixOfTemplatingEngine() {
        return null;
    }

    @Override
    public String getContentType() {
        return "text/html";
    }
    
    protected boolean getTemplateExists(String pResourceName) {
        URL resource = ConfigurationUtils.locate(FileSystem.getDefaultFileSystem(), null, pResourceName.substring(1));
        return resource != null;
    }

}
