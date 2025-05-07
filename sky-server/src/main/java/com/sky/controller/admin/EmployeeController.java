package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工 Controller")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation(value = "员工登出")
    public Result<String> logout() {
        return Result.success();
    }

    @PostMapping()
    @ApiOperation(value = "新增员工")
    public Result save(@RequestBody EmployeeDTO employee) {
        log.info("<新增员工>{}", employee);
        employeeService.save(employee);
        return Result.success();
    }

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页查询")
    public Result<PageResult> list(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("<员工分页查询>{}", employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     * @return
     */
    @ApiOperation(value = "启用禁用员工账号")
    @PostMapping("status/{status}")
    public Result startOrStop(@PathVariable(required = true,name = "status") Integer status,Long id) {
        log.info("<启用禁用员工账号> status:{} id: {}", status,id);
        employeeService.startOrStop(status,id);
        return Result.success();
    }

    /**
     * 根据 id 查询员工信息,用于页面回显
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据 id 查询员工信息操作")
    public Result<Employee>  getById(@PathVariable(required = true,name = "id") Long id) {
        log.info("<根据 id 查询员工信息操作> id: {}", id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }
    /**
     * 根据 id 编辑员工信息
     */
    @PutMapping()
    @ApiOperation(value="根据 id 编辑员工信息")
    public Result update(@RequestBody EmployeeDTO employee) {
        log.info("<根据 id 编辑员工信息操作> id: {}", employee.getId());
        employeeService.update(employee);
        return Result.success();
    }
}