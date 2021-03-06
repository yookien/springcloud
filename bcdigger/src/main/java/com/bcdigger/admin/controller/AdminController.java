package com.bcdigger.admin.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bcdigger.admin.entity.Admin;
import com.bcdigger.admin.entity.AdminRole;
import com.bcdigger.admin.entity.AdminRoleRef;
import com.bcdigger.admin.entity.RoleMenuRef;
import com.bcdigger.admin.entity.SysMenu;
import com.bcdigger.admin.service.AdminRoleRefService;
import com.bcdigger.admin.service.AdminRoleService;
import com.bcdigger.admin.service.AdminService;
import com.bcdigger.admin.service.RoleMenuRefService;
import com.bcdigger.admin.service.SysMenuService;
import com.bcdigger.common.constant.CacheConstant;
import com.bcdigger.common.page.PageInfo;
import com.bcdigger.common.utils.rsa.MD5;
import com.bcdigger.core.annotation.AdminAuth;

@Controller	
//@RestController  //只返回值，不放回页面渲染
@EnableAutoConfiguration
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private AdminService adminService;
	@Autowired
	private AdminRoleService adminRoleService;
	@Autowired
	private SysMenuService sysMenuService;
	@Autowired
	private RoleMenuRefService roleMenuRefService;
	@Autowired
	private AdminRoleRefService adminRoleRefService;
	
	
	private List<SysMenu> getUserMenu(Integer adminId){
		try{
			List<SysMenu> sysMenuList=new ArrayList<SysMenu>();
			// 查询用户角色
			AdminRoleRef adminRoleRef = new AdminRoleRef();
			adminRoleRef.setAdminId(adminId);
			adminRoleRef.setState(1);
			adminRoleRef = this.adminRoleRefService.getAdminRoleRef(adminRoleRef);
			if(adminRoleRef != null && adminRoleRef.getRoleIds() != null && !"".equals(adminRoleRef.getRoleIds())){
				String roleIds=adminRoleRef.getRoleIds();
				RoleMenuRef roleMenuRef=new RoleMenuRef();
				roleMenuRef.setRoleIds(roleIds);
				roleMenuRef.setState(1);
				// 查询角色对应的菜单id
				List<RoleMenuRef> list = roleMenuRefService.getRoleMenuRefs(roleMenuRef);
				if(list!=null && list.size()>0){
					StringBuffer sb = new StringBuffer();
					RoleMenuRef roleMenuRefTemp=null;
					for(int i=0;i<list.size();i++){
						roleMenuRefTemp = list.get(i);
						if(roleMenuRefTemp==null){
							continue;
						}
						if(i==list.size()-1){
							sb.append(roleMenuRefTemp.getMenuIds());
						}else{
							sb.append(roleMenuRefTemp.getMenuIds()).append(",");
						}
					}
					SysMenu sysMenu=new SysMenu();
					sysMenu.setMenuIds(sb.toString());
					sysMenu.setState(1);
					sysMenuList = this.sysMenuService.getSysMenuList(sysMenu);
				}
			}
			return sysMenuList;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	
	@RequestMapping(value ="/login",method=RequestMethod.GET)
	public String adminLogin(HttpServletRequest request, HttpServletResponse response) {
		try{
			Integer sessionId=(Integer)request.getSession().getAttribute(CacheConstant.ADMIN_SESSION_ID);
			if(sessionId!=null && sessionId>0){
				// 用户已经登录，自动跳转到操作页
				response.sendRedirect("/admin/index");
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "login";
	}
	
	@RequestMapping(value ="/userLogin",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> login(@RequestParam String name, String password, String vrifycode, HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> map = new HashMap<>();  
		try{
			Integer sessionId=(Integer)request.getSession().getAttribute(CacheConstant.ADMIN_SESSION_ID);
			if(sessionId!=null && sessionId>0){
				// 用户已经登录，自动跳转到操作页
				response.sendRedirect("/admin/index");
				return null;
			}
			if(name==null || name.trim().equals("")){
				map.put("result", -2);// 用户名不能为空
				return map;
			}
			
			if(password==null || password.trim().equals("")){
				map.put("result", -3);// 密码不能为空
				return map;
			}
			
			
			String sessionVrifyCode=(String)request.getSession().getAttribute("vrifyCode");
			if(vrifycode==null || vrifycode.trim().equals("") 
					|| sessionVrifyCode==null || !sessionVrifyCode.equals(vrifycode)){
				map.put("result", -4);// 验证码错误
				return map;
			}
			
			Admin admin=new Admin();
			admin.setName(name);
			Admin adminTemp=adminService.getAdmin(admin);
			if(adminTemp==null){
				map.put("result", -5);// 用户名不存在
				return map;
			}else if(adminTemp.getPassword()==null || !adminTemp.getPassword().equals(MD5.getMD5Str(password))){
				map.put("result", -6);// 账户密码不匹配
				return map;
			}
			request.getSession().setAttribute(CacheConstant.ADMIN_SESSION_ID,adminTemp.getId());
			map.put("result", 1);//登录成功
		}catch(Exception e){
			map.put("result", 0);//系统异常
			e.printStackTrace();
		}
		return map;
	}
	
	@RequestMapping(value ="/index")
	@AdminAuth
	public String index(HttpServletRequest request,ModelMap map) {
		try{
			Integer adminId=(Integer)request.getSession().getAttribute(CacheConstant.ADMIN_SESSION_ID);
			adminId=1;
			List<SysMenu> menuList=getUserMenu(adminId);
			List<SysMenu> level1List= new ArrayList<SysMenu>();
			List<SysMenu> level2List= new ArrayList<SysMenu>();
			List<SysMenu> level3List= new ArrayList<SysMenu>();
			if(menuList!=null && menuList.size()>0){
				SysMenu sysMenu=null;
				for(int i=0,j=menuList.size();i<j;i++){
					sysMenu=menuList.get(i);
					if(sysMenu==null){
						continue;
					}
					if(sysMenu.getLevel()==1){
						level1List.add(sysMenu);
					}else if(sysMenu.getLevel()==2){
						level2List.add(sysMenu);
					}else if(sysMenu.getLevel()==3){
						level3List.add(sysMenu);
					}
				}
			}
			map.addAttribute("level1List", level1List);
			map.addAttribute("level2List", level2List);
			map.addAttribute("level3List", level3List);
		}catch(Exception e){
			e.printStackTrace();
		}
		return "index";
	}
	
	/**
	 * 
	 * @Description: 退出登录
	 * @param request
	 * @return String  
	 * @throws
	 * @author ipui
	 * @date 2018年3月25日 上午1:44:45
	 */
	@RequestMapping(value ="/logout")
	@AdminAuth
	public String logout(HttpServletRequest request) {
		request.getSession().setAttribute(CacheConstant.ADMIN_SESSION_ID,null);
		request.getSession().setAttribute(CacheConstant.ADMIN_ID_SESSION_KEY,null);
		return "login";
	}
	
	@RequestMapping(value ="/getAdmin",method=RequestMethod.POST)
	@ResponseBody
	@AdminAuth
	public Map getAdmin(Admin admin) {
		
		Map<String, Object> map = new HashMap<>();
		try{
			if( admin==null || admin.getId()<=0){
				return null;
			}
			admin = adminService.getAdmin(admin.getId());
			map.put("result", 1);//登录成功
			map.put("admin", admin);
		}catch(Exception e){
			map.put("result", 0);//系统异常
			e.printStackTrace();
		}
		return map;
		
		//RedisUtils.save("admin", admin);
		//Admin admin01 = (Admin)RedisUtils.get("admin");
		
		
	}
	
	@RequestMapping(value ="/adminIndex")
	public String adminIndex(ModelMap map) {
		try{
			// 查询所有用户角色信息
			AdminRole role=new AdminRole();
			role.setState(1);// 只查询有效的角色
			List<AdminRole> roleList = adminRoleService.getAdminRoleList(role);
			map.addAttribute("roleList", roleList);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return "/admin/admin_index";
	}
	
	@RequestMapping(value ="/getAdmins",method= {RequestMethod.POST,RequestMethod.GET})
	public String getAdmins(PageInfo pageInfo,Admin admin) {
		
		if(pageInfo == null)
			pageInfo =new PageInfo<Admin>();
		
		pageInfo = adminService.getAdmins(admin, pageInfo);
		
		
	
		/*File file = new File("D:\\git_repository\\bcdigger\\src\\main\\resources\\static\\images\\20140104_210153.jpg");
		String filePath = FastDFSClient.uploadFile(file);
		String path = FastDFSClient.uploadImageAndCrtThumbImage("D:\\git_repository\\bcdigger\\src\\main\\resources\\static\\images\\20140104_210153.jpg");
		System.out.println("filePath:"+filePath);
		System.out.println("uploadImageAndCrtThumbImage:"+path);*/
		
		//RedisUtils.save("admin", adminService.getAdmin(1));
		//Admin temp = (Admin)RedisUtils.get("admin");
		//System.out.println("admin:"+ temp.getName());
		
		return "/admin/admin_list";
	}
	
	@RequestMapping(value ="/addAdmin",method= {RequestMethod.POST,RequestMethod.GET},produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public Map addAdmin(Admin admin) {
		Map<String, Object> map = new HashMap<>();  

		if(admin==null){
			map.put("result", -1);// 参数为空
			return map;
		}
		if(StringUtils.isBlank(admin.getName())||StringUtils.isBlank(admin.getPassword()) ){
			map.put("result", -2);// 名称和密码不能为空
			return map;
		}
		admin.setPassword(MD5.getMD5Str(admin.getPassword()));
		Date now=new Date();
		admin.setAddTime(now);
		admin.setUpdateTime(now);
		adminService.addAdmin(admin);
		map.put("result", 1);//登录成功
		
		return map;
		
	}
	
	@RequestMapping(value ="/editAdmin",method= {RequestMethod.POST,RequestMethod.GET},produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public Map editAdmin(Admin admin) {
		
		Map<String, Object> map = new HashMap<>();  
		
		
		if(admin==null){
			map.put("result", -1);// 参数为空
			return map;
		}
		if(admin.getId()==0||StringUtils.isBlank(admin.getName())) {
			map.put("result", -2);// 名称不能为空
			return map;
		}
		
		adminService.updateAdmin(admin);
		map.put("result", 1);//更新成功
		
		return map;
		
	}
	
	@RequestMapping(value ="/updateAdminPassword",method= {RequestMethod.POST,RequestMethod.GET},produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public Map updateAdminPassword(Admin admin) {
		
		Map<String, Object> map = new HashMap<>();  
		
		
		if(admin==null){
			map.put("result", -1);// 参数为空
			return map;
		}
		if(admin.getId()==0||StringUtils.isBlank(admin.getPassword())) {
			map.put("result", -2);// 名称不能为空
			return map;
		}
		
		adminService.updateAdminPassword(admin);
		map.put("result", 1);//更新成功
		
		return map;
		
	}
	
}
