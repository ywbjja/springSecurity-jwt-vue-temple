package com.example.demo.mapper;

import com.example.demo.entity.User; 
import java.util.ArrayList; 
import java.util.Map;

public interface UserMapper {

	public Integer add (User user ); 

	public Integer delById (Long user_id);

	public Integer updateById(Map<String,Object> map); 

	public User queryById (Long user_id);

	public Map joinMapById (Long user_id);

	public ArrayList<User> queryAll(); 

	public ArrayList<Map> queryListByCond(Map<String,Object> map);

	public User queryByUserName(Map<String,Object> map);

}
