package cn.itheima.bos.web.action;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.struts2.ServletActionContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;


import cn.itheima.bos.domain.User;
import cn.itheima.bos.service.IUserService;
import cn.itheima.bos.utils.BosUtils;
import cn.itheima.bos.utils.MD5Utils;
import cn.itheima.bos.web.base.BaseAction;
@Controller
@Scope("prototype")
public class UserAction extends BaseAction<User> {
	@Resource
	private IUserService userService;
	
	private String checkcode;
	public void setCheckcode(String checkcode) {
		this.checkcode = checkcode;
	}
	
	private List<String> roleIds;
	public void setRoleIds(List<String> roleIds) {
		this.roleIds = roleIds;
	}
	private String userIds;
	public void setUserIds(String userIds) {
		this.userIds = userIds;
	}
	/**
	 * 删除用户列表中删除指定的用户
	 * @throws IOException 
	 */
	public String delete() throws IOException{
		String flag="1";
		try {
			userService.delete(userIds);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			flag="0";
			e.printStackTrace();
		}
		BosUtils.getResponse().getWriter().print(flag);
		return NONE ;
	}
	/**
	 * 分页查询用户列表
	 * @throws IOException 
	 */
	public String pageQuery() throws IOException{
		userService.pageQuery(pageBean);
		writeObject2JsonLib(pageBean, new String[]{"noticebills","userRoles"});
		return NONE;
	}
	/**
	 * 添加用户
	 */
	public String add(){
		userService.save(model,roleIds);
		return "tolist";
	}
	/**
	 * 修改密码时校验密码
	 * @return
	 */
	public String checkPassword(){
		String password1 = model.getPassword();
		model.setUsername(BosUtils.getUser().getUsername());
		User user = userService.checkUsernameAndPassword(model);
			try {
				if(user!=null){
				BosUtils.getResponse().getWriter().write("yes");
				}else {
				BosUtils.getResponse().getWriter().write("no");	
						}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return NONE;
	}
	/**
	 * 修改密码
	 * 
	 * @return
	 * @throws IOException 
	 */
	public String editPassword() throws IOException{
		//修改成功与否
		String flag="1";
		User user = BosUtils.getUser();
		user.setPassword(MD5Utils.md5(model.getPassword()));
		try {
			userService.editPassword(user);
		} catch (Exception e) {
			flag="0";
		}
		BosUtils.getResponse().getWriter().write(flag);
		return NONE;
	}
	/**
	 * 用户登出
	 * @return
	 */
	public String logout(){
		BosUtils.getSession().setAttribute("loginuser", null);
		return "loginout";
	}
	/**
	 * 用户登录  修改login 进行shiro认证 认证成功即可登录（也就是交由框架完成用户名和密码的比对）
	 * 使用shiro框架进行登录
	 * 首先将登录的用户名和密码交给AuthenticationToken管理 进行认证
	 * @return
	 */
	public  String login(){
		//获得验证码
		String checkcodes = (String) BosUtils.getSession().getAttribute("key");
		//校验验证码 如果验证码正确
		if(!StringUtils.isBlank(checkcode)&&checkcodes.equals(checkcode)){
			//校验用户
			//使用shiro进行认证
			//1.获得subject类
			Subject subject=SecurityUtils.getSubject();//未认证状态
			AuthenticationToken authenticationToken=new UsernamePasswordToken(model.getUsername(),MD5Utils.md5(model.getPassword()) );
			//2.通过subject调用login进行认证
				try {
					//不报异常登录成功，将user放入session中
					subject.login(authenticationToken);//调用login方法进行认证
					User user = (User) subject.getPrincipal();
					BosUtils.getSession().setAttribute("loginuser", user);
					return "home";
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				
				}
				//报异常 登录失败
				this.addActionError("用户名或者密码错误");
				return "login";
				
				/*User user=userService.checkUsernameAndPassword(model);
				if(user!=null){
					//登录成功
					BosUtils.getSession().setAttribute("loginuser", user);
					return "home";
				}else {
					//登录失败
					this.addActionError("用户名或者密码错误");
					return "login";
				}*/
		}else {
			//校验验证码 如果验证码不正确	
			this.addActionError("验证码错误");
			return "login";
			
		}
		
	}
	
}
