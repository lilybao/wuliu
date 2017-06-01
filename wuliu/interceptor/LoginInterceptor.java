package cn.itheima.bos.web.interceptor;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;

import cn.itheima.bos.domain.User;
import cn.itheima.bos.utils.BosUtils;

public class LoginInterceptor extends MethodFilterInterceptor {

	@Override
	protected String doIntercept(ActionInvocation invocation) throws Exception {
		//拦截未登录的请求
		//2.如果没有就拦截  跳到登录界面
		User user =BosUtils.getUser();
		if(user==null){
			ServletActionContext.getRequest().setAttribute("message", "请登录后再访问");
			return "login";
		}
		//1 查看session中是否有用户 如果有 就放过
		return invocation.invoke();
	}

}
