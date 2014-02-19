package org.mixer2.sample.view;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.util.Map;
import java.util.regex.Pattern;

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

    private final String templateDirPrefix = "classpath:m2mockup/m2template/";
    private final String grvScriptFile = "index.groovy";

    @Override
    protected Html renderHtml(Html html, Map<String, Object> model,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        log.info("IndexView#renderHtml() processing...");

        boolean useGroovy = (boolean) model.get("useGroovy");
        if (useGroovy) {

            // prepare groovy environment
            CompilerConfiguration grvCfg = new CompilerConfiguration();
            grvCfg.setSourceEncoding("UTF-8");
            //grvCfg.getOptimizationOptions().put("indy", true);
            //grvCfg.getOptimizationOptions().put("int", false);

            // prepare binding data for groovy script
            Binding grvBinding = new Binding();
            grvBinding.setVariable("html", html);
            grvBinding.setVariable("model", model);

            // run groovy
            GroovyShell grvShell = new GroovyShell(
                    ClassUtils.getDefaultClassLoader(), grvBinding, grvCfg);
            Script grvScript = grvShell.parse(resourceLoader.getResource(
                    templateDirPrefix + grvScriptFile).getFile());
            grvScript.run();

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
