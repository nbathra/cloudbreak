package com.sequenceiq.freeipa.repository;

import javax.transaction.Transactional;

import com.sequenceiq.cloudbreak.aspect.DisableHasPermission;
import com.sequenceiq.cloudbreak.aspect.DisabledBaseRepository;
import com.sequenceiq.cloudbreak.service.EntityType;
import com.sequenceiq.freeipa.entity.Stack;

@EntityType(entityClass = Stack.class)
@Transactional(Transactional.TxType.REQUIRED)
@DisableHasPermission
public interface StackRepository extends DisabledBaseRepository<Stack, Long> {
}
