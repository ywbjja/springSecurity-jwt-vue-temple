# springSecurity-jwt-vue-temple Demo
# 如果对你有帮助，希望可以点个Star支持一下~
[Spring Security (一):整合JWT](#jump_ss1)<br>
[Spring Security (二):获取菜单树](#jump_ss2)
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
然后我们就可以拿着这个`token`查询菜单树了，
![](https://i.imgur.com/qf0Kvzo.png)
代码已经更新到`github`上，有任何疑问可以联系我~

![](https://i.imgur.com/G1gMHsi.jpg)
