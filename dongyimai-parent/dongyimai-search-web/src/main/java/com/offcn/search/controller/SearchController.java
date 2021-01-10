package com.offcn.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.search.service.ItemSearchService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("itemSearch")
@ResponseBody
public class SearchController {

    @Reference
    private ItemSearchService searchService;

    @RequestMapping("search")
    public Map<String, Object> search(@RequestBody Map searchMap) {

        return searchService.search(searchMap);
    }

}
