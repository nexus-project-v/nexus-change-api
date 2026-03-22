package br.com.nexus.change.application.database.repository;

import br.com.nexus.change.application.database.mapper.ChangeMapper;
import br.com.nexus.change.core.domain.change.Change;
import br.com.nexus.change.core.ports.out.ChangeRepositoryPort;
import br.com.nexus.change.infrastructure.entity.change.ChangeEntity;
import br.com.nexus.change.infrastructure.repository.ChangeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class ChangeRepositoryAdapter implements ChangeRepositoryPort {

    private final ChangeRepository changeRepository;
    private final ChangeMapper changeMapper;

    @Autowired
    public ChangeRepositoryAdapter(ChangeRepository changeRepository, ChangeMapper changeMapper) {
        this.changeRepository = changeRepository;
        this.changeMapper = changeMapper;
    }

    @Override
    public Change save(Change change) {
        ChangeEntity changeEntity = changeMapper.fromModelTpEntity(change);
        if (changeEntity != null) {
            ChangeEntity saved = changeRepository.save(changeEntity);
            return changeMapper.fromEntityToModel(saved);
        }
        return null;
    }

    @Override
    public void remove(UUID id) {
        changeRepository.deleteById(id);
    }

    @Override
    public Change findById(UUID id) {
        Optional<ChangeEntity> buChange = changeRepository.findById(id);
        return buChange.map(changeMapper::fromEntityToModel).orElse(null);
    }

    @Override
    public List<Change> findAll() {
        List<ChangeEntity> all = changeRepository.findAll();
        return changeMapper.map(all);
    }

    @Override
    public Change update(UUID id, Change change) {
        Optional<ChangeEntity> resultById = changeRepository.findById(id);
        if (resultById.isPresent()) {

            ChangeEntity changeEntity = changeMapper.fromModelTpEntity(change);
            ChangeEntity changeToChange = resultById.get();
            changeToChange.applyChanges(id, changeEntity);

            return changeMapper.fromEntityToModel(changeRepository.save(changeToChange));
        }

        return null;
    }

}
