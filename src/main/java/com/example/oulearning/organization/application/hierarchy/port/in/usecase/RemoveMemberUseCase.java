package com.example.oulearning.organization.application.hierarchy.port.in.usecase;

import com.example.oulearning.organization.application.hierarchy.port.in.command.RemoveMemberCommand;

public interface RemoveMemberUseCase {
    void execute(RemoveMemberCommand command);
}
