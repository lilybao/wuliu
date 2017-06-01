package cn.itheima.bos.web.action;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import cn.itheima.bos.domain.Decidedzone;
import cn.itheima.bos.domain.Staff;
import cn.itheima.bos.domain.Subarea;
import cn.itheima.bos.service.IDecidedzoneService;
import cn.itheima.bos.service.ISubareaService;
import cn.itheima.bos.utils.BosUtils;
import cn.itheima.bos.utils.CommonUtils;
import cn.itheima.bos.web.base.BaseAction;
import cn.itheima.crm.service.Customer;
import cn.itheima.crm.service.CustomerServiceInterface;

@Controller
@Scope("prototype")
public class DecidedzoneAction extends BaseAction<Decidedzone> {
	private String [] subareaid;
	public void setSubareaid(String[] subareaid) {
		this.subareaid = subareaid;
	}
	private List<Integer> customerIds;
	public void setCustomerIds(List<Integer> customerIds) {
		this.customerIds = customerIds;
	}
	@Resource
	private ISubareaService subareaService;
	@Resource
	private IDecidedzoneService decidedzoneService;
	
//	public String findNoAssociationCustomers(){
//		List<Customer> list = customerService.findNoAssociationCustomers();
//		return NONE;
//	}
	public String decidedZoneAssociationCustomer() throws IOException{
		List<Customer> list = customerService.findAssociationCustomers(model.getId());
		writeObject2Json(list);
		return NONE;
	}
	/**
	 * 查询和定区没有关联的客户
	 * @return
	 * @throws IOException 
	 */
	public String findNoAssociationSelect() throws IOException{
		List<Customer> list = customerService.findNoAssociationCustomers();
		writeList2JsonLib(list, new String[]{});
		return NONE;
	}
	/**
	 * 删除定区
	 * 删除定区 定区和staff  staff放弃维护 直接删除
	 * 定区和subarea 定区放弃维护  删除定区之前要把subarea的外键置为null
	 * @return
	 */
	public String deleteDecidedZone(){
		decidedzoneService.deleteById(model.getId());
		return "tolist";
	}
	/**
	 * 查询和定区有关联的客户
	 * @return
	 * @throws IOException 
	 */
	public String findAssociationSelect() throws IOException{
		List<Customer> list = customerService.findAssociationCustomers(model.getId());
		writeList2JsonLib(list, new String[]{});
		return NONE;
	}
	/**
	 * 保存和定区新建关联的客户
	 * @return
	 * @throws IOException 
	 */
	public String assigncustomerstodecidedzone() throws IOException{
		customerService.assignCustomerToDecidedZone(customerIds, model.getId());
		return "tolist";
	}
	/**
	 * 添加定区decidedzone
	 * 1将decidedzone的外键绑定为staff的主键
	 * 2 将subarea的外键设置为decidedzone的主键  关联两者
	 * @return
	 */
	public String add(){
		
		//decidedzone 与 staff 关联
		decidedzoneService.add(model);
		//关联 decidedzone与subarea 关联
		subareaService.addDecidedzoneId(model,subareaid);
		return "tolist";
	}
	/**
	 * 分页显示定区列表
	 * @return
	 * @throws IOException 
	 */
	public String list() throws IOException{
		List<Decidedzone> list=decidedzoneService.findAll();
		decidedzoneService.pageQuery(pageBean);
		/*
		 * 使用fastjson方式  
		 * 需要注意自动检测循环引用  fastjson会将重复的设置为ref[] 这时fastjson转化时需要设置  取消该机制
		 */
		//writeObject2Json(pageBean);
		/*
		 * 使用json-lib进行转换  
		 * 需要注意 排除的对象或者属性  必须是 数据库 查询到的实际值  如果查询到的有代理对象  ，需要设置在查询数据库时立即加载对象（lazy=false）
		 */
		String [] arr={"subareas","staffDecidedzones","detachedCriteria","staffWorkbills","staffNoticebills"};
		this.writeList2JsonLib(list, arr);
		return NONE;
	}
}
