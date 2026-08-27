package com.example.oulearning.organization.application.hierarchy.port.in.usecase;

import com.example.oulearning.organization.application.hierarchy.port.in.command.AssignMemberCommand;

public interface AssignMemberUseCase {
    void execute(AssignMemberCommand command);
}
