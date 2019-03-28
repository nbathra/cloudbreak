package com.sequenceiq.freeipa.controller;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.stereotype.Controller;

import com.sequenceiq.freeipa.api.CreateEndpoint;
import com.sequenceiq.freeipa.api.CreateFreeIpaRequest;
import com.sequenceiq.freeipa.api.CreateFreeIpaResponse;
import com.sequenceiq.freeipa.service.StackCreationService;

@Controller
@Transactional(TxType.NEVER)
public class CreateController implements CreateEndpoint {

    @Inject
    private StackCreationService stackCreationService;

    @Override
    public CreateFreeIpaResponse create(CreateFreeIpaRequest request) {
        stackCreationService.launchStack(request);
        return new CreateFreeIpaResponse();
    }
}
