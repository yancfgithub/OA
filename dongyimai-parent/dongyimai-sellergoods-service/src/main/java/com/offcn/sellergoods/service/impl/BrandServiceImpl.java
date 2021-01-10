package com.offcn.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.mapper.TbBrandMapper;
import com.offcn.pojo.TbBrand;
import com.offcn.pojo.TbBrandExample;
import com.offcn.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {
    public PageResult findPage(int pageNum, int pageSize, TbBrand brand) {

        PageHelper.startPage(pageNum,pageSize);
        TbBrandExample exp =new TbBrandExample();
        TbBrandExample.Criteria criteria = exp.createCriteria();

        if(brand != null) {
            if(brand.getName()!=null && brand.getName().length()>0 ) {
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            if(brand.getFirstChar()!=null && brand.getFirstChar().length()>0) {
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
            /*if(brand != null){
                if(brand.getName() != null && brand.getName().length() > 0){
                    criteria.andNameLike( "%"+brand.getName()+"%" );//name like '%三%'
                }
                if(brand.getFirstChar() != null && brand.getFirstChar().length()>0){
                    criteria.andFirstCharEqualTo( brand.getFirstChar() );//and first_char = 'D'
                }
            }*/
        }
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(exp);

        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public List<Map> selectOptionList(){
        return brandMapper.selectOptionList();
    }

    @Autowired
    TbBrandMapper brandMapper;


    public void del(List<Long> ids) {
        for(Long id : ids) {
            brandMapper.deleteByPrimaryKey(id);
        }
    }

    public void update(TbBrand brand) {
        brandMapper.updateByPrimaryKey(brand);
    }

    public TbBrand findOne(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);

        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);

        return new PageResult(page.getTotal(),page.getResult());
    }

    public void add(TbBrand brand) {
        brandMapper.insert(brand);
    }


}
