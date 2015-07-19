package conf;

import ninja.rythm.RythmRenderUtility;

/**
 * A render injected singleton, available in all templates if defined in application.conf
 * 
 * Must have no constructor, but can access the context using the protected member.
 * 
 * @author Christian Bourgeois
 */
public class RythmUtils extends RythmRenderUtility {

    public String assetAt(String relPath) {
        return new StringBuilder()
            .append(context.getContextPath())
            .append("/assets/")
            .append(relPath)
            .toString();
    }
    
    public String path() {
        return context.getRequestPath();
    }
    
}
