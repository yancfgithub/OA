package com.offcn.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.offcn.group.Specification;
import com.offcn.mapper.TbSpecificationOptionMapper;
import com.offcn.pojo.TbSpecificationOption;
import com.offcn.pojo.TbSpecificationOptionExample;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.mapper.TbSpecificationMapper;
import com.offcn.pojo.TbSpecification;
import com.offcn.pojo.TbSpecificationExample;
import com.offcn.pojo.TbSpecificationExample.Criteria;
import com.offcn.sellergoods.service.SpecificationService;

import com.offcn.entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;

	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
		specificationMapper.insert(specification.getSpecification());
		for(TbSpecificationOption specificationOption:specification.getSpecificationOptionList()) {
			specificationOption.setSpecId(specification.getSpecification().getId());
			specificationOptionMapper.insert(specificationOption);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){

		//根据主键修改规格
		specificationMapper.updateByPrimaryKey(specification.getSpecification());

		//修改规格项目采用先将所有的项目都删掉
		TbSpecificationOptionExample exp = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = exp.createCriteria();
		criteria.andSpecIdEqualTo(specification.getSpecification().getId());
		specificationOptionMapper.deleteByExample(exp);

		//然后再添加新的项目
		for(TbSpecificationOption specificationOption:specification.getSpecificationOptionList()) {
			specificationOption.setSpecId(specification.getSpecification().getId());
			specificationOptionMapper.insert(specificationOption);
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){

		//根据主键id查询规范
		TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);

		Specification specification = new Specification();

		//将查询到的规范放入组合实体类中
		specification.setSpecification(tbSpecification);

		//根据外键查询规范项目
		TbSpecificationOptionExample exp = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = exp.createCriteria();
		criteria.andSpecIdEqualTo(id);
		List<TbSpecificationOption> tbSpecificationOptionList = specificationOptionMapper.selectByExample(exp);

		//将查询到的规范项目集合放入组合实体类中
		specification.setSpecificationOptionList(tbSpecificationOptionList);

		return  specification;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//根据外键键删除规格选项
			TbSpecificationOptionExample exp = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = exp.createCriteria();
			criteria.andSpecIdEqualTo(id);
			specificationOptionMapper.deleteByExample(exp);
			//根据主键删除规格
			specificationMapper.deleteByPrimaryKey(id);

		}
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample exp1=new TbSpecificationExample();
		Criteria criteria = exp1.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(exp1);


			return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> selectOptionList(){
		return specificationMapper.selectOptionList();
	}

}
