package br.com.nexus.change.application.database.repository;

import br.com.nexus.change.application.database.mapper.ComponentMapper;
import br.com.nexus.change.commons.exception.ResourceFoundException;
import br.com.nexus.change.core.domain.component.ChangeComponent;
import br.com.nexus.change.core.ports.out.ComponentRepositoryPort;
import br.com.nexus.change.infrastructure.entity.component.ComponentEntity;
import br.com.nexus.change.infrastructure.repository.ComponentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class ComponentRepositoryAdapter implements ComponentRepositoryPort {

    private final ComponentRepository componentRepository;
    private final ComponentMapper componentMapper;

    @Autowired
    public ComponentRepositoryAdapter(ComponentRepository componentRepository, ComponentMapper componentMapper) {
        this.componentRepository = componentRepository;
        this.componentMapper = componentMapper;
    }

    @Override
    public ChangeComponent save(ChangeComponent productCategory) {
        ComponentEntity productCategoryEntity = componentMapper.fromModelTpEntity(productCategory);
        ComponentEntity saved = componentRepository.save(productCategoryEntity);
        validateSavedEntity(saved);
        return componentMapper.fromEntityToModel(saved);
    }

    @Override
    public void remove(UUID id) {
        componentRepository.deleteById(id);
    }

    @Override
    public ChangeComponent findById(UUID id) {
        Optional<ComponentEntity> buComponent = componentRepository.findById(id);
        return buComponent.map(componentMapper::fromEntityToModel).orElse(null);
    }

    @Override
    public List<ChangeComponent> findAll() {
        List<ComponentEntity> all = componentRepository.findAll();
        return componentMapper.map(all);
    }

    @Override
    public ChangeComponent update(UUID id, ChangeComponent changeComponent) {
        Optional<ComponentEntity> resultById = componentRepository.findById(id);
        if (resultById.isPresent()) {
            ComponentEntity componentEntityToChange = resultById.get();
            componentEntityToChange.update(id, componentEntityToChange);

            return componentMapper.fromEntityToModel(componentRepository.save(componentEntityToChange));
        }
        return null;
    }

    private void validateSavedEntity(ComponentEntity saved) {
        if (saved == null) {
            throw new ResourceFoundException("Erro ao salvar produto no repositorio: entidade salva é nula");
        }
    }
}
