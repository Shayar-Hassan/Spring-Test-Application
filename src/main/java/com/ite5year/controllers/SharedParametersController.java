package com.ite5year.controllers;


import com.ite5year.models.SharedParam;
import com.ite5year.services.SharedParametersServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Map;

import static com.ite5year.utils.GlobalConstants.BASE_URL;

@Controller
@RequestMapping(BASE_URL + "/sharedMap")
public class SharedParametersController {
    private final SharedParametersServiceImpl sharedParametersService;

    public SharedParametersController(SharedParametersServiceImpl sharedParametersService) {
        this.sharedParametersService = sharedParametersService;
    }

    @PutMapping("/put-param")
    public @ResponseBody
    Map<String, SharedParam> putParam(String key, String value) {

        if (key != null && value != null) {
            SharedParam sharedParam = sharedParametersService.findByKey(key);
            if(sharedParam != null) {
                sharedParam.setFieldValue(value);
                sharedParametersService.update(sharedParam);
            }
            else sharedParametersService.save(new SharedParam(key, value));
        }
        return sharedParametersService.findAll();
    }


    @GetMapping("/get-value")
    public @ResponseBody
    SharedParam getValueByKey(String key) {
        return sharedParametersService.findByKey(key);
    }

    @GetMapping
    // @CachePut(value = "cachedSharedParamsMap", unless = "#result.length()<3")
    public @ResponseBody
    Map<String, SharedParam> getMap() {
        return this.sharedParametersService.findAll();
    }
}
