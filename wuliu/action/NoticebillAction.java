package cn.itheima.bos.web.action;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itheima.bos.domain.Noticebill;
import cn.itheima.bos.domain.PageBean;
import cn.itheima.bos.service.INoticebillService;
import cn.itheima.bos.web.base.BaseAction;
import cn.itheima.crm.service.Customer;
import cn.itheima.crm.service.CustomerServiceInterface;
@Controller
@Scope("prototype")
public class NoticebillAction extends BaseAction<Noticebill> {
	@Resource
	private INoticebillService noticebillService;
	@Resource
	private CustomerServiceInterface customerService;
	/**
	 * 根据输入的telephone 找到customer ajax异步加载
	 * @return
	 * @throws IOException
	 */
	public String findCustomerByTelephone() throws IOException{
		Customer customer = customerService.findCustomerByTelephone(model.getTelephone());
		writeObject2Json(customer);
		return NONE;
	}
	/**
	 * 将录入的通知单noticebill保存到数据库中  单中  
	 * 跳转到列表页面
	 * @return
	 */
	public String addNoticebill(){
		noticebillService.save(model);
		return "tolist";
	}
	/**
	 * 分页查询通知单列表noticebill
	 * @return
	 * @throws IOException 
	 */
	public String pagequery() throws IOException{
		noticebillService.pagequery(pageBean);
		writeObject2JsonLib(pageBean, new String[]{"noticebillUser","noticebillStaff","staffDecidedzones","staffWorkbills","staffNoticebills","noticeWorkbills"});
		return NONE;
	}
	
	
}
