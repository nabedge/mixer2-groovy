package org.mixer2.sample.controller;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    private static Log log = LogFactory.getLog(IndexController.class);

    @Autowired
    protected ServletContext servletContext;

    /**
     * <pre>
     * using groovy: http://localhost:8080/mixer2-groogy/ 
     * without groovy:http://localhost:8080/mixer2-groogy/?useGroovy=false
     * </pre>
     * 
     * @param model
     * @param useGroovy
     * @return
     */
    @RequestMapping(value = "/")
    public String index(Model model,
            @RequestParam(defaultValue = "true") boolean useGroovy) {
        log.debug("going index()");
        String message = "Hello World !";
        model.addAttribute("helloMessage", message);
        model.addAttribute("useGroovy", useGroovy);
        return "index";
    }

}
