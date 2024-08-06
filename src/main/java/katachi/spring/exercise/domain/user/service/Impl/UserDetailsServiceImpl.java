package katachi.spring.exercise.domain.user.service.Impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import katachi.spring.exercise.domain.user.model.ExtendedUser;
import katachi.spring.exercise.domain.user.model.MUser;
import katachi.spring.exercise.domain.user.service.UserService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserService service;

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {

		//ユーザー情報取得
		MUser loginUser = service.getLoginUser(username);

		//ユーザーが存在しない場合
		if (loginUser == null) {
			throw new UsernameNotFoundException(username);
		}

		//権限List作成
		GrantedAuthority authority = new SimpleGrantedAuthority(loginUser.getIsAdmin());
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(authority);

		//UserDetails生成
		UserDetails userDetails = (UserDetails) new ExtendedUser(loginUser.getUser(),
				loginUser.getPass(),
				authorities,
				loginUser.getFamilyName(),
				loginUser.getFirstName());

		return userDetails;
	}
}
