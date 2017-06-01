package cn.itheima.bos.web.action;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itheima.bos.domain.Function;
import cn.itheima.bos.service.IFunctionService;
import cn.itheima.bos.web.base.BaseAction;

@Controller
@Scope("prototype")
public class FunctionAction extends BaseAction<Function> {
	
	@Resource
	private IFunctionService functionService;
	/**
	 * ajax异步加载功能权限管理添加权限中的父类节点
	 * @return
	 * @throws IOException
	 */
	public String listAjax() throws IOException{
		List<Function> list=functionService.findAll();
		writeList2JsonLib(list, new String[]{"functionRoles","functions"});
		return NONE;
	}
	/**
	 * 保存新添加的功能权限 返回到list页面 
	 * @return
	 */
	public String add(){
		functionService.save(model);
		return "tolist";
	}
	/**
	 * 功能权限列表分页查询
	 * 当model中含有页面分页的page属性时，page会被封装到model里 导致pagebean里的page为空 
	 * @throws IOException 
	 */
	public String pageQuery() throws IOException{
		String page1 = model.getPage();
		pageBean.setCurrentPage(Integer.parseInt(page1));
		functionService.pageQuery(pageBean);
		writeObject2JsonLib(pageBean, new String[]{"function","functions","functionRoles"});
		return NONE;
	}
}
