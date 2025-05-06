package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Component
@Slf4j
@Aspect
public class AutoFillAspect {
//    切入点
    @Pointcut("execution(* com.sky.mapper.*.*(..))  && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){

    }
//    通知
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("<公共字段自动填充>");

//        获取到当前被拦截的方法上的数据库操作类型
        MethodSignature signature =(MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType type = autoFill.value();
//        获取当前被拦截的方法参数
        Object[] args = joinPoint.getArgs();
        if(args.length == 0 || args == null){
            return;
        }

        Object entity = args[0];
//        数据获取(当前时间,通过 ThreadLocal 获取当前操作人 id)
        LocalDateTime localDateTime = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
//        根据当前不同的操作类型,通过反射:为相关的属性复制
        /**
         * Insert 需要给四个字段修改
         * 而 Update 需要给两个字段修改
         */
        if(type == OperationType.INSERT){
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                setCreateTime.invoke(entity, localDateTime);
                setUpdateTime.invoke(entity, localDateTime);
                setCreateUser.invoke(entity, currentId);
                setUpdateUser.invoke(entity, currentId);
            } catch (NoSuchMethodException e) {
//                throw new RuntimeException(e);
                log.error(e.getMessage());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        } else if (type == OperationType.UPDATE) {
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setUpdateTime.invoke(entity, localDateTime);
                setUpdateUser.invoke(entity, currentId);
            } catch (NoSuchMethodException e) {
                log.error(e.getMessage());

            }catch(Exception e){
                log.error(e.getMessage());
            }

        }

    }

}