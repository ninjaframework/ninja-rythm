package ninja.rythm;

import ninja.template.TemplateEngine;
import ninja.template.TemplateEngineHelper;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;

public class NinjaTemplateSelectorModule extends AbstractModule {
    
    private Provider<? extends TemplateEngine>[] providers;
    
    @SafeVarargs
    public NinjaTemplateSelectorModule(Provider<? extends TemplateEngine>... providers) {
        this.providers = providers;
    }
    
    @Override
    protected void configure() {
        Provider<TemplateEngineHelper> templateEngineHelperProvider = getProvider(TemplateEngineHelper.class);
        TemplateEngineSelector templateEngineSelector = new TemplateEngineSelector(templateEngineHelperProvider, providers);
        bind(TemplateEngine.class).toInstance(templateEngineSelector);
    }
}
