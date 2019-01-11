# springSecurity-jwt-vue-temple Demo
# 如果对你有帮助，希望可以点个Star支持一下~
[Spring Security (一):整合JWT](#jump_ss1)<br>
[Spring Security (二):获取菜单树](#jump_ss2)<br>
[Spring Security (三):整合Vue.js实现权限控制](#jump_ss3)
>违背的青春


<a id="jump_ss1">今天</a>写下`Spring Security`整合`jwt`的一个简单小`Demo`，目的是登录后实现返回`token`，其实整个过程很简单。
### 导入依赖
```
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.0</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```
### 首先创建一个JwtUser实现UserDetails
`org.springframework.security.core.userdetails.UserDetails`
先看一下这个接口的源码，其实很简单
```
public interface UserDetails extends Serializable {
    Collection<? extends GrantedAuthority> getAuthorities();

    String getPassword();

    String getUsername();

    boolean isAccountNonExpired();

    boolean isAccountNonLocked();

    boolean isCredentialsNonExpired();

    boolean isEnabled();
}
```
这个是`Spring Security`给我们提供的一个简单的接口，因为我们需要通过`SecurityContextHolder`去取得用户凭证等等信息，因为这个比较简单，所以我们实际业务要来加上我们所需要的信息。
```
public class JwtUser implements UserDetails {


    private String username;

    private String password;

    private Integer state;

    private Collection<? extends GrantedAuthority> authorities;

    public JwtUser() {
    }

    public JwtUser(String username, String password, Integer state, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.state = state;
        this.authorities = authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    //账户是否未过期
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //账户是否未被锁
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }


    
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    
    //是否启用
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}

```
我这个其实也很简单，只是一些用户名，密码状态和权限的集合这些。
### 编写一个工具类来生成令牌等...

```
@Data
@ConfigurationProperties(prefix = "jwt")
@Component
public class JwtTokenUtil implements Serializable {

    private String secret;

    private Long expiration;

    private String header;

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String generateToken(Map<String, Object> claims) {
        Date expirationDate = new Date(System.currentTimeMillis() + expiration);
        return Jwts.builder().setClaims(claims).setExpiration(expirationDate).signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    /**
     * 生成令牌
     *
     * @param userDetails 用户
     * @return 令牌
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(2);
        claims.put("sub", userDetails.getUsername());
        claims.put("created", new Date());
        return generateToken(claims);
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 判断令牌是否过期
     *
     * @param token 令牌
     * @return 是否过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 刷新令牌
     *
     * @param token 原令牌
     * @return 新令牌
     */
    public String refreshToken(String token) {
        String refreshedToken;
        try {
            Claims claims = getClaimsFromToken(token);
            claims.put("created", new Date());
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    /**
     * 验证令牌
     *
     * @param token       令牌
     * @param userDetails 用户
     * @return 是否有效
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        JwtUser user = (JwtUser) userDetails;
        String username = getUsernameFromToken(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }
}
```
这个类就是一些生成令牌，验证等等一些操作。具体看注释~
### 编写一个Filter
```
@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {



    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authHeader = request.getHeader(jwtTokenUtil.getHeader());
        if (authHeader != null && StringUtils.isNotEmpty(authHeader)) {
            String username = jwtTokenUtil.getUsernameFromToken(authHeader);
            log.info(username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if (jwtTokenUtil.validateToken(authHeader, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);
    }
}
```
这个其实就是用来验证令牌的是否合法，由于今天这个`Demo`是一个简单的登录返回`token`的过程，所以这个默认不会去执行里面的逻辑。但是以后登陆后的操作就会执行里面的逻辑了。
### JwtUserDetailsServiceImpl
`JwtUserDetailsServiceImpl`这个实现类是实现了`UserDetailsService`，`UserDetailsService`是`Spring Security`进行身份验证的时候会使用，我们这里就一个加载用户信息的简单方法，就是得到当前登录用户的一些用户名、密码、用户所拥有的角色等等一些信息
```
@Slf4j
@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {



    @Autowired
    private UserMapper userMapper;
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        User user = userMapper.selectByUserName(s);
        if(user == null){
            throw new UsernameNotFoundException(String.format("'%s'.这个用户不存在", s));
        }
        List<SimpleGrantedAuthority> collect = user.getRoles().stream().map(Role::getRolename).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return new JwtUser(user.getUsername(), user.getPassword(), user.getState(), collect);
    }
}
```
### 编写登录的业务实现类

```
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;



    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    public User findByUsername(String username) {
        User user = userMapper.selectByUserName(username);
        log.info("userserviceimpl"+user);
        return user;
    }

    public RetResult login(String username, String password) throws AuthenticationException {
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username, password);
        final Authentication authentication = authenticationManager.authenticate(upToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new RetResult(RetCode.SUCCESS.getCode(),jwtTokenUtil.generateToken(userDetails));
    }
}
```
从上面可以看到`login`方法，会根据用户信息然后返回一个`token`给我们。
### WebSecurityConfig
这个就是`Spring Security`的配置类了
```
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder)throws Exception{
        authenticationManagerBuilder.userDetailsService(this.userDetailsService).passwordEncoder(passwordEncoder());
    }


    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/auth/**").permitAll()
                .anyRequest().authenticated()
                .and().headers().cacheControl();

        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);


        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http.authorizeRequests();
        //让Spring security放行所有preflight request
        registry.requestMatchers(CorsUtils::isPreFlightRequest).permitAll();
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowCredentials(true);
        cors.addAllowedOrigin("*");
        cors.addAllowedHeader("*");
        cors.addAllowedMethod("*");
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", cors);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}
```
我们可以在这里设置自定义的拦截规则，注意在`Spring Security5.x`中我们要显式注入`AuthenticationManager`不然会报错~
```
@Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
```
目前基本的都已经完成了剩下的就是一些`entity,controller`的代码了具体可以看我[GitHub](https://github.com/ywbjja/springSecurityDemo)上的代码。
下载项目到本地，然后改一下mysql的数据库信息，运行就可以看到返回的token数据了
![image](https://github.com/ywbjja/springSecurityDemo/blob/master/images/QQ截图20190105230741.png)
如果有任何见解或者我有写错的地方可以联系我...我一定改...<br>


<a id="jump_ss2">今天说下登录后获取用户菜单，</a>流程就是根据用户的`token`然后通过`SecurityContextHolder`来获取用户信息，根据用户来查询菜单树，其实最主要的就是怎么把后台查询出的数据转换成树形菜单，其实也不难，前后端都可以做，因为树形还是比较常见的所以可以做成一个通用的类来使用，使用时只需要传入数据就好了。
### GenTree

这个就是把数据转成树形的方法了。
```
    public class GenTree {


    /**
     * 递归根节点
     * @param nodes
     * @return
     */
    public static Set<Menu> genRoot(Set<Menu> nodes){
        Set<Menu> root = new HashSet<>();
        //遍历数据
        nodes.forEach(menu -> {
            //当父id是0的时候应该是根节点
            System.out.println(menu.getPer_paerent_id());
            if(menu.getPer_paerent_id() == 0){
                root.add(menu);
            }
        });
        //这里是子节点的创建方法
        root.forEach(menu -> {
            genChildren(menu,nodes);
        });
        //返回数据
        return root;
    }

    /**
     * 递归子节点
     * @param menu
     * @param nodes
     * @return
     */
    private static Menu genChildren(Menu menu, Set<Menu> nodes) {
        //遍历传过来的数据
        for (Menu menu1 :nodes){
            //如果数据中的父id和上面的per_id一致应该就放children中去
            if(menu.getPer_id().equals(menu1.getPer_paerent_id())){
                //如果当前节点的子节点是空的则初始化，如果不为空就加进去
                if(menu.getChildren() == null){
                    menu.setChildren(new ArrayList<Menu>());
                }
                menu.getChildren().add(genChildren(menu1,nodes));
            }
        }
        //返回数据
        return menu;
    }

   
}
```
上面的方法其实也就是几个`forEach`，递归判断，放入数据。
其中Menu的代码如下，这个就是你要得到菜单的格式了，可以根据自己需要进行改造。
```
@Data
public class Menu {
    private Integer per_id;
    private Integer per_paerent_id;
    private String per_name;
    private String per_resource;
    private List<Menu> children;
    public Menu(Integer per_id,Integer per_paerent_id,String per_name,String per_resource){
        this.per_id = per_id;
        this.per_paerent_id = per_paerent_id;
        this.per_name = per_name;
        this.per_resource = per_resource;
    }
}

```

然后就是`Mapper`、`Service`等的编写了，在`Controller`层中通过`SecurityContextHolder`来获得用户信息。
```
//使用Spring Security 获取用户信息
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
```
其实之前的项目应该改一下，我把`token`放到`msg`里了，修改了一下，
![](https://i.imgur.com/ifefJLq.png)
然后我们就可以拿着这个`token`查询菜单树了。
![](https://i.imgur.com/qf0Kvzo.png)
<a id="jump_ss3">本篇</a>
内容较长，先看下效果：
![](https://i.imgur.com/VQ9UTCe.png)
然后侧边栏的路由其实根据我们后台获取到的：（这个数据结构比较简单，在这里只是演示）

    {
    "code": 200,
    "msg": "",
    "data": {
        "id": "1",
        "username": "admin",
        "roles": [
            {
                "id": 1,
                "describe": null,
                "rolename": "ROLE_ADMIN",
                "permissions": null
            }
        ],
        "menus": [
            {
                "per_id": 1101,
                "per_paerent_id": 0,
                "per_name": "权限管理",
                "per_resource": "auth",
                "children": [
                    {
                        "per_id": 1102,
                        "per_paerent_id": 1101,
                        "per_name": "角色管理",
                        "per_resource": "role",
                        "children": null
                    },
                    {
                        "per_id": 1103,
                        "per_paerent_id": 1101,
                        "per_name": "资源管理",
                        "per_resource": "per",
                        "children": null
                    }
                ]
            }
          ]
    	}
	}


由于本人是个后端小辣鸡，前端对我来说简直就是地狱...就使用了`GitHub`上的轮子试着改了一下。感谢[@PanJiaChen](https://github.com/PanJiaChen/vue-element-admin) 的项目，我就使用了其中的模板来做的，如果有什么不完美的希望指出。
### 主要步骤
在前后端分离项目中我们前端只需要对数据进行渲染就ok了。目前后端已经可以成功给我们返回了用户信息，前端实现权限体系的主要思路是
1. 首先就是查询用户具有的权限资源信息，然后我们前端根据这个信息，然后从所有的路由页面中动态的加载用户具有的路由页面。并且在没有访问权限的时候提示无权限。
2. 由于Vue2.2.0以后新增了router.addRoutes,我们就可以通过这个来添加用户的路由即可~
3. 本项目侧边栏和路由是一个文件，所以我们要按照一定的规则去设置路由。具体方法见：[vue-element-admin](https://panjiachen.gitee.io/vue-element-admin-site/zh/guide/essentials/router-and-nav.html "路由和侧边栏")
### 用户信息
首先修改login接口：`src\api\login.js`修改为Spring Boot的接口。
    
    import request from '@/utils/request'
	/**
	 * 登录操作
	 * @param {用户名} username
	 * @param {密码} password
	 */
	export function login(username, password) {
	  return request({
	    url: '/auth/login',
	    method: 'post',
	    data: {
	      username,
	      password
	    }
	  })
	}
	/**
	 * 获取用户信息
	 * @param {token} token
	 */
	export function getInfo(token) {
	  return request({
	    url: '/getUserInfo',
	    method: 'get',
	    params: { token }
	  })
	}
	
	export function logout() {
	  return request({
	    url: 'user/logout',
	    method: 'post'
	  })
	}

修改`dev.env.js`为本地接口

    'use strict'
	const merge = require('webpack-merge')
	const prodEnv = require('./prod.env')
	
	module.exports = merge(prodEnv, {
	  NODE_ENV: '"development"',
	  BASE_API: '"http://localhost:8086"',
	})


修改`axios`配置文件

    import axios from 'axios'
	import { Message, MessageBox } from 'element-ui'
	import store from '../store'
	import { getToken } from '@/utils/auth'
	
	// 创建axios实例
	const service = axios.create({
	  baseURL: process.env.BASE_API, // api 的 base_url
	  timeout: 5000 // 请求超时时间
	})
	
	// request拦截器
	service.interceptors.request.use(
	  config => {
    if (store.getters.token) {
		// 这里修改为'jwtHeader'
      config.headers['jwtHeader'] = getToken() // 让每个请求携带自定义token 请根据实际情况自行修改
    }
    return config
	  },
	  error => {
	    // Do something with request error
	    console.log(error) // for debug
	    Promise.reject(error)
	  }
	)
	
	// response 拦截器
	service.interceptors.response.use(
	  response => {
    /**
     * code为非200是抛错 可结合自己业务进行修改
     */
    const res = response.data
    if (res.code !== 200) {
      Message({
        message: res.message,
        type: 'error',
        duration: 5 * 1000
      })

      // 50008:非法的token; 50012:其他客户端登录了;  50014:Token 过期了;
      if (res.code === 500 || res.code === 500 || res.code === 500) {
        MessageBox.confirm(
          '你已被登出，可以取消继续留在该页面，或者重新登录',
          '确定登出',
          {
            confirmButtonText: '重新登录',
            cancelButtonText: '取消',
            type: 'warning'
          }
        ).then(() => {
          store.dispatch('FedLogOut').then(() => {
            location.reload() // 为了重新实例化vue-router对象 避免bug
          })
        })
      }
      return Promise.reject('error')
    } else {
      return response.data
    }
	  },
	  error => {
	    console.log('err' + error) // for debug
	    Message({
	      message: error.message,
	      type: 'error',
	      duration: 5 * 1000
	    })
	    return Promise.reject(error)
	  }
	)
	
	export default service

以上都是最基本的连接配置。
### 路由配置
src\router\index.js

    import Vue from 'vue'
	import Router from 'vue-router'
	
	// in development-env not use lazy-loading, because lazy-loading too many pages will cause webpack hot update too slow. so only in production use lazy-loading;
	// detail: https://panjiachen.github.io/vue-element-admin-site/#/lazy-loading
	
	Vue.use(Router)
	
	/* Layout */
	import Layout from '../views/layout/Layout'
	
	/**
	* hidden: true                   if `hidden:true` will not show in the sidebar(default is false)
	* alwaysShow: true               if set true, will always show the root menu, whatever its child routes length
	*                                if not set alwaysShow, only more than one route under the children
	*                                it will becomes nested mode, otherwise not show the root menu
	* redirect: noredirect           if `redirect:noredirect` will no redirect in the breadcrumb
	* name:'router-name'             the name is used by <keep-alive> (must set!!!)
	* meta : {
	    title: 'title'               the name show in submenu and breadcrumb (recommend set)
	    icon: 'svg-name'             the icon show in the sidebar
	    breadcrumb: false            if false, the item will hidden in breadcrumb(default is true)
	  }
	**/
	
	// 所有权限通用路由表
	// 这里就是一些公共界面如，错误提示页面，登录页面是不需要权限的就可以在这个里面配置
	
	export const constantRouterMap = [
	  {
	    path: '/login',
	    name: 'Login',
	    component: () =>
	      import('@/views/login/index'),
	    hidden: true
	  },
	
	  {
	    path: '/',
	    component: Layout,
	    redirect: '/dashboard',
	    name: 'Dashboard',
	    hidden: true,
	    children: [{
	      path: 'dashboard',
	      component: () =>
	        import('@/views/dashboard/index')
	    }, {
	      path: 'userinfo',
	      name: 'UserInfo',
	      component: () =>
	        import('@/views/dashboard/userinfo')
	    }]
	  },
	  {
	    path: '*',
	    redirect: '/404',
	    hidden: true
	  }
	]
	
	export default new Router({
	  // mode: 'history', //后端支持可开
	  scrollBehavior: () => ({ y: 0 }),
	  routes: constantRouterMap
	})
	// 异步挂载的路由
	// 动态需要根据权限加载的路由表（这里的路由时用来动态加载的，通俗点讲就是需要权限控制的路由都在这个里面配置)
	export const asyncRouterMap = [
	  {
	    path: '/auth',
	    component: Layout,
	    name: 'auth',
	    meta: {
	      resources: 'auth',
	      title: '权限管理'
	    },
	    children: [
	      {
	        path: 'per',
	        component: () => import('@/views/pre/perm/index'),
	        name: 'per',
	        meta: {
	          resources: 'per'
	        }
	      },
	      {
	        path: 'user',
	        component: () => import('@/views/pre/user/index'),
	        name: 'user',
	        meta: {
	          resources: 'user'
	        }
	      },
	      {
	        path: 'role',
	        component: () => import('@/views/pre/role/index'),
	        name: 'role',
	        meta: {
	          resources: 'role'
	        }
	      }
	    ]
	  }
	]

然后你需要根据`import`的指令去添加相应的`Vue`文件。
### 权限控制
其实也就是拿着后台获取到的路由，然后和上面配置的路由进行判断，如果符合就加到用户的真实路由中。
1、首先修改src\store\modules\user.js

    import { login, logout, getInfo } from '@/api/login'
	import { getToken, setToken, removeToken } from '@/utils/auth'
	
	const user = {
	  state: {
	    token: getToken(),
	    username: '',
	    user: {},
	    roles: [], // 用户角色列表
	    menus: [] // 菜单列表
	  },
	
	  mutations: {
	    SET_TOKEN: (state, token) => {
	      state.token = token
	    },
	    SET_INFO: (state, user) => {
	      state.username = user.username
	      state.user = user
	    },
	    SET_MENUS: (state, menus) => {
	      state.menus = menus
	    },
	    SET_ROLES: (state, roles) => {
	      state.roles = roles
	    }
	  },
	
	  actions: {
	    // 登录
	    Login({ commit }, userInfo) {
	      const username = userInfo.username.trim()
	      return new Promise((resolve, reject) => {
	        login(username, userInfo.password).then(res => {
	          const data = res.data
	          setToken(data)
	          commit('SET_TOKEN', data)
	          resolve()
	        }).catch(error => {
	          reject(error)
	        })
	      })
	    },
	
	    // 获取用户信息
	    GetInfo({ commit, state }) {
	      return new Promise((resolve, reject) => {
	        getInfo(state.token).then(response => {
	          const data = response.data
	          if (data.roles && data.roles.length > 0) { // 验证返回的roles是否是一个非空数组
	            commit('SET_ROLES', data.roles)
	          } else {
	            reject('getInfo: roles must be a non-null array !')
	          }
	          commit('SET_MENUS', data.menus)
	          commit('SET_INFO', data)
	          resolve(response)
	        }).catch(error => {
	          reject(error)
	        })
	      })
	    },
	
	    // 登出
	    LogOut({ commit, state }) {
	      return new Promise((resolve, reject) => {
	        logout(state.token).then(() => {
	          commit('SET_TOKEN', '')
	          commit('SET_ROLES', [])
	          commit('SET_INFO', '')
	          removeToken()
	          resolve()
	        }).catch(error => {
	          reject(error)
	        })
	      })
	    },
	
	    // 前端 登出
	    FedLogOut({ commit }) {
	      return new Promise(resolve => {
	        commit('SET_TOKEN', '')
	        removeToken()
	        resolve()
	      })
	    }
	  }
	}
	
	export default user

这里使用了`Vuex`进行状态管理，重点关注下`actions`中的`Login`和`GetInfo`方法，`Login`则是登录之后获取到`token`然后把`token`存储,然后`GetInfo`就是拿着`token`去请求接口获取用户角色、权限资源信息了。
### 创建一个新文件 src\store\modules\permission.js
2、这个文件的作用就是处理路由，把从后台获取的路由和我们配置的`asyncRouterMap`进行匹配，然后就返回用户当前真实的路由了...也就是几个`forEach`然后把公共的和当前用户的路由`addRouters`就搞定了。

    // store/permission.js
	import { asyncRouterMap, constantRouterMap } from '@/router'
	
	/**
	 *
	 * @param  {Array} userRouter 后台接口请求的路由
	 * @param  {Array} allRouter  前端配置好的所有动态路由的集合
	 * @return {Array} userRealRouters 过滤后的路由
	 */
	
	export function userCurrentRouter(userRouter = [], allRouter = []) {
	  var userRealRouters = []
	  allRouter.forEach((router, index) => {
	    userRouter.forEach((item, index) => {
	      // 拿用户的路由和配置路由进行匹配判断
	      if (item.per_resource === router.meta.resources) {
	        // 对路由下的子路由进行判断，递归添加
	        if (item.children && item.children.length > 0) {
	          router.children = userCurrentRouter(item.children, router.children)
	        }
	        // 这里是设置侧边栏的显示title还可以显示图标(没做)
	        router.meta.title = item.per_name
	        userRealRouters.push(router)
	      }
	    })
	  })
	  return userRealRouters
	}
	const permission = {
	  state: {
	    routers: constantRouterMap,
	    apiRouters: [] // 后台接口获取得到的路由(per_resource)
	  },
	  mutations: {
	    SET_ROUTERS: (state, routers) => {
	      state.apiRouters = routers
	      state.routers = constantRouterMap.concat(routers)
	    }
	  },
	  actions: {
	    GenerateRoutes({ commit }, data) {
	      return new Promise(resolve => {
	        commit('SET_ROUTERS', userCurrentRouter(data, asyncRouterMap))
	        resolve()
	      })
	    }
	  }
	}
	
	export default permission


重点关注下`userCurrentRouter`和`GenerateRoutes`，第一个是匹配路由，第二个是把公共的路由和真实路由匹配到一起。最主要的是理解`Vuex`的意义。
3、此外修改一下src\store\getters.js文件

    const getters = {
	  sidebar: state => state.app.sidebar,
	  token: state => state.user.token,
	  username: state => state.user.username,
	  roles: state => state.user.roles,
	  user: state => state.user.user,
	  menus: state => state.user.menus,
	  menu: state => state.permission.routers,
	  apiRouters: state => state.permission.apiRouters
	}
	export default getters

其实也就是你想要哪些信息，然后在里面定义好，然后我们就可以全局从`Vuex`中拿了，是不是很方便！
4、修改src\store\index.js

    import Vue from 'vue'
	import Vuex from 'vuex'
	import app from './modules/app'
	import user from './modules/user'
	import permission from './modules/permission'
	import getters from './getters'
	
	Vue.use(Vuex)
	
	const store = new Vuex.Store({
	  modules: {
	    app,
	    user,
	    permission
	  },
	  getters
	})
	
	export default store


5、然后重要的一步来了，src\permission.js

    import router from './router'
	import store from './store'
	import NProgress from 'nprogress' // Progress 进度条
	import 'nprogress/nprogress.css'// Progress 进度条样式
	import { Message } from 'element-ui'
	import { getToken } from '@/utils/auth' // 验权
	
	const whiteList = ['/login'] // 不重定向白名单
	router.beforeEach((to, from, next) => {
	  NProgress.start()
	  if (getToken()) {
	    if (to.path === '/login') {
	      next({ path: '/' })
	      NProgress.done() // if current page is dashboard will not trigger	afterEach hook, so manually handle it
	    } else {
	      if (store.getters.roles.length === 0) { // 判断当前用户是否已拉取完user_info信息
	        store.dispatch('GetInfo').then(res => {
	          // 生成可访问的路由表
	          store.dispatch('GenerateRoutes', store.getters.menus).then(r => {
	            // 动态添加可访问路由表
	            router.addRoutes(store.getters.apiRouters)
	            next({ ...to, replace: true }) // hack方法 确保addRoutes已完成 ,set the replace: true so the navigation will not leave a history record
	          })
	        }).catch((err) => {
	          store.dispatch('FedLogOut').then(() => {
	            Message.error(err || 'Verification failed, please login again')
	            next({ path: '/' })
	          })
	        })
	      } else {
	        next()
	      }
	    }
	  } else {
	    if (whiteList.indexOf(to.path) !== -1) {
	      next()
	    } else {
	      next(`/login?redirect=${to.path}`) // 否则全部重定向到登录页
	      NProgress.done()
	    }
	  }
	})
	
	router.afterEach(() => {
	  NProgress.done() // 结束Progress
	})

这个就是判断是否正确的拿到路由信息然后会给我们生成路由。
6、修改src\views\layout\components\Sidebar\index.vue

    <template>
	  <el-scrollbar wrap-class="scrollbar-wrapper">
	
	
	    <el-menu
	      :show-timeout="200"
	      :default-active="$route.path"
	      :collapse="isCollapse"
	      mode="vertical"
	      background-color="#304156"
	      text-color="#bfcbd9"
	      active-text-color="#409EFF"
	    >
	      <sidebar-item v-for="route in menu" :key="route.path" :item="route" :base-path="route.path"/>
	    </el-menu>
	  </el-scrollbar>
	</template>
	
	<script>
	import { mapGetters } from 'vuex'
	import SidebarItem from './SidebarItem'
	
	export default {
	  components: {
	    SidebarItem
	  },
	  computed: {
	    ...mapGetters([
	      'menu',
	      'sidebar'
	    ]),
	    isCollapse() {
	      return !this.sidebar.opened
	    }
	  }
	}
	</script>

用`v-for`指令放到`sideBar`中去，目前为止基本动态的路由菜单已经可以生成了。如果有什么问题，请联系我指正...感谢...代码已经同步到`GitHub`。
`Vue`前端clone下来后：
# Getting started
```
# clone the project
vue:
git clone https://github.com/ywbjja/Vue_templete.git
Spring:
https://github.com/ywbjja/springSecurity-jwt-vue-temple.git

# install dependency
npm install

# develop
npm run dev
```
代码已经更新到`github`上，有任何疑问可以联系我~

