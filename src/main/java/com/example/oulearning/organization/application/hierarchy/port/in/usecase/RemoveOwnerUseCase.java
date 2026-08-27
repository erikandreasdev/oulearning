package com.example.oulearning.organization.application.hierarchy.port.in.usecase;

import com.example.oulearning.organization.application.hierarchy.port.in.command.RemoveOwnerCommand;

public interface RemoveOwnerUseCase {
    void execute(RemoveOwnerCommand command);
}
