package org.mixer2.sample.view;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.mixer2.jaxb.xhtml.Div;
import org.mixer2.jaxb.xhtml.Html;
import org.mixer2.spring.webmvc.AbstractMixer2XhtmlView;
import org.mixer2.xhtml.PathAdjuster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;

public class IndexView extends AbstractMixer2XhtmlView {

    private static Log log = LogFactory.getLog(IndexView.class);

    @Autowired
    protected ResourceLoader resourceLoader;

    private final String groovyScriptFilePath = "classpath:m2mockup/m2template/index.groovy";
    
    private groovy.lang.Script groovyScript;
    
    @PostConstruct
    private void init() throws IOException {
        CompilerConfiguration grvCfg = new CompilerConfiguration();
        grvCfg.setSourceEncoding("UTF-8");
        GroovyShell groovyShell = new GroovyShell(ClassUtils.getDefaultClassLoader());
        File file = resourceLoader.getResource(groovyScriptFilePath).getFile();
        this.groovyScript = groovyShell.parse(file);
    }
    
    private synchronized void runScript(Binding binding) {
        this.groovyScript.setBinding(binding);
        this.groovyScript.run();
    }

    @Override
    protected Html renderHtml(Html html, Map<String, Object> model,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        log.info("IndexView#renderHtml() processing...");

        boolean useGroovy = (boolean) model.get("useGroovy");
        if (useGroovy) {
            // prepare binding data for groovy script
            Binding binding = new Binding();
            binding.setVariable("html", html);
            binding.setVariable("model", model);
            runScript(binding);
        } else {
            // normal java & mixer2 usage.
            String helloMessage = (String) model.get("helloMessage");
            Div div = html.getById("message", Div.class);
            div.unsetContent();
            div.getContent().add(helloMessage + " without groovy");
        }

        // replace static file path
        Pattern pattern = Pattern.compile("^\\.+/.*m2static/(.*)$");
        String ctx = request.getContextPath();
        PathAdjuster.replacePath(html, pattern, ctx + "/m2static/$1");

        return html;
    }
}
