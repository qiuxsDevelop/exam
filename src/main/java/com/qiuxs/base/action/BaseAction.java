package com.qiuxs.base.action;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.qiuxs.base.entity.Entity;
import com.qiuxs.base.page.PageLimit;
import com.qiuxs.base.service.BaseService;
import com.qiuxs.base.util.Strings;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Map;

import static java.lang.System.out;
import static org.apache.struts2.ServletActionContext.getRequest;
import static org.apache.struts2.ServletActionContext.getResponse;

@Controller
@Scope("prototype")
public class BaseAction extends ActionSupport{

	@Resource(name = "baseServiceImpl")
	protected BaseService baseService;

	private PageLimit pageLimit;

	private static final long serialVersionUID = -7771051448180391606L;

	protected void put(String key,Object value){
		ActionContext.getContext().getContextMap().put(key, value);
	}

	protected String getString(String attr){

		Object value = ActionContext.getContext().getParameters().get(attr);
		if (null == value) { return null; }
		if (!value.getClass().isArray()) { return value.toString(); }
		String[] values = (String[]) value;
		if (values.length == 1) {
			return values[0];
		} else {
			return Strings.join(values, ",");
		}
	}

	protected Integer getInt(String attr){
		Object value = getObject(attr);
		try {
			return Integer.parseInt(value.toString());
		} catch (Exception e) {
			return null;
		}
	}

	protected Double getDouble(String attr){
		Object value = getObject(attr);
		try {
			return Double.parseDouble(value.toString());
		} catch (Exception e) {
			return null;
		}
	}

	protected <T extends Entity<ID>,ID extends Serializable> T getEntity(Class<T> clazz,ID id){
		if (id == null) {
			try {
				return (T) Class.forName(clazz.getName()).newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return baseService.get(clazz,id);
	}

	protected Boolean getBoolean(String attr){
		Object value = getObject(attr);
		if (value==null) return null;
		return "1".equals(value.toString())||"true".equalsIgnoreCase(value.toString());
	}

	protected Float getFloat(String attr){
		Object value = getObject(attr);
		try {
			return Float.parseFloat(value.toString());
		} catch (Exception e) {
			return null;
		}
	}

	private Object getObject(String attr) {
		Object value = ActionContext.getContext().getParameters().get(attr);
		if (null == value) {
			return null;
		} else if (value instanceof String && Strings.isEmpty((String)value)) {
			return null;
		} else {
			if (value.getClass().isArray()) {
				Object[] values = (Object[])((Object[])value);
				if (values.length >= 1) {
					value = values[0];
				}
			}
			return value;
		}
	}

	protected void write(String msg){
		getResponse().setContentType("text/html;charset=utf-8");
		//获取输出流，然后使用
		PrintWriter out = null;
		try {
			out = getResponse().getWriter();
			getResponse().getWriter().print(msg);
			getResponse().getWriter().flush();
		} catch (Exception e) {
		}finally{
			if (out!=null){
				out.close();
			}
		}

	}
	protected void write(Object o){
		getResponse().setContentType("text/html;charset=utf-8");
		//获取输出流，然后使用
		PrintWriter out = null;
		try {
			out = getResponse().getWriter();
			out.print(getJson(o));
			out.flush();
		} catch (Exception e) {
		}finally{
			if (out!=null){
				out.close();
			}
		}
	}
	public void writeSuccese(String msg){
		Map<String, String> map = new HashedMap<String, String>();
		map.put("status", "success");
		map.put("msg", msg);
		write(map);

	}
	public void writeFail(String msg){
		Map<String, String> map = new HashedMap<String, String>();
		map.put("status", "error");
		map.put("msg", msg);
		write(map);

	}

	protected String getJson(Object object){
		Gson gson = new GsonBuilder().serializeNulls() .create();
		return gson.toJson(object);

		//		Gson gson = new GsonBuilder()
		//	    //序列化null
		//	    .serializeNulls()
		//	    // 设置日期时间格式，另有2个重载方法
		//	    // 在序列化和反序化时均生效
		//	    .setDateFormat("yyyy-MM-dd")
		//	    // 禁此序列化内部类
		//	     .disableInnerClassSerialization()
		//	    //生成不可执行的Json（多了 )]}' 这4个字符）
		//	    .generateNonExecutableJson()
		//	     //禁止转义html标签
		//	    .disableHtmlEscaping()
		//	    //格式化输出
		//	    .setPrettyPrinting()
		//	    .create();
		//	//：内部类(Inner Class)和嵌套类(Nested Class)的区别
	}

	public PageLimit getPageLimit() {
		if (pageLimit == null) {
			pageLimit = new PageLimit();
			pageLimit.init(getRequest());
		}
		return pageLimit;
	}

	public void setPageLimit(PageLimit pageLimit) {
		this.pageLimit = pageLimit;
	}
}
