/**
 * 
 */
package com.jeesuite.demo.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.jeesuite.cache.command.RedisHashMap;
import com.jeesuite.cache.command.RedisString;
import com.jeesuite.common.util.BeanCopyUtils;
import com.jeesuite.demo.api.IUserService;
import com.jeesuite.demo.dao.entity.UserEntity;
import com.jeesuite.demo.dao.mapper.UserEntityMapper;
import com.jeesuite.demo.dto.User;
import com.jeesuite.demo.exception.UserNoExistException;
import com.jeesuite.kafka.message.DefaultMessage;
import com.jeesuite.kafka.spring.TopicProducerSpringProvider;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2016年10月25日
 */
@Service
public class UserServiceImpl implements IUserService {

	@Resource
	private UserEntityMapper userMapper;
	
	@Resource
	private TopicProducerSpringProvider kafkaProducer;

	@Override
	public User getUser(Integer userId) throws UserNoExistException {
		UserEntity entity = userMapper.selectByPrimaryKey(userId);
		if(entity == null)throw new UserNoExistException();
		return BeanCopyUtils.copy(entity, User.class);
	}


	@Override
	public void updateUser(User user) {
		UserEntity entity = BeanCopyUtils.copy(user, UserEntity.class);
		userMapper.updateByPrimaryKeySelective(entity);
	}

	@Override
	public void removeUser(Integer userId) throws UserNoExistException {
		UserEntity entity = userMapper.selectByPrimaryKey(userId);
		if(entity == null)throw new UserNoExistException();
		userMapper.deleteByPrimaryKey(userId);
	}


	@Override
	public void kafkaProducerTest() {
		kafkaProducer.publish("demo-topic", new DefaultMessage("hello"));
	}


	@Override
	public void cacheTest() {
		RedisString redisString = new RedisString("key1");
		
		if(redisString.exists()){			
			String value = redisString.get();
			System.out.println(value);
			redisString.remove();
		}
		
		RedisHashMap redisHashMap = new RedisHashMap("key2");
		
		redisHashMap.containsKey("field1");
		redisHashMap.set("field1", "111");
		
		Map<String, User> users = redisHashMap.getAll();
		
	}

}
