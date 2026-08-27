package com.example.oulearning.organization.application.hierarchy.port.in.usecase;

import com.example.oulearning.organization.application.hierarchy.port.in.command.AssignOwnerCommand;

public interface AssignOwnerUseCase {
    void execute(AssignOwnerCommand command);
}
