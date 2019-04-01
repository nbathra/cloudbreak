package com.sequenceiq.freeipa.repository;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.sequenceiq.cloudbreak.aspect.DisableHasPermission;
import com.sequenceiq.cloudbreak.aspect.DisabledBaseRepository;
import com.sequenceiq.cloudbreak.service.EntityType;
import com.sequenceiq.freeipa.entity.SecurityConfig;

@EntityType(entityClass = SecurityConfig.class)
@Transactional(TxType.REQUIRED)
@DisableHasPermission
public interface SecurityConfigRepository extends DisabledBaseRepository<SecurityConfig, Long> {

    SecurityConfig findOneByStackId(Long stackId);

}
