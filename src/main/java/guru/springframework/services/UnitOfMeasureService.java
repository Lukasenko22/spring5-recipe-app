package guru.springframework.services;

import guru.springframework.commands.UnitOfMeasureCommand;

import java.util.List;

public interface UnitOfMeasureService {
    List<UnitOfMeasureCommand> findAllUnitOfMeasures();
}
