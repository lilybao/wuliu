package cn.itheima.bos.web.action;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itheima.bos.domain.Role;
import cn.itheima.bos.service.IRoleService;
import cn.itheima.bos.web.base.BaseAction;

@Controller
@Scope("prototype")
public class RoleAction extends BaseAction<Role> {
	
	@Resource
	private IRoleService roleService;
	private String pids;
	public void setPids(String pids) {
		this.pids = pids;
	}
	/**
	 * 
	* @Description: TODO(角色删除)
	* @author 李保健
	* @date 2017年4月8日 下午4:12:55
	* @return String
	 */
	public String deleteRole(){
		roleService.delete(pids);
		return NONE;
	}
	/**
	 * 添加角色 并关联权限
	 * @return
	 */
	public String add(){
		roleService.add(model,pids);
		return "tolist";
	}
	/**
	 * 角色列表分页查询
	 * @throws IOException 
	 */
	public String pageQuery() throws IOException{
		roleService.pageQuery(pageBean);
		writeObject2JsonLib(pageBean, new String[]{"functions","roleUsers"});
		return NONE;
	}
	/**
	 * 用户管理页面  添加用户时  需要异步加载所有的角色信息 
	 * @throws IOException 
	 */
	public String findAll() throws IOException{
		List<Role> list=roleService.findAll();
		writeList2JsonLib(list, new String[]{"functions","roleUsers"});
		return NONE;
	}
}
