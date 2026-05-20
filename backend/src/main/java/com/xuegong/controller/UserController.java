package com.xuegong.controller;

import com.xuegong.common.Result;
import com.xuegong.entity.User;
import com.xuegong.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping
    public Result<List<User>> list(@RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) String role,
                                   @RequestParam(required = false) String status) {
        List<User> list;
        if (role != null && !role.isEmpty()) {
            list = service.findByRole(role);
        } else if (status != null && !status.isEmpty()) {
            list = service.findByStatus(status);
        } else if (keyword != null && !keyword.isEmpty()) {
            list = service.search(keyword);
        } else {
            list = service.findAll();
        }
        return Result.ok(list);
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User u = service.findById(id);
        return u != null ? Result.ok(u) : Result.error("用户不存在");
    }

    @PostMapping
    public Result<User> create(@Valid @RequestBody User user) {
        if (service.existsByUsername(user.getUsername())) {
            return Result.error("用户名已存在");
        }
        return Result.ok(service.save(user));
    }

    @PutMapping("/{id}")
    public Result<User> update(@PathVariable Long id, @Valid @RequestBody User user) {
        User updated = service.update(id, user);
        return updated != null ? Result.ok(updated) : Result.error("用户不存在");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? Result.ok() : Result.error("用户不存在");
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginForm) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");
        
        if (username == null || password == null) {
            return Result.error("用户名和密码不能为空");
        }
        
        Optional<User> userOpt = service.findByUsername(username);
        if (userOpt.isEmpty()) {
            return Result.error("用户名或密码错误");
        }
        
        User user = userOpt.get();
        if (!user.getPassword().equals(password)) {
            return Result.error("用户名或密码错误");
        }
        
        if (!"启用".equals(user.getStatus())) {
            return Result.error("账号已被禁用");
        }
        
        service.updateLastLogin(username);
        
        Map<String, Object> data = new HashMap<>();
        data.put("token", "token-" + System.currentTimeMillis());
        data.put("user", Map.of(
            "id", user.getId(),
            "username", user.getUsername(),
            "name", user.getName(),
            "role", user.getRole()
        ));
        
        return Result.ok(data);
    }

    @PostMapping("/reset-password/{id}")
    public Result<Void> resetPassword(@PathVariable Long id) {
        User user = service.findById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword("123456");
        service.save(user);
        return Result.ok();
    }
}
