package com.ta2khu75.quiz.configuration;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.ta2khu75.quiz.exception.NotFoundException;
import com.ta2khu75.quiz.model.HTTPMethod;
import com.ta2khu75.quiz.model.entity.Account;
import com.ta2khu75.quiz.model.entity.Permission;
import com.ta2khu75.quiz.model.entity.PermissionGroup;
import com.ta2khu75.quiz.model.entity.Role;
import com.ta2khu75.quiz.repository.AccountRepository;
import com.ta2khu75.quiz.repository.PermissionGroupRepository;
import com.ta2khu75.quiz.repository.PermissionRepository;
import com.ta2khu75.quiz.repository.RoleRepository;
import com.ta2khu75.quiz.util.StringUtil;

@Configuration
public class RunnerConfig {
	@Value("${app.api-prefix}")
	private String apiPrefix;

	@Bean
	CommandLineRunner init(AccountRepository accountRepository, RoleRepository roleRepository,
			PermissionRepository permissionRepository, PermissionGroupRepository permissionGroupRepository,
			PasswordEncoder passwordEncoder, ApplicationContext applicationContext) {
		return args -> {
			if (accountRepository.count() == 0) {
				initPermission(permissionRepository, permissionGroupRepository, applicationContext);
				Account account = Account.builder().email("root@g.com").password(passwordEncoder.encode("123"))
						.displayName("root").firstName("root").lastName("root").enabled(true).birthday(LocalDate.now())
						.role(initRole(roleRepository)).build();
				accountRepository.save(account);
			} else {
				initPermission(permissionRepository, permissionGroupRepository, applicationContext);
			}
		};
	}

	private Role initRole(RoleRepository roleRepository) {
		List<Role> roles = List.of(Role.builder().name("USER").build(), Role.builder().name("ADMIN").build(),
				Role.builder().name("ROOT").build());
		roles = roleRepository.saveAll(roles);
		return roles.stream().filter(role -> "ROOT".equals(role.getName())).findFirst()
				.orElseThrow(() -> new NotFoundException("Not found role name ROOT"));
	}

	@SuppressWarnings({ "null" })
	private void initPermission(PermissionRepository permissionRepository,
			PermissionGroupRepository permissionGroupRepository, ApplicationContext applicationContext) {
		Map<String, RequestMappingHandlerMapping> allRequestMappings = applicationContext
				.getBeansOfType(RequestMappingHandlerMapping.class);
		for (RequestMappingHandlerMapping handlerMapping : allRequestMappings.values()) {
			Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
			for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
				RequestMappingInfo requestMappingInfo = entry.getKey();
				HandlerMethod handlerMethod = entry.getValue();

				// Extract path from PathPatternsRequestCondition
				PathPatternsRequestCondition pathPatternsCondition = requestMappingInfo.getPathPatternsCondition();
				String path = pathPatternsCondition.getPatternValues().iterator().next();// : null;
				if (isExcludedPath(path)) {
					continue; // Skip this path
				}
				Class<?> beanType = handlerMethod.getBeanType();
				String permissionGroup = StringUtil.convertCamelCaseToReadable(beanType.getSimpleName());
				PermissionGroup group = permissionGroupRepository.findByName(permissionGroup);
				if (group == null) {
					group = permissionGroupRepository.save(PermissionGroup.builder().name(permissionGroup).build());
				}
				try {
				@SuppressWarnings("null")
				Permission permission = Permission.builder()
						.name(StringUtil.convertCamelCaseToReadable(handlerMethod.getMethod().getName())).path(path)
						.method(HTTPMethod.valueOf(
								requestMappingInfo.getMethodsCondition().getMethods().iterator().next().name()))
						.permissionGroup(group).build();
				permissionRepository.save(permission);
				} catch (Exception e) {
					// TODO: handle exception
//					e.printStackTrace();
					continue;
				}
			}
		}
	}

	private boolean isExcludedPath(String path) {
		return path.startsWith("/v3/api-docs") || path.startsWith("%s/account".formatted(apiPrefix))
				|| path.startsWith("%s/auth".formatted(apiPrefix)) || path.startsWith("/error")
				|| path.startsWith("/swagger-ui.html") || path.startsWith("/v3/api-docs.yaml");
	}
}
