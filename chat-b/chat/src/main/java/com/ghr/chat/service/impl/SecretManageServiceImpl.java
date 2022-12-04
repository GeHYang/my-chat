package com.ghr.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghr.chat.dao.SecretMapper;
import com.ghr.chat.domain.entity.Secret;
import com.ghr.chat.service.SecretManageService;
import org.springframework.stereotype.Service;

/**
 * @author: Yang
 * @create: 2022-12-02
 * @Description:
 */
@Service
public class SecretManageServiceImpl extends ServiceImpl<SecretMapper, Secret> implements SecretManageService {
}
