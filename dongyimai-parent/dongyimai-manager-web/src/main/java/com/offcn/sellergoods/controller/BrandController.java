package com.offcn.sellergoods.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.pojo.TbBrand;
import com.offcn.sellergoods.service.BrandService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("brand")
@ResponseBody
public class BrandController {

    @Reference
    BrandService brandService;

    @RequestMapping("findAll")
    public List<TbBrand> findAll() {
        return brandService.findAll();
    }

    @RequestMapping("findPage")
    public PageResult findPage(int page, int rows) {
        return brandService.findPage(page,rows);
    }

    @RequestMapping("add")
    public Result add(@RequestBody TbBrand brand) {

        try {
            brandService.add(brand);
            return new Result(true,"添加成功");
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }

    }

    @RequestMapping("findOne")
    public TbBrand findOne(Long id) {
        return brandService.findOne(id);
    }

    @RequestMapping("update")
    public Result update(@RequestBody TbBrand brand) {
        try {
            brandService.update(brand);
            return new Result(true,"修改成功");
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    @RequestMapping("del")
    public Result del(@RequestParam("ids") List<Long> ids) {

        try {
            brandService.del(ids);
            return new Result(true,"删除成功");
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    @RequestMapping("search")
    public PageResult findPage( @RequestBody TbBrand brand,int page, int rows) {
        return brandService.findPage(page,rows,brand);
    }

    @RequestMapping("selectOptionList")
    public List<Map> selectOptionList() {
        return brandService.selectOptionList();
    }
}
