package com.example.demo.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;
@Data
public class User { 
	private Long id;
	private String username;
	private String password;
	private List<String> roles;

	public User(){
	
	}
	public User(Map<String,Object> map){
		this.username = (String) map.get("username");
		this.password = (String) map.get("password");
	}
}
